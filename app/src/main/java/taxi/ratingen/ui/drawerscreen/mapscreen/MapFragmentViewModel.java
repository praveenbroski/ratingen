package taxi.ratingen.ui.drawerscreen.mapscreen;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Car;
import taxi.ratingen.retro.responsemodel.ProfileModel;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.retro.dynamicInterceptor;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.FirebaseHelper;
import taxi.ratingen.utilz.MyComponent;
import taxi.ratingen.utilz.SocketHelper;
import taxi.ratingen.utilz.SocketMessageModel;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static taxi.ratingen.ui.drawerscreen.mapscrn.MapScrnViewModel.PickupLatLng;

/**
 * Created by root on 10/11/17.
 */

//public class MapFragmentViewModel extends BaseNetwork<User, MapNavigator> implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, SocketHelper.SocketListener, FirebaseHelper.FirebaseObserver {
public class MapFragmentViewModel extends BaseNetwork<User, MapNavigator> implements
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, SocketHelper.SocketListener
        , FirebaseHelper.FirebaseObserver {
    private static final String TAG = "MapFragment";
    public static ObservableField<LatLng> mMapLatLng = new ObservableField<>();

    private static ObservableBoolean setlistener, IsSocketConnected;
    private static ObservableBoolean zoomset = new ObservableBoolean(false);
    public static ObservableBoolean IsIdle;
    public static ObservableBoolean isProgressShown = new ObservableBoolean(true);
    public static Boolean mMapIsTouched = true;

    public ObservableArrayMap<String, Object> observableArrayMap = new ObservableArrayMap<>();
    public static ObservableField<String> address = new ObservableField<>();
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    LatLng MarkerlatLng;
    BaseResponse updatedcar;

    public static dynamicInterceptor interceptor;
    private static GoogleMap googleMap;

    static SharedPrefence sharedPrefence;
    static SocketMessageModel socketMessageModel = new SocketMessageModel();
    static Gson gson;
    ScheduledFuture<?> scheduledFuture;

    private ArrayList<String> resultList = new ArrayList<>();

    HashMap<String, Marker> driverPins = new HashMap<>();
    HashMap<String, String> driverDatas = new HashMap<>();

    public HashMap<String, String> hashMap;
    private static GitHubService gitHubService;
    public static GitHubMapService gitgoogle;
    private static Context context;
    public static ObservableBoolean isLocationAvailable;
    public static ObservableBoolean IsCurrentloction;
    public static ObservableBoolean isFromAutoComplete = new ObservableBoolean(false);
    public static ObservableBoolean IsdrivercarsPlaced, Istypedata;
    public ObservableField<String> ImageUrl = new ObservableField<>("");

    public MapFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                @Named(Constants.googleMap) GitHubMapService googlegit, SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        MapFragmentViewModel.gitHubService = gitHubService;
        gitgoogle = googlegit;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        DataBindingUtil.setDefaultComponent(new MyComponent(this));
        DefaultBooleans();
    }

    /**
     * define default booleans
     **/
    private void DefaultBooleans() {
        updatedcar = new BaseResponse();
        IsdrivercarsPlaced = new ObservableBoolean(false);
        Istypedata = new ObservableBoolean(true);
        IsIdle = new ObservableBoolean();
        IsCurrentloction = new ObservableBoolean();
        isLocationAvailable = new ObservableBoolean(false);
        IsSocketConnected = new ObservableBoolean(false);
        setlistener = new ObservableBoolean();
    }

    /**
     * custom {@link BindingAdapter} function to initialize {@link MapView}
     **/
    @BindingAdapter({"initMap"})
    public static void initMap(MapView mapView, final LatLng latLng) {
        if (mapView != null && latLng != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googlMap) {
                    googleMap = googlMap;
//                    AnimationMarker(latLng);
                }
            });
        }
    }

    /**
     * called when API call is successful
     **/
    @Override
    public void onSuccessfulApi(long taskId, User response) {

    }

    /**
     * called when API call fails
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /**
     * add marker and animate google map camera to focus marker
     **/
    private void AnimationMarker(LatLng latLng) {
        if (googleMap != null)
            googleMap.clear();

        if (!setlistener.get()) {
            setlistener.set(true);
            setListeners();
        }

        googleMap.addMarker(new MarkerOptions().position(latLng).title("currentlocation"));
        if (!zoomset.get()) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng, 15));
        } else {
            CameraUpdateFactory.newLatLngZoom(latLng, 15);
        }
    }

    /**
     * set google map camera idle listener to get address
     **/
    private void setListeners() {
        googleMap.setOnCameraIdleListener(() -> {
            IsIdle.set(true);
            if (!mMapIsTouched && !isFromAutoComplete.get()) {
                PickupLatLng = new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude);
//                FirebaseHelper.queryDrivers(PickupLatLng);
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.lat = googleMap.getCameraPosition().target.latitude;
                socketMessageModel.lng = googleMap.getCameraPosition().target.longitude;
                System.out.println(TAG + "+++IAmEmmiting++" + gson.toJson(socketMessageModel));
                SocketHelper.sendTypes(gson.toJson(socketMessageModel));

                GetAddressFromLatLng(googleMap.getCameraPosition().target);
            } else {
                isFromAutoComplete.set(false);
            }
        });
        googleMap.setOnCameraMoveStartedListener(i -> {
            IsIdle.set(false);
            switch (i) {
                case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                    IsIdle.set(false);
                    break;

                case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
                case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                    IsIdle.set(false);
                    break;
            }
        });
    }

    public void sd() {

    }

    /**
     * gets address for the given {@link LatLng}
     **/
    private void GetAddressFromLatLng(LatLng latLng) {
        mMapLatLng.set(latLng);
        gitgoogle.GetAddressFromLatLng(mMapLatLng.get().latitude + "," + mMapLatLng.get().longitude, false, Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getAsJsonArray("results") != null) {
                       /* if(address.get()!=null&&!address.get().isEmpty()){
                            address.set(null);
                        }*/

                        String status = response.body().get("status").getAsString();
                        if (status.equals("OK")) {
                            address.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
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
                                address.set(mAddress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        observableArrayMap.put("Address", address.get());
                        socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                        socketMessageModel.lat = latLng.latitude;
                        socketMessageModel.lng = latLng.longitude;
//                            socketMessageModel.pickup_address = mPickupAddress.get();
                        System.out.println(TAG + "+++IAmEmmiting++" + gson.toJson(socketMessageModel));
                        SocketHelper.sendTypes(gson.toJson(socketMessageModel));
//                        FirebaseHelper.queryDrivers(latLng);
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

    /**
     * Reverse {@link Geocoder} -> Get {@link Location} from given address {@link String}
     *
     * @param place
     **/
    public void getLocationFromAddress(final String place) {
        try {
            Geocoder gCoder = new Geocoder(context);
            final List<Address> list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                PickupLatLng = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
                mMapLatLng.set(PickupLatLng);
//                FirebaseHelper.queryDrivers(PickupLatLng);
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.lat = PickupLatLng.latitude;
                socketMessageModel.lng = PickupLatLng.longitude;
                System.out.println(TAG + "+++IamEmmiting+++" + gson.toJson(socketMessageModel));
                SocketHelper.sendTypes(gson.toJson(socketMessageModel));
            }
            if (googleMap != null)
                MarkerAnimation(PickupLatLng, googleMap.getCameraPosition().zoom);
        } catch (Exception e) {
            e.printStackTrace();
            gitgoogle.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {
                            double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            PickupLatLng = new LatLng(lat, lng);
                            mMapLatLng.set(PickupLatLng);
//                            FirebaseHelper.queryDrivers(PickupLatLng);
                            socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                            socketMessageModel.lat = PickupLatLng.latitude;
                            socketMessageModel.lng = PickupLatLng.longitude;
                            System.out.println(TAG + "+++IamEmmiting+++" + gson.toJson(socketMessageModel));
                            SocketHelper.sendTypes(gson.toJson(socketMessageModel));

                            if (googleMap != null)
                                MarkerAnimation(PickupLatLng, googleMap.getCameraPosition().zoom);
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
     * custom {@link BindingAdapter} function to listen pickup address change
     *
     * @param PickAdd
     **/
    @BindingAdapter({"pickupAddress"})
    public static void PickAddChange(TextView view, String PickAdd) {
//        getLocationFromAddress(PickAdd);
    }

    /**
     * initialize {@link GoogleApiClient} to make use of google API's
     **/
    public void buildGoogleApiClient(GoogleMap mGoogleMap) {
        context = getmNavigator().getAttachedContext();
        googleMap = mGoogleMap;
        mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getAttachedContext())
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        if (!setlistener.get()) {
            setlistener.set(true);
            setListeners();
        }
    }

    /**
     * callback to notify {@link GoogleApiClient} has been connected successfully
     **/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
//            changeMapStyle();
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                sharedPrefence.savevalue(SharedPrefence.LONGITUDE, mLastLocation.getLongitude() + "");
                sharedPrefence.savevalue(SharedPrefence.LATITUDE, mLastLocation.getLatitude() + "");
                isLocationAvailable.set(true);
                MarkerAnimation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constants.ZOOMLEVELMAP);
                GetAddressFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                // mMapLatLng.set();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize {@link SocketHelper}
     **/
    public void SetSocketListener() {
        SocketHelper.init(sharedPrefence, this, TAG, false);
//        FirebaseHelper.init(sharedPrefence, this, false);
        Types(SocketHelper.getLastLoadedTypes());
    }

    public static void MarkerAnimation(LatLng latLng, float zoomLevel) {
        mMapIsTouched = true;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    /**
     * callback when {@link GoogleApiClient} connection is suspended and retry
     **/
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    /**
     * callback to notify {@link GoogleMap} has been loaded
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapFragmentViewModel.googleMap = googleMap;
    }

    /**
     * called when an address was chosen from autocomplete address list
     **/
    public void AutoItemclicked(AdapterView<?> adapterView, View view, int i, long l) {
        String address = (String) adapterView.getAdapter().getItem(i);
        Log.d(TAG, "Autocomplete Select Address" + address != null ? address : "Null");

        if (address != null && !address.isEmpty()) {
            LatLng latlng = getmNavigator().getReverseGeocode(address);

            if (latlng != null) {
                zoomset.set(false);
                mMapLatLng.set(latlng);
            }
        }
    }

    /**
     * change map style and appearance by using style json file
     **/
    private void changeMapStyle() {
//        if (googleMap == null)
//            return;
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
//        googleMap.setMapStyle(style);
    }


    //change later with retrofit...

    /**
     * get address results from autocomplete api
     **/
    public ArrayList<String> autocomplete(String input) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Constants.URL.PLACES_API_BASE
                    + Constants.URL.TYPE_AUTOCOMPLETE + Constants.URL.OUT_JSON);
            sb.append("?sensor=false&key=" + Constants.PlaceApi_key);
            if (mMapLatLng != null) {
                LatLng latLng = mMapLatLng.get();
                sb.append("&location=" + latLng.latitude
                        + "," + latLng.longitude);
            }
            /*if (Const.COUNTRY_CODE != null) {
                sb.append("&components=country:" + Const.COUNTRY_CODE);
            }*/
            sb.append("&components=country:IN");
            sb.append("&radius=500");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.d(TAG, "PlaceApi" + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            System.out.println("++Placeapiresponse++" + jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList.clear();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString(
                        "description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
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
//        getmNavigator().izettleLogin();
    }

    public void onClickPickLocation(View v) {
        getmNavigator().pickAddressClicked();
    }

    public void chooseDestination(View v) {
//        stopTypesTimer();
        getmNavigator().openDestinationFragment("", driverPins, driverDatas);
    }

    /**
     * Types callback is from {@link SocketHelper}. This callback is called whenever types data is received in socket.
     * Driver markers are added to map from this data.
     *
     * @param typesString
     **/
    @Override
    public void Types(final String typesString) {
        if (typesString != null) {
            BaseResponse baseResponse = gson.fromJson(typesString, BaseResponse.class);

            if (baseResponse != null && baseResponse.success) {
                Istypedata.set(true);
                isProgressShown.set(false);
            } else {
                Istypedata.set(false);
                isProgressShown.set(false);
            }
        }

        if (getmNavigator().getAttachedContext() != null)
            getmNavigator().getAttachedContext().runOnUiThread(() -> {
                System.out.println("++" + typesString);
                if (typesString != null) {
                    BaseResponse baseResponse = gson.fromJson(typesString, BaseResponse.class);

                    if (baseResponse != null && baseResponse.success) {
                        Istypedata.set(true);
//                        isProgressShown.set(false);
                        getmNavigator().addcarList(baseResponse.getTypes());
                        if (baseResponse.getTypes() != null)
                            if (baseResponse.getTypes().size() > 0) {
                                final List<Car> driverList = new ArrayList<>();
                                Log.d(TAG, "Obj = " + (getmNavigator().GetSelectedCarObj() != null));
                                for (Type type : baseResponse.getTypes()) {
                                    if (type != null)
                                        if (type.drivers != null)
                                            driverList.addAll(type.drivers);
                                }
                                if (getmNavigator() != null && getmNavigator().getAttachedContext() != null)
                                    getmNavigator().getAttachedContext().runOnUiThread(() -> setDriverMarkers(driverList));
                                if (baseResponse.getTypes().get(0) != null && baseResponse.getTypes().get(0).preferred_payment != null)
                                    sharedPrefence.saveInt(SharedPrefence.PREFFERED_PAYMENT, baseResponse.getTypes().get(0).preferred_payment);
                            }
                    } else {
                        Istypedata.set(false);
//                        isProgressShown.set(false);
                    }
                }
            });
    }

    /**
     * Displays car markers on home screen map
     *
     * @param cars
     **/
    public synchronized void setDriverMarkers(List<Car> cars) {
        if (cars != null && cars.size() != 0) {
            Iterator litr = cars.iterator();
            if (googleMap != null) {
                googleMap.clear();
            }
            while (litr.hasNext()) {
                Car element = (Car) litr.next();
                MarkerlatLng = new LatLng(element.latitude, element.longitude);
                createMarker(MarkerlatLng, element.bearing);
            }

        } else {
            if (googleMap != null) {
                googleMap.clear();
            }
        }
    }

    MarkerOptions markeroption = new MarkerOptions();
    BitmapDescriptor bitmapDescriptorFactory;

    /**
     * Create car marker with given {@link LatLng} and bearing
     *
     * @param latLng
     * @param bearing
     **/
    public void createMarker(LatLng latLng, Float bearing) {
        if (bitmapDescriptorFactory == null)
            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
        markeroption.position(latLng).anchor(0.5f, 0.5f).rotation(bearing).icon(bitmapDescriptorFactory);
        /*return */
        googleMap.addMarker(markeroption);
    }

    @Override
    public void TripStatus(String trip_status) {
//        Log.i(TAG, "Trip_Status" + trip_status);
//        if (getmNavigator().getAttachedContext() != null) {
//            getmNavigator().getAttachedContext().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = null;
//                    try {
//                        data = new JSONObject(trip_status);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
////                        System.out.println("+get_cars+" + data.toString());
//                    if (data != null) {
//                        BaseResponse baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
//                        if (baseResponse != null && baseResponse.successMessage != null
//                                && baseResponse.successMessage.equalsIgnoreCase("Accepted") && baseResponse.getRequest() != null) {
////                                if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
////                                    if (getmNavigator().getAttachedcontext() != null)
////                                        getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.Txt_DriverAccepted));
////                                } else
////                                    getmNavigator().openTripFragment(baseResponse.getRequest());
//
////                                if (getmNavigator().getAttachedcontext() != null)
////                                    getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.Txt_DriverAccepted));
//                            if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
//                                getmNavigator().openRideLaterAlert(baseResponse.getRequest());
//                            } else
//                                getmNavigator().openTripFragment(baseResponse.getRequest());
//                        }
//                    }
//                }
//            });
//        }
    }

    @Override
    public void CancelledRequest(String cancelled_request) {
        if (!CommonUtils.IsEmpty(cancelled_request)) {
            Log.i(TAG, "keyss---------Cancel" + cancelled_request);
            getmNavigator().notifyNoDriverMessage(cancelled_request);
        }
    }

    @Override
    public void RideLaterNoCaptainAlert(String ride_later_no_captain) {

    }

    @Override
    public void DurationHandler(String duration_handler) {

    }

    @Override
    public boolean isNetworkConnected() {
        return getmNavigator().isNetworkConnected();
    }

    @Override
    public void OnConnect() {
        IsSocketConnected.set(true);
    }

    @Override
    public void OnDisconnect() {
        IsSocketConnected.set(false);
//        isProgressShown.set(false);
    }

    @Override
    public void OnConnectError() {
        Istypedata.set(false);
        IsSocketConnected.set(false);
//        isProgressShown.set(false);
    }
    @Override
    public void driverEnteredFence(String key, GeoLocation location, String response) {
        Log.v("fatal_log", "Driver entered: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("is_active")) {
                boolean isActive = jsonObject.getBoolean("is_active");
                if (isActive) {
                    long updatedAt = jsonObject.getLong("updated_at");
                    long currentTime = new Date().getTime();
                    long diff = (currentTime - updatedAt) / 1000;
                    if (diff < (5 * 60)) {
                        Istypedata.set(true);
                        isProgressShown.set(false);

                        double bearing = jsonObject.getDouble("bearing");
                        if (driverPins.containsKey(key)) {
                            /*Marker driverPin = */
                            driverPins.get(key).remove();
                            /*driverPin.remove();*/

                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                            markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory);
//                            Marker marker =;

                            driverPins.put(key, googleMap.addMarker(markeroption));
                            driverDatas.put(key, response);
                            googleMap.clear();
                            for (String keyValues : driverPins.keySet()) {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                                markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                                googleMap.addMarker(markeroption);
                            }
                        } else {
                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                            markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory);
//                            Marker marker = ;
                            driverPins.put(key, googleMap.addMarker(markeroption));
                            driverDatas.put(key, response);
                            googleMap.clear();
                            for (String keyValues : driverPins.keySet()) {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                                markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                                googleMap.addMarker(markeroption);
                            }
                            FirebaseHelper.addObserverFor(key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void driverExitedFence(String key, String response) {
        Log.v("fatal_log", "Driver exited: " + response);
        try {
            if (driverPins.containsKey(key)) {
                Marker driverPin = driverPins.get(key);
                driverPin.remove();
                driverPins.remove(key);
                driverDatas.remove(key);

                FirebaseHelper.removeObserverFor(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void driverMovesInFence(String key, GeoLocation location, String response) {
        Log.v("fatal_log", "Driver moves: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("is_active")) {
                boolean isActive = jsonObject.getBoolean("is_active");
                if (isActive) {
                    if (driverPins.containsKey(key)) {
                        Marker driverPin = driverPins.get(key);
                        double bearing = jsonObject.getDouble("bearing");

                        driverPin.remove();

                        bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                        markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory);
                        Marker marker = googleMap.addMarker(markeroption);
                        driverPins.put(key, marker);
                        driverDatas.put(key, response);
                        googleMap.clear();
                        for (String keyValues : driverPins.keySet()) {
                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                            markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                            googleMap.addMarker(markeroption);
                        }
                    } else {
                        long updatedAt = jsonObject.getLong("updated_at");
                        long currentTime = new Date().getTime();
                        long diff = (currentTime - updatedAt) / 1000;
                        if (diff < (5 * 60)) {
                            Istypedata.set(true);
                            isProgressShown.set(false);

                            double bearing = jsonObject.getDouble("bearing");
                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                            markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory);
                            Marker marker = googleMap.addMarker(markeroption);
                            driverPins.put(key, marker);
                            driverDatas.put(key, response);
                            googleMap.clear();
                            for (String keyValues : driverPins.keySet()) {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                                markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                                googleMap.addMarker(markeroption);
                            }
                            FirebaseHelper.addObserverFor(key);
                        }
                    }
                } else {
                    if (driverPins.containsKey(key)) {
                        Marker driverPin = driverPins.get(key);
                        driverPin.remove();
                        driverPins.remove(key);
                        driverDatas.remove(key);

                        FirebaseHelper.removeObserverFor(key);
                    }
                    googleMap.clear();
                    for (String keyValues : driverPins.keySet()) {
                        bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                        markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                        googleMap.addMarker(markeroption);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void driverWentOffline(String key) {
//        Log.v("fatal_log", "driverWentOffline: " + key);
        try {
            if (driverPins.containsKey(key)) {
                Marker driverPin = driverPins.get(key);
                driverPin.remove();
                driverPins.remove(key);
                driverDatas.remove(key);

                googleMap.clear();
                for (String keyValues : driverPins.keySet()) {
                    bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                    markeroption.position(driverPins.get(keyValues).getPosition()).anchor(0.5f, 0.5f).rotation((float) driverPins.get(keyValues).getRotation()).icon(bitmapDescriptorFactory);
                    googleMap.addMarker(markeroption);
                }
                FirebaseHelper.removeObserverFor(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void driverDataUpdated(String key, String response) {

    }

    @Override
    public void tripStatusReceived(String response) {

    }
    /**
     * click listener for QR code scanner for scanning private key
     **/
    public void onClickScanQr(View view) {
        getmNavigator().openScannerPage();
    }

    public void qrScanned(String scanContent) {
//        if (mDropupAddress != null && !CommonUtils.IsEmpty(mDropupAddress.get()) && DropLatLng != null
//                && DropLatLng.latitude != 0.0 && DropLatLng.longitude != 0.0)
//            getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, mDropupAddress.get(), DropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent);
//        else
//            getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent);
    }

    public void setupProfile() {
        String userStr = sharedPrefence.Getvalue(SharedPrefence.USERDETAILS);
        ProfileModel user = CommonUtils.IsEmpty(userStr) ? null : gson.fromJson(userStr, ProfileModel.class);
        if (user != null) {
            if (!CommonUtils.IsEmpty(user.getProfilePicture()))
                ImageUrl.set(user.getProfilePicture());
        }
    }

    public void onMenuClick(View view) {
        getmNavigator().openSideMenu();
    }

    public void startTypesTimer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture =
                service.scheduleAtFixedRate(typesRunnable, 5, 10, TimeUnit.SECONDS);
    }

    public void stopTypesTimer() {
        if (scheduledFuture != null)
            try {
                scheduledFuture.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    Runnable typesRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMapLatLng != null && socketMessageModel != null
                    && mMapLatLng.get().latitude != 0 && mMapLatLng.get().longitude != 0) {
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.lat = mMapLatLng.get().latitude;
                socketMessageModel.lng = mMapLatLng.get().longitude;
                System.out.println(TAG + "+++IamEmmiting+++" + gson.toJson(socketMessageModel));
                SocketHelper.sendTypes(gson.toJson(socketMessageModel));
            }
        }
    };

    @BindingAdapter("setProfileImage")
    public static void setProfileImage(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url)
                .apply(RequestOptions.circleCropTransform()
                        .override(57, 57)
                        .error(R.drawable.ic_menu_ham)
                        .placeholder(R.drawable.ic_menu_ham))
                .into(imageView);
    }

}
