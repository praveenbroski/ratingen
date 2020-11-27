package taxi.ratingen.ui.companyvalidation.dialog;

import android.app.AlertDialog;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryCodeModel;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by root on 9/25/17.
 */


public class DialogCompanyViewModel extends BaseNetwork<BaseResponse, DialogComanyNavigator> {
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> companyKey = new ObservableField<>("");
    public ObservableField<String> email = new ObservableField<>("");
    public ObservableField<String> countrycode = new ObservableField<>("");
    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableBoolean isRequest = new ObservableBoolean();
    public ObservableBoolean isRequestSubmit = new ObservableBoolean();
    public ObservableBoolean onRequest = new ObservableBoolean();
    AlertDialog.Builder builder;
    String expiryText = "";

    //EmptyViewModel created if we are not gng to access viewmodel..

    SharedPrefence sharedPrefence;
    GitHubCountryCode gitHubCountryCode;
    public HashMap<String, String> map = new HashMap<>();
    public ArrayList<CountryListModel> countryListModels;

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject
    public DialogCompanyViewModel(@Named(Constants.ourApp) GitHubService gitHubService, GitHubCountryCode gitHubCountryCode, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gitHubCountryCode = gitHubCountryCode;


    }

    /** builds API call query parameters **/
    @Override
    public HashMap<String, String> getMap() {
        /*name,email,dialcode,phoneNumber
         company_token*/
        map.put(Constants.NetworkParameters.name, name.get());
        return map;
    }

    /** called when API call was successful **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response != null && response.success)
            if (response.successMessage.equalsIgnoreCase("request_sent")) {
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_request_processed));
                isRequest.set(false);

                name.set("");
                email.set("");
                phone.set("");
            } else if (response.successMessage.equalsIgnoreCase("token_verified")) {
                if (response.client != null && !CommonUtils.IsEmpty(response.client.client_token)) {
                    sharedPrefence.savevalue(SharedPrefence.COMPANY_KEY, response.client.client_token);
                    sharedPrefence.savevalue(SharedPrefence.COMPANY_ID, response.client.client_id);
                    expiryText = response.expirySoon;
                    if (CommonUtils.IsEmpty(response.expirySoon))
                        getmNavigator().openDrawerActivity();
                    else
                        getmNavigator().continueUsage(response.expirySoon);

                }
            } else if (response.successMessage.equalsIgnoreCase("country_list")) {
                countryListModels = response.getCountryList();
                if (countryListModels != null) {
                    CountryListModel listModel = CommonUtils.getDefaultCountryDetails(countryListModels, Constants.COUNTRY_CODE);
                    if(listModel!=null){
//                        countryFlag.set(listModel.flag);
//                        CountryId.set(listModel.id);
                        countrycode.set(listModel.dialCode);
//                        CountryShort.set(listModel.iso2);
                    }
                }
            }

    }

    /** called when API call fails **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            sharedPrefence.savevalue(SharedPrefence.COMPANY_KEY, "");
            sharedPrefence.savevalue(SharedPrefence.COMPANY_ID, "");
        }

        if (e != null && !CommonUtils.IsEmpty(e.getMessage()))
            getmNavigator().showAlert(e.getMessage());
    }

    /** gets and saves the current country code **/
    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null) {
                           // Constants.COUNTRY_CODE = response.body().toString();
                            Constants.COUNTRY_CODE = "IN";
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCurrentCountryCode(Constants.COUNTRY_CODE);
                }
            });
        }
    }


    public void onCancelButon(View v) {

    }

    public void onClickCC(View v) {
        getmNavigator().openCountryDialog(countryListModels);
    }

    /** calls the API to validate the entered company key **/
    public void onSubmit(View v) {
        if (isRequest.get())
            isRequest.set(false);
        else {
            if (!validate())
                return;
            map.put(Constants.NetworkParameters.company_token, companyKey.get());
            if (getmNavigator().isNetworkConnected()) {
                validateCompanyKey();
            } else
                getmNavigator().showNetworkMessage();
        }
    }

    /** calls the API request a new company key **/
    public void onRequest(View v) {
        if (isRequest.get()) {
            if (!validate())
                return;
            map.put(Constants.NetworkParameters.name, name.get());
            map.put(Constants.NetworkParameters.email, email.get());
            map.put(Constants.NetworkParameters.phoneNumber, phone.get());
            map.put(Constants.NetworkParameters.dialcode, countrycode.get());

            if (getmNavigator().isNetworkConnected()) {
                requestCompanyKey();
            } else
                getmNavigator().showNetworkMessage();
        } else
            isRequest.set(true);
    }

    /** form validation: validates if entered fields are not empty & in required format **/
    public boolean validate() {
        if (isRequest.get()) {
            if (CommonUtils.IsEmpty(name.get()) || CommonUtils.IsEmpty(email.get()) || CommonUtils.IsEmpty(phone.get())) {
                return false;
            } else if (!CommonUtils.isEmailValid(email.get())) {
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_error_email_valid));
                return false;
            } else return true;
        } else {
            if (CommonUtils.IsEmpty(companyKey.get())) {
                getmNavigator().showAlert("Company key required.");
                return false;
            } else
                return true;
        }

    }

    /** called when text is entered in editText & sets isRequestSubmit true or false based on text length **/
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() == 0) {
            isRequestSubmit.set(false);
        } else {
            isRequestSubmit.set(true);

        }
    }

}
