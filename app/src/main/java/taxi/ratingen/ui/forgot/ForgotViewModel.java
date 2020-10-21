package taxi.ratingen.ui.forgot;

import android.content.Context;
import androidx.databinding.ObservableField;
import android.text.Editable;
import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.CountryCodeModel;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/9/17.
 */

public class ForgotViewModel extends BaseNetwork<User, ForgotNavigator> {

    @Inject
    HashMap<String, String> Map;

    @Inject
    Context context;
    public ObservableField<String> EmailorPhone = new ObservableField<>();

    SharedPrefence sharedPrefence;
    GitHubService gitHubService;
    GitHubCountryCode gitHubCountryCodeService;

    @Inject
    public ForgotViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCodeService,
                           SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.gitHubCountryCodeService = gitHubCountryCodeService;
        // setBinding();
    }

    /** Click listener for submit button. After validating input ic calls Token Generator API **/
    public void onclickSumbit(View view) {

        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get())) {
                getmNavigator().showSnackBar(view, getmNavigator().getAttachedContext().getTranslatedString(R.string.text_mail_phNum));
            } else {

                TokenGeneratorcall();

            }


        } else {
            getmNavigator().showNetworkMessage();
        }

    }

    /** Calls API to generate token **/
    public void TokenGeneratorcall() {

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            TokenGeneratorNetcall();
        } else
            getmNavigator().showNetworkMessage();
    }

    /** Called whenever text changes on the phone number field and assigns it to EmailorPhone
     * @param e {@link Editable} **/
    public void ForgotuserChanged(Editable e) {
        EmailorPhone.set(e.toString());
        if (e.toString().contains("@") || e.toString().contains(".co") || e.toString().contains(".com")) {
            getmNavigator().OpenCountrycodeINvisible();

        } else {

            if (e.toString().matches("[a-zA-Z]")) //only alphanumeric
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().isEmpty())
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().matches("[0-9]+")) {//only number
                getmNavigator().OpenCountrycodevisible();
            }
        }

    }

    /** Calls {@link GitHubService} to generate token **/
    public void TokenGeneratorNetcall() {
        if(Map==null)
            Map=new HashMap<>();
        Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        gitHubService.TokenGenerator(Map).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                setIsLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().success) {
                        if (!CommonUtils.IsEmpty(response.body().token)) {
                            sharedPrefence.savevalue(SharedPrefence.TOKEN, response.body().token);
                            setIsLoading(true);
                            Map.clear();
                            Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                            if (EmailorPhone.get().contains("@") || EmailorPhone.get().contains(".co") || EmailorPhone.get().contains(".com"))
                                Map.put(Constants.NetworkParameters.email, EmailorPhone.get()); //change later
                            else
                                Map.put(Constants.NetworkParameters.phonenumber, getmNavigator().getCountryCode() + CommonUtils.removeFirstZeros(EmailorPhone.get()));
                            ForgotNetworkcall();
                        }else {
                            getmNavigator().showMessage(response.body().errorMessage);
                            if (response.body() != null && response.body().errorCode == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                                    response.body().errorCode == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                                    response.body().errorCode == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                                    response.body().errorCode == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
                                if (getmNavigator() != null)
                                    getmNavigator().refreshCompanyKey();
                            }
                        }
                    } else {
                        getmNavigator().showMessage(response.body().errorMessage);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                setIsLoading(false);
                getmNavigator().showMessage(t.getLocalizedMessage());
            }
        });
    }

    /** Called to get current country code **/
    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCodeService.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null && response.body() != null) {
                            //Constants.COUNTRY_CODE = response.body().toString();
                            Constants.COUNTRY_CODE = "IN";
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
                }
            });
        }
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response {@link User} response model **/
    @Override
    public void onSuccessfulApi(long taskId, User response) {
        Map.clear();
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("forgot_password")) {
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.Toast_Forgot_Password));
            getmNavigator().openLoginActivity();
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("forgot_password_sms")) {
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_pass_sent_sms));
            getmNavigator().openLoginActivity();
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("forgot_password_email")) {
            getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_pass_sent_email));
            getmNavigator().openLoginActivity();
        }

    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e {@link CustomException} **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);

        getmNavigator().showMessage(e);
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }
}
