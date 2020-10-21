package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.MyComponent;
import taxi.ratingen.utilz.SharedPrefence;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 11/17/17.
 */

public class RideFragViewModel extends BaseNetwork<BaseResponse, RideNavigator> {

    private static final String TAG = "RideFragViewModel";
    public Type type;
    public ShareRideDetails shareRideDetails;
    public GoogleMap googleMap;
    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public Context mcontext;
    public BaseResponse baseResponse = null;
    public ObservableField<String> DateandTime, PickAddress, DropAddress, CarType, TotalPrize, currency, totalSharePrice, NofSeat, duration_min;
    public ObservableBoolean DropProceed, IsFrom, is_enableBooking, is_CorporateUser, isShareDriver, isShareRide, isDurationAvailable;
    public ObservableField<String> paymentOption;
    public View view;
    Integer nofUser, isShare = 0;
    GitHubMapService gitHubMapService;
    SimpleDateFormat realformatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
    private SimpleDateFormat TargetFormatter = new SimpleDateFormat("yyyy-MM-dd kk:mm");
    Socket socket;
    String scancontent = "";

    public RideFragViewModel(GitHubService gitHubService,
                             SharedPrefence sharedPrefence, HashMap<String, String> hashMap, Context context, GitHubMapService gitHubMapService, Socket socket, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        this.mcontext = context;
        this.gitHubMapService = gitHubMapService;
        this.socket = socket;
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
        duration_min = new ObservableField<>("");
        isDurationAvailable = new ObservableBoolean(false);
        DropProceed = new ObservableBoolean(false);
        is_enableBooking = new ObservableBoolean(true);
        isShareDriver = new ObservableBoolean(false);
        isShareRide = new ObservableBoolean(false);
        is_CorporateUser = new ObservableBoolean(false);
        DataBindingUtil.setDefaultComponent(new MyComponent(this));
        isShare = 0;
    }

    /**
     * Change map style
     * SetValues
     * Add pickup and drop {@link com.google.android.gms.maps.model.Marker}
     **/
    public void setPins(Type mParam1, GoogleMap googleMap) {
        type = mParam1;
        this.googleMap = googleMap;
        changeMapStyle();
        SetValues();
    }

    /** Changes style & appearance of {@link GoogleMap} by using json from {@link android.content.res.Resources} **/
    private void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mcontext, R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /** Set data from API to varaible
     * Adds Pickup & Drop to {@link GoogleMap}
     * Zooms {@link GoogleMap} to fit both markers
     **/
    public void SetValues() {
        if (!CommonUtils.IsEmpty(type.getIsFrom())) {
            if (type.getIsFrom().equalsIgnoreCase(Constants.RideNow)) {
                IsFrom.set(false);
            } else if (type.getIsFrom().equalsIgnoreCase(Constants.RideLater)) {
                IsFrom.set(true);
                isShareDriver.set(false);
            }
        }

        is_CorporateUser.set(sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER));
        if (is_CorporateUser.get())
            getmNavigator().setCorporateUser(is_CorporateUser.get());
        if (!CommonUtils.IsEmpty(type.getDropAddress())) {
            getmNavigator().ShowDropLayout(false);
            DropProceed.set(true);
            DropAddress.set(type.getDropAddress());
            scancontent = type.getScanContent();
            ETANetWorkcall(scancontent);
        } else {
            getmNavigator().ShowDropLayout(true);
        }


        if (!CommonUtils.IsEmpty(type.name))
            CarType.set(type.name);


        if (!CommonUtils.IsEmpty(type.getPickAddress()))
            PickAddress.set(type.getPickAddress());

        if (!CommonUtils.IsEmpty(type.getDateform()))
            DateandTime.set(type.getDateform());


        if (type.getPicklatlng() != null)
            googleMap.addMarker(new MarkerOptions().position(type.getPicklatlng()).title("Pickup Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().GetBaseAct(), R.drawable.ic_pickup)));

        if (type.getDroplatlng() != null)
            googleMap.addMarker(new MarkerOptions().position(type.getDroplatlng()).title("Drop Point").icon(CommonUtils.getBitmapDescriptor(getmNavigator().GetBaseAct(), R.drawable.ic_drop)));

        if (type.getDroplatlng() != null && type.getPicklatlng() != null) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                    .include(type.getPicklatlng())
                    .include(type.getDroplatlng());

            /* LatLngBounds tmpBounds = boundsBuilder.build();
             *//** Add 2 points 1000m northEast and southWest of the center.
             * Th if they ey increase the bounds only,are not already larger
             * than this.
             * 1000m on the diagonal translates into about 709m to each direction. *//*
            LatLng center = tmpBounds.getCenter();
            LatLng northEast = CommonUtils.move(center, 15809, 15809);
            LatLng southWest = CommonUtils.move(center, -15809, -15809);
            boundsBuilder.include(southWest);
            boundsBuilder.include(northEast);*/
            //calculateZoomLevel(CommonUtils.GetScreenwidth(getmNavigator().GetBaseAct()))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(), 150));


        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    type.getPicklatlng(), 150));
        }


        /*  if (!CommonUtils.IsEmpty(type.getDropAddress())) {


         *//*getmNavigator().ShowPaymentProgress(true);*//*

        } else {




        }*/
        NofSeat.set("Choose Seat ");
    }

    private int calculateZoomLevel(int screenWidth) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > 2000) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        Log.i("ADNAN", "zoom level = " + zoomLevel);
        return zoomLevel;
    }

    /** Calls ETA API to get eta details **/
    private void ETANetWorkcall(String scancontent) {

        if (getmNavigator().isNetworkConnected()) {

            if (type.getPicklatlng() != null && type.getDroplatlng() != null) {
                hashMap.clear();
                if (!scancontent.equalsIgnoreCase(""))
                    hashMap.put(Constants.NetworkParameters.privateKey,"");
                hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                hashMap.put(Constants.NetworkParameters.type_id, "" + type.id);
                hashMap.put(Constants.NetworkParameters.olat, "" + type.getPicklatlng().latitude);
                hashMap.put(Constants.NetworkParameters.olng, "" + type.getPicklatlng().longitude);
                hashMap.put(Constants.NetworkParameters.dlat, "" + type.getDroplatlng().latitude);
                hashMap.put(Constants.NetworkParameters.dlng, "" + type.getDroplatlng().longitude);
                getETANetworkcall();
            }


        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /** Click listener of drop location card **/
    public void onclickDropcard(View view) {

        getmNavigator().DropCardClicked();

    }

    /** Click listener for normal ride **/
    public void onclickNormalRide(View view) {
        isShare = 0;
        isShareRide.set(false);
    }

    /** Click listener for shared ride **/
    public void onclickShareRide(View view) {
        isShare = 1;
        isShareRide.set(true);
    }

    /** Click listener for payment click **/
    public void onclickPayment(View view) {
        if (!is_CorporateUser.get())
            getmNavigator().onClickPayment(type);
    }

    /** Click listener for no. of seats on shared ride **/
    public void onclickNofSeat(View view) {
        getmNavigator().onClickNofSeat(shareRideDetails, currency.get());
    }

    /** Click listener for drip layout **/
    public void onclickEnterDropLayout(View view) {
        getmNavigator().DropCardClicked();
    }

    /** Click listener for ETA info icon **/
    public void onclickInfoETA(View view) {
        if (baseResponse != null) {
            getmNavigator().openETADialog(baseResponse);
        }
    }

    /** Custom {@link BindingAdapter} function to change drop address **/
    @BindingAdapter({"RideDropAddr"})
    public void DropAddChange(TextView mapView, String DropAdd) {
        if (!DropProceed.get() && DropAdd != null) {
            getLocationFromAddress(DropAdd);
        }
    }

    /** Click listener or confirm booking button
     * Calls Create request API **/
    public void OnClickConfirmBooking(View view) {
        this.view = view;
        if (!CommonUtils.IsEmpty(DropAddress.get()) && !CommonUtils.IsEmpty(paymentOption.get())) {


            if (isShareRide.get())
                if (nofUser == null || nofUser == 0) {
                    getmNavigator().showSnackBar(view, getmNavigator().GetBaseAct().getTranslatedString(R.string.please_choose_seat));
                    return;
                }


            setIsLoading(true);
            is_enableBooking.set(false);
            hashMap.clear();
            if (type.getDroplatlng() == null)
                return;
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.type, "" + type.id);
            hashMap.put(Constants.NetworkParameters.platitude, "" + type.getPicklatlng().latitude);
            hashMap.put(Constants.NetworkParameters.plongitude, "" + type.getPicklatlng().longitude);
            hashMap.put(Constants.NetworkParameters.dlongitude, "" + type.getDroplatlng().longitude);
            hashMap.put(Constants.NetworkParameters.dlatitude, "" + type.getDroplatlng().latitude);
            hashMap.put(Constants.NetworkParameters.paymentOpt, "" + paymentOption.get());
            hashMap.put(Constants.NetworkParameters.dlocation, "" + DropAddress.get());
            hashMap.put(Constants.NetworkParameters.plocation, "" + PickAddress.get());
            hashMap.put(Constants.NetworkParameters.no_of_seats, "" + nofUser);
            hashMap.put(Constants.NetworkParameters.is_share, "" + isShare);
            if (type.getIsFrom().equalsIgnoreCase(Constants.RideLater)) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                        Locale.getDefault());
                Date currentLocalTime = calendar.getTime();
                DateFormat date = new SimpleDateFormat("Z");
                String s = date.format(currentLocalTime);
                Log.d(TAG, date.format(currentLocalTime));
                hashMap.put(Constants.NetworkParameters.timezone, "" + s.substring(0, 3) + ":" + s.substring(3));
                Log.d(TAG, s.substring(0, 3) + ":" + s.substring(3));
                try {

                    hashMap.put(Constants.NetworkParameters.datetime, "" + TargetFormatter.format(realformatter.parse(type.getDateform())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                CreateRequestLaterNetwork();
            } else {
                CreateRequestNetwork();
            }


        } else {


            if (CommonUtils.IsEmpty(paymentOption.get()))
                getmNavigator().showSnackBar(view, getmNavigator().GetBaseAct().getTranslatedString(R.string.Validate_SelectPayment));


            if (CommonUtils.IsEmpty(DropAddress.get()))
                getmNavigator().showSnackBar(view, getmNavigator().GetBaseAct().getTranslatedString(R.string.Validate_EnterDropLocation));
        }


    }

    /** Reverse geo-coding function to get {@link android.location.Location} from given address String
     * @param place Address string **/
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

    /** Sets current shared ride details **/
    public void currentShareETA(Integer i) {
        nofUser = i;
        if (i == 1) {
            totalSharePrice.set(CommonUtils.doubleDecimalFromat(shareRideDetails.one_seat_total) + "");

        } else {
            totalSharePrice.set(CommonUtils.doubleDecimalFromat(shareRideDetails.two_seat_total) + "");
        }
    }

    /** Click listener to close current screen and display previous screen **/
    public void onclickBackBtn(View view) {
        getmNavigator().goback();
    }

    /** Callback for successful API calls
     * @param taskId Id of the API call
     * @param response {@link BaseResponse} data model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        is_enableBooking.set(true);

        if (response != null && response.success) {
            if (response.successMessage.equalsIgnoreCase("Eta_For_Trip")) {
                baseResponse = response;
                if (response.is_accept_share_ride == 1 && !IsFrom.get())
                    isShareDriver.set(true);
                else
                    isShareDriver.set(false);

                shareRideDetails = response.share_ride_details;
                CarType.set(type.name);
                currency.set(response.currency);
                if (response.approximate_value != null && response.approximate_value == 1)
                    TotalPrize.set(currency.get() + CommonUtils.doubleDecimalFromat(response.min_amount) + " - " + currency.get() + CommonUtils.doubleDecimalFromat(response.max_amount));
                else
                    TotalPrize.set("" + response.total);
                if (!CommonUtils.IsEmpty(response.driver_arival_estimation) && !response.driver_arival_estimation.contains("-")) {
                    isDurationAvailable.set(true);
                    duration_min.set(getmNavigator().getAttachedcontext() != null ? (String.format(getmNavigator().GetBaseAct().getTranslatedString(R.string.driver_away_txt), response.driver_arival_estimation)) : response.driver_arival_estimation);
                } else {
                    isDurationAvailable.set(true);
                }
                totalSharePrice.set("" + CommonUtils.doubleDecimalFromat(response.share_ride_price));


            } else if (response.successMessage.equalsIgnoreCase("Create_Request_successfully")) {
                if (getmNavigator().isAttached()) {
                    getmNavigator().showMessage(getmNavigator().GetBaseAct().getTranslatedString(R.string.Txt_RideCreated));
                    getmNavigator().ShowWaitingDialog("" + response.request.id);
                }
            } else if (response.successMessage.equalsIgnoreCase("Trip_registered_successfully")) {
                if (getmNavigator().isAttached())
                    getmNavigator().showMessage(getmNavigator().GetBaseAct().getTranslatedString(R.string.Txt_RideHas));
                getmNavigator().goback();
            }
        }

    }

    /** Callback for failed API calls
     * @param taskId Id of API call
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        is_enableBooking.set(true);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedcontext() != null)
                getmNavigator().showMessage(getmNavigator().GetBaseAct().getTranslatedString(R.string.text_already_login));
            getmNavigator().logoutApp();
        } else if (e.getCode() == 728) {
            getmNavigator().goback();
            getmNavigator().showMessage(e);
        }  else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }else
            getmNavigator().showMessage(e);


      /*  if(e.getMessage().equalsIgnoreCase("No Drivers Found")){
            setIsLoading(false);
        }*/

    }

    /** Returns {@link HashMap} with query parameters used in API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }


}
