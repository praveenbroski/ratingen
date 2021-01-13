package taxi.ratingen.ui.drawerscreen.ridescreen.etafarechild;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.view.View;

import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.utilz.CommonUtils;
import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/18/17.
 */

public class EtachildFareViewModel extends BaseNetwork<BaseResponse, EtachildFareNavigator> {

    public ObservableField<String> vType, Totalfare, Taxesamt, FaredetailsAmt, BaseFare, RatePerKm, Ridetimecharge, waitingTimeCharge,
            currency, out_of_zoneAmnt, promo_applied, distancePrice, rideTimeCost;
    public ObservableBoolean zoneEnable, promoAvail;

    public EtachildFareViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        vType = new ObservableField<>("");
        Totalfare = new ObservableField<>();
        Taxesamt = new ObservableField<>("");
        FaredetailsAmt = new ObservableField<>();
        BaseFare = new ObservableField<>();
        RatePerKm = new ObservableField<>();
        currency = new ObservableField<>();
        Ridetimecharge = new ObservableField<>();
        waitingTimeCharge = new ObservableField<>();
        out_of_zoneAmnt = new ObservableField<>();
        zoneEnable = new ObservableBoolean(false);
        promoAvail = new ObservableBoolean(false);
        promo_applied = new ObservableField<>();
        distancePrice = new ObservableField<>();
        rideTimeCost = new ObservableField<>();
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

    /** Set data from API to variables
     * @param mParam1 Response model **/
    public void setValues(BaseResponse mParam1) {
        if (mParam1 != null) {
            if (mParam1.currency != null)
                currency.set(mParam1.currency);

            if (mParam1.ride_fare != null && mParam1.ride_fare != 0.0)
                FaredetailsAmt.set(CommonUtils.doubleDecimalFromat(Double.valueOf(mParam1.ride_fare)));

            if (mParam1.drop_out_of_zone_fee != null && !mParam1.drop_out_of_zone_fee.equalsIgnoreCase("0")) {
                zoneEnable.set(true);
                out_of_zoneAmnt.set(CommonUtils.doubleDecimalFromat(Double.valueOf(mParam1.drop_out_of_zone_fee)));
            }

            if (mParam1.promo_available == 1) {
                promoAvail.set(true);
                double bonusAmnt = mParam1.total - mParam1.total_after_promo;
                promo_applied.set(CommonUtils.doubleDecimalFromat(bonusAmnt));
                Totalfare.set(CommonUtils.doubleDecimalFromat(mParam1.total_after_promo));
            } else {
                if (mParam1.total != null && mParam1.total != 0.0)
                    Totalfare.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.total))));
            }

            if (mParam1.tax_amount != null)
                Taxesamt.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.tax_amount))));


            if (mParam1.baseprice != null && mParam1.baseprice != 0.0)
                BaseFare.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.baseprice))));


            if (mParam1.price_per_distance != null && mParam1.price_per_distance != 0.0)
                RatePerKm.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.price_per_distance))));

            if (mParam1.distance_price != null && mParam1.distance_price != 0.0)
                distancePrice.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.distance_price))));

            if (mParam1.price_per_time != null && mParam1.price_per_time != 0.0)
                Ridetimecharge.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.price_per_time))));

            if (mParam1.time_price != null && mParam1.time_price != 0.0)
                rideTimeCost.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.time_price))));

            if (mParam1.waiting_charge != null && mParam1.waiting_charge != 0.0)
                waitingTimeCharge.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.waiting_charge))));
            else
                waitingTimeCharge.set("0");

            if (mParam1.tax_amount != null && mParam1.tax_amount != 0.0)
                Taxesamt.set(CommonUtils.doubleDecimalFromat(Double.valueOf((mParam1.tax_amount))));
        }
    }

    public void setTypeValues(Type mParam2, Route mParam3) {
        if (mParam2 != null) {
            if (mParam2.currency != null)
                currency.set(mParam2.currency);
            if (mParam2.name != null)
                vType.set(mParam2.name);
            if (mParam2.etaModel != null) {
                if (mParam2.etaModel.currency_symbol != null)
                    currency.set(mParam2.etaModel.currency_symbol);
                BaseFare.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.base_price));
                RatePerKm.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.price_per_distance));
                distancePrice.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.distance_price));
                Ridetimecharge.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.price_per_time));
                rideTimeCost.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.time_price));
                waitingTimeCharge.set("");
                FaredetailsAmt.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.ride_fare));
                Taxesamt.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.tax_amount));
                Totalfare.set(CommonUtils.doubleDecimalFromat(mParam2.etaModel.total));
            }
        }
    }

    /** Click listener for info button **/
    public void onclickFareInfo(View view) {
        getmNavigator().FareInfoClicked();
    }

    /** Click listener for got it button to dismiss dialog **/
    public void onclickFareGotIt(View view) {
        getmNavigator().dismissDialog();
    }

}
