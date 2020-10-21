package taxi.ratingen.ui.drawerscreen.ridescreen.etabasefare;

import android.content.Context;
import androidx.databinding.ObservableField;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/19/17.
 */

public class EtachildBaseViewModel extends BaseNetwork<BaseResponse, EtachildBaseNavigator> {

    public ObservableField<String> BaseFare, RatePerKm, RatePerKmTitle, Ridetimecharge, currency, v_type;

    public EtachildBaseViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);

        BaseFare = new ObservableField<>();
        RatePerKm = new ObservableField<>();
        currency = new ObservableField<>();
        Ridetimecharge = new ObservableField<>();
        RatePerKmTitle = new ObservableField<>();
        v_type = new ObservableField<>();
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

    /** Set API values to variable
     * @param mParam1 Response model **/
    public void setValues(BaseResponse mParam1, Context cn) {
        if (mParam1 != null) {

            if (!CommonUtils.IsEmpty(mParam1.currency))
                currency.set(mParam1.currency);

            if (!CommonUtils.IsEmpty(mParam1.type_name))
                v_type.set(mParam1.type_name);

            if (mParam1.baseprice != null && mParam1.baseprice != 0.0)
                BaseFare.set(String.valueOf(mParam1.baseprice));
            if (mParam1.price_per_distance != null /*&& mParam1.price_per_distance != 0.0*/)
                RatePerKm.set(String.valueOf(mParam1.price_per_distance));

            if (mParam1.unit_in_words != null)
                RatePerKmTitle.set(getmNavigator().getBaseAct().getTranslatedString(R.string.txt_RateperKm) + " " + mParam1.unit_in_words);
            if (mParam1.price_per_time != null /*&& mParam1.price_per_time != 0.0*/)
                Ridetimecharge.set(String.valueOf(mParam1.price_per_time));

//            if (mParam1.price_per_distance != null && mParam1.price_per_distance != 0.0)
//                RatePerKm.set(String.valueOf(mParam1.price_per_distance)); ;

            if (mParam1.price_per_time != null && mParam1.price_per_time != 0.0)
                Ridetimecharge.set(String.valueOf(mParam1.price_per_time));


        }
    }

    /** Click listener for dismiss button **/
    public void onclickBaseGotIt(View view) {
        getmNavigator().dismissDialog();
    }

    /** Click listener for info button **/
    public void onclickFareDetails(View view) {
        getmNavigator().FareonClicked();
    }

}
