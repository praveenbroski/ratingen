package taxi.ratingen.ui.drawerscreen.ridescreen.eta;

import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/7/17.
 */

public class EtaViewModel extends BaseNetwork<User, EtaNavigator> {

    public String Totalfare, Taxesamt, FaredetailsAmt, BaseFare, RatePerKm, Ridetimecharge,Currency;

    public EtaViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService,sharedPrefence,gson);
    }

    public void onclickFareInfo(View view) {

    }

    /** Click listener for Got it button to dismiss the dialog **/
    public void onclickFareGotIt(View view) {
        getmNavigator().dismissDialog();
    }

    public void onclickBaseGotIt(View view) {

    }

    public void onclickFareDetails(View view) {

    }

    @Override
    public void onSuccessfulApi(long taskId, User response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    /** Set values from API to variables
     * @param mParam1 Response model **/
    public void setValues(BaseResponse mParam1) {
        if (mParam1 != null) {

            if(Currency!=null)
                Currency=mParam1.currency;


            if (mParam1.ride_fare != null && mParam1.ride_fare != 0.0)
                FaredetailsAmt = String.valueOf(mParam1.ride_fare);

            if (mParam1.tax_amount != null && mParam1.tax_amount != 0.0)
                Taxesamt = String.valueOf(mParam1.tax_amount);

            if (mParam1.total != null && mParam1.total != 0.0)
                Totalfare = String.valueOf(mParam1.total);

            if (mParam1.baseprice != null && mParam1.baseprice != 0.0)
                BaseFare = String.valueOf(mParam1.baseprice);


            if (mParam1.price_per_distance != null && mParam1.price_per_distance != 0.0)
                RatePerKm = String.valueOf(mParam1.price_per_distance);

            if (mParam1.price_per_time != null && mParam1.price_per_time != 0.0)
                Ridetimecharge = String.valueOf(mParam1.price_per_time);
        }
    }
}



