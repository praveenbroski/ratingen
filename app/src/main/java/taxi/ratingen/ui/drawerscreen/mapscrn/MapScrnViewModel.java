package taxi.ratingen.ui.drawerscreen.mapscrn;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Car;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.MyComponent;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.SocketHelper;
import taxi.ratingen.utilz.SocketMessageModel;
import taxi.ratingen.utilz.exception.CustomException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 11/13/17.
 */
@BindingMethods({
        @BindingMethod(type = android.widget.ImageView.class,
                attribute = "app:srcCompat",
                method = "setImageDrawable")})
public class MapScrnViewModel extends BaseNetwork<BaseResponse, MapScrnNavigator> implements GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener, SocketHelper.SocketListener {
    private static final String TAG = "MapScrnViewModel";
    private static Context context;
    public boolean isScreenAvailable = true;
    private GoogleApiClient mGoogleApiClient;

    private static GoogleMap mGoogleMap;

    public Location mLastLocation;

    public static Boolean mMapIsTouched = true;

    public String FavCheckedNickName = "Home";
    public String FavEditNickName = null;
    public BaseResponse FavListResponse;

    public ObservableArrayMap<String, Object> observableArrayMap = new ObservableArrayMap<>();
    public ObservableField<String> mDropupAddress = new ObservableField<>();
    public ObservableField<String> mPickupAddress = new ObservableField<>();
    public static ObservableBoolean setlistener, isLocationAvailable, enableLocationPopupPickup, IsSocketConnected;
    public static ObservableBoolean DropFavImage, PickFavImage;

    public static ObservableBoolean IsGesture, IsFavGesture;
    //IsCurrentloction->true currentloction btn clicked and shouldn't fire further flow(like pick/drop/setlisener..->..
    public static ObservableBoolean IsCurrentloction;
    public static ObservableBoolean IsFavourite;
    //PickDropProceed->false Pick&Drop Address shouldn't fire..true->enable fire..
    public static ObservableBoolean PickDropProceed;
    //PickDropCard->false PickCard foreground..true->DropCard foreground..
    public static ObservableBoolean PickDropCard;
    //is
    public static ObservableBoolean IsIdle;
    public static ObservableBoolean isProgressShown = new ObservableBoolean(true);
    public static ObservableBoolean IsdrivercarsPlaced, Istypedata;
    public static ObservableBoolean canShowConfirm = new ObservableBoolean(false);
    private boolean isPickDropTapped = false;
    private int confirmFlag1 = 0;
    private int confirmFlag2 = 0;

    public HashMap<String, String> hashMap;
    private static GitHubMapService gitgoogle;
    public static LatLng PickupLatLng = null, DropLatLng = null;
    //  static Socket mSocket;
    static SharedPrefence sharedPrefence;
    static SocketMessageModel socketMessageModel = new SocketMessageModel();
    static Gson gson;
    LatLng MarkerlatLng;
    List<Marker> Markers;
    BaseResponse updatedcar;
    private boolean isMarkerRotating;
    //  isAvailable used as flag to syncronzed loading of getaddress() method
    boolean isAvailable = true;
    // Schedule the task such that it will be executed every second
    ScheduledFuture<?> scheduledFuture;

    public ObservableBoolean mapType = new ObservableBoolean(false);

    public void MapScrnViewModel() {

    }

    public MapScrnViewModel(GitHubService gitHubService, GitHubMapService mapService, Socket socket, Gson gson, SharedPrefence sharedPrefence, HashMap<String, String> hashMap) {
        super(gitHubService, sharedPrefence, gson);
        gitgoogle = mapService;
        this.hashMap = hashMap;
        MapScrnViewModel.sharedPrefence = sharedPrefence;
        MapScrnViewModel.gson = gson;
        /*if (this.mSocket != null)
            this.mSocket = null;
        this.mSocket = socket;*/
        DataBindingUtil.setDefaultComponent(new MyComponent(this));
        DefaultBooleans();
    }

    /**
     * called when API call is successful
     **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("Favorite_Added_Successfully")) {

            if (response.getFavplace() != null && response.getFavplace().size() > 0) {
                if (FavListResponse != null)
                    if (FavListResponse.getFavplace() != null)
                        if (response.getFavplace() != null)
                            if (response.getFavplace().get(0) != null)
                                FavListResponse.getFavplace().add(response.getFavplace().get(0));
                sharedPrefence.savevalue(SharedPrefence.FAVLIST, gson.toJson(FavListResponse));
                if (!PickDropCard.get()) {
                    PickFavImage.set(true);
                } else {
                    DropFavImage.set(true);
                }

            }

            FavCheckedNickName = "Home";
            FavEditNickName = null;
            IsIdle.set(true);
            IsGesture.set(false);
            IsFavourite.set(false);
            IsFavGesture.set(false);
            getmNavigator().HideNshowToolbar(false);
            getmNavigator().ChangeOldLayoutParams();
            if (!PickDropCard.get()) {
                observableArrayMap.put("Drawable", "Pick");
            } else {
                observableArrayMap.put("Drawable", "Drop");
            }
            getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.Toast_Favorite_Add));
        }

    }

    /**
     * called when API call fails
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    /**
     * adds client_id, client_token to {@link HashMap} used for API calls
     **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /**
     * custom {@link BindingAdapter} function to change the visibility of pickup
     *
     * @param isdel
     * @param Pickdropcard
     * @param mpicktext
     * @param isFav
     * @param view
     **/
    @BindingAdapter({"PickanimatedVisibility", "cardchange", "PickTxtchange", "IsFavchange"})
    public static void setPickVisibility(View view,
                                         boolean isdel, boolean Pickdropcard, String mpicktext, boolean isFav) {


        //Becuase when  intially camera gets ideal isdeal changed as true so pickdotline shows even dropcard foreground..
        //so here checking dropAddress.get..

        if (!Pickdropcard && isdel && !IsGesture.get() && !CommonUtils.IsEmpty(mpicktext) && !isFav) {

            view.setVisibility(View.VISIBLE);

        } else {
            view.setVisibility(View.GONE);
        }


    }

    /**
     * custom {@link BindingAdapter} function to change the visibility of drop
     *
     * @param isdel
     * @param Pickdropcard
     * @param mdroptext
     * @param isFav
     * @param view
     **/
    @BindingAdapter({"DropanimatedVisibility", "cardchange", "DropTxtchange", "IsFavchange"})
    public static void setDropVisibility(View view, boolean isdel, boolean Pickdropcard, String mdroptext, boolean isFav) {
        if (Pickdropcard && isdel && !IsGesture.get() && !CommonUtils.IsEmpty(mdroptext) && !isFav) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * custom {@link BindingAdapter} function to change the visibility of favourites
     *
     * @param IsFavGesture
     * @param IsFavourite
     * @param view
     **/
    @BindingAdapter({"IsFavLinenDownArrow", "IsFavLinenDownArrowone"})
    public static void setFavDotlineandDownArrowVisibility(View view,
                                                           boolean IsFavGesture, boolean IsFavourite) {

        //Fav Downarrow and Fav dot lines should shows when app gest
        if (!IsFavGesture && IsFavourite) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * called when drop is clicked
     **/
    public void onclickDrop(View view) {
        if (!PickDropCard.get()) {
            observableArrayMap.put("Drawable", "Drop");
            PickDropCard.set(true);
            if (confirmFlag2 == 1) {
                isPickDropTapped = true;
            } else {
                confirmFlag2 = 1;
            }
            getmNavigator().DropCardClicked();
            MarkerAnimation(true, Constants.ZOOMLEVELMAP);
        } else {
            isPickDropTapped = true;
            getmNavigator().DropAddressClicked();
        }
    }

    /**
     * called when pickup is clicked
     **/
    public void onclickPick(View view) {
        context = getmNavigator().getAttachedcontext();
        if (PickDropCard.get()) {
            observableArrayMap.put("Drawable", "Pick");
            PickDropCard.set(false);
            isPickDropTapped = true;
            getmNavigator().PickCardClicked();
            MarkerAnimation(true, Constants.ZOOMLEVELMAP);
        } else {
            isPickDropTapped = true;
            getmNavigator().PickAddressClicked();
        }
    }

    /**
     * click listener for QR code scanner for scanning private key
     **/
    public void onClickScanQr(View view) {
        getmNavigator().openScannerPage();
    }

    /**
     * called when my location button is clicked
     **/
    public void onclickCurrentLocation(View view) {
        if (mGoogleMap != null) {
            if (getmNavigator().checkLocationPermission()) {
                if (CommonUtils.isGpscheck(getmNavigator().getAttachedcontext()))
                    buildGoogleApiClient(mGoogleMap);
                else
                    getmNavigator().openRequestLocation();
            } else
                getmNavigator().requestLocationPermission();
        }
    }

    /**
     * ride later clicked
     **/
    public void onclickRideLater(View view) {
        chooseRide(2);
    }

    /**
     * ride now clicked
     **/
    public void onclickRideNow(View view) {
        chooseRide(1);
    }

    /**
     * ride now / ride later based on ride_type
     *
     * @param ride_type
     **/
    private void chooseRide(int ride_type) {
        if (mLastLocation == null && PickupLatLng != null) {
            mLastLocation = new Location("");
            mLastLocation.setLatitude(PickupLatLng.latitude);
            mLastLocation.setLongitude(PickupLatLng.longitude);
        }
        if (mDropupAddress != null && !CommonUtils.IsEmpty(mDropupAddress.get()) && DropLatLng != null
                && DropLatLng.latitude != 0.0 && DropLatLng.longitude != 0.0) {
            if (ride_type == 1)
                getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, mDropupAddress.get(), DropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), "");
            else if (ride_type == 2)
                getmNavigator().rideLaterClick(mPickupAddress.get(), PickupLatLng, mDropupAddress.get(), DropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        } else if (mPickupAddress != null && !CommonUtils.IsEmpty(mPickupAddress.get()) && PickupLatLng != null
                && PickupLatLng.latitude != 0.0 && PickupLatLng.longitude != 0.0) {
            if (ride_type == 1)
                getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), "");
            else if (ride_type == 2)
                getmNavigator().rideLaterClick(mPickupAddress.get(), PickupLatLng, "", null, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        }
    }

    public void onclickConfirm(View view) {
    }

    /**
     * custom {@link BindingAdapter} function to listen pickup address change
     *
     * @param PickAdd
     **/
    @BindingAdapter({"PickAddress"})
    public static void PickAddChange(TextView view, String PickAdd) {
        if (!PickDropProceed.get()) {
            PickDropProceed.set(true);
            getLocationFromAddress(PickAdd);
        }
    }

    /**
     * custom {@link BindingAdapter} function to listen drop address change
     *
     * @param DropAdd
     **/
    @BindingAdapter({"DropAddress"})
    public static void DropAddChange(TextView mapView, String DropAdd) {
        if (!PickDropProceed.get()) {
            PickDropProceed.set(true);
            getLocationFromAddress(DropAdd);
        }
    }

    /**
     * called when pickup favourite clicked
     **/
    public void onclickPickFav(View view) {

        if (!CommonUtils.IsEmpty(mPickupAddress.get()) && !PickFavImage.get()) {
            getmNavigator().HideNshowToolbar(true);

            IsGesture.set(true);
            IsFavourite.set(true);
            //we have hide dotlines of pick and drop when fav showing...
            IsIdle.set(false);
            canShowConfirm.set(false);
        } else {
            if (PickFavImage.get()) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Fav_already_saved));
            } else {
                getmNavigator().PickAddressClicked();
            }

        }
    }

    /**
     * called when drop favourite clicked
     **/
    public void onclickDropFav(View view) {
        if (!CommonUtils.IsEmpty(mDropupAddress.get()) && !DropFavImage.get()) {
            getmNavigator().HideNshowToolbar(true);
            IsGesture.set(true);
            IsFavourite.set(true);
            //we have hide dotlines of pick and drop when fav showing...
            IsIdle.set(false);
            canShowConfirm.set(false);
        } else {
            if (DropFavImage.get()) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Fav_already_saved));
            } else {
                getmNavigator().DropAddressClicked();
            }


        }
    }

    /**
     * called when favourites cancel clicked
     **/
    public void onclickFavCancel(View view) {
        FavCheckedNickName = "Home";
        FavEditNickName = null;
        //we have restate dotlines of pick and drop when fav close...
        IsIdle.set(true);
        IsGesture.set(false);
        IsFavourite.set(false);
        IsFavGesture.set(false);

        getmNavigator().HideNshowToolbar(false);
        getmNavigator().ChangeOldLayoutParams();
        if (!PickDropCard.get()) {
            observableArrayMap.put("Drawable", "Pick");
        } else {
            observableArrayMap.put("Drawable", "Drop");
        }


    }

    /**
     * listens and sets favourites nickname if it is edited
     *
     * @param e
     **/
    public void onFavNickNameChanged(Editable e) {
        FavEditNickName = e.toString();
    }

    /**
     * Calls add favourite API
     **/
    public void onclickFavSave(View view) {
        if (FavCheckedNickName.equalsIgnoreCase(getmNavigator().getBaseAct().getTranslatedString(R.string.txt_Other)) && FavEditNickName == null) {
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_NickName));
        } else {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.nickName, FavEditNickName == null ? FavCheckedNickName : FavEditNickName);
            hashMap.put(Constants.NetworkParameters.placeId, "" + observableArrayMap.get("Address"));
            if (!PickDropCard.get() && PickupLatLng != null) {
                hashMap.put(Constants.NetworkParameters.latitude, "" + PickupLatLng.latitude);
                hashMap.put(Constants.NetworkParameters.longitude, "" + PickupLatLng.longitude);
            } else if (PickDropCard.get() && DropLatLng != null) {
                hashMap.put(Constants.NetworkParameters.latitude, "" + DropLatLng.latitude);
                hashMap.put(Constants.NetworkParameters.longitude, "" + DropLatLng.longitude);
            }
            if (getmNavigator().isNetworkConnected())
                AddFavNetworkcall();
            else
                getmNavigator().showNetworkMessage();
        }
        hideKeyboard(view);

    }

    /**
     * Listens to favourite type radio group (Home, Work, Others)
     *
     * @param checkedId
     **/
    public void onRadioGropChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.FMS_Radio_Home) {
            FavCheckedNickName = getmNavigator().getBaseAct().getTranslatedString(R.string.txt_Home);
            getmNavigator().HideNShowFavTitleEdit(false);
        } else if (checkedId == R.id.FMS_Radio_Work) {
            FavCheckedNickName = getmNavigator().getBaseAct().getTranslatedString(R.string.txt_Work);
            getmNavigator().HideNShowFavTitleEdit(false);
        } else if (checkedId == R.id.FMS_Radio_Other) {
            FavCheckedNickName = getmNavigator().getBaseAct().getTranslatedString(R.string.txt_Other);
            getmNavigator().HideNShowFavTitleEdit(true);
        }

    }

    /**
     * initializes {@link GoogleApiClient}
     *
     * @param googleMap
     **/
    public void buildGoogleApiClient(GoogleMap googleMap) {
        context = getmNavigator().getAttachedcontext();
        mGoogleMap = googleMap;

        mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getAttachedcontext())
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        if (!setlistener.get()) {
            IsIdle.set(true);
            setlistener.set(true);
            setListeners();
        }
    }

    /**
     * re-initializes {@link GoogleApiClient}
     **/
    public void restartGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getAttachedcontext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
        if (!setlistener.get()) {
            IsIdle.set(true);
            setlistener.set(true);
            setListeners();
        }
        setIsLoading(false);
    }

    /**
     * this callback is called when {@link GoogleApiClient} is successfully initialized & connected
     **/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
//                Log.d(TAG,"keys----------"+(mLastLocation!=null));
            if (mLastLocation != null) {
                sharedPrefence.savevalue(SharedPrefence.LONGITUDE, mLastLocation.getLongitude() + "");
                sharedPrefence.savevalue(SharedPrefence.LATITUDE, mLastLocation.getLatitude() + "");
                isLocationAvailable.set(true);
                if (!PickDropCard.get()) {
                    PickupLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                    socketMessageModel.lat = PickupLatLng.latitude;
                    socketMessageModel.lng = PickupLatLng.longitude;
                    System.out.println(TAG + "+++IAmEmmiting++" + gson.toJson(socketMessageModel));
                    enableLocationPopupPickup.set(false);
                } else if (PickDropCard.get())
                    DropLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


                MarkerAnimation(false, Constants.ZOOMLEVELMAP);
                GetAddressFromLatLng(!PickDropCard.get() ? PickupLatLng : DropLatLng);


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
        Types(SocketHelper.getLastLoadedTypes());

    }

    /**
     * animate zoom {@link GoogleMap} to given zoom level
     *
     * @param zoom
     * @param zoomlevel
     **/
    public static void MarkerAnimation(boolean zoom, float zoomlevel) {
        //mGoogleMap.clear();
        mMapIsTouched = true;
        if (zoom) {
            if (!PickDropCard.get() && PickupLatLng != null) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        PickupLatLng, 18));
            } else if (PickDropCard.get() && DropLatLng != null) {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        DropLatLng, mGoogleMap.getCameraPosition().zoom));
            }
        } else {
            if (!PickDropCard.get() && PickupLatLng != null)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PickupLatLng, zoomlevel));
            else if (PickDropCard.get() && DropLatLng != null)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DropLatLng, zoomlevel));
        }
        Log.i("zoomlevel", "Zooming level - " + zoomlevel);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * set listeners for {@link GoogleMap} camera events
     **/
    private void setListeners() {
        if (mGoogleMap != null) {
            mGoogleMap.setOnCameraIdleListener(this);
            mGoogleMap.setOnCameraMoveStartedListener(this);
        }

    }

    /**
     * geo-coding - retrieves address from given {@link LatLng}
     *
     * @param latLng
     **/
    private void GetAddressFromLatLng(final LatLng latLng) {
        if (!isAvailable)
            return;
        PickDropProceed.set(true);
        isAvailable = false;
        gitgoogle.GetAddressFromLatLng(latLng.latitude + "," + latLng.longitude, false, Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getAsJsonArray("results") != null) {

                        if (!PickDropCard.get() && response.body().getAsJsonArray("results").size() != 0) {
                            mPickupAddress.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                            observableArrayMap.put("Address", mPickupAddress.get());
                            socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                            socketMessageModel.lat = latLng.latitude;
                            socketMessageModel.lng = latLng.longitude;
//                            socketMessageModel.pickup_address = mPickupAddress.get();
                            if (isScreenAvailable) {
                                System.out.println(TAG + "+++IAmEmmiting++" + gson.toJson(socketMessageModel));
                                SocketHelper.sendTypes(gson.toJson(socketMessageModel));
                            }
                            if (checkaddressFav(mPickupAddress.get())) {
                                PickFavImage.set(true);
                            } else {
                                PickFavImage.set(false);
                            }

                        } else if (PickDropCard.get() && response.body().getAsJsonArray("results").size() != 0) {
                            mDropupAddress.set(response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("formatted_address").getAsString());
                            observableArrayMap.put("Address", mDropupAddress.get());
                            if (checkaddressFav(mDropupAddress.get())) {
                                DropFavImage.set(true);
                            } else {
                                DropFavImage.set(false);
                            }

                        }
                        isAvailable = true;
                    }


                } else {
                    isAvailable = true;
                    Log.d(TAG, "GetAddressFromLatlng" + response.toString());
                }
                canShowConfirm.set(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "GetAddressFromLatlng" + t.toString());
                isAvailable = true;
            }
        });


    }

    /**
     * define default booleans
     **/
    private void DefaultBooleans() {
        DropFavImage = new ObservableBoolean();
        PickFavImage = new ObservableBoolean();
        updatedcar = new BaseResponse();
        IsdrivercarsPlaced = new ObservableBoolean(false);
        Istypedata = new ObservableBoolean(true);
        IsGesture = new ObservableBoolean(false);
        IsFavGesture = new ObservableBoolean(false);
        IsIdle = new ObservableBoolean();
        PickDropProceed = new ObservableBoolean(true);
        PickDropCard = new ObservableBoolean(false);
        IsFavourite = new ObservableBoolean();
        IsCurrentloction = new ObservableBoolean();
        isLocationAvailable = new ObservableBoolean(false);
        enableLocationPopupPickup = new ObservableBoolean(true);
        IsSocketConnected = new ObservableBoolean(false);
        setlistener = new ObservableBoolean();
    }

    /**
     * Check if an address is saved in favourites
     *
     * @param addres
     **/
    public Boolean checkaddressFav(String addres) {
        if (FavListResponse == null) {
            String favStrng = sharedPrefence.Getvalue(SharedPrefence.FAVLIST);
            FavListResponse = gson.fromJson(favStrng, BaseResponse.class);
        }
        if (FavListResponse != null)
            if (FavListResponse.getFavplace() != null)
                if (FavListResponse.getFavplace() != null && FavListResponse.getFavplace().size() > 0) {
                    List<Favplace> data = FavListResponse.getFavplace();
                    for (Favplace dd : data) {
                        if (dd.placeId != null && dd.placeId.equalsIgnoreCase(addres))
                            return true;
                    }

                }
        return false;
    }

    /**
     * Triggered when {@link GoogleMap} camera is in idle state
     **/
    @Override
    public void onCameraIdle() {
        if (confirmFlag1 == 1) {
            canShowConfirm.set(true);
        } else {
            confirmFlag1 = 1;
        }

        if (isPickDropTapped) {
            canShowConfirm.set(false);
            isPickDropTapped = false;
        }

        IsIdle.set(true);
        if (!IsFavourite.get()) {
            getmNavigator().HideNshowToolbar(false);
            IsGesture.set(false);
        } else {
            getmNavigator().HideNshowFavLayout(false);
            IsFavGesture.set(false);
        }

        Log.v("MapIdle===", "onCameraIdle()");

        //  IsIdel.set(true);
        if (!IsFavourite.get()) {
            getmNavigator().HideNshowToolbar(false);
            IsGesture.set(false);
        } else {
            getmNavigator().HideNshowFavLayout(false);
            IsFavGesture.set(false);
        }
        if (!mMapIsTouched) {
            if (!PickDropCard.get()) {
                PickupLatLng = new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                enableLocationPopupPickup.set(false);
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.lat = PickupLatLng.latitude;
                socketMessageModel.lng = PickupLatLng.longitude;
//                socketMessageModel.pickup_address = mPickupAddress.get();
                if (isScreenAvailable && !CommonUtils.IsEmpty(mPickupAddress.get())) {
                    System.out.println(TAG + "+++IamEmmiting++++" + gson.toJson(socketMessageModel));
                    SocketHelper.sendTypes(gson.toJson(socketMessageModel));
                }
            } else if (PickDropCard.get()) {
                DropLatLng = new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
            }
        }
    }

    /**
     * Triggered when {@link GoogleMap} camera is starting to move
     *
     * @param i
     **/
    @Override
    public void onCameraMoveStarted(int i) {
        IsIdle.set(false);
        canShowConfirm.set(false);
        switch (i) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                if (IsFavourite.get()) {
                    observableArrayMap.put("Drawable", "Fav");
                    IsFavGesture.set(true);
                }
                IsIdle.set(false);
                canShowConfirm.set(false);
                IsGesture.set(true);
                break;

            case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                IsIdle.set(false);
                canShowConfirm.set(false);
                break;
        }
    }

    /**
     * Called when click to confirm button is clicked. Click to confirm button is added to reduce no. of hits on Place API
     **/
    public void onConfirmClick(View v) {
        if (!mMapIsTouched) {
            if (!PickDropCard.get()) {
                PickupLatLng = new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);
                enableLocationPopupPickup.set(false);
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.lat = PickupLatLng.latitude;
                socketMessageModel.lng = PickupLatLng.longitude;
                System.out.println(TAG + "+++IamEmmiting++++" + gson.toJson(socketMessageModel));
                SocketHelper.sendTypes(gson.toJson(socketMessageModel));
            } else if (PickDropCard.get())
                DropLatLng = new LatLng(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude);

            MarkerAnimation(false, mGoogleMap.getCameraPosition().zoom);
            GetAddressFromLatLng(!PickDropCard.get() ? PickupLatLng : DropLatLng);
        }
    }

    /**
     * Displays car markers on home screen map
     *
     * @param cars
     **/
    public synchronized void setDriverMarkers(List<Car> cars) {
        if (cars != null && cars.size() != 0) {
            Iterator litr = cars.iterator();
            if (mGoogleMap != null) {
                mGoogleMap.clear();
            }
            while (litr.hasNext()) {
                Car element = (Car) litr.next();
                MarkerlatLng = new LatLng(element.latitude, element.longitude);
                createMarker(MarkerlatLng, element.bearing);
            }

        } else {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
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
            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_carmarker);
        markeroption.position(latLng).anchor(0.5f, 0.5f).rotation(bearing).icon(bitmapDescriptorFactory);
        /*return */
        mGoogleMap.addMarker(markeroption);
    }


    /**
     * Reverse {@link Geocoder} -> Get {@link Location} from given address {@link String}
     *
     * @param place
     **/
    private static void getLocationFromAddress(final String place) {
        try {
            Geocoder gCoder = new Geocoder(context);
            final List<Address> list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                if (!PickDropCard.get()) {
                    PickupLatLng = new LatLng(list.get(0).getLatitude(), list.get(0)
                            .getLongitude());
                    enableLocationPopupPickup.set(false);
                    socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                    socketMessageModel.lat = PickupLatLng.latitude;
                    socketMessageModel.lng = PickupLatLng.longitude;
                    //  socketMessageModel.lat = 33.67500000;
                    //  socketMessageModel.lng = -116.24100000;
                    System.out.println(TAG + "+++IamEmmiting+++" + gson.toJson(socketMessageModel));
                    SocketHelper.sendTypes(gson.toJson(socketMessageModel));
                } else if (PickDropCard.get()) {
                    DropLatLng = new LatLng(list.get(0).getLatitude(), list.get(0)
                            .getLongitude());
                }
                if (mGoogleMap != null)
                    MarkerAnimation(true, mGoogleMap.getCameraPosition().zoom);

            }
        } catch (IOException e) {
            e.printStackTrace();
            gitgoogle.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {

                            Double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            Double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            if (!PickDropCard.get()) {
                                PickupLatLng = new LatLng(lat, lng);
                                enableLocationPopupPickup.set(false);
                                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                                socketMessageModel.lat = PickupLatLng.latitude;
                                socketMessageModel.lng = PickupLatLng.longitude;
                                System.out.println(TAG + "++IamEmmiting+++" + gson.toJson(socketMessageModel));
                                SocketHelper.sendTypes(gson.toJson(socketMessageModel));
                            } else if (PickDropCard.get()) {
                                DropLatLng = new LatLng(lat, lng);
                            }
                            MarkerAnimation(true, mGoogleMap.getCameraPosition().zoom);
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
     * Stop types countdown timer
     **/
    public void stopTypesTimer() {
        if (scheduledFuture != null)
            if (!scheduledFuture.isCancelled())
                scheduledFuture.cancel(true);
    }

    /**
     * Ride with scanned QR code
     *
     * @param scanContent
     **/
    public void qrScanned(String scanContent) {
        if (mDropupAddress != null && !CommonUtils.IsEmpty(mDropupAddress.get()) && DropLatLng != null
                && DropLatLng.latitude != 0.0 && DropLatLng.longitude != 0.0)
            getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, mDropupAddress.get(), DropLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent);
        else
            getmNavigator().onConfirmation(mPickupAddress.get(), PickupLatLng, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), scanContent);
    }

    /**
     * Toggle {@link GoogleMap} type between normal and satellite
     **/
    public void mapTypeClick(View v) {
        if (!mapType.get() && mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mapType.set(true);
            sharedPrefence.saveBoolean(SharedPrefence.MAPTYPE, true);
        } else if (mGoogleMap != null) {
            mapType.set(false);
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            sharedPrefence.saveBoolean(SharedPrefence.MAPTYPE, false);
        }

    }

    /**
     * Types callback is from {@link SocketHelper}. This callback is called whenever types data is received in socket.
     * Driver markers are added to map from this data.
     *
     * @param typesString
     **/
    @Override
    public void Types(final String typesString) {
        Log.i(TAG, "types" + typesString);

        if (getmNavigator().getBaseAct() != null)
            getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) typesString;
                    System.out.println("++" + typesString);
                    BaseResponse baseResponse = gson.fromJson(typesString, BaseResponse.class);

                    if (baseResponse != null && baseResponse.success) {
                        Istypedata.set(true);
                        isProgressShown.set(false);
                        getmNavigator().addcarList(baseResponse.getTypes());
                        if (baseResponse.getTypes() != null)
                            if (baseResponse.getTypes().size() > 0) {
                                final List<Car> driverlist = new ArrayList<>();
                                Log.d(TAG, "Obj=" + (getmNavigator().GetSelectedCarObj() != null));
                                for (Type type : baseResponse.getTypes()) {
                                    if (type != null)
                                        if (type.drivers != null)
                                            driverlist.addAll(type.drivers);
                                }
                                if (getmNavigator() != null && getmNavigator().getBaseAct() != null)
                                    getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setDriverMarkers(driverlist);
                                        }
                                    });
                                if (baseResponse.getTypes().get(0) != null && baseResponse.getTypes().get(0).preferred_payment != null)
                                    sharedPrefence.saveInt(SharedPrefence.PREFFERED_PAYMENT, baseResponse.getTypes().get(0).preferred_payment);
                            }

                    } else {
                        Istypedata.set(false);
                        isProgressShown.set(false);
                    }


                }
            });


    }

    /**
     * TripStatus called is called from {@link SocketHelper}. This callback is called whenever trip_status msg is received from
     * socket. This data is used to update status of the ongoing trip
     *
     * @param trip_status
     **/
    @Override
    public void TripStatus(final String trip_status) {
        Log.i(TAG, "Trip_Status" + trip_status);
        if (getmNavigator().getBaseAct() != null) {
            getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(trip_status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                        System.out.println("+get_cars+" + data.toString());
                    if (data != null) {
                        BaseResponse baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
                        if (baseResponse != null && baseResponse.successMessage != null
                                && baseResponse.successMessage.equalsIgnoreCase("Accepted") && baseResponse.getRequest() != null) {
                            if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                                getmNavigator().openRideLaterAlert(baseResponse.getRequest());
                            } else
                                getmNavigator().openTripFragment(baseResponse.getRequest());


                        }
                    }
                }
            });
        }
    }

    /**
     * CancelledRequest called is called from {@link SocketHelper}. This callback is called whenever cancelled_request msg is received from
     * socket. This data is used to notify the user the trip has been cancelled by driver.
     *
     * @param cancelled_request
     **/
    @Override
    public void CancelledRequest(String cancelled_request) {
        if (!CommonUtils.IsEmpty(cancelled_request)) {
            Log.i(TAG, "keyss---------Cancel" + cancelled_request);
            getmNavigator().notifyNoDriverMessage(cancelled_request);
        }
    }

    @Override
    public void RideLaterNoCaptainAlert(final String ride_later_no_captain) {
        if (ride_later_no_captain == null)
            return;
        if (!CommonUtils.IsEmpty(ride_later_no_captain)) {
            if (getmNavigator().getBaseAct() != null) {
                getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = null;
                        try {
                            data = new JSONObject(ride_later_no_captain);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (data != null) {
                            BaseResponse baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
                            if (baseResponse != null && baseResponse.successMessage != null
                                    && baseResponse.successMessage.equalsIgnoreCase("ride_later_cancelled_because_of_no_driver_found")
                                    && baseResponse.getRequest() != null) {
                                Log.v(TAG, "laterReqId: " + baseResponse.getRequest().id);
                                getmNavigator().openLaterAlert(baseResponse.getRequest().id);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void DurationHandler(String duration_handler) {

    }

    /**
     * Returns if internet connection is connected or not
     **/
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
        Istypedata.set(false);
        IsSocketConnected.set(false);
        isProgressShown.set(false);
    }

    @Override
    public void OnConnectError() {
        Istypedata.set(false);
        IsSocketConnected.set(false);
        isProgressShown.set(false);
    }

}
