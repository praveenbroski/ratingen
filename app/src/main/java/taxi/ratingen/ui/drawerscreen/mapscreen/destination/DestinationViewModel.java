package taxi.ratingen.ui.drawerscreen.mapscreen.destination;

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
import com.google.android.gms.maps.model.Marker;
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

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationViewModel extends BaseNetwork<BaseResponse, DestinationNavigator> implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "DestinationFragment";

    public static ObservableField<LatLng> mMapLatLng = new ObservableField<>();
    private static ObservableBoolean setListener = new ObservableBoolean(false);
    private static ObservableBoolean zoomSet = new ObservableBoolean(false);
    public static ObservableField<String> pickAddress = new ObservableField<>();
    public static ObservableField<String> dropAddress = new ObservableField<>("");
    public ObservableBoolean isScanEnabled;

    GitHubService gitHubService;
    HashMap<String, String> hashMap;
    public static GitHubMapService gitGoogle;
    public static LatLng pickLatLng = null;
    public static LatLng dropLatLng = null;
    private static Context context;
    private static GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String scancontent = "";

    public SharedPrefence sharedPrefence;

    HashMap<String, Marker> driverPins = new HashMap<>();
    HashMap<String, String> driverDatas = new HashMap<>();

    @Inject
    public DestinationViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                @Named(Constants.googleMap) GitHubMapService googleGit, SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        gitGoogle = googleGit;
        isScanEnabled = new ObservableBoolean(false);
    }

    public void setPins(LatLng pickUpLatlng, String pickupAddress, GoogleMap googleMap, String scanContent, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        this.scancontent = scanContent;
        this.googleMap = googleMap;
        this.driverPins = driverPins;
        this.driverDatas = driverDatas;

        if (!CommonUtils.IsEmpty(scanContent)) {
            isScanEnabled.set(true);
        }
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

    public void onClickSearchLocation(View v) {
        getmNavigator().openSearchDestination();
    }

    public void onClickSetDestination(View v) {
        if (mLastLocation == null && pickLatLng != null) {
            mLastLocation = new Location("");
            mLastLocation.setLatitude(pickLatLng.latitude);
            mLastLocation.setLongitude(pickLatLng.longitude);
        }
        getmNavigator().onClickConfirmation(pickAddress.get(), pickLatLng, dropAddress.get(), dropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), "", driverPins, driverDatas);
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
                            dropLatLng = latLng;
                            dropAddress.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                        } else if (status.equals("OVER_QUERY_LIMIT")) {
                            try {
                                Geocoder geocoder = new Geocoder(context);
                                List<Address> addresses = geocoder.getFromLocation(mMapLatLng.get().latitude
                                        , mMapLatLng.get().longitude, 1);
                                String mAddress = "";
                                if (addresses != null && addresses.size() > 0) {
                                    if (addresses.get(0).getAddressLine(0) != null) {
                                        mAddress = addresses.get(0).getAddressLine(0);
                                    }
                                    if (addresses.get(0).getAddressLine(1) != null) {
                                        mAddress = mAddress + ", " + addresses.get(0).getAddressLine(1);
                                    }
                                    if (addresses.get(0).getAddressLine(2) != null) {
                                        mAddress = mAddress + ", " + addresses.get(0).getAddressLine(2);
                                    }
                                }
                                dropLatLng = latLng;
                                dropAddress.set(mAddress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
//            changeMapStyle();
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (!dropAddress.get().equals("")) {
                getLocationFromAddress(dropAddress.get(), true);
            } else {
                if (mLastLocation != null) {
                    MarkerAnimation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constants.ZOOMLEVELMAP);
                    GetAddressFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    // mMapLatLng.set();
                }
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
    public static void getLocationFromAddress(final String place, boolean zoom) {
        float zoomLevel = zoom ? Constants.ZOOMLEVELMAP : googleMap.getCameraPosition().zoom;
        try {
            Geocoder gCoder = new Geocoder(context);
            final List<Address> list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                    dropLatLng = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
                if (googleMap != null)
                    MarkerAnimation(dropLatLng, zoomLevel);
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
                            dropLatLng = new LatLng(lat, lng);
                            if (googleMap != null)
                                MarkerAnimation(dropLatLng, zoomLevel);
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

    /**
     * click listener for QR code scanner for scanning private key
     **/
    public void onClickScanQr(View view) {
        getmNavigator().openScannerPage();
    }

    public void qrScanned(String scanContent) {
        if (dropAddress != null && !CommonUtils.IsEmpty(dropAddress.get()) && dropLatLng != null
                && dropLatLng.latitude != 0.0 && dropLatLng.longitude != 0.0)
            getmNavigator().onClickConfirmation(pickAddress.get(), pickLatLng, dropAddress.get(), dropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent, driverPins, driverDatas);
        else
            getmNavigator().onClickConfirmation(pickAddress.get(), pickLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent, driverPins, driverDatas);
    }

}
