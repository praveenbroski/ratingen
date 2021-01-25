package taxi.ratingen.ui.drawerscreen.tripscreen;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import taxi.ratingen.retro.responsemodel.MarkerResponseModel;
import taxi.ratingen.retro.responsemodel.NewRequestModel;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.retro.responsemodel.TranslationModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Car;
import taxi.ratingen.retro.responsemodel.Driver;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.Step;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.FirebaseHelper;
import taxi.ratingen.utilz.SocketHelper;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import taxi.ratingen.ui.drawerscreen.promoscrn.PromoAct;
import taxi.ratingen.ui.drawerscreen.sos.SosFragment;

import static com.google.android.gms.maps.model.JointType.ROUND;

/**
 * Created by root on 12/21/17.
 */

public class TripFragViewModel extends BaseNetwork<BaseResponse, TripNavigator> implements
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, SocketHelper.SocketListener, FirebaseHelper.FirebaseObserver {

    private static final String TAG = "TripFragViewModel";
    HashMap<String, String> hashMap;
    SharedPrefence sharedPrefence;
    TaxiRequestModel.ResultData request;
    GitHubMapService gitHubMapService;
    TaxiRequestModel.DriverData driver;
    private GoogleApiClient mGoogleApiClient;
    Marker marker;
    //  Socket mSocket;
    private PolylineOptions options = new PolylineOptions();
    GoogleMap mGoogleMap;
    public PolylineOptions lineOptionsDest1, lineOptionDesDark;
    Polyline polyLineDest1, polyLineDestDark;
    public Route routeDest1;
    List<Car> cars;
    boolean isPickupDrop;
    public ObservableField<String> driverName, profileurl, car_number, car_model, car_color, tripOTP,
            StatusofTrip, Distance, paymenttype, pickupLocation, dropLocation, waitingtime, arrivalTime;
    public ObservableField<String> userRating;
    public ObservableBoolean isTrpStatusShown, isTripArrived, isTripStared, isShare, isPromodone, dropcheck, isExpCollpClicked;
    public Gson gson, gsoncustom;
    public List<LatLng> pointsDest1;
    public ObservableBoolean isMapRendered = new ObservableBoolean(false);
    private int graceTime;
    private GoogleMap googleMap;
    ObservableField<LatLng> driverLatLng;
    public ObservableBoolean mapType = new ObservableBoolean(false);
    public ObservableBoolean enablePromoOption = new ObservableBoolean(true);
    String waitingValue = "0";
    Boolean cancelationFeeApplied = true;
    float defaultMapZoom = 15;
    boolean zoomFlag = false;

    public TripFragViewModel(GitHubService gitHubService,
                             GitHubMapService gitHubMapService,
                             HashMap<String, String> hashMap,
                             SharedPrefence sharedPrefence, Socket socket, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.gitHubMapService = gitHubMapService;
        //  this.mSocket = socket;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;

        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = gson.fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);
        }

        gsoncustom = new GsonBuilder()
                .registerTypeAdapter(BaseResponse.class, new BaseResponse.OptionsDeserilizer())
                .create();
        driverName = new ObservableField<>();
        car_number = new ObservableField<>();
        car_model = new ObservableField<>();
        Distance = new ObservableField<>();
        isPromodone = new ObservableBoolean(true);
        isTripArrived = new ObservableBoolean(false);
        isTrpStatusShown = new ObservableBoolean(false);
        dropcheck = new ObservableBoolean(false);
        paymenttype = new ObservableField<>();
        profileurl = new ObservableField<>();
        userRating = new ObservableField<>("0");
        car_color = new ObservableField<>("");
        isTripStared = new ObservableBoolean(true);
        StatusofTrip = new ObservableField<>(translationModel.txt_captain_Accepted);
        tripOTP = new ObservableField<>("");
        waitingtime = new ObservableField<>("0:0");
        pickupLocation = new ObservableField<>("");
        dropLocation = new ObservableField<>("");
        driverLatLng = new ObservableField<>();
        isShare = new ObservableBoolean();
        isMapRendered.set(false);
        isExpCollpClicked = new ObservableBoolean(false);
        arrivalTime = new ObservableField<>("");
    }


    public void onclickRightNavigator(View view) {
        getmNavigator().RightNavclicked();
        isTrpStatusShown.set(true);
    }

    public void onclickLeftNavigator(View view) {
        getmNavigator().LeftNavclicked();
        isTrpStatusShown.set(false);
    }

    /** Click listener for expand/collapse arrow **/
    public void onExpCollpClick(View v) {
        if (!isExpCollpClicked.get())
            isExpCollpClicked.set(true);
        else if (isExpCollpClicked.get())
            isExpCollpClicked.set(false);
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response {@link BaseResponse} data model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("trip_pick_up_location_changed")) {
//            if (response.request != null) {
//                request.pickLng = response.request.pickLng;
//                request.pickLat = response.request.pickLat;
//                request.pickLocation = response.request.pickLocation;
//                if (request.dropLat != null && request.dropLng != null && request.dropLat != 0 && request.dropLng != 0 && response.request.pickLat != 0 && response.request.pickLng != 0) {
//                    DrawPathCurrentToHero(true, new LatLng(response.request.pickLat, response.request.pickLng), new LatLng(request.dropLat, request.dropLng));
//                } else {
//                    if (markerPickup != null)
//                        markerPickup.remove();
//                    //   boundLatLang();
//                }
//                pickupLocation.set(response.request.pickLocation);
//            }
        } else if (response.successMessage.equalsIgnoreCase("trip_drop_location_changed")) {
//            if (response.request != null) {
//                request.dropLng = response.request.dropLng;
//                request.dropLat = response.request.dropLat;
//                request.dropLocation = response.request.dropLocation;
//                if (request.pickLat != null && request.pickLng != null && request.pickLat != 0 && request.pickLng != 0 && response.request.dropLat != 0 && response.request.dropLng != 0) {
//                    DrawPathCurrentToHero(true, new LatLng(request.pickLat, request.pickLng), new LatLng(response.request.dropLat, response.request.dropLng));
//                } else {
//                    if (markerDrop != null)
//                        markerDrop.remove();
//                    //  boundLatLang();
//                }
//                dropLocation.set(response.request.dropLocation);
//            }
        } else if (response.successMessage.equalsIgnoreCase("ride promocode available")) {
            isPromodone.set(false);
            enablePromoOption.set(false);
        } else if (response.successMessage.equalsIgnoreCase("Thank you! Enjoy this ride")) {
            isPromodone.set(false);
            enablePromoOption.set(false);
        } else if (response.success) {
            getmNavigator().HomeScreen();
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getMessage().equalsIgnoreCase("Trip already cancelled")) {
            getmNavigator().HomeScreen();
        }
    }

    /** Returns {@link HashMap} with query parameters for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** Custom {@link BindingAdapter} method to set image to {@link ImageView} from {@link java.net.URL} **/
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(imageView);
    }

    @BindingAdapter("font_color")
    public static void setTextColor(TextView textView, boolean isTripStarted) {
        if (isTripStarted)
            textView.setTextColor(Color.parseColor("#E0D426"));
        else
            textView.setTextColor(Color.parseColor("#027D61"));
    }

    public void SetSocketListener() {
       /* mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("trip_status", trip_status);
        mSocket.on(Constants.NetworkParameters.TIME_TAKES, duration_handler);
        mSocket.on("cancelled_request", cancelled_request);
        mSocket.connect();*/
    }

    private Emitter.Listener cancelled_request = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (args != null && args.length > 0 && args[0] != null && !CommonUtils.IsEmpty(args[0].toString())) {
                Log.i(TAG, "keyss---------Cancel" + args[0]);
                getmNavigator().notifyNoDriverMessage();
            }
        }
    };

    /** Click listener for cancel button. Opens cancel dialog when user clicks cancel. **/
    public void Onclickcancel(View view) {
        if (cancelationFeeApplied)
            getmNavigator().ShowCancelDialog("" + request.id);
        else {
            getmNavigator().openCancelalert();
        }
    }

    /** This method calls cancel API after confirming the cancellation process **/
    public void cancelApi() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.request_id, "" + request.id);
        hashMap.put(Constants.NetworkParameters.reason, "0");
        RequestCancelNetwork();
    }

    /** Call phone {@link Intent} to make phone call to diver when call button is clicked **/
    public void OnclickCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + driver.mobile));
        view.getContext().startActivity(callIntent);
    }

    /** Opens compose message screen with driver's phone number on it via {@link Intent} when sms button is clicked **/
    public void OnclickSms(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + driver.mobile));
        view.getContext().startActivity(sendIntent);
    }

    /** Click listener for promo code button. Open {@link PromoAct} **/
    public void OnclickPromocode(View view) {
        getmNavigator().ShowPromoCodeScrn("" + request.id);
    }

    /** Assigns values to variables using data from API
     * @param mParam1 {@link Request} response model
     * @param mParam2 {@link Driver} response model **/
    public void setValues(TaxiRequestModel.ResultData mParam1, TaxiRequestModel.DriverData mParam2) {
        Distance.set("0.0");

        request = mParam1;
        driver = mParam2;

        if (!CommonUtils.IsEmpty(driver.name))
            driverName.set(driver.name);

        if (!CommonUtils.IsEmpty(driver.profilePicture))
            profileurl.set(driver.profilePicture);

        if (!CommonUtils.IsEmpty(driver.carMakeName))
            car_number.set(driver.carMakeName);

        if (!CommonUtils.IsEmpty(driver.carColor))
            car_color.set(driver.carColor);

        if (!CommonUtils.IsEmpty(driver.carModelName))
            car_model.set(driver.carModelName);

        if (driver.rating > 0.0)
            userRating.set(driver.rating + "");

//        isPromodone.set(request.promo_used == 0);
//        enablePromoOption.set(request.promo_used == 0);
        isPromodone.set(true);
        enablePromoOption.set(false);
        if (request.paymentOpt != null) {
            paymenttype.set(getmNavigator().getbaseAct().getTranslatedString(
                    request.paymentOpt.equals("0") ? R.string.txt_card :
                            request.paymentOpt.equals("1") ? R.string.txt_cash :
                                    request.paymentOpt.equals("4") ? R.string.text_corporate :
                                            R.string.txt_wallet));
        }
        if (request.requestOtp != null)
            tripOTP.set(translationModel.text_otp + ": " + request.requestOtp);

        if (mParam1.dropLat != null && mParam1.dropLng != null && mParam1.dropLat != 0 && mParam1.dropLng != 0 && mParam1.pickLat != 0 && mParam1.pickLng != 0) {
            DrawPathCurrentToHero(true, new LatLng(request.pickLat, request.pickLng), new LatLng(request.dropLat, request.dropLng));
        }
        if (!CommonUtils.IsEmpty(mParam1.pickAddress))
            pickupLocation.set(mParam1.pickAddress);
        if (!CommonUtils.IsEmpty(mParam1.dropAddress)) {
            dropcheck.set(true);
            dropLocation.set(mParam1.dropAddress);
        } else dropcheck.set(false);

        if (request.isCompleted == 1) {
//            getmNavigator().ShowFeedBackScreen(request, false);
////            getmNavigator().ShowFeedBackScreen(request, request.isCorporate == 1);
        } else if (request.isTripStart == 1) {
            StatusofTrip.set(translationModel.txt_driver_started_destination);
            isTripStared.set(false);
            isTripArrived.set(true);
            waitingtime.set("0.0");
            enablePromoOption.set(false);

        } else if (request.isDriverArrived == 1) {
            StatusofTrip.set(translationModel.txt_driver_arrived_pickup);
            isTripArrived.set(true);
            waitingtime.set("0.0");
        }
//        isShare.set(request.isShare == 1);
        isShare.set(false);
//        if (request.waiting_grace_time != 0)
//            graceTime = request.waiting_grace_time;

        SocketHelper.init(sharedPrefence, this, TAG, true);
    }


    private long lastUpdatedDriverLocation = 0;

    /** Draws route between pickup & drop locations using Google Directions API
     * @param pick Pickup {@link LatLng}
     * @param drop Drop {@link LatLng} **/
    public void DrawPathCurrentToHero(final boolean clearMap, LatLng pick, LatLng drop) {
        gitHubMapService.GetDrawpath(pick.latitude + "," + pick.longitude, drop.latitude + "," + drop.longitude
                , false, Constants.PlaceApi_key).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    routeDest1 = new Route();
                    CommonUtils.parseRoute(response.body(), routeDest1);

                    final ArrayList<Step> step = routeDest1.getListStep();
                    System.out.println("step size=====> " + step.size());
                    pointsDest1 = new ArrayList<>();
                    lineOptionsDest1 = new PolylineOptions();
                    lineOptionDesDark = new PolylineOptions();

                    lineOptionsDest1.geodesic(true);
                    lineOptionDesDark.geodesic(true);

                    for (int i = 0; i < step.size(); i++) {
                        List<LatLng> path = step.get(i).getListPoints();
                        System.out.println("step =====> " + i + " and "
                                + path.size());
                        pointsDest1.addAll(path);
                    }
                    if (polyLineDest1 != null)
                        polyLineDest1.remove();
                    lineOptionsDest1.addAll(pointsDest1);
                    lineOptionsDest1.width(10f);
                    lineOptionsDest1.startCap(new SquareCap());
                    lineOptionsDest1.endCap(new SquareCap());
                    lineOptionsDest1.jointType(ROUND);

                    if (getmNavigator().isAddedinAct()) {
                        lineOptionsDest1.color(getmNavigator().getbaseAct().getResources().getColor(
                                R.color.clr_FB4A46));
                    }
                    try {
                        if (lineOptionsDest1 != null && mGoogleMap != null) {
                            if (clearMap) {
                                mGoogleMap.clear();
                                marker = null;
                                if (driver.latitude != 0 && driver.longitude != 0) {
                                    if (marker == null)
                                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)));
                                    else {
                                        marker.setPosition(new LatLng(driver.latitude, driver.longitude));
                                        marker.setAnchor(0.5f, 0.5f);
                                    }
                                }
                            }
                            polyLineDest1 = mGoogleMap.addPolyline(lineOptionsDest1);
                            polyLineDestDark = mGoogleMap.addPolyline(lineOptionsDest1);
                            boundLatLang(true);
                            //animatePolyLine();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (getmNavigator().isAddedinAct())
                    getmNavigator().showMessage(t.getMessage());
            }
        });
    }

    /* not used */
    private void animatePolyLine() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = polyLineDest1.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * pointsDest1.size()) / 100;

                if (initialPointSize < newPoints) {
                    latLngList.addAll(pointsDest1.subList(initialPointSize, newPoints));
                    polyLineDestDark.setPoints(latLngList);
                }


            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

            addMarker(pointsDest1.get(pointsDest1.size() - 1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            List<LatLng> blackLatLng = polyLineDest1.getPoints();
            List<LatLng> greyLatLng = polyLineDestDark.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            polyLineDest1.setPoints(blackLatLng);
            polyLineDestDark.setPoints(greyLatLng);

            polyLineDest1.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private void addMarker(LatLng destination) {
        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        //  options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //  mGoogleMap.addMarker(options);
    }
    /* not used */

    /** Initiates {@link GoogleApiClient} **/
    public void buildGoogleApiClient(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        changeMapStyle();
        if (getmNavigator().getbaseAct() != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getmNavigator().getbaseAct())
                    .addConnectionCallbacks(this)
                    /*.addOnConnectionFailedListener(this)*/
                    .addApi(LocationServices.API).build();

            mGoogleApiClient.connect();
        }
        if (request.pickLat != null && request.pickLng != null && request.pickLat != 0 && request.pickLng != 0) {
            boundLatLang(true);
//            if (request.dropLat != null && request.dropLng != null && request.dropLat !=0 && request.dropLng != 0)
//                DrawPathCurrentToHero(true, new LatLng(request.pickLat, request.pickLng), new LatLng(request.dropLat, request.dropLng));
        }
    }

    /** Changes the style and appearance of the {@link GoogleMap} using styles json file **/
    public void changeMapStyle() {
        if (mGoogleMap == null)
            return;
        if (getmNavigator().getbaseAct() != null) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getmNavigator().getbaseAct(), R.raw.style_json);
            mGoogleMap.setMapStyle(style);
        }
    }

    Marker markerPickup, markerDrop;

    /** Adjusts {@link GoogleMap} zoom to fit pickup and drop location **/
    private void boundLatLang(boolean bound) {
        if(mGoogleMap==null)
            return;
        LatLngBounds.Builder bld = new LatLngBounds.Builder();
//        if (isTripArrived.get() && getmNavigator().getbaseAct() != null)
        if (getmNavigator().getbaseAct() != null) {
            Marker mPick = markerPickup = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(request.pickLat, request.pickLng)).title("Pickup Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().getbaseAct(), R.drawable.ic_pick_pin)));
            mPick.setAnchor(0.5f, 0.5f);
        }
        if (request.dropLat != null && request.dropLng != null && getmNavigator().getbaseAct() != null) {
            Marker mDrop = markerDrop = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(request.dropLat, request.dropLng)).title("Drop Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().getbaseAct(), R.drawable.ic_drop_pin)));
            bld.include(new LatLng(request.dropLat, request.dropLng));
            mDrop.setAnchor(0.5f, 0.5f);
        }
        bld.include(new LatLng(request.pickLat, request.pickLng));
        if (driver != null && driver.latitude != null && driver.longitude != null)
            bld.include(new LatLng(driver.latitude, driver.longitude));
        if (isMapRendered.get())
            if (bound)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bld.build(), 25));
    }

    /** {@link Socket} connection successful callback **/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (driver.latitude != null && driver.latitude != 0 && driver.longitude != null && driver.longitude != 0) {
            if (marker == null)
                marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)));
            else {
                marker.setPosition(new LatLng(driver.latitude, driver.longitude));
                marker.setAnchor(0.5f, 0.5f);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    CountDownTimer durationEstimateTimer;

    /** Click listener for change pickup location **/
    public void chagneAddress(View view) {
        getmNavigator().chagePickup(true);
    }

    /** Click listener for change drop location **/
    public void changeDropAddress(View view) {
        getmNavigator().chagePickup(false);
    }

    /** Move to updated location **/
    public void moveCurrentLoc(View view) {
        getmNavigator().openRequestLocation();
//                CommonUtils.ShowGpsDialog(getmNavigator().getBaseAct());
    }

    /** Click listener for SOS. Opens {@link SosFragment} **/
    public void sos_click(View v) {
        getmNavigator().sosClicked();
    }

    /**
     * Geocode received address to Latlong and change with Pickup/Drop AddressChange api
     */
    public void processSelectedAddress(String addressString, boolean isPickupDrop) {
        this.isPickupDrop = isPickupDrop;
        if (!CommonUtils.IsEmpty(addressString))
            getLocationFromAddress(addressString);
    }

    /** Reverse geo-coding: Gets {@link android.location.Location} from given address string
     * @param place Address that needs to be converted into {@link android.location.Location} **/
    private void getLocationFromAddress(final String place) {
        LatLng loc = null;
        Geocoder gCoder = new Geocoder(getmNavigator().getbaseAct());
        final List<Address> list;
        try {
            list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
//                MarkerAnimation(true, mGoogleMap.getCameraPosition().zoom);
                updateLocation(place, loc.latitude, loc.longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
            setIsLoading(true);
            gitHubMapService.GetLatLngFromAddress(place, false, Constants.PlaceApi_key).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAsJsonArray("results") != null && response.body().getAsJsonArray("results").size() != 0) {
                            Double lat = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            Double lng = response.body().getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            updateLocation(place, lat, lng);
                        }

                    } else {
                        Log.d(TAG, "GetAddressFromLatlng" + response.toString());
                        getmNavigator().showMessage(getmNavigator().getbaseAct().getTranslatedString(R.string.txt_try_again));
                    }
                    setIsLoading(false);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    setIsLoading(false);
                    getmNavigator().showMessage(getmNavigator().getbaseAct().getTranslatedString(R.string.txt_try_again));
                    Log.d(TAG, "GetAddressFromLatlng" + t.toString());
                }
            });
        }

    }

    /** Calls updateLocationInRideDrop API to update the location
     * @param place Address string
     * @param latitude Latitude of the place
     * @param longitude Longitude of the place **/
    private void updateLocation(String place, double latitude, double longitude) {
        if (isPickupDrop) {
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.request_id, "" + request.id);
            hashMap.put(Constants.NetworkParameters.plocation, place);
            hashMap.put(Constants.NetworkParameters.platitude, "" + latitude);
            hashMap.put(Constants.NetworkParameters.plongitude, "" + longitude);
            if (!getmNavigator().isNetworkConnected()) {
                getmNavigator().showNetworkMessage();
                return;
            }
            setIsLoading(true);
            updateLocationInRide(hashMap);
        } else {
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.request_id, "" + request.id);
            hashMap.put(Constants.NetworkParameters.dlocation, place);
            hashMap.put(Constants.NetworkParameters.dlatitude, "" + latitude);
            hashMap.put(Constants.NetworkParameters.dlongitude, "" + longitude);
            if (!getmNavigator().isNetworkConnected()) {
                getmNavigator().showNetworkMessage();
                return;
            }
            setIsLoading(true);
            updateLocationInRideDrop(hashMap);
        }
    }

    /** Toggles {@link GoogleMap} type between Satellite and Normal when called **/
    public void mapTypeClick(View v) {
        if (!mapType.get()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mapType.set(true);
            sharedPrefence.saveBoolean(SharedPrefence.MAPTYPE, true);
        } else {
            mapType.set(false);
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            sharedPrefence.saveBoolean(SharedPrefence.MAPTYPE, false);
        }
    }

    /** Creates custom marker for pickup location
     * @param context Current {@link Context}
     * @param from Address of pickup location **/
    private void createPickupCustomMarker(Context context, String from) {
        getmNavigator().setVAlue(from, request.pickLat, request.pickLng, request.dropLat, request.dropLng);
    }

    /** Creates custom marker for drop location
     * @param context Current {@link Context}
     * @param from Address of drop location **/
    private void createDropMArker(Context context, String from) {
        getmNavigator().setDropValue(from, request.pickLat, request.pickLng, request.dropLat, request.dropLng);
    }

    /** Callback for {@link GoogleMap} loading complete **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void Types(String typesString) {

    }

    /** {@link SocketHelper} callback to receiver `trip_status`
     * @param trip_status Current status of the trip **/
    @Override
    public void TripStatus(final String trip_status) {
        Log.i(TAG, "trip_status" + trip_status);
        if (getmNavigator().getbaseAct() != null) {
            getmNavigator().getbaseAct().runOnUiThread(() -> {
                BaseResponse baseResponse = null;
                JSONObject data = null;
                try {
                    data = new JSONObject(trip_status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //System.out.println("+get_cars+" + data.toString());
                if (data != null) {
                    baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
                }

                if (baseResponse != null)
                    if (baseResponse.success && mGoogleMap != null) {
                        if (baseResponse.successMessage == null) {
                            if (!CommonUtils.IsEmpty(baseResponse.waiting_time)) {
                                waitingValue = baseResponse.waiting_time;
                                waitingtime.set(baseResponse.waiting_time + (getmNavigator().getbaseAct() != null ? getmNavigator().getbaseAct().getTranslatedString(R.string.txt_min) : " Mins"));
                            }
//                                    cancelationFeeApplied = baseResponse.show_cancellation_reason;
                            if (baseResponse.trip_start != null) {

                                if (baseResponse.trip_start == 1) {
                                    isTripStared.set(false);
                                }

                                if (baseResponse.distancee != null) {
                                    Distance.set("" + CommonUtils.doubleDecimalFromat(baseResponse.distancee));
                                }
                            } else {
                                driverLatLng.set(new LatLng(baseResponse.lat, baseResponse.lng));
                            }

                        } else if (baseResponse.successMessage != null) {
                            if (baseResponse.successMessage.equalsIgnoreCase("driver_arrived")) {
                                StatusofTrip.set(translationModel.txt_driver_arrived_pickup);
                                isTripArrived.set(true);
                                waitingtime.set("0.0");
                                mGoogleMap.clear();
                                drawSavedRoute(true);
                                marker = null;
                                if (driver.latitude != null && driver.latitude != 0 && driver.longitude != 0 && driver.longitude != 0) {
                                    if (marker == null)
                                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)));
                                    else {
                                        marker.setPosition(new LatLng(driver.latitude, driver.longitude));
                                        marker.setAnchor(0.5f, 0.5f);
                                    }
                                }
                            } else if (baseResponse.successMessage.equalsIgnoreCase("driver_started_the_trip")) {
                                StatusofTrip.set(translationModel.txt_driver_started_destination);
                                isTripStared.set(false);
                                isTripArrived.set(true);
                                waitingtime.set("0.0");
                                enablePromoOption.set(false);
                            } else if (baseResponse.successMessage.equalsIgnoreCase("driver_end_the_trip")) {
                                if (baseResponse.result != null) {
                                    String requestStrBase = CommonUtils.ObjectToString(baseResponse.result);
                                    BaseResponse requestBase = (BaseResponse) CommonUtils.StringToObject(requestStrBase, BaseResponse.class);
                                    if (requestBase.data != null) {
                                        String requestStr = CommonUtils.ObjectToString(requestBase.data);
                                        TaxiRequestModel.ResultData metaRequest = (TaxiRequestModel.ResultData) CommonUtils.StringToObject(requestStr, TaxiRequestModel.ResultData.class);
                                        if (metaRequest != null)
                                            getmNavigator().ShowFeedBackScreen(metaRequest, false);
                                    }
                                }
                            } else if (baseResponse.successMessage.equalsIgnoreCase("request_cancelled_by_driver")) {
                                getmNavigator().openTripCancelMsg();
                            } else if (baseResponse.successMessage.equalsIgnoreCase("driver_location_got_successfully")) {
                                mGoogleMap.clear();
                                drawSavedRoute(false);
                                marker = null;
                                if (baseResponse.lat != null && baseResponse.lat != 0 && baseResponse.lng != 0 && baseResponse.lng != 0) {
                                    marker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(baseResponse.lat, baseResponse.lng))
                                            .title("Driver Point")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)).rotation(baseResponse.bearing));
                                    if (!zoomFlag) {
                                        boundLatLang(true);
                                        zoomFlag = true;
                                    }
                                    if (mGoogleMap != null && defaultMapZoom != 0) {
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(baseResponse.lat, baseResponse.lng), defaultMapZoom));
                                    }
                                }
                            }
//                            if (baseResponse.getRequest().isCompleted == 1)
//                                getmNavigator().ShowFeedBackScreen(baseResponse.getRequest(), baseResponse.getRequest().isCorporate == 1);
//                            if (baseResponse.getRequest().isTripStart == 1) {
//                                StatusofTrip.set(translationModel.txt_driver_started_destination);
//                                isTripStared.set(false);
//                                isTripArrived.set(true);
//                                waitingtime.set("0.0");
//                                enablePromoOption.set(false);
//                            } else if (baseResponse.getRequest().isDriverArrived == 1) {
//                                StatusofTrip.set(translationModel.txt_driver_arrived_pickup);
//                                isTripArrived.set(true);
//                                waitingtime.set("0.0");
//                                mGoogleMap.clear();
//                                drawSavedRoute();
//                                boundLatLang();
//                                marker = null;
//                                if (driver.latitude != 0 && driver.longitude != 0) {
//                                    if (marker == null)
//                                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)));
//                                    else {
//                                        marker.setPosition(new LatLng(driver.latitude, driver.longitude));
//                                        marker.setAnchor(0.5f, 0.5f);
//                                    }
//                                }
//                            }
                        }
                    }
            });
        }
    }

    private void drawSavedRoute(boolean bound) {
        if (routeDest1 != null) {
            final ArrayList<Step> step = routeDest1.getListStep();
            System.out.println("step size=====> " + step.size());
            pointsDest1 = new ArrayList<LatLng>();
            lineOptionsDest1 = new PolylineOptions();
            lineOptionDesDark = new PolylineOptions();

            lineOptionsDest1.geodesic(true);
            lineOptionDesDark.geodesic(true);

            for (int i = 0; i < step.size(); i++) {
                List<LatLng> path = step.get(i).getListPoints();
                System.out.println("step =====> " + i + " and "
                        + path.size());
                pointsDest1.addAll(path);
            }
            if (polyLineDest1 != null)
                polyLineDest1.remove();
            lineOptionsDest1.addAll(pointsDest1);
            lineOptionsDest1.width(10f);
            lineOptionsDest1.startCap(new SquareCap());
            lineOptionsDest1.endCap(new SquareCap());
            lineOptionsDest1.jointType(ROUND);

            if (getmNavigator().isAddedinAct()) {
                lineOptionsDest1.color(getmNavigator().getbaseAct().getResources().getColor(
                        R.color.clr_FB4A46));
            }
            try {
                if (lineOptionsDest1 != null && mGoogleMap != null) {
                    polyLineDest1 = mGoogleMap.addPolyline(lineOptionsDest1);
                    polyLineDestDark = mGoogleMap.addPolyline(lineOptionsDest1);
                    boundLatLang(bound);
                    //animatePolyLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** {@link SocketHelper} callback to receive `trip_cancelled` msg
     * This callback is called when driver cancels the trip
     * @param cancelled_request Request cancelled msg **/
    @Override
    public void CancelledRequest(String cancelled_request) {
        if (cancelled_request != null && !CommonUtils.IsEmpty(cancelled_request)) {
            Log.i(TAG, "keyss---------Cancel" + cancelled_request);
            getmNavigator().notifyNoDriverMessage();
        }
    }

    /** Returns true/false based on the internet connection **/
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
        if (!CommonUtils.IsEmpty(duration_handler)) {
            Log.v(TAG, "keys_duration_handler: " + duration_handler);
            final MarkerResponseModel baseResponse;
            JSONObject data = null;
            try {
                data = new JSONObject(duration_handler);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            baseResponse = gson.fromJson(data.toString(), MarkerResponseModel.class);
            if (!isTripArrived.get() && baseResponse != null && baseResponse.getDuration() != null) {
                createPickupCustomMarker(getmNavigator().getbaseAct(), baseResponse.getDuration());
                /* if (isTripArrived.get() && request.dropLng != null && request.dropLat != null && request.dropLng != 0.0 && request.dropLat != 0.0) {
                    if (baseResponse.getDuration() != null) {
                        createDropMArker(getmNavigator().getbaseAct(), baseResponse.getDuration());
                    }
                } */
            }
        }
    }
    /** Called to change the trip status from driver arrived to trip started **/
    public void tripstarted() {
        StatusofTrip.set(translationModel.txt_driver_started_destination);
        isTripStared.set(false);
        enablePromoOption.set(false);
        isTripArrived.set(true);
        waitingtime.set("0.0");
    }

    /** Called to change trip status from driver accepted to driver arrived **/
    public void tripArrived() {
        StatusofTrip.set(translationModel.txt_driver_arrived_pickup);
        isTripArrived.set(true);
        waitingtime.set("0.0");
        if(mGoogleMap!=null)
        mGoogleMap.clear();
        else
            return;

        drawSavedRoute(true);
        boundLatLang(true);
        marker = null;
        if (driver.latitude != 0 && driver.longitude != 0) {
            if (marker == null)
                marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car)));
            else {
                marker.setPosition(new LatLng(driver.latitude, driver.longitude));
                marker.setAnchor(0.5f, 0.5f);
            }
        }
    }

    @Override
    public void driverEnteredFence(String key, GeoLocation location, String response) {

    }

    @Override
    public void driverExitedFence(String key, String response) {

    }

    @Override
    public void driverMovesInFence(String key, GeoLocation location, String response) {

    }

    @Override
    public void driverWentOffline(String key) {

    }

    @Override
    public void driverDataUpdated(String key, String response) {

    }

    @Override
    public void tripStatusReceived(String response) {
//        Log.i(TAG, "trip_status" + response);
        if (getmNavigator().getbaseAct() != null) {
            getmNavigator().getbaseAct().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseResponse baseResponse = null;
                    JSONObject data = null;
                    try {
                        data = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("+get_cars+" + data.toString());
                    if (data != null) {
                        baseResponse = gson.fromJson(data.toString(), BaseResponse.class);
                    }

                    if (baseResponse != null)
                        if (baseResponse.success && mGoogleMap != null) {

                            if (baseResponse.successMessage == null) {
                                if (!CommonUtils.IsEmpty(baseResponse.waiting_time)) {
                                    // waitingValue = baseResponse.waiting_time;
                                    waitingtime.set(baseResponse.waiting_time + (getmNavigator().getbaseAct() != null ? getmNavigator().getbaseAct().getTranslatedString(R.string.txt_min) : "Mins"));
                                }
//                                    cancelationFeeApplied = baseResponse.show_cancellation_reason;
                                if (baseResponse.trip_start != null) {
                                    if ((System.currentTimeMillis() - lastUpdatedDriverLocation) > 1000 || lastUpdatedDriverLocation == 0) {
                                        driverLatLng.set(new LatLng(baseResponse.lat, baseResponse.lng));
                                        if (marker != null) {
                                            marker.setPosition(new LatLng(baseResponse.lat, baseResponse.lng));
                                            marker.setRotation(baseResponse.bearing);
                                            marker.setAnchor(0.5f, 0.5f);
                                        } else {
                                            marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(baseResponse.lat, baseResponse.lng)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_carmarker)));

                                        }
                                        CameraPosition newCamPos = new CameraPosition(new LatLng(baseResponse.lat, baseResponse.lng),
                                                15.5f,
                                                mGoogleMap.getCameraPosition().tilt, //use old tilt
                                                mGoogleMap.getCameraPosition().bearing);
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 15, null);
                                        lastUpdatedDriverLocation = System.currentTimeMillis();
                                    }
                                    if (baseResponse.trip_start == 1) {
                                        isTripStared.set(false);


                                           /* Polyline line = mGoogleMap.addPolyline(options
                                                    .add(new LatLng(baseResponse.lat, baseResponse.lng))
                                                    .width(10)
                                                    .color(Color.BLUE));*/
                                        CameraPosition newCamPos = new CameraPosition(new LatLng(baseResponse.lat, baseResponse.lng),
                                                15.5f,
                                                mGoogleMap.getCameraPosition().tilt, //use old tilt
                                                mGoogleMap.getCameraPosition().bearing); //use old bearing
                                        /* mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 15, null);*/
                                    }

                                    if (baseResponse.distancee != null) {
                                        Distance.set("" + CommonUtils.doubleDecimalFromat(baseResponse.distancee));
                                    }
                                } else {
                                    driverLatLng.set(new LatLng(baseResponse.lat, baseResponse.lng));
                                    if (marker != null) {
                                        marker.setPosition(new LatLng(baseResponse.lat, baseResponse.lng));
                                        marker.setRotation(baseResponse.bearing);
                                        marker.setAnchor(0.5f, 0.5f);
                                    } else
                                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_carmarker)));
                                    //  Toast.makeText(getmNavigator().getbaseAct(), baseResponse.lat + "," + baseResponse.lng, Toast.LENGTH_SHORT).show();
                                }

                            } else if (baseResponse.successMessage != null && baseResponse.getRequest() != null) {
                                if (baseResponse.getRequest().isCompleted == 1)
                                    getmNavigator().ShowFeedBackScreen(baseResponse.getRequest(), baseResponse.getRequest().isCorporate == 1);
                                if (baseResponse.getRequest().isTripStart == 1) {
                                    StatusofTrip.set(translationModel.txt_trip_started);
                                    isTripStared.set(false);
                                    isTripArrived.set(true);
                                    waitingtime.set("0.0");
                                    isPromodone.set(false);
                                    setupPeriodicUpdate();
                                } else if (baseResponse.getRequest().isDriverArrived == 1) {
                                    StatusofTrip.set(translationModel.txt_arrived);
                                    isTripArrived.set(true);
                                    waitingtime.set("0.0");
                                    mGoogleMap.clear();
                                    boundLatLang(true);
                                    marker = null;
                                    if (driver.latitude != 0 && driver.longitude != 0) {
                                        if (marker == null)
                                            marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(driver.latitude, driver.longitude)).title("Driver Point").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_carmarker)));
                                        else {
                                            marker.setPosition(new LatLng(driver.latitude, driver.longitude));
                                            marker.setAnchor(0.5f, 0.5f);
                                        }
                                    }
                                }
                            }
                        }


                }
            });
        }
    }


    public void applyPromoAPI(String promocode) {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.request_id, "" + request.id);
            hashMap.put(Constants.NetworkParameters.promo_code, promocode);
            RidePromocodeTrip(hashMap);
        } else {
            getmNavigator().showNetworkMessage();
        }
    }
    public ScheduledFuture scheduledFuture;

    private void setupPeriodicUpdate() {
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        scheduledFuture =
                service.scheduleAtFixedRate(typesRunnable, Constants.INITIAL_DELAY, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    Runnable typesRunnable = new Runnable() {
        @Override
        public void run() {
            if (!SocketHelper.isSocketConnected())
                SocketHelper.setSocketListener();
        }
    };
}
