package taxi.ratingen.ui.drawerscreen.historydetails;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 1/5/18.
 */

public class HistoryDetViewModel extends BaseNetwork<BaseResponse, HistoryDetNavigator> implements OnMapReadyCallback {
    @Inject
    Context context;

    SharedPrefence sharedPrefence;
    @Inject
    HashMap<String, String> map;
    public ObservableBoolean isShare = new ObservableBoolean(false);
    public ObservableBoolean isCompleted;
    public ObservableBoolean iscancelled;
    public ObservableBoolean showBill;
    public ObservableBoolean isAddnlChargeAvailable;
    public ObservableBoolean islater;
    public ObservableField<LatLng> pickupLatlng = new ObservableField<>();
    public ObservableField<LatLng> dropLatlng = new ObservableField<>();
    public ObservableField<String> driverName,
            txt_Distance, txt_Time, txt_pick, txt_drop, txt_basePrice, txt_distanceCost,
            txt_price_perDistance, txt_TimeCost, txt_price_pertime, txt_ExtraPersonCost, txt_refferalBonus,
            txt_promoBonus, txt_waitingCost, txt_taxiCost, txt_total, txt_paymentMode, txt_walletAmount, bitmap_profilePic, carurl, typename, DatenTime,
            requedid, totalAdditionalCharge, cancellation_fees, zone_fees, custom_captain_fee, total_trip_cost, service_fee, car_number, duration;
    public ObservableInt driverRating = new ObservableInt();
    public ObservableField<String> ratingText = new ObservableField<>();
    public static GoogleMap googleMap;
    String currency;
    private String requestID;
    private boolean isAlreadyDrawer = false;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("EE, MMM dd, hh:mm aa", Locale.US);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);
    public ObservableBoolean isDisputeAvailable = new ObservableBoolean(false);
    public ObservableBoolean cancelFee = new ObservableBoolean(true);
    public ObservableBoolean zoneFee = new ObservableBoolean(true);
    public ObservableBoolean customCaptainShown = new ObservableBoolean(false);
    public ObservableBoolean driverNotAvail = new ObservableBoolean(false);
    public ObservableBoolean iscancelShown = new ObservableBoolean(false);

    @Inject
    public HistoryDetViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                               SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        /*DataBindingUtil.setDefaultComponent(new MyComponent(this));*/
        driverName = new ObservableField<>();
        txt_Distance = new ObservableField<>();
        txt_Time = new ObservableField<>();
        txt_pick = new ObservableField<>();
        txt_drop = new ObservableField<>();
        txt_basePrice = new ObservableField<>();
        txt_walletAmount = new ObservableField<>();
        txt_distanceCost = new ObservableField<>();
        txt_price_perDistance = new ObservableField<>();
        txt_TimeCost = new ObservableField<>();
        txt_price_pertime = new ObservableField<>();
        txt_ExtraPersonCost = new ObservableField<>();
        txt_refferalBonus = new ObservableField<>();
        txt_promoBonus = new ObservableField<>();
        txt_waitingCost = new ObservableField<>();
        txt_taxiCost = new ObservableField<>();
        cancellation_fees = new ObservableField<>();
        zone_fees = new ObservableField<>();
        DatenTime = new ObservableField<>();
        requedid = new ObservableField<>();
        typename = new ObservableField<>();
        carurl = new ObservableField<>();
        txt_total = new ObservableField<>();
        txt_paymentMode = new ObservableField<>();
        totalAdditionalCharge = new ObservableField<>();
        bitmap_profilePic = new ObservableField<>();
        isCompleted = new ObservableBoolean(false);
        iscancelled = new ObservableBoolean(false);
        showBill = new ObservableBoolean(false);
        islater = new ObservableBoolean(false);
        custom_captain_fee = new ObservableField<>();
        isAddnlChargeAvailable = new ObservableBoolean(false);
        total_trip_cost = new ObservableField<>();
        service_fee = new ObservableField<>();
        car_number = new ObservableField<>();
        duration = new ObservableField<>("");
    }

    /** custom {@link BindingAdapter} function to set user's profile picture **/
    @BindingAdapter({"bind:userPic"})
    public static void userPicture(ImageView view, String profilePicURL) {
        if (!CommonUtils.IsEmpty(profilePicURL))
            Glide.with(view.getContext()).load(profilePicURL).
                    apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                            placeholder(R.drawable.ic_user)).into(view);
    }

    /** custom {@link BindingAdapter} function to set car icon **/
    @BindingAdapter("imagecaricon")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.fitCenterTransform()
                .error(R.drawable.ic_car)
                .placeholder(R.drawable.ic_car))
                .into(imageView);
    }

    /** called when need to cancel a scheduled trip **/
    public void onClickCancelSchedule(View view) {
//        map.clear();
//        if (getmNavigator().isNetworkConnected()) {
//            setIsLoading(true);
//            map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
//            map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
//            map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
//            map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
//            map.put(Constants.NetworkParameters.request_id, requestID);
//            SchedulecancelNetwork();
//        } else {
//            getmNavigator().showNetworkMessage();
//        }

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            map.clear();
            map.put(Constants.NetworkParameters.request_id, requestID);
            map.put(Constants.NetworkParameters.custom_reason, "User Cancelled");
            RequestCancelNetwork();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /** set trip data to variables **/
    public void initializeValues(TaxiRequestModel.ResultData model) {
        if (model != null) {
            if (model.driverDetail != null && model.driverDetail.driverData != null) {
                driverNotAvail.set(true);
                bitmap_profilePic.set(model.driverDetail.driverData.profilePicture);
                driverName.set(model.driverDetail.driverData.name);
                driverRating.set((int) model.driverDetail.driverData.rating);
                ratingText.set("" + model.driverDetail.driverData.rating);
                car_number.set(model.driverDetail.driverData.carMakeName + " - " + model.driverDetail.driverData.carModelName);
            } else driverNotAvail.set(false);

            iscancelShown.set(model.isDriverStarted == 1);

            pickupLatlng.set(new LatLng(model.pickLat, model.pickLng));
            if (model.dropLat != null)
                dropLatlng.set(new LatLng(model.dropLat, model.dropLng));
            isCompleted.set(model.isCompleted == 1);
            iscancelled.set(model.isCancelled == 1);
            islater.set(model.isLater == 1);
            isShare.set(model.isShare != null && model.isShare == 1);
            if (model.driverDetail != null && model.driverDetail.driverData != null) {
                if (!CommonUtils.IsEmpty(model.driverDetail.driverData.vehicleTypeImage))
                    carurl.set(model.driverDetail.driverData.vehicleTypeImage);

                if (!CommonUtils.IsEmpty(model.driverDetail.driverData.vehicleTypeName))
                    typename.set(model.driverDetail.driverData.vehicleTypeName);
            }

            if (islater.get())
                driverName.set(getmNavigator().getBaseAct().getTranslatedString(R.string.Txt_RideScheduled));
//            isDisputeAvailable.set(!iscancelled.get() && isCompleted.get() && model.enable_dispute_button);
            isDisputeAvailable.set(false);
//            txt_Distance.set(model.distance + " " + context.getString(R.string.text_km));
            txt_Time.set(model.totalTime + " " + getmNavigator().getBaseAct().getTranslatedString(R.string.txt_min));
            txt_pick.set(model.pickAddress);
            txt_drop.set(model.dropAddress);
            if (model.billDetail != null && model.billDetail.billData != null && isCompleted.get()) {
//                showBill.set(model.billDetail.billData.show_bill == 1);
                showBill.set(true);
                currency = model.billDetail.billData.requestedCurrencySymbol;
//                txt_basePrice.set(currency + " " + CommonUtils.doubleDecimalFromat(isShare.get() ? model.billDetail.billData.ride_fare : model.bill.base_price));
                txt_basePrice.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.basePrice));

                if (model.billDetail.billData.cancellationFee != null && model.billDetail.billData.cancellationFee != 0.0)
                    cancellation_fees.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.cancellationFee));
                else cancelFee.set(false);

//                if (model.bill.drop_out_of_zone_fee != null && model.bill.drop_out_of_zone_fee != 0.0)
//                    zone_fees.set(currency + " " + CommonUtils.doubleDecimalFromat(model.bill.drop_out_of_zone_fee));
//                else zoneFee.set(false);
                zoneFee.set(false);

                txt_distanceCost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.distancePrice));
//                txt_price_perDistance.set(currency + CommonUtils.doubleDecimalFromat(model.bill.price_per_distance) + " / " + context.getString(R.string.text_km));
                txt_price_perDistance.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.pricePerDistance) + " / " +
                        (model.unit != null ? model.unit : getmNavigator().getBaseAct().getTranslatedString(R.string.text_km)));
                txt_Distance.set(CommonUtils.doubleDecimalFromat(model.totalDistance) + " " + (model.unit != null ? model.unit : getmNavigator().getBaseAct().getTranslatedString(R.string.text_km)));
                txt_TimeCost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.timePrice));
                txt_price_pertime.set(currency + " " + model.billDetail.billData.pricePerTime + " / " + getmNavigator().getBaseAct().getTranslatedString(R.string.txt_min));
                txt_ExtraPersonCost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.extraPersonCharge));
                txt_refferalBonus.set("-" + currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.referral_amount));
                txt_taxiCost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.serviceTax));
                txt_promoBonus.set("-" + currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.promoDiscount));
                txt_waitingCost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.waitingCharge));
                txt_total.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.totalAmount));
//                totalAdditionalCharge.set(currency + " " + CommonUtils.doubleDecimalFromat(model.bill.totalAdditionalCharge == null ? 0 : model.bill.totalAdditionalCharge));

                txt_paymentMode.set(getmNavigator().getBaseAct().getTranslatedString(model.paymentOpt.equals("0") ? R.string.txt_card : model.paymentOpt.equals("1") ? R.string.txt_cash : R.string.txt_wallet));
//                txt_walletAmount.set(currency + " " + ((model.paymentOpt.equals("2") || model.paymentOpt.equals("3")) ? CommonUtils.doubleDecimalFromat(model.billDetail.billData.wallet_amount) : CommonUtils.doubleDecimalFromat(model.billDetail.billData.totalAmount)));

//                customCaptainShown.set(model.bill.custom_select_driver_fee != 0);
//                custom_captain_fee.set(currency + " " + CommonUtils.doubleDecimalFromat(model.bill.custom_select_driver_fee));
                customCaptainShown.set(false);
                total_trip_cost.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.totalAmount - model.billDetail.billData.cancellationFee) + " +");
                service_fee.set(currency + " " + CommonUtils.doubleDecimalFromat(model.billDetail.billData.adminCommision));

//                if (model.bill.additionalCharge != null && getmNavigator() != null) {
//                    getmNavigator().setRecyclerAdapter(model.bill.currency, model.bill.additionalCharge);
//                    isAddnlChargeAvailable.set(model.bill.additionalCharge.size() > 0);
//                } else {
//                    isAddnlChargeAvailable.set(false);
//                }
                isAddnlChargeAvailable.set(false);
            }
        }
//        try {
//            if (!CommonUtils.IsEmpty(model.tripStartTime)) {
//                DatenTime.set(TargetFormatter.format(realformatter.parse(model.tripStartTime)));
//            }
//            duration.set(getmNavigator().getBaseAct().getTranslatedString(R.string.txt_trip_time_text) +
//                    ": " + CommonUtils.doubleDecimalFromatFloat(model.totalTime) + " " +
//                    getmNavigator().getBaseAct().getTranslatedString(R.string.txt_min));
//
//            if (!CommonUtils.IsEmpty(model.requestNumber))
//                requedid.set(translationModel.txt_rideID + " " + model.requestNumber);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        DatenTime.set(model.tripStartTime);
        duration.set(getmNavigator().getBaseAct().getTranslatedString(R.string.txt_trip_time_text) +
                ": " + CommonUtils.doubleDecimalFromatFloat(model.totalTime) + " " +
                getmNavigator().getBaseAct().getTranslatedString(R.string.txt_min));
        if (!CommonUtils.IsEmpty(model.requestNumber))
            requedid.set(translationModel.txt_rideID + " " + model.requestNumber);

        if (!isAlreadyDrawer)
            if (pickupLatlng.get() != null && dropLatlng.get() != null)
                drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());
            else if (pickupLatlng.get() != null)
                markerBuildBound(pickupLatlng.get());
    }

    /** draw polyline between pickup and drop locations **/
    public void drawPathPickupDrop(final LatLng pickupLatLng, final LatLng dropLatLng) {
        Log.d("keys", "END:" + pickupLatLng + "DROP:" + dropLatLng);
        if (googleMap != null) {

            googleMap.addMarker(new MarkerOptions()
                    .position(pickupLatLng)
                    .anchor(0.5f, 0.5f)
                    .icon(getmNavigator().getMarkerIcon(R.drawable.ic_pick_pin)));
            if (dropLatLng != null)
                googleMap.addMarker(new MarkerOptions()
                        .position(dropLatLng)
                        .anchor(0.5f, 0.5f)
                        .icon(getmNavigator().getMarkerIcon(R.drawable.ic_drop_pin)));
        }

        buildLatlongBound(pickupLatLng, dropLatLng);
        isAlreadyDrawer = true;
    }

    /** creates latLnBounds to fit map zoom with pickup and drop locations **/
    public void buildLatlongBound(LatLng pickup, LatLng drop) {
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        latlngBuilder.include(pickup);
        if (drop != null)
            latlngBuilder.include(drop);
        if (googleMap != null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 80));
    }

    /** add pickup marker and moves the camera to it **/
    public void markerBuildBound(LatLng pickupLocation) {
        if (googleMap != null) {

            googleMap.addMarker(new MarkerOptions()
                    .position(pickupLocation)
                    .anchor(0.5f, 0.5f)
                    .icon(getmNavigator().getMarkerIcon(R.drawable.ic_pickup)));

            LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
            if (pickupLocation != null)
                latlngBuilder.include(pickupLocation);
            if (googleMap != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 17));
        }
    }

    /** called when API call is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.success) {
            if (response.message.equalsIgnoreCase("single_request_history")) {
                if (response.data != null) {
                    TaxiRequestModel.ResultData requestModel = (TaxiRequestModel.ResultData) CommonUtils.StringToObject(CommonUtils.ObjectToString(response.data), TaxiRequestModel.ResultData.class);
                    initializeValues(requestModel);
                }
            } else if (response.message.equalsIgnoreCase("request_cancelled_by_user")) {
                getmNavigator().setResultnFinish();
            }
        } else
            getmNavigator().showMessage(response.errorMessage);
    }

    /** called when API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        e.printStackTrace();
        getmNavigator().showMessage(e.getMessage());
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    /** call API to get details of the ride **/
    public void getRequestDetails(String requestID) {
        this.requestID = requestID;
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getSingleHistoryNetwork(requestID);
        }
    }

    /** add client_id, client_token to {@link HashMap} used for API calling **/
    @Override
    public HashMap<String, String> getMap() {
        return map;
    }

    /** this callback is called when {@link GoogleMap} components are loaded and ready for use **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        HistoryDetViewModel.googleMap = googleMap;
        changeMapStyle();
        //  googleMap.getUiSettings().setAllGesturesEnabled(false);
        if (!isAlreadyDrawer)
            if (pickupLatlng.get() != null/* && dropLatlng.get() != null*/)
                drawPathPickupDrop(pickupLatlng.get(), dropLatlng.get());

        if (googleMap != null)
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
    }

    /** changes {@link GoogleMap} style and appearance using json style from resources **/
    private void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /** called when dispute button is clicked.used to raise dispute on any raid **/
    public void makeDispute(View view) {
        getmNavigator().openDisputeDialog(sharedPrefence.Getvalue(SharedPrefence.ID),
                sharedPrefence.Getvalue(SharedPrefence.TOKEN), requestID);
    }

}
