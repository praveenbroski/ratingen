package taxi.ratingen.ui.drawerscreen.promoscrn;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.text.Editable;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 1/3/18.
 */

public class PromoViewModel extends BaseNetwork<BaseResponse, PromoNavigator> {

    public ObservableBoolean Enable;

    public ObservableField<String> promocode, isRide;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    HashMap<String, String> hashMap;

    public ObservableField<String> requestid;
    int typeId = -1;


    @Inject
    public PromoViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                          SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        Enable = new ObservableBoolean(false);
        promocode = new ObservableField<>();
        requestid = new ObservableField<>();
        isRide = new ObservableField<>("");
    }

    /** Callback for successful API calls
     * @param taskId Id of the API call
     * @param response response model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("ride promocode available")) {
            String bookedID = response.promoCodeQueue.getPromoBookedId();
            getmNavigator().setResult(bookedID);
        } else {
            getmNavigator().showMessage(response.successMessage);
            getmNavigator().setResult();
        }

    }

    /** Callback for failed API calls
     * @param taskId Id of the API call
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        getmNavigator().showMessage(e);
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    /** Adds client_id & client_token to {@link HashMap} used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** {@link android.text.TextWatcher} for promo code field. In this case it was used for enable/disable apply button **/
    public void onPromoEditTextchanged(Editable e) {
        if (e.toString().isEmpty() || e.length() == 0) {
            Enable.set(false);
        } else if (e.length() > 0 || !e.toString().isEmpty()) {
            Enable.set(true);
        }
    }

    /** Click listener for promo code apply button. Calls promo code API **/
    public void OnclickApplyPromo(View view) {
        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(promocode.get())) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Promocode));
            } else {
                if (isRide.get().equalsIgnoreCase("0")) {
                    setIsLoading(true);
                    hashMap.clear();
                    hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                    hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                    hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                    hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                    hashMap.put(Constants.NetworkParameters.request_id, requestid.get());
                    hashMap.put(Constants.NetworkParameters.promo_code, promocode.get());
                    PromoCodeNetworkcall();
                } else {
                    setIsLoading(true);
                    hashMap.clear();
                    hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                    hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                    hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                    hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                    hashMap.put(Constants.NetworkParameters.type, "" + typeId);
                    hashMap.put(Constants.NetworkParameters.promo_code, promocode.get());
                    RidePromocode(hashMap);
                }
            }
        } else {
            getmNavigator().showNetworkMessage();
        }


    }

    /** Empties promo code field when called **/
    public void OnclickPromoclear(View view) {
        promocode.set("");
    }

   /* public static void Isvisibility(View view, boolean enable){
        if(!enable)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);



    }*/

}

