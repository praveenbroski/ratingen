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
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class BillDialogViewModel extends BaseNetwork<BaseResponse, BillDialogNavigator> {
    public String time, distance, basePrice, distanceCost, distancePerUnit, timeCost, timeCostPerUnit, pickup, drop,
            waitingPrice, serviceTAx, referralBonus, promoBonus, walletAmount, total, txt_Additional_Charge, total_trip_cost,
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
    public void setBillDetails(TaxiRequestModel.ResultData request) {
        context = getmNavigator().getAttachedContext();
        currency = context.getTranslatedString(R.string.Rs);

        if (request != null) {
            time = request.totalTime + " " + context.getTranslatedString(R.string.txt_min);
            isShare = (request.isShare != null && request.isShare == 1);
            distance = CommonUtils.doubleDecimalFromat(request.totalDistance) + " " + (request.unit != null ? request.unit : context.getTranslatedString(R.string.text_km));
            if (request.billDetail.billData != null) {
                TaxiRequestModel.BillData bill = request.billDetail.billData;
                if (!CommonUtils.IsEmpty(bill.requestedCurrencySymbol))
                    currency = bill.requestedCurrencySymbol;

                if (bill.cancellationFee != null && bill.cancellationFee != 0.0)
                    cancellation_fees = currency + " " + CommonUtils.doubleDecimalFromat(bill.cancellationFee);
                else cancelFee.set(false);

//                if (request.bill.drop_out_of_zone_fee != null && request.bill.drop_out_of_zone_fee != 0.0)
//                    zone_fees = currency + " " + CommonUtils.doubleDecimalFromat(request.bill.drop_out_of_zone_fee);
//                else zoneFee.set(false);

                basePrice = currency + " " + CommonUtils.doubleDecimalFromat(isShare ? bill.basePrice : bill.basePrice);
                distanceCost = currency + " " + CommonUtils.doubleDecimalFromat(bill.distancePrice);
                distancePerUnit = currency + " " + CommonUtils.doubleDecimalFromat(bill.pricePerDistance) + " / " + (request.unit != null ? request.unit : context.getTranslatedString(R.string.text_km));
                distance = CommonUtils.doubleDecimalFromat(request.totalDistance) + " " + (request.unit != null ? request.unit : context.getTranslatedString(R.string.text_km));
                timeCost = currency + " " + CommonUtils.doubleDecimalFromat(bill.timePrice);
                timeCostPerUnit = currency + " " + CommonUtils.doubleDecimalFromat(bill.pricePerTime) + " / " + context.getTranslatedString(R.string.txt_min);
                waitingPrice = currency + " " + CommonUtils.doubleDecimalFromat(bill.waitingCharge);
                serviceTAx = currency + " " + CommonUtils.doubleDecimalFromat(bill.serviceTax);
                referralBonus = "-" + currency + " " + (bill.referral_amount != null ? CommonUtils.doubleDecimalFromat(bill.referral_amount) : "0.00");
                promoBonus = "-" + currency + " " + CommonUtils.doubleDecimalFromat(bill.promoDiscount);
                total = currency + " " + CommonUtils.doubleDecimalFromat(bill.totalAmount);
                isWalletTrip = request.paymentOpt.equals("2") || request.paymentOpt.equals("3");
                txt_Additional_Charge = currency + " " + CommonUtils.doubleDecimalFromat(bill.totalAdditionalCharge == null ? 0 : bill.totalAdditionalCharge);
//                walletAmount = currency + " " + CommonUtils.doubleDecimalFromat(bill.wallet_amount);
                if (bill.cancellationFee != null)
                    total_trip_cost = currency + " " + CommonUtils.doubleDecimalFromat(bill.totalAmount - bill.cancellationFee) + " +";

                customCaptainShown.set(bill.driverCommision != 0);
                custom_captain_fee = currency + " " + CommonUtils.doubleDecimalFromat(bill.driverCommision);

                pickup = request.pickAddress;
                drop = request.dropAddress;
                driverName = request.driverDetail.driverData.name;
                vehType = request.driverDetail.driverData.vehicleTypeName + " ( " + request.driverDetail.driverData.carNumber + " )";
                reqId = request.requestNumber;
                dateTime = request.tripStartTime;
                duration = getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_trip_time_text) +
                        ": " + request.totalTime + " " +
                        getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_min);
                profilePic = request.driverDetail.driverData.profilePicture;
                rating = request.driverDetail.driverData.rating + "";
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
