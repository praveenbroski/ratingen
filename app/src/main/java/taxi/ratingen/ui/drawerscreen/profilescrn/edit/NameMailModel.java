package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;

import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.retro.responsemodel.ProfileModel;
import taxi.ratingen.retro.responsemodel.UUID;
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
    ArrayList<CountryListModel> countryListModels = new ArrayList<>();

    public ObservableField<String> txtTitle = new ObservableField<>("");
    public ObservableField<String> txtDescription = new ObservableField<>("");
    public ObservableField<Integer> mode = new ObservableField<>(-1);
    public ObservableField<String> extraData = new ObservableField<>("");
    public ObservableField<String> extraMobile = new ObservableField<>("");
    public ObservableField<Boolean> isSubmitEnable = new ObservableField<>(false);
    public ObservableField<String> countryCode = new ObservableField<>("+");
    public ObservableField<String> CountryId = new ObservableField<>("");
    public ObservableField<String> countryShort = new ObservableField<>("");
    public ObservableField<String> countryFlag = new ObservableField<>("");
    ObservableField<String> UUID_VALUE = new ObservableField<>("");

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
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.message != null) {
            if (response.message.equalsIgnoreCase("success")) {
                if (mCurrentTaskId == Constants.TaskId.PROFILE_UPDATE) {
                    String userString = gson.toJson(response.data);
                    sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userString);
                    getmNavigator().SendBroadcast();
                    getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.Txt_updateSuccess));
                    getmNavigator().finishAct();
                } else if (mCurrentTaskId == Constants.TaskId.SEND_OTP) {
                    String uuid = CommonUtils.ObjectToString(response.data);
                    UUID uuidInstance = (UUID) CommonUtils.StringToObject(uuid, UUID.class);
                    UUID_VALUE.set(uuidInstance.getUuid());
                    getmNavigator().openOtpPage(UUID_VALUE.get(), 3, extraData.get(), CountryId.get(), countryCode.get());
                }
            } else if (response.message.equalsIgnoreCase("new user")) {
                callSendOtpApi();
            } else if (response.message.equalsIgnoreCase("exists user")) {
                getmNavigator().showSnackBar(getmNavigator().getBaseView(), getmNavigator().getBaseAct().getTranslatedString(R.string.txt_already_registered));
            }
        } else if (response.successMessage != null) {
            if (response.successMessage.equalsIgnoreCase("country_list")) {
                getmNavigator().countryResponse(response.data);
            }
        }
    }

    private void callSendOtpApi() {
        if (getmNavigator().isNetworkConnected()) {
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.country, CountryId.get());
            hashMap.put(Constants.NetworkParameters.mobile, extraData.get());
            sendRegisterOtp(hashMap);
        } else getmNavigator().showNetworkMessage();
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

                String userStr = sharedPrefence.Getvalue(SharedPrefence.USERDETAILS);

                ProfileModel user = CommonUtils.IsEmpty(userStr) ? null : gson.fromJson(userStr, ProfileModel.class);
                if (user != null) {
                    if (mode.get() == 0) {
                        requestbody.put(Constants.NetworkParameters.name, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                        requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), user.getEmail()));
                    } else if (mode.get() == 1) {
                        requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                        requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), user.getEmail()));
                    } else if (mode.get() == 2) {
                        requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
                        requestbody.put(Constants.NetworkParameters.name, RequestBody.create(MediaType.parse("text/plain"), user.getName()));
                    }
                    ProfileNetworkCall();
                }
            }
        } else if (mode.get() == 3) {
            if (!extraMobile.get().equals(extraData.get())) {
                if (getmNavigator().isNetworkConnected()) {
                    hashMap.clear();
                    hashMap.put(Constants.NetworkParameters.country, CountryId.get());
                    hashMap.put(Constants.NetworkParameters.mobile, extraData.get());
                    hashMap.put(Constants.NetworkParameters.android, "android");
                    validateMobile(hashMap);
                } else
                    getmNavigator().showNetworkMessage();
            }
        }
    }

    public void callMobileUpdate() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            requestbody.clear();
            requestbody.put(Constants.NetworkParameters.mobile, RequestBody.create(MediaType.parse("text/plain"), extraData.get()));
            String userStr = sharedPrefence.Getvalue(SharedPrefence.USERDETAILS);
            ProfileModel user = CommonUtils.IsEmpty(userStr) ? null : gson.fromJson(userStr, ProfileModel.class);
            if (user != null) {
                requestbody.put(Constants.NetworkParameters.name, RequestBody.create(MediaType.parse("text/plain"), user.getName()));
                requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), user.getEmail()));
            }
            ProfileNetworkCall();
        } else getmNavigator().showNetworkMessage();
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        extraData.set(s.toString());
        if (s.toString().trim().length() == 0) {
            isSubmitEnable.set(false);
        } else {
            if (mode.get() == 0 || mode.get() == 1 || mode.get() == 3) {
                if (mode.get() == 3) {
                    isSubmitEnable.set(!CommonUtils.IsEmpty(extraData.get().trim()) && (!extraData.get().equals(extraMobile.get())));
                } else
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
