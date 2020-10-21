package taxi.ratingen.ui.drawerscreen.support;

import androidx.databinding.ObservableField;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 11/3/17.
 */

public class SupportFragViewModel extends BaseNetwork<User, SupportFragNavigator> {

    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public ObservableField<String> comments = new ObservableField<>("");

    public SupportFragViewModel(GitHubService gitHubService,
                                SharedPrefence sharedPrefence,
                                HashMap<String, String> hashMap, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response {@link User} response data model **/
    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("email_send_to_support_email")) {
            comments.set("");
            if(getmNavigator().getAttachedContext()!=null)
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.txt_support_email_send));
//            getmNavigator().showMessage(response.successMessage);
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_already_login));
            }
        } else
            getmNavigator().showMessage(e);
    }

    /** Returns {@link HashMap} with query parameters used in API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** Called when submit button is clicked. It calls send feedback API to submit the support query of the user. **/
    public void onclickSubmit(View view) {
        if (CommonUtils.IsEmpty(comments.get()))
            return;
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.message, comments.get());
            sendSupportFeedback();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void GetRefferalDetails() {
        if (getmNavigator().isNetworkConnected()) {

            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            GetRefferalDetailsNetwork();
        } else {
            getmNavigator().showNetworkMessage();
        }

    }
}
