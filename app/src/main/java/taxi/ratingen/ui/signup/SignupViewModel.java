package taxi.ratingen.ui.signup;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryCodeModel;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.retro.responsemodel.UUID;
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
 * Created by root on 10/7/17.
 */

public class SignupViewModel extends BaseNetwork<BaseResponse, SignupNavigator> {

    @Inject
    HashMap<String, String> Map;
    public ArrayList<CountryListModel> countryListModels = new ArrayList<>();

    SharedPrefence sharedPrefence;

    /*BaseView baseView;*/

    public ObservableField<String> EmailorPhone = new ObservableField<>("");
    public ObservableField<String> countryCode = new ObservableField<>("+");
    public ObservableField<String> CountryShort = new ObservableField<>("");
    public ObservableField<String> CountryId = new ObservableField<>("");
    public ObservableField<String> countryFlag = new ObservableField<>();
    ObservableField<Integer> toRegOrLog = new ObservableField<>();
    ObservableField<String> UUID_VALUE = new ObservableField<>("");
    GitHubService gitHubService;
    GitHubCountryCode gitHubCountryCode;


    @Inject
    public SignupViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                           SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubService = gitHubService;
        this.gitHubCountryCode = gitHubCountryCode;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
    }

    public void onClickLogin(View view) {

        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Mob_Number));
            else if (CommonUtils.IsEmpty(countryCode.get().trim().replace("+", ""))) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.bt_country_code_invalid));
            } else
                sendLoginRequest();


        } else {
            getmNavigator().showNetworkMessage();
        }

    }

    /**
     * Calls login API
     **/
    private void sendLoginRequest() {
        getmNavigator().HideKeyBoard();
        Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        Map.put(Constants.NetworkParameters.phoneNumber, countryCode.get().trim().replace(" ", "") + EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", ""));
        Map.put(Constants.NetworkParameters.is_signup, "0");
        loginOtpVerification();
    }

    public void onClickSignup(View view) {

        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Mob_Number));
            else if (CommonUtils.IsEmpty(countryCode.get().trim().replace("+", ""))) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.bt_country_code_invalid));
            } else {
                getmNavigator().HideKeyBoard();
                TokenGeneratorcall();
            }
        } else {
            getmNavigator().showNetworkMessage();
        }

    }

    /**
     * Closes soft keyboard when tapped outside of the screen
     **/
    public void onClickOutSide(View view) {
        getmNavigator().HideKeyBoard();
    }

    /**
     * Click listener for social login clicked
     **/
    public void onClickSocial(View view) {
        getmNavigator().openSocialActivity();
    }

    /**
     * Callback for successful API calls
     *
     * @param taskId   ID of the API task
     * @param response {@link BaseResponse} model
     **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
//        setIsLoading(false);
//        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("country_list")) {
//            countryListModels = response.getCountryList();
//            if (countryListModels != null) {
//                CountryListModel listModel = CommonUtils.getDefaultCountryDetails(countryListModels, Constants.COUNTRY_CODE);
//                if (listModel != null) {
//                    countryFlag.set(listModel.flag);
//                    CountryId.set(listModel.id);
//                    Countrycode.set(listModel.callingCode);
//                    CountryShort.set(listModel.iso2);
//                }
//            }
//        } else if (response.success) {
//            if (response.exist_user)
//                getmNavigator().openOtpPage(true, Countrycode.get().trim().replace(" ", "") + CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")), CountryShort.get());
//            else
//                getmNavigator().openOtpPage(false, Countrycode.get().trim().replace(" ", "") + CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")), CountryShort.get());
//        }

        setIsLoading(false);
        if (response.success) {
            if (response.successMessage != null) {
                if (response.successMessage.equalsIgnoreCase("country_list") || response.successMessage.equalsIgnoreCase("country list")) {
                    getmNavigator().countryResponse(response.data);
                }
            } else {
                if (response.message != null) {
                    if (response.message.equalsIgnoreCase("new user")) {
                        callSendOtpApi(1);
                    } else if (response.message.equalsIgnoreCase("exists user")) {
                        callSendOtpApi(2);
                    } else if (response.message.equalsIgnoreCase("success")) {
                        String uuid = CommonUtils.ObjectToString(response.data);
                        UUID uuidInstance = (UUID) CommonUtils.StringToObject(uuid, UUID.class);
                        UUID_VALUE.set(uuidInstance.getUuid());
                        getmNavigator().openOtpPage(UUID_VALUE.get(), toRegOrLog.get(), EmailorPhone.get(), CountryId.get(), countryCode.get());
                    }
                }
            }
        }
    }

    /**
     * Callback for failed API calls
     *
     * @param taskId ID of the API task
     * @param e      {@link CustomException}
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == 702) {
            getmNavigator().openOtpActivity(true);
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }
    }

    /**
     * Returns {@link HashMap} with query parameters to call APIs
     **/
    @Override
    public HashMap<String, String> getMap() {
        Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        return Map;
    }

    private void callSendOtpApi(int type) {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            Map.clear();
            Map.put(Constants.NetworkParameters.country, CountryId.get());
            Map.put(Constants.NetworkParameters.mobile, EmailorPhone.get());
            toRegOrLog.set(type);
            if (type == 1)
                sendRegisterOtp(Map);
            else if (type == 2)
                sendLoginOtp(Map);
        } else getmNavigator().showNetworkMessage();
    }

    /**
     * Calls API to generate token
     **/
    public void TokenGeneratorcall() {

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            TokenGeneratorNetcall();
        } else
            getmNavigator().showNetworkMessage();
    }

    /**
     * Country code changed listener
     *
     * @param e {@link Editable}
     **/
    public void onCCodeChanged(Editable e) {
        countryCode.set(e.toString());

    }

    /**
     * Text changed listener for EmailorPhone field
     *
     * @param e {@link Editable}
     **/
    public void onUsernameChanged(Editable e) {
        EmailorPhone.set(e.toString());
        /*if (e.toString().length() == 1 && e.toString().startsWith("0")) {
            e.delete(0, 1);
            EmailorPhone.set("");
        }*/
    }

    /**
     * Calls API to generate token for login
     **/
    public void TokenGeneratorNetcall() {
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
                            Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                            Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                            Map.put(Constants.NetworkParameters.phonenumber, countryCode.get().replaceAll(" ", "") + CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")));
                            Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
//                            Map.put(Constants.NetworkParameters.country_code, getmNavigator().getCountryCode());
                            Map.put(Constants.NetworkParameters.country_code, CountryId.get());
                            Map.put(Constants.NetworkParameters.disp_country_code, countryCode.get().replaceAll(" ", ""));
                            Map.put(Constants.NetworkParameters.disp_phonenumber, CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")));
                            Map.put(Constants.NetworkParameters.country, CountryShort.get());
                            Map.put(Constants.NetworkParameters.is_signup, "1");
                            Map.put(Constants.NetworkParameters.new_flow, "true");

                            // Map.put(Constants.NetworkParameters.CountryDummy,Countrycode.get()+"_"+phonenumber.get());
                            sendOtpCall();
                        }
                    } else {
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

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                setIsLoading(false);
                getmNavigator().showMessage(t.getLocalizedMessage());
            }
        });
    }

    /**
     * Get current country code
     **/
    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null && response.body() != null) {
                            Constants.COUNTRY_CODE = response.body().toString();
                            //Constants.COUNTRY_CODE = "IN";
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                }
            });
        }
    }

    /**
     * Click listener for confirm button
     **/
    public void onClickConfirm(View view) {
//        if (getmNavigator().isNetworkConnected()) {
//            if (CommonUtils.IsEmpty(EmailorPhone.get().trim().replace("-", "").replace("_", "").replace(" ", "")))
//                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Mob_Number));
//            else if (CommonUtils.IsEmpty(countryCode.get().replaceAll(" ", "").replace("+", ""))) {
//                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.bt_country_code_invalid));
//            } else {
//                getmNavigator().HideKeyBoard();
//                TokenGeneratorcall();
//            }
//        } else {
//            getmNavigator().showNetworkMessage();
//        }

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            if (!TextUtils.isEmpty(EmailorPhone.get())) {
                Map.clear();
                Map.put(Constants.NetworkParameters.country, CountryId.get());
                Map.put(Constants.NetworkParameters.mobile, EmailorPhone.get());
                Map.put(Constants.NetworkParameters.android, "1");
                validateMobile(Map);
            } else {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Mob_Number));
                setIsLoading(false);
            }
        } else getmNavigator().showNetworkMessage();
    }

    public void getCountryListApi() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getCountryList();
        } else getmNavigator().showNetworkMessage();

    }

    public void CountryChoose(View view) {
        getmNavigator().openCountryDialog(countryListModels);
    }

    @BindingAdapter({"bind:countryFlag"})
    public static void setCountryFlag(ImageView view, String profilePicURL) {
        if (!CommonUtils.IsEmpty(profilePicURL))
            Glide.with(view.getContext()).load(profilePicURL).into(view);
    }
}
