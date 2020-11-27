package taxi.ratingen.ui.companyvalidation;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.BuildConfig;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class CompanyValidationViewModel extends BaseNetwork<BaseResponse, CompanyValidationNavigator> {
    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableField<String> currentAppVersion = new ObservableField<>();
    public HashMap<String, String> map = new HashMap<>();

    @Inject
    public CompanyValidationViewModel(@Named(Constants.ourApp) GitHubService gitHubService
            , SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        currentAppVersion.set("v " + BuildConfig.VERSION_NAME);
    }

    /** called when API call was successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.success)
            if (response.successMessage.equalsIgnoreCase("Language Selected")) {
                String getTranslations = CommonUtils.ObjectToString(response.data);
                BaseResponse.DataObject uuidInstance = (BaseResponse.DataObject) CommonUtils.StringToObject(getTranslations, BaseResponse.DataObject.class);
                response.saveLanguageTranslations(sharedPrefence, gson, uuidInstance);
                checkCompanyKey();

            } else if (response.successMessage.equalsIgnoreCase("token_verified"))
                if (response.client != null && !CommonUtils.IsEmpty(response.client.client_token)) {
                    sharedPrefence.savevalue(SharedPrefence.COMPANY_KEY, response.client.client_token);
                    sharedPrefence.savevalue(SharedPrefence.COMPANY_ID, response.client.client_id);
                    if (CommonUtils.IsEmpty(response.expirySoon))
                        getmNavigator().openDrawerActivity();
                    else
                        getmNavigator().showExpiryMessage(response.expirySoon);
                }

    }

    /** called when API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
        if (e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED
                || e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            sharedPrefence.savevalue(SharedPrefence.COMPANY_KEY, "");
            sharedPrefence.savevalue(SharedPrefence.COMPANY_ID, "");
            getmNavigator().showRequestDialog();
        }

    }

    /** get API query parameters **/
    @Override
    public HashMap<String, String> getMap() {
        return map;
    }

    /** get translation sheet data **/
    public void getLanguagees() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getTranslations();
        } else
            getmNavigator().showNetworkMessage();

    }

    /** call company key checking API **/
    public void checkCompanyKey() {
        if (sharedPrefence.Getvalue(SharedPrefence.COMPANY_KEY) != null && !CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.COMPANY_KEY))
                && sharedPrefence.Getvalue(SharedPrefence.COMPANY_KEY).length() > 0) {
            if (getmNavigator().isNetworkConnected()) {
                map.clear();
                map.put(Constants.NetworkParameters.company_token, sharedPrefence.Getvalue(SharedPrefence.COMPANY_KEY));
                validateCompanyKey();
            } else
                getmNavigator().showNetworkMessage();
        } else {
            getmNavigator().showRequestDialog();
        }

    }
}
