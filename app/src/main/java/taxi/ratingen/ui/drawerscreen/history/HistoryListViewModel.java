package taxi.ratingen.ui.drawerscreen.history;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        setIsLoading(false);
        if (response.message.equalsIgnoreCase("request_history_list")) {
            if (response.data != null) {
                String historyStr = CommonUtils.ObjectToString(response.data);
                List<TaxiRequestModel.ResultData> historyList = CommonUtils.stringToArray(historyStr, TaxiRequestModel.ResultData[].class);
                if (historyList.size() > 0) {
                    isdata.set(true);
                    if (pageno > 1) {
                        getmNavigator().Dostaff();
                    }
                    getmNavigator().addHistoryItem(response, historyList);
                } else {
                    if (response.meta != null && response.meta.pagination != null) {
                        if (response.meta.pagination.current_page >= response.meta.pagination.total_pages) {
                            getmNavigator().MentionLastPage();
                        }
                    }
                }
            }
        } else {
            getmNavigator().showMessage(response.message);
        }
    }

    /** called when the API call fails ***/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
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
    public void fetchData(int currentPage, boolean showLoader) {
        if (getmNavigator().isNetworkConnected()) {
            pageno = currentPage;
            setIsLoading(showLoader);
            if (scheduledClick.get()) {
                GetHistoryNetworkCallLater(pageno + "");
            } else if (cancelledClick.get()) {
                GetHistoryNetworkCallCancelled(pageno + "");
            } else if (completedClick.get()) {
                GetHistoryNetworkCallCompleted(pageno + "");
            }
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void getNextPage(String nextPageURL) {
        if (getmNavigator().isNetworkConnected()) {
            GetHistoryNextPage(nextPageURL);
        } else
            getmNavigator().showNetworkMessage();
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
