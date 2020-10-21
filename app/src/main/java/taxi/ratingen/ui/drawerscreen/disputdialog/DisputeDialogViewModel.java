package taxi.ratingen.ui.drawerscreen.disputdialog;

import android.content.Context;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import android.view.View;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 12/21/17.
 */

public class DisputeDialogViewModel extends BaseNetwork<BaseResponse, DisputeDialogeNavigator> {

    GitHubService gitHubService;
    HashMap<String, String> hashMap;
    Context context;
    public ObservableList<ComplaintList> compLaintObservableList = new ObservableArrayList<>();
    public String SelectedId = null;
    public SharedPrefence sharedPrefence;

    public ObservableField<String> text_cmts;
    public String requestID, zoneRefID;

    public DisputeDialogViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        text_cmts = new ObservableField<>();

    }

    /** gets dispute reasons from the API **/
    public void getComplaintList(String requestID) {
        this.requestID = requestID;
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            hashMap = new HashMap<>();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.complaint_type, "request");
            if (sharedPrefence.Getvalue(SharedPrefence.LATITUDE) != null && sharedPrefence.Getvalue(SharedPrefence.LONGITUDE) != null) {
                hashMap.put(Constants.NetworkParameters.latitude, sharedPrefence.Getvalue(SharedPrefence.LATITUDE));
                hashMap.put(Constants.NetworkParameters.longitude, sharedPrefence.Getvalue(SharedPrefence.LONGITUDE));
            }
            hashMap.put(Constants.NetworkParameters.type, "1");
            getComplaintsNetwork();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /** called when API call is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("complaint_list")) {
            if (response.getComplaintList() != null && response.getComplaintList().size() > 0) {

                compLaintObservableList.addAll(response.getComplaintList());
                zoneRefID = CommonUtils.IsEmpty(response.admin_key) ? "" : response.admin_key;
                getmNavigator().addList(compLaintObservableList);
            } else if (response.complaintList.size() == 0) {
                ComplaintList complaintList = new ComplaintList();
                complaintList.id = 0;
                ArrayList<ComplaintList> list = new ArrayList<>();
                list.add(complaintList);
                getmNavigator().addList(list);
            }
        } else if (response.successMessage.equalsIgnoreCase("compliant registered successfully")) {
            getmNavigator().showMessage(response.successMessage);
            text_cmts.set("");
            getmNavigator().dismissDialog(false);
        }
    }

    /** called when the API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);

        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().GetContext() != null) {
                getmNavigator().showMessage(getmNavigator().GetContext().getTranslatedString(R.string.text_already_login));
            }
        } else
            getmNavigator().showMessage(e);
    }

    /** returns {@link HashMap} with the query parameters for API call **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** called when send button is clicked and calls API **/
    public void OnlickSend(View view) {
        if (CommonUtils.IsEmpty(text_cmts.get())) {
            getmNavigator().showSnackBar(view, getmNavigator().GetContext().getTranslatedString(R.string.Validate_EnterDescription));
        } else if (CommonUtils.IsEmpty(getmNavigator().getSelectedItemID())) {
        } else {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.title, getmNavigator().getSelectedItemID());
            hashMap.put(Constants.NetworkParameters.request_id, requestID);
            hashMap.put(Constants.NetworkParameters.description, text_cmts.get());
            hashMap.put(Constants.NetworkParameters.admin_key, zoneRefID);
            hashMap.put(Constants.NetworkParameters.complaint_type, "request");
            SendComplaintNetwork();
        }
    }
}
