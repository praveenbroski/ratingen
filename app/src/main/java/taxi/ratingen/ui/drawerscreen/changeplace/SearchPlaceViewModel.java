package taxi.ratingen.ui.drawerscreen.changeplace;


import android.content.Context;
import androidx.databinding.ObservableField;
import android.text.Editable;
import android.view.View;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by root on 11/30/17.
 */


public class SearchPlaceViewModel extends BaseNetwork<BaseResponse, SearchPlaceNavigator> {

    @Inject
    SharedPrefence sharedPrefence;
    public ObservableField<String> title;
    public ObservableField<Boolean> isPickup;
    public ObservableField<String> pickAddress = new ObservableField<>();
    public ObservableField<String> dropAddress = new ObservableField<>();

    @Named(Constants.googleMap)
    @Inject
    GitHubMapService mapService;

    @Inject
    Context context;

    List<Favplace> favplaces = new ArrayList<>();
    @Inject
    HashMap<String, String> hashMap;

    @Inject
    Gson gson;


    @Inject
    public SearchPlaceViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        title = new ObservableField<>();
        isPickup = new ObservableField<>(true);
    }

    /** called when back button is clicked **/
    public void onClickBack(View view) {
        getmNavigator().FinishAct();
    }

    /** triggered when text content changes in pickup search box **/
    public void onPickEditTextChanged(Editable editable) {
        pickAddress.set(editable.toString());
        if (editable.toString().isEmpty() && editable.length() == 0) {
            System.out.println("++onPlaceEditTextchanged+++");
            getmNavigator().showPickClearButton(false);
            BaseResponse baseResponse = gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.FAVLIST), BaseResponse.class);
            if (baseResponse != null && baseResponse.data != null) {
                String favList = CommonUtils.ObjectToString(baseResponse.data);
                List<Favplace> favPlaces = CommonUtils.stringToArray(favList, Favplace[].class);
                getmNavigator().addList(favPlaces);
            }
        } else if (!editable.toString().isEmpty() && editable.length() > 3) {
            getmNavigator().showPickClearButton(true);
            getPlaceApicall(editable.toString());
        } else {
            getmNavigator().showPickClearButton(false);
            BaseResponse baseResponse = gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.FAVLIST), BaseResponse.class);
            if (baseResponse != null && baseResponse.data != null) {
                String favList = CommonUtils.ObjectToString(baseResponse.data);
                List<Favplace> favPlaces = CommonUtils.stringToArray(favList, Favplace[].class);
                getmNavigator().addList(favPlaces);
            }
        }
    }

    /** triggered when text content changes in search box **/
    public void onPlaceEditTextchanged(Editable editable) {
        dropAddress.set(editable.toString());
        if (editable.toString().isEmpty() && editable.length() == 0) {
            System.out.println("++onPlaceEditTextchanged+++");
            getmNavigator().showclearButton(false);
            BaseResponse baseResponse = gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.FAVLIST), BaseResponse.class);
            if (baseResponse != null && baseResponse.data != null) {
                String favList = CommonUtils.ObjectToString(baseResponse.data);
                List<Favplace> favPlaces = CommonUtils.stringToArray(favList, Favplace[].class);
                getmNavigator().addList(favPlaces);
            }
        } else if (!editable.toString().isEmpty() && editable.length() > 3) {
            getmNavigator().showclearButton(true);
            getPlaceApicall(editable.toString());
        } else {
            getmNavigator().showclearButton(false);
            BaseResponse baseResponse = gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.FAVLIST), BaseResponse.class);
            if (baseResponse != null && baseResponse.data != null) {
                String favList = CommonUtils.ObjectToString(baseResponse.data);
                List<Favplace> favPlaces = CommonUtils.stringToArray(favList, Favplace[].class);
                getmNavigator().addList(favPlaces);
            }
        }
    }

    /** calls places API with the search term entered in search box **/
    private void getPlaceApicall(String s) {
        LatLng latLng;
        String input = null;
        latLng = getmNavigator().getLatLngForStart();
       /* try {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ar"))
                input = s;
            else
                input = URLEncoder.encode(s, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        input = s;

        favplaces.clear();
        Callback placesapi_callback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().get("status").getAsString().equalsIgnoreCase("OK")) {
                    JsonArray predsJsonArray = response.body().getAsJsonArray("predictions");
                    for (int i = 0; i < predsJsonArray.size(); i++) {
                        Favplace favplace = new Favplace();
                        favplace.IsPlaceLayout = true;
                        String address = predsJsonArray.get(i).getAsJsonObject().get("description").getAsString();
                        favplace.PlaceApiOGaddress = address;
                        favplace.IsPlaceLayout = true;
                        if (address.contains(",")) {
                            favplace.nickName = address.substring(0, address.indexOf(","));
                            int firstIndex = address.indexOf(",");
                            favplace.placeId = address.substring(firstIndex).replace(",", "").trim();
                        } else if (!address.contains(",")) {
                            favplace.nickName = address;
                            favplace.placeId = "";
                        }
                        favplaces.add(favplace);
                    }
                    getmNavigator().addList(favplaces);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("++" + t.toString());
            }
        };

        if (latLng != null) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ar"))
                mapService.GetPlaceApi(Locale.getDefault().getLanguage(), latLng.latitude
                        + "," + latLng.longitude, input, false, Constants.PlaceApi_key).enqueue(placesapi_callback);
            else
                mapService.GetPlaceApi(latLng.latitude
                        + "," + latLng.longitude, input, false, Constants.PlaceApi_key).enqueue(placesapi_callback);
        } else {
            mapService.GetPlaceApi(input, false, Constants.PlaceApi_key).enqueue(placesapi_callback);
        }
    }

    /** called when API call response is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.message.equalsIgnoreCase("success")) {
            if (response.data != null) {
                String baseStr = CommonUtils.ObjectToString(response.data);
                BaseResponse baseResponse = (BaseResponse) CommonUtils.StringToObject(baseStr, BaseResponse.class);
                if (baseResponse.data != null) {
                    String favList = CommonUtils.ObjectToString(baseResponse.data);
                    List<Favplace> favPlaces = CommonUtils.stringToArray(favList, Favplace[].class);
                    if (favPlaces.size() > 0) {
                        getmNavigator().addList(favPlaces);
                    }
                    sharedPrefence.savevalue(SharedPrefence.FAVLIST, baseStr);
                }
            }
        }
    }

    /** called when API call response fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        if (e.getCode() != Constants.ErrorCode.EMPTY_FAV_LIST)
            getmNavigator().showMessage(e);
        else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().showMessage(e);
            getmNavigator().refreshCompanyKey();
        }
    }

    /** called when clear button on search box is clicked **/
    public void onClickClearBtn(View v) {
        getmNavigator().clearBtn();
    }

    /** called when clear button on search box is clicked **/
    public void onClickClearDropBtn(View v) {
        getmNavigator().clearDropBtn();
    }

    /** returns a {@link HashMap} with query parameters for API call **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** called when favourite button on search box is clicked **/
    public void onClickFav(View v) {
        getmNavigator().openFavorites();
    }

    public void GetFavListData() {
//        title.set(hashMap.get(Constants.Extra_identity));
//        isPickup.set(hashMap.get(Constants.EXTRA_IS_PICKUP).equals("1"));
//        if (hashMap.get(Constants.EXTRA_PICK_ADDRESS) != null) {
//            pickAddress.set(hashMap.get(Constants.EXTRA_PICK_ADDRESS));
//        }
//        if (hashMap.get(Constants.EXTRA_DROP_ADDRESS) != null) {
//            dropAddress.set(hashMap.get(Constants.EXTRA_DROP_ADDRESS));
//        }

        setIsLoading(true);

        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));

        if (getmNavigator().isNetworkConnected())
            GetFavListNetworkCall();
        else
            getmNavigator().showNetworkMessage();
    }

}