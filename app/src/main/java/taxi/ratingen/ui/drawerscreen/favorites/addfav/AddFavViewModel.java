package taxi.ratingen.ui.drawerscreen.favorites.addfav;

import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AddFavViewModel extends BaseNetwork<BaseResponse, AddFavFragment> {

    HashMap<String, String> hashMap;
    SharedPrefence sharedPrefence;
    GitHubMapService gitGoogle;

    public ObservableField<String> mPlaceName = new ObservableField<>("");
    public ObservableField<String> mAddress = new ObservableField<>("");
    public ObservableBoolean mIsLoading = new ObservableBoolean(false);
    public ObservableBoolean mIsEditEnable = new ObservableBoolean(true);
    public ObservableBoolean mIsSubmitEnable = new ObservableBoolean(false);
    LatLng mLatLng;

    String TAG = "AddFavFragment";

    public AddFavViewModel(GitHubService gitHubService, GitHubMapService mapService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitGoogle = mapService;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        mIsLoading.set(false);
        if (response.successMessage.equalsIgnoreCase("Favorite_Added_Successfully")) {
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.Toast_Favorite_Add));
            getmNavigator().goBackRefresh();
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        mIsLoading.set(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    public void onHomeClick(View v) {
        mPlaceName.set(getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_Home));
        mIsEditEnable.set(false);
    }

    public void onWorkClick(View v) {
        mPlaceName.set(getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_Work));
        mIsEditEnable.set(false);
    }

    public void onOtherClick(View v) {
        mPlaceName.set("");
        mIsEditEnable.set(true);
    }

    public void onPickFromMapClick(View v) {
        getmNavigator().openPickFromMapFragment();
    }

    public void onPlaceTextChanged(Editable editable) {
        mPlaceName.set(editable.toString());
        if (mPlaceName.get().length() > 0 && mAddress.get().length() > 0)
            mIsSubmitEnable.set(true);
        else
            mIsSubmitEnable.set(false);
    }

    public void onAddressTextChanged(Editable editable) {
        mAddress.set(editable.toString());
        if (mPlaceName.get().length() > 0 && mAddress.get().length() > 0)
            mIsSubmitEnable.set(true);
        else
            mIsSubmitEnable.set(false);
    }

    public void onClickSavePlace(View view) {
        if (getmNavigator().isNetworkConnected()) {
            getLocationFromAddress(mAddress.get());
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    private void getLocationFromAddress(final String place) {
        try {
            Geocoder gCoder = new Geocoder(getmNavigator().getAttachedContext());
            final List<Address> list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                mLatLng = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
                String address = "";
                if (list.get(0).getAddressLine(0) != null) {
                    address = list.get(0).getAddressLine(0);
                }
                if (list.get(0).getAddressLine(1) != null) {
                    address = address + ", " + list.get(0).getAddressLine(1);
                }
                if (list.get(0).getAddressLine(2) != null) {
                    address = address + ", " + list.get(0).getAddressLine(2);
                }
                mAddress.set(address);

                callAddFavAPI();
            }
        } catch (Exception e) {
            e.printStackTrace();
            gitGoogle.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {
                            double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            mLatLng = new LatLng(lat, lng);
                            mAddress.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());

                            callAddFavAPI();
                        }
                    } else {
                        Log.d(TAG, "GetAddressFromLatlng " + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d(TAG, "GetAddressFromLatlng" + t.toString());
                }
            });
        }
    }

    public void callAddFavAPI() {
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.nickName, mPlaceName.get());
            hashMap.put(Constants.NetworkParameters.placeId, "" + mAddress.get());
            hashMap.put(Constants.NetworkParameters.latitude, "" + mLatLng.latitude);
            hashMap.put(Constants.NetworkParameters.longitude, "" + mLatLng.longitude);

            AddFavNetworkcall();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void clickBack(View v) {
        getmNavigator().goBack();
    }

}
