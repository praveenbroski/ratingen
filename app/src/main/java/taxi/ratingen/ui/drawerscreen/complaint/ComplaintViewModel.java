package taxi.ratingen.ui.drawerscreen.complaint;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * Created by root on 12/20/17.
 */

public class ComplaintViewModel extends BaseNetwork<BaseResponse, ComplaintNavigator> {

    GitHubService gitHubService;
    HashMap<String, String> hashMap;
    Context context;
    List<ComplaintList> complaintList;
    public String SelectedId = null;
    public SharedPrefence sharedPrefence;

    public ObservableField<String> text_cmts;
    public String zoneRefID;

    public ComplaintViewModel(@Named(Constants.ourApp) GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        text_cmts = new ObservableField<>();

    }

    /** get {@link ComplaintList} from API **/
    public void getComplaintList() {
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            hashMap = new HashMap<>();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
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

    /** sets {@link ArrayAdapter} to spinner with data from API **/
    @BindingAdapter("spinList")
    public void setImageUrl(Spinner spinner, List<ComplaintList> compLaintList) {
        Context context = spinner.getContext();
//        spinnerItem = spinner;
        ArrayAdapter<ComplaintList> adapter = new ArrayAdapter<ComplaintList>(context, android.R.layout.simple_list_item_1, compLaintList);
        spinner.setAdapter(adapter);
    }

    /** called when API call is successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("complaint_list")) {
            if (response.getComplaintList() != null && response.getComplaintList().size() > 0) {
                getmNavigator().DisableSpinner(true);
                complaintList = response.getComplaintList();
                zoneRefID = CommonUtils.IsEmpty(response.admin_key) ? "" : response.admin_key;
                getmNavigator().addList(complaintList);
            } else if (response.complaintList.size() == 0) {
                ComplaintList complaintList = new ComplaintList();
                complaintList.id = 0;
                if (getmNavigator().GetContext() != null)
                    complaintList.title = getmNavigator().GetContext().getTranslatedString(R.string.Txt_NoServiceAvailable);
                ArrayList<ComplaintList> list = new ArrayList<>();
                list.add(complaintList);
                getmNavigator().addList(list);
            }
        } else if (response.successMessage.equalsIgnoreCase("compliant registered successfully")) {
            getmNavigator().showMessage(response.successMessage);
            text_cmts.set("");
        }
    }

    /** called when any of the complaint is selected from the spinner **/
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String items = parent.getSelectedItem().toString();
        if (!items.equalsIgnoreCase(getmNavigator().GetContext().getTranslatedString(R.string.Txt_NoServiceAvailable))) {

            if (!CommonUtils.IsEmpty(items)) {
                for (ComplaintList complaintLi : complaintList) {
                    if (complaintLi.title.equalsIgnoreCase(items)) {
                        SelectedId = String.valueOf(complaintLi.id);
//                        break;
                    }
                }
            }
        } else if (items.equalsIgnoreCase(getmNavigator().GetContext().getTranslatedString(R.string.Txt_NoServiceAvailable))) {
            getmNavigator().DisableSpinner(false);
        }
    }

  /*  public void onCommentChanged(Editable e) {
        text_cmts.set(e.toString());
    }*/

    /** called when API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);

        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().GetContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().GetContext().getTranslatedString(R.string.text_already_login));
            }
        }  else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }else
            getmNavigator().showMessage(e);
    }

    /** sets client_id & client_token to {@link HashMap} used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** called when send button is clicked **/
    public void OnlickSend(View view) {
        if (CommonUtils.IsEmpty(text_cmts.get())) {
            getmNavigator().showSnackBar(view, getmNavigator().GetContext().getTranslatedString(R.string.Validate_EnterDescription));
        } else {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.title, SelectedId);
            hashMap.put(Constants.NetworkParameters.description, text_cmts.get());
            hashMap.put(Constants.NetworkParameters.admin_key, zoneRefID);
            SendComplaintNetwork();
        }
    }

}
