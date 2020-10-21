package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;

import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.GenericsType;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.RequestBody;

@SuppressWarnings("ConstantConditions")
public class NameMailModel extends BaseNetwork<BaseResponse, NameMailNavigator> {

    @Inject
    Context context;
    Gson gson;
    SharedPrefence sharedPrefence;

    @Inject
    HashMap<String, String> hashMap;
    GitHubService gitHubService;
    GitHubCountryCode gitHubCountryCode;
    GenericsType genericsType;
    public ArrayList<CountryListModel> countryListModels;

    public ObservableField<Integer> mode = new ObservableField<>(-1);
    public ObservableField<String> extraData = new ObservableField<>("");
    public ObservableField<Boolean> isSubmitEnable = new ObservableField<>(false);
    public ObservableField<String> Countrycode = new ObservableField<>("+");
    public ObservableField<String> CountryShort = new ObservableField<>("");
    public ObservableField<String> CountryId = new ObservableField<>();
    public ObservableField<String> countryFlag = new ObservableField<>();

    @Inject
    public NameMailModel(@Named(Constants.ourApp) GitHubService gitHubService,
                         @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                         SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.gitHubService = gitHubService;
        this.gitHubCountryCode = gitHubCountryCode;
        genericsType = new GenericsType();
    }

    /** Returns {@link HashMap} with query parameters used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("Profile Updated Successfully")) {
            if (response.success) {
                String userString = gson.toJson(response.getUser());
                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userString);
                getmNavigator().SendBroadcast();
                Toast.makeText(context, response.successMessage, Toast.LENGTH_SHORT).show();
                getmNavigator().finishAct();
            }
        } else if (response.successMessage.equalsIgnoreCase("country_list")) {
            countryListModels = response.getCountryList();
            if (countryListModels != null) {
                CountryListModel listModel = CommonUtils.getDefaultCountryDetails(countryListModels, Constants.COUNTRY_CODE);
                if (listModel != null) {
                    countryFlag.set(listModel.flag);
                    CountryId.set(listModel.id);
                    Countrycode.set(listModel.callingCode);
                    CountryShort.set(listModel.iso2);
                    extraData.set(extraData.get().replace(Countrycode.get(), ""));
                }
            }
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().showMessage(e);
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }
    }

    public void getCountryListApi() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getCountryList();
        } else getmNavigator().showNetworkMessage();
    }

    /** Clear text field **/
    public void clearText(View view) {
        getmNavigator().clearText();
    }

    public void onClickUpdate(View view) {
        if (mode.get() == 0 || mode.get() == 1 || mode.get() == 2) {
            if (getmNavigator().isNetworkConnected()) {
                setIsLoading(true);
                requestbody.clear();
                requestbody.put(Constants.NetworkParameters.client_id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyID()));
                requestbody.put(Constants.NetworkParameters.client_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyToken()));
                requestbody.put(Constants.NetworkParameters.id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.ID)));
                requestbody.put(Constants.NetworkParameters.token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.TOKEN)));
                if (mode.get() == 0) {
                    requestbody.put(Constants.NetworkParameters.firstname, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                } else if (mode.get() == 1) {
                    requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                } else if (mode.get() == 2) {
                    requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                }
                ProfileNetworkcall();
            }
        } else if (mode.get() == 3) {
            getmNavigator().openOtpPage(Countrycode.get().trim().replace(" ", "") + CommonUtils.removeFirstZeros(extraData.get().trim().replace("-", "").replace("_", "").replace(" ", "")));
        }
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        extraData.set(s.toString().trim());
        if (s.toString().trim().length() == 0) {
            isSubmitEnable.set(false);
        } else {
            if (mode.get() == 0 || mode.get() == 1 || mode.get() == 3) {
                isSubmitEnable.set(!CommonUtils.IsEmpty(extraData.get().trim()));
            } else if (mode.get() == 2) {
                isSubmitEnable.set(CommonUtils.isEmailValid(extraData.get().trim()));
            }
        }
    }

    public void CountryChoose(View view) {
        getmNavigator().openCountryDialog(countryListModels);
    }

}
