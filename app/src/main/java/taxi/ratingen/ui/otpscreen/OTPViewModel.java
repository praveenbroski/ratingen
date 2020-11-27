package taxi.ratingen.ui.otpscreen;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.UUID;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

/**
 * Created by root on 10/9/17.
 */

public class OTPViewModel extends BaseNetwork<BaseResponse, OTPNavigator> {

    @Inject
    HashMap<String, String> Map;

    SharedPrefence sharedPrefence;
    public View view;

    String mVerificationId = "";
    PhoneAuthProvider.ForceResendingToken mToken;
    public ObservableField<String> phoneNumber = new ObservableField<>("");
    ObservableField<String> UUIDValue = new ObservableField<>("");
    ObservableField<String> RegOrLogin = new ObservableField<>("");
    ObservableField<String> CountryId = new ObservableField<>("");
    ObservableField<String> CountryPrefix = new ObservableField<>("");
    public ObservableBoolean resendClicked = new ObservableBoolean(false);
    public ObservableBoolean isOTPDone = new ObservableBoolean(false);

    @Inject
    public OTPViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                        @Named("HashMapData") HashMap<String, String> stringHashMap,
                        SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        Map = stringHashMap;
        Log.i("firefcmdevice_token", "==========" + sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);

        if (response.accessToken != null) {
            if (!TextUtils.isEmpty(response.accessToken)) {
                sharedPrefence.savevalue(SharedPrefence.AccessToken, response.accessToken);
                getmNavigator().openHomeActivity();
            }
        } else {
            if (response.success) {
                if (resendClicked.get()) {
                    String uuid = CommonUtils.ObjectToString(response.data);
                    UUID uuidInstance = (UUID) CommonUtils.StringToObject(uuid, UUID.class);
                    UUIDValue.set(uuidInstance.getUuid());
                    resendClicked.set(false);
                } else {
                    if (RegOrLogin.get().equalsIgnoreCase("1")) {
                        /* Move to register */
                        getmNavigator().openSignUpActivity();
                    }
                }
            }
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
    }

    public void onClickVerify(View v) {
        if (getmNavigator().isNetworkConnected()) {
            if (!getmNavigator().getOpt().isEmpty() && getmNavigator().getOpt().length() == 6) {
                setIsLoading(true);
                Map.clear();
                Map.put(Constants.NetworkParameters.UUId, UUIDValue.get());
                Map.put(Constants.NetworkParameters.OTP, getmNavigator().getOpt());
                Map.put(Constants.NetworkParameters.device_token, sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
                if (RegOrLogin.get().equalsIgnoreCase("1"))
                    registerOtpValidate(Map);
                else {
                    Map.clear();
                    Map.put(Constants.NetworkParameters.mobile, phoneNumber.get());
                    Map.put(Constants.NetworkParameters.OTP, getmNavigator().getOpt());
                    Map.put(Constants.NetworkParameters.device_token, sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
                    Map.put(Constants.NetworkParameters.login_by, "android");
                    Map.put(Constants.NetworkParameters.Role, "user");
                    getUserLoginApi(Map);
                }
            }
        } else getmNavigator().showNetworkMessage();
    }

    public void onClickResend(View v) {
        resendClicked.set(true);
        getmNavigator().openResendAlert();
    }

    public void onclickEditNumber(View view) {
        getmNavigator().FinishAct();
    }

    public void ResendOtpApi() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            Map.clear();
            Map.put(Constants.NetworkParameters.country, CountryId.get());
            Map.put(Constants.NetworkParameters.mobile, phoneNumber.get());
            if (RegOrLogin.get().equalsIgnoreCase("1"))
                sendRegisterOtp(Map);
            else if (RegOrLogin.get().equalsIgnoreCase("2"))
                sendLoginOtp(Map);
        } else getmNavigator().showNetworkMessage();
    }

    static int resendtimer = 120;

    public void startTimerTwoMinuts() {
        enableResend.set(false);
        new android.os.Handler().postDelayed(runnable, resendtimer * 1000);
    }

    public ObservableBoolean enableResend = new ObservableBoolean(true);

    public Runnable runnable = () -> enableResend.set(true);

}
