package taxi.ratingen.ui.otpscreen;

import androidx.databinding.ObservableBoolean;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
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

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String mVerificationId = "";
    PhoneAuthProvider.ForceResendingToken mToken;
    static int resendtimer = 120;

    String phoneWithCountry = "";
    boolean isLogin = false;


    public ObservableBoolean enableResend = new ObservableBoolean(true);
    public ObservableField<String> phoneNumber = new ObservableField<>("");
    public Runnable runnable = () -> enableResend.set(true);


    @Inject
    public OTPViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                        @Named("HashMapData") HashMap<String, String> stringHashMap,
                        SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;

        Map = stringHashMap;

        System.out.println("+++" + Map.get(Constants.NetworkParameters.phonenumber));
        phoneNumber.set(Map.get(Constants.NetworkParameters.phonenumber));

    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }

    public void onclickverfiy(View view) {
        setIsLoading(true);
        try {
/*            //TODO remove this block at release
            if(BuildConfig.DEBUG)
                initializeSucessNavigation();
            else {*/
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, getmNavigator().getOpt());
            signInWithPhoneAuthCredential(credential);
//            }
        } catch (Exception e) {
            setIsLoading(false);
            if (!getmNavigator().getOpt().isEmpty()) {
                getmNavigator().showMessage(translationModel.txt_otp_wrong);
            } else {
                getmNavigator().showMessage(translationModel.txt_enter_otp);
            }
        }
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInSucess", "signInWithCredential:success");
                            initializeSucessNavigation();
                            //
                        } else {
                            // Sign in failed, display a message and update the UI
                            setIsLoading(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                getmNavigator().showMessage(task.getException().getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    public void initializeSucessNavigation() {
        setIsLoading(false);
        if (!isLogin)  //new User
        {
            getmNavigator().openSinupuActivity();
        } else {
            Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            Map.put(Constants.NetworkParameters.login_by, Constants.NetworkParameters.android);
            Map.put(Constants.NetworkParameters.device_token, sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
            Map.put(Constants.NetworkParameters.password, getmNavigator().getOpt());
            Map.put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
            Map.put(Constants.NetworkParameters.username, phoneWithCountry);
            Map.put(Constants.NetworkParameters.new_flow, "true");
            LoginNetworkcall();
        }
    }

    public void onClickResend(View view) {
        setIsLoading(false);
        getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Toast_OTP_send));
        startTimerTwoMinuts();
        getmNavigator().resendOtp();
        getmNavigator().startTimer(resendtimer);

//        if (getmNavigator().isNetworkConnected()) {
//            setIsLoading(true);
//            this.view = view;
//            if (Map.get(Constants.NetworkParameters.is_signup) != null &&
//                    Integer.parseInt(Map.get(Constants.NetworkParameters.is_signup)) == 1 &&
//                    !CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.is_signup))) {
//                Map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
//                OptResendcall();
//            } else {
//                Map.put(Constants.NetworkParameters.phoneNumber, Map.get(Constants.NetworkParameters.phoneNumber));
//                loginOtpVerification();
//            }
//
//            getmNavigator().startTimer(resendtimer);
//
//
//        } else {
//            getmNavigator().showNetworkMessage();
//        }
    }

    public void onclickEditNumber(View view) {
        getmNavigator().FinishAct();
    }

    public void startTimerTwoMinuts() {
        enableResend.set(false);
        new android.os.Handler().postDelayed(runnable, resendtimer * 1000);
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);

        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("Resend_Otp")) {
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Toast_OTP_send));
            } else if (response.successMessage.equalsIgnoreCase("Otp_Validate")) {
                getmNavigator().openSinupuActivity();
            } else if (!CommonUtils.IsEmpty(response.successMessage) && response.successMessage.equalsIgnoreCase("User_Logged")) {
                String userstring = gson.toJson(response.getUser());
                if (response.isCorporate == 1)
                    sharedPrefence.saveBoolean(SharedPrefence.IS_CORPORATE_USER, true);
                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.TOKEN, response.getUser().token);
                sharedPrefence.savevalue(SharedPrefence.ID, "" + response.getUser().id);
                getmNavigator().openHomeActivity();
            }
        }
    }


    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
    }
}
