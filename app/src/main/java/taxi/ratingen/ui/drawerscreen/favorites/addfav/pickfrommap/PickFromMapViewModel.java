package taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickFromMapViewModel extends BaseNetwork<BaseResponse, PickFromMapFragment> implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "PickFromMapFragment";

    HashMap<String, String> hashMap;
    SharedPrefence sharedPrefence;
    public static GitHubMapService gitGoogle;

    public static ObservableField<LatLng> mMapLatLng = new ObservableField<>();
    public static ObservableField<String> mAddress = new ObservableField<>();
    public static ObservableBoolean isLocationAvailable;
    private static ObservableBoolean zoomSet = new ObservableBoolean(false);

    private static Context context;
    private static GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public PickFromMapViewModel(@Named(Constants.ourApp)GitHubService gitHubService,
                                @Named(Constants.googleMap) GitHubMapService googleGit, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        gitGoogle = googleGit;
        defaultBooleans();
    }

    private void defaultBooleans() {
        isLocationAvailable = new ObservableBoolean(false);
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    /** initialize {@link GoogleApiClient} to make use of google API's **/
    public void buildGoogleApiClient(GoogleMap mGoogleMap) {
        context = getmNavigator().getAttachedContext();
        googleMap = mGoogleMap;
        mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getAttachedContext())
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        setListeners();
    }

    /** set google map camera idle listener to get address **/
    private void setListeners() {
        googleMap.setOnCameraIdleListener(() -> {
            zoomSet.set(true);
            GetAddressFromLatLng(googleMap.getCameraPosition().target);
        });
    }

    /** gets address for the given {@link LatLng} **/
    private static void GetAddressFromLatLng(LatLng latLng) {
        mMapLatLng.set(latLng);
        gitGoogle.GetAddressFromLatLng(mMapLatLng.get().latitude + "," + mMapLatLng.get().longitude, false, Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getAsJsonArray("results") != null) {
                       /* if(address.get()!=null&&!address.get().isEmpty()){
                            address.set(null);
                        }*/

                        String status = response.body().get("status").getAsString();
                        if (status.equals("OK")) {
                            mMapLatLng.set(latLng);
                            mAddress.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                        }
                    }
                } else {
                    Log.d(TAG, "GetAddressFromLatlng" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "GetAddressFromLatlng" + t.toString());
            }
        });
    }

    public void onClickSearchLocation(View v) {
        getmNavigator().openSearchDestination();
    }

    public void onClickSelectPlace(View v) {
        getmNavigator().selectPlaceClicked();
    }

    public void onClickCurrentLocation(View v) {
        if (googleMap != null) {
            if (getmNavigator().checkLocationPermission()) {
                if (CommonUtils.isGpscheck(getmNavigator().getAttachedContext()))
                    buildGoogleApiClient(googleMap);
                else
                    getmNavigator().openRequestLocation();
            } else
                getmNavigator().requestLocationPermission();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            changeMapStyle();
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                isLocationAvailable.set(true);
                MarkerAnimation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constants.ZOOMLEVELMAP);
                GetAddressFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                // mMapLatLng.set();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public static void MarkerAnimation(LatLng latLng, float zoomLevel) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    /** change map style and appearance by using style json file **/
    private void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /**
     * Reverse {@link Geocoder} -> Get {@link Location} from given address {@link String}
     *
     * @param place
     **/
    public static void getLocationFromAddress(final String place) {
        try {
            Geocoder gCoder = new Geocoder(context);
            final List<Address> list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                mMapLatLng.set(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()));
                if (googleMap != null)
                    MarkerAnimation(mMapLatLng.get(), googleMap.getCameraPosition().zoom);

            }
        } catch (IOException e) {
            e.printStackTrace();
            gitGoogle.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {
                            double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            mMapLatLng.set(new LatLng(lat, lng));
                            if (googleMap != null)
                                MarkerAnimation(mMapLatLng.get(), googleMap.getCameraPosition().zoom);
                        }
                    } else {
                        Log.d(TAG, "GetAddressFromLatlng" + response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d(TAG, "GetAddressFromLatlng" + t.toString());
                }
            });
        }
    }

}
