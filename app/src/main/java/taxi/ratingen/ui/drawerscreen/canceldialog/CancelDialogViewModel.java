package taxi.ratingen.ui.drawerscreen.canceldialog;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 1/29/18.
 */

public class CancelDialogViewModel extends BaseNetwork<BaseResponse, cancelDialogNavigator> {


    HashMap<String, String> hashMap;
    String reqid;
    boolean arrived;
    public ObservableBoolean iscancelenable;
    public ObservableField<String> otherCancelReason = new ObservableField<>();
    public ObservableBoolean otherCancelAvaialability = new ObservableBoolean(false);
    public SharedPrefence sharedPrefence;
    private String FavCheckedNickName = null;
    public List<BaseResponse.ReasonCancel> reasonCancelList = new ArrayList<>();

    public CancelDialogViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        iscancelenable = new ObservableBoolean(false);

        this.sharedPrefence = sharedPrefence;

    }

    /** called when the don't cancel button tapped **/
    public void onclickDontcancel(View view) {
        getmNavigator().dismissDialog();

    }

    /** calls RequestCancelReasonList API **/
    public void setvalues(String reqid, boolean arrived) {
        this.reqid = reqid;
        this.arrived = arrived;

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            RequestCancelReasonList(arrived ? "2" : "1");
        }
    }

    /** called after cancel reason selected and cancel button clicked **/
    public void onclickcancel(View view) {
        Log.d("kesy,", "Clicked");
        if (!CommonUtils.IsEmpty(getmNavigator().getSelectionPosition())) {
            if (getmNavigator().isNetworkConnected()) {
//                setIsLoading(true);
                hashMap.clear();
                hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                hashMap.put(Constants.NetworkParameters.request_id, "" + reqid);
                hashMap.put(Constants.NetworkParameters.reason, "" + getmNavigator().getSelectionPosition());
//                if (getmNavigator().getSelectionPosition().equalsIgnoreCase("0")) {
//                    hashMap.put(Constants.NetworkParameters.cancel_other_reason, otherCancelReason.get());
//                }
                if (getmNavigator().getSelectionPosition().equalsIgnoreCase("0")) {
                    if (otherCancelReason.get() != null && !otherCancelReason.get().isEmpty()) {
                        hashMap.put(Constants.NetworkParameters.cancel_other_reason, otherCancelReason.get());
                        RequestCancelNetwork();

                    } else getmNavigator().showMessage("Please enter the reason");
                } else RequestCancelNetwork();
            }
        } else getmNavigator().showMessage("Please choose the reason");

    }

    /** called when API result was successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.message.equalsIgnoreCase("cancellation_reasons_listed")) {
            if (response.success) {
                if (response.data != null) {
                    iscancelenable.set(true);
                    String reasonStr = CommonUtils.ObjectToString(response.data);
                    reasonCancelList = CommonUtils.stringToArray(reasonStr, BaseResponse.ReasonCancel[].class);
                    getmNavigator().setCancelList(reasonCancelList);
                }
            } else {
                getmNavigator().showMessage(response.errorMessage);
            }
        } else {
            if (response.success) {
                getmNavigator().DismisswithTarget();
            } else {
                getmNavigator().DismisswithTarget();
                getmNavigator().showMessage(response.errorMessage);
            }
        }
    }

    /** called when API request fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == 1001) {
            getmNavigator().showMessage(e);
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }else {
            getmNavigator().showMessage(e);
            getmNavigator().DismisswithTarget();
        }
    }

    /** adds client_id & client_token to {@link HashMap} **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

}
