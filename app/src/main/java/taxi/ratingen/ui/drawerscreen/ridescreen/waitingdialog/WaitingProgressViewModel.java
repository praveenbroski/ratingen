package taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog;

import androidx.databinding.ObservableBoolean;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 12/21/17.
 */

public class WaitingProgressViewModel extends BaseNetwork<BaseResponse, WaitProgressNavigator> {
    HashMap<String,String> hashMap;
    SharedPrefence sharedPrefence;
    ObservableBoolean IsRequestcompleted;
    View view;
    public WaitingProgressViewModel(GitHubService gitHubService,
                                    HashMap<String, String> hashMap,
                                    SharedPrefence sharedPrefence, Gson gson){
        super(gitHubService,sharedPrefence,gson);
        this.hashMap=hashMap;
        this.sharedPrefence=sharedPrefence;
        IsRequestcompleted=new ObservableBoolean(false);
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response Response model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        if(response.successMessage.equalsIgnoreCase("Trip cancelled")){
//            getmNavigator().showSnackBar(view, response.successMessage);
            getmNavigator().showMessage(response.successMessage);
            getmNavigator().dismissDialog();
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
            getmNavigator().dismissDialog();
       getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    /** Returns {@link HashMap} with query parameters used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }
    /** Calls cancel request API to cancel the booking **/
    public void OnclickcancelRequest(View view){
        this.view=view;

    }


    public void cancelTrip()
    {
        if(getmNavigator().isNetworkConnected()) {
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.request_id, getmNavigator().getRequestid());
            hashMap.put(Constants.NetworkParameters.reason,"0");
            hashMap.put(Constants.NetworkParameters.cancel_other_reason,"User Cancelled");
            RequestcancelNetwork();
        }else {
            getmNavigator().showNetworkMessage();
        }
    }


}
