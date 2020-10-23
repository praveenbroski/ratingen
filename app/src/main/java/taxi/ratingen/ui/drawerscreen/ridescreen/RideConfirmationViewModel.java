package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import taxi.ratingen.BuildConfig;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Car;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.Step;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.FirebaseHelper;
import taxi.ratingen.utilz.SocketHelper;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.MyComponent;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.SocketMessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

/**
 * Created by root on 11/17/17.
 */

public class RideConfirmationViewModel extends BaseNetwork<BaseResponse, RideConfirmNavigator>
        implements SocketHelper.SocketListener, FirebaseHelper.FirebaseObserver {

    private static final String TAG = "RideConfirmation";
    public Type type;
    public TypeNew typeNew;
    public ShareRideDetails shareRideDetails;
    public GoogleMap googleMap;
    public ObservableBoolean isPromodone, isPromoAvail, isDropEmpty, isPromoApply;
    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public Context mcontext;
    public BaseResponse baseResponse = null;
    public ObservableField<String> DateandTime, PickAddress, DropAddress, CarType, TotalPrize,
            currency, totalSharePrice, NofSeat, duration_min, carImage, promoAmnt, driverNotes;
    public ObservableBoolean DropProceed, IsFrom, is_enableBooking, is_CorporateUser, isShareDriver,
            isShareRide, isDurationAvailable, istypeDataAVailable, isEtaAVailable, isScanEnabled, hideProgress;
    public ObservableField<String> paymentOption;
    public ObservableBoolean isDriversAvailable = new ObservableBoolean(false);
    public View view;
    public Integer nofUser = 1, isShare = 0;
    GitHubMapService gitHubMapService;
//    SimpleDateFormat realformatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
//    private SimpleDateFormat TargetFormatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);


    SimpleDateFormat realformatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    private SimpleDateFormat TargetFormatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);

    //  Socket mSocket;
    LatLng pickup, drop;
    String pickupAddr, dropAddr;
    LatLng MarkerlatLng;
    MarkerOptions markeroption = new MarkerOptions();
    BitmapDescriptor bitmapDescriptorFactory;
    boolean isboundSet = false;
    SocketMessageModel socketMessageModel = new SocketMessageModel();
    // Schedule the task such that it will be executed every second
    ScheduledFuture<?> scheduledFuture;
    private String userID;
    public ObservableField<String> numberofBasicDriver = new ObservableField<>();
    private float lastBearing = 0f;
    private double lastlatitude = 0.0, lastlongitude = 0.0;
    String scancontent = "";
    int DriverTypeId = 0;
    private String typeId;
    private String reqId;
    List<Type> getTypesResponse;
    List<TypeNew> getTypeNewResponse = new ArrayList<>();
    int rideType = 1;
    String dateFormat = "";
    String bookedId = "";
    String currentRideETA;

    HashMap<String, Marker> driverPins = new HashMap<>();
    HashMap<String, String> driverDatas = new HashMap<>();


    public Route routeDest;
    public List<LatLng> pointsDest;
    public PolylineOptions lineOptionsDest1, lineOptionDesDark;
    Polyline polyLineDest1, polyLineDestDark;

    public RideConfirmationViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence,
                                     HashMap<String, String> hashMap, Context context, GitHubMapService gitHubMapService, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        userID = sharedPrefence.Getvalue(SharedPrefence.ID);
        this.mcontext = context;
        this.gitHubMapService = gitHubMapService;
        //   this.mSocket = socket;
        IsFrom = new ObservableBoolean();
        paymentOption = new ObservableField<>();
        DateandTime = new ObservableField<>();
        PickAddress = new ObservableField<>();
        currency = new ObservableField<>();
        DropAddress = new ObservableField<>();
        TotalPrize = new ObservableField<>();
        totalSharePrice = new ObservableField<>();
        NofSeat = new ObservableField<>();
        CarType = new ObservableField<>();
        carImage = new ObservableField<>();
        promoAmnt = new ObservableField<>("");
        driverNotes = new ObservableField<>("");
        duration_min = new ObservableField<>("");
        isDurationAvailable = new ObservableBoolean(false);
        DropProceed = new ObservableBoolean(false);
        is_enableBooking = new ObservableBoolean(true);
        isShareDriver = new ObservableBoolean(false);
        isShareRide = new ObservableBoolean(false);
        isEtaAVailable = new ObservableBoolean(true);
        hideProgress = new ObservableBoolean(true);
        isPromoApply = new ObservableBoolean(false);

        istypeDataAVailable = new ObservableBoolean(false);
        is_CorporateUser = new ObservableBoolean(false);
        isScanEnabled = new ObservableBoolean(false);
        isPromodone = new ObservableBoolean(false);
        isPromoAvail = new ObservableBoolean(false);
        isDropEmpty = new ObservableBoolean(true);
        DataBindingUtil.setDefaultComponent(new MyComponent(this));
        isShare = 0;
    }

    public void setPins(Type mParam1, GoogleMap googleMap) {
        type = mParam1;

        this.googleMap = googleMap;
        changeMapStyle();
        SetValues();

    }

    /** Setups Pickup and Drop markers on {@link GoogleMap}
     * @param pickup Pickup {@link LatLng}
     * @param drop Drop {@link LatLng}
     * @param pickupAddr Address string of pickup location
     * @param dropAddr Address string of drop location
     * @param googleMap {@link GoogleMap}
     * @param scanContent String parameter **/
    public void setPins(LatLng pickup, LatLng drop, String pickupAddr, String dropAddr, GoogleMap googleMap, String scanContent, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        this.pickup = pickup;
        this.drop = drop;
        this.dropAddr = dropAddr;
        this.pickupAddr = pickupAddr;
        this.scancontent = scanContent;
        this.driverPins = driverPins;
        this.driverDatas = driverDatas;

        if (!CommonUtils.IsEmpty(scanContent)) {
            isScanEnabled.set(true);
            is_enableBooking.set(true);
//            ETANetWorkcall(scanContent, rideType, "");
        }

        getTypesAPI();

        if (dropAddr == null || dropAddr.isEmpty()) {
            isDropEmpty.set(false);
        }

        this.googleMap = googleMap;
        changeMapStyle();
        SetValues();
    }

    /** Changes style and appearance of {@link GoogleMap} from json resource **/
    private void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mcontext, R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /** Assigns values from API to variables **/
    public void SetValues() {
//        //  SetSocketListener();
//        socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
//        socketMessageModel.pick_lat = pickup.latitude;
//        socketMessageModel.pick_lng = pickup.longitude;
//        Types(SocketHelper.getLastLoadedTypes());
//        //mSocket.emit("types", gson.toJson(socketMessageModel));

        setPikupDropBoundMarkers(true);
        if (!CommonUtils.IsEmpty(pickupAddr))
            PickAddress.set(pickupAddr);
        if (!CommonUtils.IsEmpty(dropAddr))
            DropAddress.set(dropAddr);
        NofSeat.set("Choose Seat ");
        FirebaseHelper.addObservers(this);
        SocketHelper.init(sharedPrefence, this, TAG, false);
        addLastKnownMarkers();
    }

    /** Adds pickup and drop marker on {@link GoogleMap} **/
    public void setPikupDropBoundMarkers(boolean setBound) {
        if (pickup != null && getmNavigator().GetBaseAct() != null) {
            Marker mPick = googleMap.addMarker(new MarkerOptions().position(pickup).title("Pickup Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().GetBaseAct(), R.drawable.ic_pick_pin)));
            mPick.setAnchor(0.5f, 0.5f);
        }

        if (drop != null && getmNavigator().GetBaseAct() != null) {
            Marker mDrop = googleMap.addMarker(new MarkerOptions().position(drop).title("Drop Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().GetBaseAct(), R.drawable.ic_drop_pin)));
            mDrop.setAnchor(0.5f, 0.5f);
        }
        if (setBound)
            buildbound();

        if (pickup != null && drop != null) {
            drawPathPickToDrop(pickup, drop);
        }
    }

    /** Zooms the {@link GoogleMap} to fit pickup and drop {@link com.google.android.gms.maps.model.Marker} **/
    public void buildbound() {
        if (pickup != null && drop != null) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                    .include(pickup)
                    .include(drop);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(), 30));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    pickup, 30));
        }
    }

    private void drawPathPickToDrop(LatLng pick, LatLng drop) {
        gitHubMapService.GetDrawpath(pick.latitude + "," + pick.longitude,
                drop.latitude + "," + drop.longitude, false,
                Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    routeDest = new Route();
                    CommonUtils.parseRoute(response.body(), routeDest);
                    getmNavigator().setRouteData(routeDest);
                    final ArrayList<Step> step = routeDest.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest = new ArrayList<>();
                    lineOptionsDest1 = new PolylineOptions();
                    lineOptionDesDark = new PolylineOptions();

                    lineOptionsDest1.geodesic(true);
                    lineOptionDesDark.geodesic(true);

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest.addAll(path);
                    }
                    if (polyLineDest1 != null)
                        polyLineDest1.remove();
                    lineOptionsDest1.addAll(pointsDest);
                    lineOptionsDest1.width(10f);
                    lineOptionsDest1.startCap(new SquareCap());
                    lineOptionsDest1.endCap(new SquareCap());
                    lineOptionsDest1.jointType(ROUND);

                    if (getmNavigator().isAddedInAct()) {
                        lineOptionsDest1.color(getmNavigator().getBaseAct().getResources().getColor(
                                R.color.clr_FB4A46));
                    }
                    try {
                        if (lineOptionsDest1 != null && googleMap != null) {
                            polyLineDest1 = googleMap.addPolyline(lineOptionsDest1);
                            polyLineDestDark = googleMap.addPolyline(lineOptionsDest1);
                            buildbound();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                getmNavigator().showMessage(t.getMessage());
            }
        });
    }

    /** Calls ETA API to get fare and other details
     * @param type Type response model
     * @param rideType Type of car SUV, Sedan, Hatchback, etc.,
     * @param bookedId Id of the booking request **/
    public void ETANetWorkcall(Type type, int rideType, String bookedId) {
       /* if (this.type != null && this.type.id == type.id)
            return;*/
        this.bookedId = bookedId;
        this.type = type;
        if (!CommonUtils.IsEmpty(dropAddr)) {
            DropProceed.set(true);
            DropAddress.set(dropAddr);
        }
        if (getmNavigator().isNetworkConnected()) {

            if (pickup != null && this.type != null) {
                hashMap.clear();
                if (!bookedId.equalsIgnoreCase(""))
                    hashMap.put(Constants.NetworkParameters.promo_booked_id, this.bookedId);
                hashMap.put(Constants.NetworkParameters.request_type, "" + rideType);
                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                hashMap.put(Constants.NetworkParameters.type_id, "" + this.type.id);
                hashMap.put(Constants.NetworkParameters.car_id, "" + this.type.type_id);
                hashMap.put(Constants.NetworkParameters.olat, "" + pickup.latitude);
                hashMap.put(Constants.NetworkParameters.olng, "" + pickup.longitude);
                if (drop != null) {
                    hashMap.put(Constants.NetworkParameters.dlat, "" + drop.latitude);
                    hashMap.put(Constants.NetworkParameters.dlng, "" + drop.longitude);
                }

                isEtaAVailable.set(false);
                getETANetworkcall();
            }


        } else {
            if (getmNavigator() != null)
                getmNavigator().showNetworkMessage();
        }
    }

    /** Calls ETA API to get fare and other details
     * @param privateKey String value of private key of the driver
     * @param rideType Type of car SUV, Sedan, Hatchback, etc.,
     * @param ridePromoResult Promo code result **/
    public void ETANetWorkcall(String privateKey, int rideType, String ridePromoResult) {
        if (CommonUtils.IsEmpty(privateKey))
            return;
        if (!CommonUtils.IsEmpty(dropAddr)) {
            DropProceed.set(true);
            DropAddress.set(dropAddr);
        }
        this.bookedId = ridePromoResult;
        if (getmNavigator().isNetworkConnected()) {

            if (pickup != null && drop != null) {
                hashMap.clear();
                if (!scancontent.isEmpty()) {
                    hashMap.put(Constants.NetworkParameters.privateKey, scancontent);
                    hashMap.put(Constants.NetworkParameters.request_type, "1");
                } else
                    hashMap.put(Constants.NetworkParameters.request_type, "" + rideType);

                if (!ridePromoResult.equalsIgnoreCase(""))
                    hashMap.put(Constants.NetworkParameters.promo_booked_id, ridePromoResult);

                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
//                hashMap.put(Constants.NetworkParameters.car_id, "" + this.type.type_id);
                hashMap.put(Constants.NetworkParameters.olat, "" + pickup.latitude);
                hashMap.put(Constants.NetworkParameters.olng, "" + pickup.longitude);
                if (drop != null) {
                    hashMap.put(Constants.NetworkParameters.dlat, "" + drop.latitude);
                    hashMap.put(Constants.NetworkParameters.dlng, "" + drop.longitude);
                }
                isEtaAVailable.set(false);
                setIsLoading(true);
                getETANetworkcall();
            }


        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void newETACall(TypeNew typeNew, int rideType, String bookedId) {
//        Log.v("http", "Ride fare: " + typeNew.etaPrice);
//        this.bookedId = bookedId;
//        this.typeNew = typeNew;
//        if (!CommonUtils.IsEmpty(dropAddr)) {
//            DropProceed.set(true);
//            DropAddress.set(dropAddr);
//        }
//        if (getmNavigator().isNetworkConnected()) {
//            if (pickup != null && this.typeNew != null) {
//                hashMap.clear();
//                if (!bookedId.equalsIgnoreCase(""))
//                    hashMap.put(Constants.NetworkParameters.promo_booked_id, this.bookedId);
//                hashMap.put(Constants.NetworkParameters.request_type, "" + rideType);
//                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
//                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.AccessToken));
//                hashMap.put(Constants.NetworkParameters.type_id, "" + this.typeNew.zoneId);
//                hashMap.put(Constants.NetworkParameters.car_id, "" + this.typeNew.typeId);
//                hashMap.put(Constants.NetworkParameters.olat, "" + pickup.latitude);
//                hashMap.put(Constants.NetworkParameters.olng, "" + pickup.longitude);
//                if (drop != null) {
//                    hashMap.put(Constants.NetworkParameters.dlat, "" + drop.latitude);
//                    hashMap.put(Constants.NetworkParameters.dlng, "" + drop.longitude);
//                }
//
//                JSONArray jDrivers = new JSONArray();
//                if (getmNavigator().getNewSelectedCar() != null) {
//                    try {
//                        for (String key: driverDatas.keySet()) {
//                            JSONObject jData = new JSONObject(driverDatas.get(key));
//                            String d_id = jData.getString("id");
//                            Marker marker = driverPins.get(key);
//                            String typeId = jData.getString("type");
//
//                            if (typeId == getmNavigator().getNewSelectedCar().typeId) {
//                                JSONObject jDriver = new JSONObject();
//                                jDriver.put("driver_id", d_id);
//                                jDriver.put("driver_lat", marker.getPosition().latitude);
//                                jDriver.put("driver_lng", marker.getPosition().longitude);
//
//                                jDrivers.put(jDriver);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.v("FatalLog", "getmNavigator().getNewSelectedCar() null");
//                }
//                if (jDrivers.length() == 0) {
//                    getmNavigator().setNoDrivers();
//                    return;
//                }
//                hashMap.put("drivers", jDrivers.toString());
//
//                isEtaAVailable.set(false);
////                getNewETANetworkCall();
//            }
//        } else {
//            if (getmNavigator() != null)
//                getmNavigator().showNetworkMessage();
//        }
    }

    public void getTypesAPI() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);

            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.PICK_LAT, "" + pickup.latitude);
            hashMap.put(Constants.NetworkParameters.PICK_LNG, "" + pickup.longitude);
            getTypesNetworkCall();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void onclickDropcard(View view) {

        getmNavigator().DropCardClicked();

    }

    public void onclickNormalRide(View view) {
        isShare = 0;
        isShareRide.set(false);
    }

    public void onclickShareRide(View view) {
        isShare = 1;
        isShareRide.set(true);

    }

    /** Calls when payment is clicked. Opens choose payment {@link com.google.android.material.bottomsheet.BottomSheetDialog} **/
    public void onclickPayment(View view) {
        if (!is_CorporateUser.get())
            getmNavigator().onClickPayment(typeNew);

    }

    /** Calls when different cars are selected from list of cars **/
    public void onclickRideType(View view) {
        if (!is_CorporateUser.get())
            if (baseResponse != null && type != null)
                getmNavigator().onClickRideType(type.is_accept_share_ride == 1);

    }

    public void onclickNofSeat(View view) {
        getmNavigator().onClickNofSeat(shareRideDetails, currency.get());

    }

    public void onclickEnterDropLayout(View view) {
        getmNavigator().DropCardClicked();
    }

    /** Opens ETA dialog info button was clicked **/
    public void onclickInfoETA(View view) {
        if (baseResponse != null) {
            getmNavigator().openETADialog(baseResponse);
        }
    }

    /** Custom {@link BindingAdapter} function to set drop adress to {@link TextView}
     * @param mapView {@link TextView} reference
     * @param DropAdd Drop address string **/
    @BindingAdapter({"RideDropAddr"})
    public void DropAddChange(TextView mapView, String DropAdd) {
        if (!DropProceed.get() && DropAdd != null) {
            getLocationFromAddress(DropAdd);
        }
    }

    /** Called when confirm button is clicked. Calls create request API **/
    public void OnClickConfirmBooking(String dateSelected) {
        this.dateFormat = dateSelected;
        if (typeNew != null) {
            //        this.view = view;
            if (!CommonUtils.IsEmpty(paymentOption.get())) {
                if (isShareRide.get())
                    if (nofUser == null || nofUser == 0) {
                        if (view != null && view.getContext() != null)
                            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.please_choose_seat));
                        else if (getmNavigator().getAttachedcontext() != null)
                            getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.please_choose_seat));
                        return;
                    }
                is_enableBooking.set(false);
                hashMap.clear();
//            if (drop == null)
//                return;
                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                hashMap.put(Constants.NetworkParameters.type, "" + typeNew.getZoneId());
                hashMap.put(Constants.NetworkParameters.platitude, "" + pickup.latitude);
                hashMap.put(Constants.NetworkParameters.plongitude, "" + pickup.longitude);
                if (isPromoAvail.get())
                    hashMap.put(Constants.NetworkParameters.promo_booked_id, bookedId);

                if (!CommonUtils.IsEmpty(DropAddress.get())) {
                    hashMap.put(Constants.NetworkParameters.dlongitude, "" + drop.longitude);
                    hashMap.put(Constants.NetworkParameters.dlatitude, "" + drop.latitude);
                    hashMap.put(Constants.NetworkParameters.dlocation, "" + DropAddress.get());
                }
                hashMap.put(Constants.NetworkParameters.paymentOpt, "" + paymentOption.get());

                hashMap.put(Constants.NetworkParameters.plocation, "" + PickAddress.get());
                hashMap.put(Constants.NetworkParameters.no_of_seats, "" + nofUser);
                hashMap.put(Constants.NetworkParameters.is_share, "" + isShare);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                        Locale.getDefault());
                Date currentLocalTime = calendar.getTime();
                DateFormat date = new SimpleDateFormat("Z");
                String s = date.format(currentLocalTime);
                Log.d(TAG, date.format(currentLocalTime));
                hashMap.put(Constants.NetworkParameters.timezone, "" + s.substring(0, 3) + ":" + s.substring(3));
                Log.d(TAG, s.substring(0, 3) + ":" + s.substring(3));
                try {
                    hashMap.put(Constants.NetworkParameters.datetime, "" + TargetFormatter.format(realformatter.parse(dateFormat)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                setIsLoading(true);
                CreateRequestLaterNetwork();
            } else {
                if (CommonUtils.IsEmpty(paymentOption.get()) && view != null && view.getContext() != null)
                    getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_SelectPayment));
            }
        }
    }

    /** Users reverse geo-coding get {@link Location} from address string
     * @param place Address string that needs to be converted to {@link Location} object **/
    private void getLocationFromAddress(final String place) {
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(mcontext);

        final List<Address> list;
        try {
            list = gCoder.getFromLocationName(place, 1);

            if (list != null && list.size() > 0) {
                type.setDroplatlng(new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude()));
                type.setDropAddress(place);
                googleMap.clear();

                SetValues();
            } else {
                throw new IOException("Empty address");
            }
        } catch (IOException e) {
            e.printStackTrace();

            gitHubMapService.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {
                            Double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            Double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            type.setDroplatlng(new LatLng(lat, lng));
                            type.setDropAddress(place);
                            googleMap.clear();

                            SetValues();
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

    /** Sets share price
     * @param i no. of passengers sharing the ride **/
    public void currentShareETA(Integer i) {
        nofUser = i;
        if (i == 1) {
            totalSharePrice.set(CommonUtils.doubleDecimalFromat(shareRideDetails.one_seat_total) + "");

        } else {
            totalSharePrice.set(CommonUtils.doubleDecimalFromat(shareRideDetails.two_seat_total) + "");
        }
    }

    /** Click listener for back button. Closes current screen and opens previous screen **/
    public void onclickBackBtn(View view) {
        getmNavigator().goback();
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response {@link BaseResponse} response model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        is_enableBooking.set(true);

        if (response != null && response.success) {
            if (response.successMessage.equalsIgnoreCase("types_listed")&& response.message.equalsIgnoreCase("success")) {
                if (response.getNewTypes() != null) {
//                    String typesResponse = CommonUtils.arrayToString((ArrayList<Object>) response.data);
                    getTypeNewResponse.addAll(response.getNewTypes());
                    for (TypeNew typeNew: getTypeNewResponse) {
                        if (typeNew.getZoneTypePrice()!=null&&typeNew.getZoneTypePrice().getData().size() > 0) {
                            TypeNew.TypePrice rideNowPrice = null;
                            for (TypeNew.TypePrice typePrice: typeNew.getZoneTypePrice().getData()) {
                                if (typePrice.getPriceType() != null && typePrice.getPriceType() == 1) {
                                    rideNowPrice = typePrice;
                                }
                            }
                            if (rideNowPrice != null) {
                                double divideBy;
                                if (typeNew.getUnit() == 1)
                                    divideBy = 1000f;
                                else
                                    divideBy = 1609.34f;

                                double chargeableDistance;
                                if (routeDest != null) {
                                    if (routeDest.getDistanceValue() >= 1)
                                        chargeableDistance = (routeDest.getDistanceValue() / divideBy) - rideNowPrice.getBaseDistance();
                                    else
                                        chargeableDistance = 0;
                                } else
                                    chargeableDistance = 0;

                                double distPrice = chargeableDistance * rideNowPrice.getPricePerDistance();
                                double chargeableTime = 0;
                                if (routeDest != null) {
                                    chargeableTime = (routeDest.getDurationValue() / 60f);
                                }
                                double timePrice = chargeableTime * rideNowPrice.getPricePerDistance();
                                double rideMinFare = rideNowPrice.getBasePrice() + distPrice + timePrice;
                                double rideMaxFare = rideMinFare + ((rideMinFare / 100f) * 8);
                                typeNew.etaPrice = typeNew.currency + CommonUtils.doubleDecimalFromat(rideMinFare) + " - " +
                                        typeNew.currency + CommonUtils.doubleDecimalFromat(rideMaxFare);
                            } else {
                                typeNew.etaPrice = "NA";
                            }
                        } else {
                            typeNew.etaPrice = "NA";
                        }
                    }
                    getmNavigator().addCarListNew(getTypeNewResponse, 2);
                    calculateETA();
                    if (getTypeNewResponse.size() > 0) {
                        if (getTypeNewResponse.get(0) != null && getTypeNewResponse.get(0).getPreferredPayment() != null) {
                            sharedPrefence.saveInt(SharedPrefence.PREFFERED_PAYMENT, getTypeNewResponse.get(0).getPreferredPayment());
                        }
                    }
                    if (!CommonUtils.IsEmpty(scancontent)) {
                        isScanEnabled.set(true);
                        is_enableBooking.set(true);
//            ETANetWorkcall(scanContent, rideType, "");
                    }
                }
            }else if (response.successMessage.equalsIgnoreCase("user_declined"))
                getmNavigator().openBlockedAlert();
            else if (response.successMessage.equalsIgnoreCase("trip_registered_within_period"))
                getmNavigator().openTripRegisteredAlert(response.getTripRegisteredDetails());
            else if (response.successMessage.equalsIgnoreCase("Create_Request_successfully")) {
                if (getmNavigator().isAttached()) {
                    getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.Txt_RideCreated));
                    getmNavigator().ShowWaitingDialog("" + response.request.id);
                }
            }else if (response.successMessage.equalsIgnoreCase("ride promocode available")) {
                String bookedID = response.promoCodeQueue.getPromoBookedId();
                getmNavigator().promoCodeSet(bookedID);
            } else if (response.successMessage.equalsIgnoreCase("Trip_registered_successfully")) {
//                if (typeNew != null)
//                    typeId = typeNew.zoneId;
//                reqId = response.request_id;
//                //  getmNavigator().openAlert(response.currency, response.DriverAddCharges);
//                if (typeNew != null)
//                    getmNavigator().scheduleSucess("" + typeNew.zoneId, "" + response.request_id, sharedPrefence.Getvalue(SharedPrefence.ID), sharedPrefence.Getvalue(SharedPrefence.TOKEN), pickup.latitude, pickup.longitude);

                if (typeNew != null)
                    typeId = typeNew.getTypeId()+"";
                reqId = response.request_id+"";
                //  getmNavigator().openAlert(response.currency, response.DriverAddCharges);
                if (typeNew != null)
                    getmNavigator().scheduleSucess("" + typeNew.getTypeId(), "" + response.request_id, sharedPrefence.Getvalue(SharedPrefence.ID), sharedPrefence.Getvalue(SharedPrefence.TOKEN), pickup.latitude, pickup.longitude);
            }  /*else if (response.message.equalsIgnoreCase("request_in_progress")) {
                getmNavigator().enableCorporateUser(sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER));
                ReqInProgress model = CommonUtils.getSingleObject(new Gson().toJson(response.data), ReqInProgress.class);

                if (model != null && model.getOnTripRequest() != null) {
                    String requestString = gson.toJson(model.getOnTripRequest());
                    TaxiRequestModel.Result metaRequest = CommonUtils.getSingleObject(requestString, TaxiRequestModel.Result.class);
                    if (metaRequest != null) {
                        if (metaRequest.resultData != null) {
                            if (metaRequest.resultData.isLater != null && metaRequest.resultData.isLater == 1) {
//                                 getmNavigator().openRideLaterAlert(metaRequest.requestData, metaRequest.requestData.driverDetail.driverData);
                            } else {
                                getmNavigator().showTripFragment(metaRequest.resultData, metaRequest.resultData.driverDetail.driverData);
                            }
                        }
                    }
                }
            }*/
        }

    }

    private void calculateETA() {
        if (getTypeNewResponse != null) {
            HashMap<String, List<Float>> distances = new HashMap<>();
            for (TypeNew typeNew: getTypeNewResponse) {
                List<Float> distanceList = new ArrayList<>();
                for (String driverId: driverPins.keySet()) {
                    String driverData = driverDatas.get(driverId);
                    try {
                        JSONObject jsonObject = new JSONObject(driverData);
                        if (jsonObject.has("type")) {
                            String typeId = jsonObject.getString("type");
                            if (typeId.equalsIgnoreCase(typeNew.getTypeId())) {
                                String[] arr = jsonObject.getString("l")
                                        .replace("[", "")
                                        .replace("]", "")
                                        .split(",");
                                LatLng carLatLng = new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));

                                Location pickLocation = new Location("pickup");
                                pickLocation.setLatitude(pickup.latitude);
                                pickLocation.setLongitude(pickup.longitude);

                                Location driverLocation = new Location("driver");
                                driverLocation.setLatitude(carLatLng.latitude);
                                driverLocation.setLongitude(carLatLng.longitude);

                                float distance = driverLocation.distanceTo(pickLocation);
                                distanceList.add(distance);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    distances.put(typeNew.getTypeId(), distanceList);
                }
            }
            Log.v("http_log", distances.toString());
            for (TypeNew typeNew: getTypeNewResponse) {
                String typeId = typeNew.getTypeId();
                if (distances.get(typeId) != null) {
                    List<Float> distanceList = distances.get(typeId);
                    if (distanceList.size() > 0) {
                        float minDistance = Collections.min(distances.get(typeId));
                        int speed = 50;
                        float time = (minDistance / 1000) / (float) speed;
                        String sMinutes = String.format(Locale.ENGLISH, "%.0f", (time * 60));
                        int iMinutes = Integer.parseInt(sMinutes);
                        if (iMinutes >= 60) {
                            int hours = iMinutes / 60;
                            int minutes = iMinutes % 60;
                            if (hours == 1) {
                                typeNew.etaTime = hours + " hr " + minutes + " mins";
                            } else {
                                typeNew.etaTime = hours + " hrs " + minutes + " mins";
                            }
                        } else if (iMinutes == 1 || iMinutes == 0) {
                            typeNew.etaTime = iMinutes + " min";
                        } else {
                            typeNew.etaTime = iMinutes + " mins";
                        }
                    } else {
                        typeNew.etaTime = "NA";
                    }
                } else {
                    typeNew.etaTime = "NA";
                }
            }
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        //   isEtaAVailable.set(true);
        setIsLoading(false);
        is_enableBooking.set(true);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedcontext() != null)
                getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.text_already_login));
            getmNavigator().logoutApp();
        } else if (e.getCode() == 9004) {
            setIsLoading(true);
            userBlockedApi();
        } else if (e.getCode() != 429 && e.getCode() != 9005) {
            getmNavigator().showMessage(e);
//            getmNavigator().goback();
        }

//        else if (e.getCode() == 728) {
//
//        } else if (e.getCode() == 725)
//            getmNavigator().showMessage(e);
//        else if (e.getCode() == 723)
//            getmNavigator().showMessage(e);
//        else if (e.getCode() == 9004) {
//            setIsLoading(true);
//            userBlockedApi();
//        } else if (e.getCode() == 9005)
//            getmNavigator().showMessage(e);

//        } else
//            getmNavigator().showMessage(e);

        istypeDataAVailable.set(true);
    }

    /** Returns {@link HashMap} with query parameters used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }


   /* public void SetSocketListener() {
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("types", types);
        mSocket.on("cancelled_request", cancelled_request);
        mSocket.connect();

    }*/

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "connected");
            JSONObject object = new JSONObject();
            try {
                object.put(Constants.NetworkParameters.id, CommonUtils.IsEmpty(userID) ? sharedPrefence.Getvalue(SharedPrefence.ID) : userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "start_connect = " + object.toString());
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "diconnected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "diconnected" + (args.length > 0 ? args[0] : ""));
        }
    };

    private Emitter.Listener types = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


        }
    };
    private Emitter.Listener cancelled_request = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (args != null && args.length > 0 && args[0] != null && !CommonUtils.IsEmpty(args[0].toString())) {
                Log.i(TAG, "keyss---------Cancel" + args[0]);
                getmNavigator().notifyNoDriverMessage();
            }
        }
    };

    /** Set drivers {@link com.google.android.gms.maps.model.Marker} on {@link GoogleMap}
     * @param cars {@link List} of {@link Car}s **/
    public synchronized void setDriverMarkers(List<Car> cars) {
        Log.e(TAG, "keys--Markers=" + cars.size());
//        if (BuildConfig.DEBUG)
        numberofBasicDriver.set("cars= " + cars.size());
        if (cars != null && cars.size() != 0) {
            Iterator litr = cars.iterator();
            if (googleMap != null) {
                googleMap.clear();
            }
            while (litr.hasNext()) {
                Car element = (Car) litr.next();
//                if (lastlatitude == element.latitude && lastlongitude == element.longitude)
//                    MarkerlatLng = new LatLng(element.latitude, element.longitude);
//                else
                MarkerlatLng = new LatLng(element.latitude, element.longitude);
                createMarker(MarkerlatLng, (lastBearing == element.bearing) ? element.bearing + new Random().nextInt(40) + 100 : element.bearing);
                lastlatitude = element.latitude;
                lastlongitude = element.longitude;
            }
            isDriversAvailable.set(true);
        } else {
            if (googleMap != null) {
                googleMap.clear();
            }
        }
        setPikupDropBoundMarkers(false);
    }

    /** Creates {@link com.google.android.gms.maps.model.Marker} at given {@link LatLng} with given bearing
     * @param latLng {@link LatLng} of the location where marker needs to be drawn
     * @param bearing direction of the vehicle **/
    public void createMarker(LatLng latLng, Float bearing) {
        if (bitmapDescriptorFactory == null)
            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
        markeroption.position(latLng).anchor(0.5f, 0.5f).rotation(bearing).icon(bitmapDescriptorFactory);
        googleMap.addMarker(markeroption);
        lastBearing = bearing;
        Log.e(TAG, "keys--AddedMarkers=" + latLng + " B= " + bearing);
    }

    /** Click listener for ride later **/
    public void onclickRideLater(View view) {
        this.view = view;
        if (type != null)
            getmNavigator().RideLaterClicked();
        else
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Alert_Select_carType));
    }

    public void onclickRideNow(View view) {
        this.view = view;
    }

   /* public void startTypesTimer() {
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        scheduledFuture =
                service.scheduleAtFixedRate(typesRunnable, 6, 6, TimeUnit.SECONDS);
    }*/

   /* Runnable typesRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mSocket.connected())
                SetSocketListener();
            if (pickup != null && pickup.longitude != 0.0 && pickup.latitude != 0.0) {
                socketMessageModel.id = sharedPrefence.Getvalue(SharedPrefence.ID);
                socketMessageModel.client_id = sharedPrefence.Getvalue(SharedPrefence.COMPANY_ID);
                socketMessageModel.client_token = sharedPrefence.Getvalue(SharedPrefence.COMPANY_KEY);
                socketMessageModel.lat = pickup.latitude;
                socketMessageModel.lng = pickup.longitude;
                mSocket.emit("types", gson.toJson(socketMessageModel));
            }
        }
    };*/

    /** Stops {@link ScheduledFuture} timer **/
    void stopTypesTimer() {
        if (scheduledFuture != null)
            if (!scheduledFuture.isCancelled())
                scheduledFuture.cancel(true);
    }

    /** Sets driver's details to CarType
     * @param driverDetails {@link Type} data model **/
    void setDriverDetails(Type driverDetails) {
        if (driverDetails == null)
            return;
        CarType.set(driverDetails.name);
        if (!CommonUtils.IsEmpty(driverDetails.icon))
            carImage.set(BuildConfig.BASE_VEHICLE_IMG_URL + driverDetails.icon);
    }

    void setDriverDetailsNew(TypeNew driverDetails) {
        if (driverDetails == null)
            return;
        CarType.set(driverDetails.getName());
        if (!CommonUtils.IsEmpty(driverDetails.getIcon()))
            carImage.set(driverDetails.getIcon());
    }

    /** API call to get top drivers **/
    void topdriverApi() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.type, "" + typeId);
        hashMap.put(Constants.NetworkParameters.platitude, "" + pickup.latitude);
        hashMap.put(Constants.NetworkParameters.plongitude, "" + pickup.longitude);
        hashMap.put(Constants.NetworkParameters.request_id, "" + reqId);
        TopDriverList(hashMap);
    }

    /** Click listener for ride confirmation button. Calls create request API **/
    public void onclickConfirm(View view) {
        if (rideType == 1) {
            if (typeNew != null) {
                if (!CommonUtils.IsEmpty(paymentOption.get())) {
                    if (isShareRide.get())
                        if (nofUser == null || nofUser == 0) {
                            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.please_choose_seat));
                            return;
                        }
                    is_enableBooking.set(false);
                    hashMap.clear();
//                if (drop == null)
//                    return;
                    if (!scancontent.isEmpty())
                        hashMap.put(Constants.NetworkParameters.privateKey, scancontent);
                    hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                    hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                    hashMap.put(Constants.NetworkParameters.type, "" + typeNew.getTypeId());
//                    hashMap.put(Constants.NetworkParameters.type, "" + typeNew.zoneId);
                    hashMap.put(Constants.NetworkParameters.platitude, "" + pickup.latitude);
                    hashMap.put(Constants.NetworkParameters.plongitude, "" + pickup.longitude);
                    if (isPromoAvail.get())
                        hashMap.put(Constants.NetworkParameters.promo_booked_id, bookedId);

                    if (!CommonUtils.IsEmpty(DropAddress.get())) {
                        hashMap.put(Constants.NetworkParameters.dlongitude, "" + drop.longitude);
                        hashMap.put(Constants.NetworkParameters.dlatitude, "" + drop.latitude);
                        hashMap.put(Constants.NetworkParameters.dlocation, "" + DropAddress.get());
                    }
                    hashMap.put(Constants.NetworkParameters.paymentOpt, "" + paymentOption.get());
                    hashMap.put(Constants.NetworkParameters.plocation, "" + PickAddress.get());
                    hashMap.put(Constants.NetworkParameters.no_of_seats, "" + nofUser);
                    hashMap.put(Constants.NetworkParameters.is_share, "" + isShare);

                    /* Adding drivers notes if any **/
                    if (!CommonUtils.IsEmpty(driverNotes.get())) {
                        hashMap.put(Constants.NetworkParameters.driver_notes, "" + driverNotes.get());
                    }

                    /* Add driver details to the request **/
                    JSONArray jDrivers = new JSONArray();
                    if (getmNavigator().getNewSelectedCar() != null) {
                        try {
                            for (String key: driverDatas.keySet()) {
                                JSONObject jData = new JSONObject(driverDatas.get(key));
                                String d_id = jData.getString("id");
                                Marker marker = driverPins.get(key);
                                String typeId = jData.getString("type");

                                if (typeId.equalsIgnoreCase(getmNavigator().getNewSelectedCar().getTypeId())) {
                                    JSONObject jDriver = new JSONObject();
                                    jDriver.put("driver_id", d_id);
                                    jDriver.put("driver_lat", marker.getPosition().latitude);
                                    jDriver.put("driver_lng", marker.getPosition().longitude);

                                    jDrivers.put(jDriver);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (jDrivers.length() == 0) {
                        getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.Txt_NoDriverFound));
                        return;
                    }
                    hashMap.put("drivers", jDrivers.toString());

                    setIsLoading(true);
                    CreateRequestNetwork();
                } else {
                    if (CommonUtils.IsEmpty(paymentOption.get()))
                        getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_SelectPayment));
                }
            }
        } else {
            OnClickConfirmBooking("");
        }
    }

    /** Calls user blocked API to check if the user is blocked **/
    public void userBlockedApi() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        UserBlockedApi();
    }

    /** Click listener for Apply promo code **/
    public void onClickPromocode(View view) {
//        if (type != null && type.id != null)
//            getmNavigator().onclickpromoCode(type.id);
        if (typeNew != null && typeNew.getZoneId() != null)
            getmNavigator().onclickpromoCode(typeNew.getZoneId());
    }

    /* Click listener for schedule */
    public void onClickSchedule(View view) {
        getmNavigator().onClickTripSchedule();
    }

    /* Click listener for note to driver */
    public void onClickNotesToDriver(View view) {
        getmNavigator().onClickNotesToDriver();
    }


//        /** {@link SocketHelper} callback when types data receives
//         * @param typesString String data of {@link Type} **/
//    @Override
//    public void Types(final String typesString) {
//        if (getmNavigator().getBaseAct() != null)
//            getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //JSONObject data = (JSONObject) args[0];
//                    BaseResponse baseResponse = gson.fromJson(typesString, BaseResponse.class);
//                    if (baseResponse != null && baseResponse.success) {
////                        Istypedata.set(true);
//                        getmNavigator().addcarList(baseResponse.getTypes(), baseResponse.default_selected_type);
//                        // getmNavigator().addcarList(baseResponse.getTypes());
//                        if (baseResponse.getTypes() != null)
//                            if (baseResponse.getTypes().size() > 0) {
//                                getTypesResponse = baseResponse.getTypes();
//
//
//                                final List<Car> driverlist = new ArrayList<>();
//                                if (getmNavigator().GetSelectedCarObj() != null) {
//                                    if (getmNavigator().GetSelectedCarObj().drivers != null) {
//                                        String result = "";
//                                        for (Car driverS : getmNavigator().GetSelectedCarObj().drivers)
//                                            if (driverS != null)
//                                                result = result + "," + driverS.id;
//                                        Log.e(TAG, "keys listOfDriver=" + result);
//                                        driverlist.addAll(getmNavigator().GetSelectedCarObj().drivers);
//                                    }
//                                } else
//                                    for (Type type : baseResponse.getTypes()) {
//                                        if (type != null)
//                                            if (type.drivers != null)
//                                                driverlist.addAll(type.drivers);
//                                    }
//                                Log.e(TAG, "keys-- Types Size=" + driverlist.size());
//                                if (driverlist != null && getmNavigator() != null && getmNavigator().getBaseAct() != null)
//                                    getmNavigator().getBaseAct().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (driverlist != null) {
//                                                if (googleMap != null) {
//                                                    googleMap.clear();
//                                                }
//                                                setDriverMarkers(driverlist);
//                                            }
//                                        }
//                                    });
//                                if (baseResponse.getTypes().get(0) != null && baseResponse.getTypes().get(0).preferred_payment != null)
//                                    sharedPrefence.saveInt(SharedPrefence.PREFFERED_PAYMENT, baseResponse.getTypes().get(0).preferred_payment);
//                            }
//                    }
//                }
//            });
//
//    }

    @Override
    public void Types(String typesString) {

    }

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (data != null) {
                        BaseResponse baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
                        if (baseResponse != null && baseResponse.successMessage != null
                                && baseResponse.successMessage.equalsIgnoreCase("Accepted") && baseResponse.getRequest() != null) {
//                                if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
//                                    if (getmNavigator().getAttachedcontext() != null)
//                                        getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.Txt_DriverAccepted));
//                                } else
//                                    getmNavigator().openTripFragment(baseResponse.getRequest());

//                                if (getmNavigator().getAttachedcontext() != null)
//                                    getmNavigator().showMessage(getmNavigator().getAttachedcontext().getString(R.string.Txt_DriverAccepted));
                            if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
//                                getmNavigator().openRideLaterAlert(baseResponse.getRequest());
                            } else
                                getmNavigator().openTripFragment(baseResponse.getRequest());
                        }
                    }
                }
            });
        }
    }

    /** {@link SocketHelper} callback for cancelled requests
     * @param cancelled_request String response notifies user that the trip was cancelled by driver **/
    @Override
    public void CancelledRequest(String cancelled_request) {
        if (cancelled_request != null && !CommonUtils.IsEmpty(cancelled_request)) {
            Log.i(TAG, "keyss---------Cancel" + cancelled_request);
            getmNavigator().notifyNoDriverMessage();
        }
    }

    /** Returns if internet is connected or not **/
    @Override
    public boolean isNetworkConnected() {
        return getmNavigator().isNetworkConnected();
    }

    @Override
    public void OnConnect() {

    }

    @Override
    public void OnDisconnect() {

    }

    @Override
    public void OnConnectError() {

    }

    @Override
    public void RideLaterNoCaptainAlert(String ride_later_no_captain) {

    }

    @Override
    public void DurationHandler(String duration_handler) {

    }

    @Override
    public void driverEnteredFence(String key, GeoLocation location, String response) {
        Log.v("fatal_log", "Driver entered1: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("is_active")) {
                long updatedAt = jsonObject.getLong("updated_at");
                long currentTime = new Date().getTime();
                long diff = (currentTime - updatedAt) / 1000;
                if (diff < (5 * 60)) {
                    double bearing = jsonObject.getDouble("bearing");
                    if (driverPins.containsKey(key)) {
                        Marker driverPin = driverPins.get(key);
                        driverPin.remove();

                        bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                        markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory).visible(false);
                        Marker marker = googleMap.addMarker(markeroption);

                        driverPins.put(key, marker);
                        driverDatas.put(key, response);
                    } else {
                        bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                        markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory).visible(false);
                        Marker marker = googleMap.addMarker(markeroption);

                        driverPins.put(key, marker);
                        driverDatas.put(key, response);
                    }
                    showFirebaseCarMarkers();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLastKnownMarkers() {
        for (String key: driverPins.keySet()) {
            Marker marker = driverPins.get(key);
            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
            markeroption.position(marker.getPosition()).anchor(0.5f, 0.5f).rotation(marker.getRotation()).icon(bitmapDescriptorFactory);
            Marker nMarker = googleMap.addMarker(markeroption);
            driverPins.put(key, nMarker);
            nMarker.setVisible(false);
        }
    }

    public void showFirebaseCarMarkers() {
        for (String key: driverPins.keySet()) {
            driverPins.get(key).setVisible(false);
            FirebaseHelper.removeObserverFor(key);
        }

        int driverCount = 0;
        for (String key: driverPins.keySet()) {
            String driverId = key;
            String driverData = driverDatas.get(driverId);
            try {
                JSONObject jsonObject = new JSONObject(driverData);
                if (jsonObject.has("type")) {
                    String typeId = jsonObject.getString("type");
                    if (getmNavigator().getNewSelectedCar() != null) {
                        if (getmNavigator().getNewSelectedCar().getTypeId().equals(typeId)) {
                            Marker marker = driverPins.get(key);
                            marker.setVisible(true);
                            driverCount++;
                            FirebaseHelper.addObserverFor(driverId);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isDriversAvailable.set(driverCount > 0);
    }

    @Override
    public void driverExitedFence(String key, String response) {
        if (driverPins.containsKey(key)) {
            Marker marker = driverPins.get(key);
            marker.remove();
            driverPins.remove(key);
            driverDatas.remove(key);

            FirebaseHelper.removeObserverFor(key);
        }
    }

    @Override
    public void driverMovesInFence(String key, GeoLocation location, String response) {
        Log.v("fatal_log", "Driver moves1: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("is_active")) {
                boolean isActive = jsonObject.getBoolean("is_active");
                if (isActive) {
                    if (driverPins.containsKey(key)) {
                        Log.v("fatal_log", "If");
                        Marker driverPin = driverPins.get(key);
                        driverPin.setPosition(new LatLng(location.latitude, location.longitude));
                        driverPin.setRotation((float) jsonObject.getDouble("bearing"));

                        driverPins.put(key, driverPin);
                        driverDatas.put(key, response);

                        showFirebaseCarMarkers();
                    } else {
                        Log.v("fatal_log", "Else");
                        long updatedAt = jsonObject.getLong("updated_at");
                        long currentTime = new Date().getTime();
                        long diff = (currentTime - updatedAt) / 1000;
                        if (diff < (5 * 60)) {
                            double bearing = jsonObject.getDouble("bearing");
                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                            markeroption.position(new LatLng(location.latitude, location.longitude)).anchor(0.5f, 0.5f).rotation((float) bearing).icon(bitmapDescriptorFactory).visible(false);
                            Marker marker = googleMap.addMarker(markeroption);
                            driverPins.put(key, marker);
                            driverDatas.put(key, response);

                            FirebaseHelper.addObserverFor(key);

                            showFirebaseCarMarkers();
                        }
                    }
                } else {
                    if (driverPins.containsKey(key)) {
                        Marker marker = driverPins.get(key);
                        marker.remove();
                        driverPins.remove(key);
                        driverDatas.remove(key);

                        FirebaseHelper.removeObserverFor(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void driverWentOffline(String key) {
        Log.v("fatal_log", "driverWentOffline1: " + key);
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
    public void driverDataUpdated(String key, String response) {
        Log.v("fatal_log", "driverDataUpdated1: " + response);
    }

    @Override
    public void tripStatusReceived(String response) {

    }

    public void applyPromoAPICall(String zoneId, String promocode) {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.type, "" + zoneId);
            hashMap.put(Constants.NetworkParameters.promo_code, promocode);
            RidePromocode(hashMap);
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

}
