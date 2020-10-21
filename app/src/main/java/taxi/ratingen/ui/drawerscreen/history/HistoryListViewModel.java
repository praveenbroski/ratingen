package taxi.ratingen.ui.drawerscreen.history;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 1/4/18.
 */

public class HistoryListViewModel extends BaseNetwork<BaseResponse, HistoryListNavigator> {

    HashMap<String, String> hashMap;
    private int pageno;
    public ObservableBoolean isdata;
    SharedPrefence sharedPrefence;

    public ObservableField<Boolean> scheduledClick = new ObservableField<>(true);
    public ObservableField<Boolean> completedClick = new ObservableField<>(false);
    public ObservableField<Boolean> cancelledClick = new ObservableField<>(false);

    public HistoryListViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        isdata = new ObservableBoolean();
    }

    /** called when the API call is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        if (response.getHistory() != null && response.getHistory().size() != 0) {
            isdata.set(true);
            if (pageno > 1) {
                getmNavigator().Dostaff();
            }
            getmNavigator().addItem(response.getHistory());
        } else {
            if (response.successMessage.equalsIgnoreCase("user_history_not_found")) {
                getmNavigator().MentionLastPage();
            } else
                getmNavigator().showMessage(response.successMessage);

        }
    }

    /** called when the API call fails ***/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_already_login));
            }
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        } else
            getmNavigator().showMessage(e);
    }

    /** adds client_id & client_token to the {@link HashMap} used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** gets user's ride history data from server via GetHistoryNetworkCall() API **/
    public void fetchData(int currentPage) {
        if (getmNavigator().isNetworkConnected()) {
            pageno = currentPage;
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.page, "" + pageno);
            if (scheduledClick.get()) {
                hashMap.put(Constants.NetworkParameters.later, "1");
            } else if (cancelledClick.get()) {
                hashMap.put(Constants.NetworkParameters.cancelled, "1");
            }
            GetHistoryNetworkCall();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void scheduledClick(View v) {
        isdata.set(false);
        setClickedTab(true, false, false);
        getmNavigator().fetchHistoryList();
    }

    public void completedClick(View v) {
        isdata.set(false);
        setClickedTab(false, true, false);
        getmNavigator().fetchHistoryList();
    }

    public void cancelledClick(View v) {
        isdata.set(false);
        setClickedTab(false, false, true);
        getmNavigator().fetchHistoryList();
    }

    public void setClickedTab(boolean scheduled, boolean completed, boolean cancelled) {
        scheduledClick.set(scheduled);
        completedClick.set(completed);
        cancelledClick.set(cancelled);
    }

}
