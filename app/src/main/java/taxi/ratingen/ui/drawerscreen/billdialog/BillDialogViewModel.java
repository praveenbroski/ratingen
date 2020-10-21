package taxi.ratingen.ui.drawerscreen.billdialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class BillDialogViewModel extends BaseNetwork<BaseResponse, BillDialogNavigator> {
    public String time, distance, baseprice, distanceCost, distanceperunit, timeCost, timecostperunit, pickup, drop,
            waitingPrice, serviceTAx, refferalBonus, promoBonus, walletAmount, total, txt_Additional_Charge, total_trip_cost,
            cancellation_fees, zone_fees, custom_captain_fee, profilePic, rating;
    public String driverName, vehType, reqId, dateTime, duration;
    public String currency;
    public BaseActivity context;
    public boolean isWalletTrip, isShare;
    public ObservableBoolean isAddnlChargeAvailable = new ObservableBoolean(false);
    public ObservableBoolean cancelFee = new ObservableBoolean(true);
    public ObservableBoolean zoneFee = new ObservableBoolean(true);
    public ObservableBoolean customCaptainShown = new ObservableBoolean(false);

    public BillDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
    }


    /**
     * sets the bill values
     **/
    public void setBillDetails(Request request) {
        context = getmNavigator().getAttachedContext();
        currency = context.getTranslatedString(R.string.Rs);

        if (request != null) {
            time = request.time + " " + context.getTranslatedString(R.string.txt_min);
            isShare = (request.is_share == 1);
            distance = CommonUtils.doubleDecimalFromat(Double.valueOf(request.distance)) + " " + (request.bill.unit_in_words != null ? request.bill.unit_in_words : context.getTranslatedString(R.string.text_km));
            if (request.bill != null) {
                if (!CommonUtils.IsEmpty(request.bill.currency))
                    currency = request.bill.currency;

                if (request.bill.cancellation_fee != null && request.bill.cancellation_fee != 0.0)
                    cancellation_fees = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.cancellation_fee);
                else cancelFee.set(false);

                if (request.bill.drop_out_of_zone_fee != null && request.bill.drop_out_of_zone_fee != 0.0)
                    zone_fees = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.drop_out_of_zone_fee);
                else zoneFee.set(false);

                baseprice = currency + " " + CommonUtils.doubleDecimalFromat(isShare ? request.bill.ride_fare : request.bill.base_price);
                distanceCost = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.distance_price);
//                distanceperunit = currency + CommonUtils.doubleDecimalFromat(request.bill.price_per_distance) + " / " + context.getTranslatedString(R.string.text_km);
                distanceperunit = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.price_per_distance) + " / " + (request.bill.unit_in_words != null ? request.bill.unit_in_words : context.getTranslatedString(R.string.text_km));
                distance = CommonUtils.doubleDecimalFromat(Double.valueOf(request.distance)) + " " + (request.bill.unit_in_words != null ? request.bill.unit_in_words : context.getTranslatedString(R.string.text_km));
                timeCost = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.time_price);
                timecostperunit = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.price_per_time) + " / " + context.getTranslatedString(R.string.txt_min);
                waitingPrice = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.waiting_price);
                serviceTAx = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.service_tax);
                refferalBonus = "-" + currency + " " + CommonUtils.doubleDecimalFromat(request.bill.referral_amount);
                promoBonus = "-" + currency + " " + CommonUtils.doubleDecimalFromat(request.bill.promo_amount);
                total = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.total);
                isWalletTrip = request.paymentOpt == 2 || request.paymentOpt == 3;
                txt_Additional_Charge = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.totalAdditionalCharge == null ? 0 : request.bill.totalAdditionalCharge);
                walletAmount = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.wallet_amount);
                if (request.bill.cancellation_fee != null)
                    total_trip_cost = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.total - request.bill.cancellation_fee) + " +";

                customCaptainShown.set(request.bill.custom_select_driver_fee != 0);
                custom_captain_fee = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.custom_select_driver_fee);

                pickup = request.pickLocation;
                drop = request.dropLocation;
                driverName = request.driver.firstname + " " + request.driver.lastname;
                vehType = request.driver.type_name + " ( " + request.driver.carnumber + " )";
                reqId = request.request_id;
                dateTime = request.tripStartTime;
                duration = getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_trip_time_text) +
                        ": " + request.time + " " +
                        getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_min);
                profilePic = Constants.URL.BaseURL + Constants.URL.DRIVER_PROFILE_PIC + request.driver.profilePic;
                rating = request.driver.review + "";
            }
        }

    }

    /** Custom {@link BindingAdapter} method to set image to {@link ImageView} from {@link java.net.URL} **/
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().error(R.drawable.invoice_profile).placeholder(R.drawable.invoice_profile)).into(imageView);
    }

    /**
     * called when confirm button is clicked
     **/
    public void onConfirm(View v) {
        getmNavigator().dismissDialog();
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

}
