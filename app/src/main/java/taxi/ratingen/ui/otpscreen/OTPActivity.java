package taxi.ratingen.ui.otpscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityOtpBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.getReady.GetReadyAct;
import taxi.ratingen.ui.registration.RegistrationAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class OTPActivity extends BaseActivity<ActivityOtpBinding, OTPViewModel> implements OTPNavigator {
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    OTPViewModel OTPViewModel;
    ActivityOtpBinding activityOtpBinding;
    OtpView optview;
    String phonenUmber = "";
    String defaultOtp = "123456";
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    TextView textTimer;
    int time = 120;

    String phoneWithCountry = "";
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpBinding = getViewDataBinding();
        OTPViewModel.setNavigator(this);
        optview = activityOtpBinding.optCustomview;

        getGCMDeviceToken();
        OTPViewModel.startTimerTwoMinuts();
        textTimer = findViewById(R.id.timertxt);
        startTimer(time);

        if (getIntent() != null) {
            phoneWithCountry = getIntent().getStringExtra(Constants.PhonewithCountry);
            isLogin = getIntent().getBooleanExtra(Constants.isLogin, false);
            OTPViewModel.isLogin = isLogin;
            OTPViewModel.phoneWithCountry = phoneWithCountry;
            OTPViewModel.phoneNumber.set(phoneWithCountry);
            if (Constants.ENABLE_FIREBASE_OTP) {
                PhoneAuth(phoneWithCountry);
                OTPViewModel.phoneNumber.set(phoneWithCountry);
            } else
                defaultOtpPlot();
        }

        activityOtpBinding.backImg.setOnClickListener(v -> finish());
    }

    private void defaultOtpPlot() {
        activityOtpBinding.imageView2.setVisibility(View.INVISIBLE);
        activityOtpBinding.progressBar2.setVisibility(View.INVISIBLE);
        new CountDownTimer(3000, 500) {
            public void onTick(long millisUntilFinished) {
                int i = 6 - (int) ((millisUntilFinished / 500) + 1);
                optview.setOTPCustom(defaultOtp.substring(0, i));
            }

            public void onFinish() {
                optview.setOTPCustom(defaultOtp);
                activityOtpBinding.progressBar2.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                    activityOtpBinding.progressBar2.setVisibility(View.INVISIBLE);
                    activityOtpBinding.imageView2.setVisibility(View.VISIBLE);
                    Drawable drawable = activityOtpBinding.imageView2.getDrawable();
                    ((Animatable) drawable).start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OTPViewModel.initializeSucessNavigation();
                        }
                    }, 500);
                }, 2000);
            }
        }.start();
    }

    public void PhoneAuth(String phoneNum) {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e("Verify==", "onVerificationCompleted:" + credential);

                OTPViewModel.signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e("VerifiFailed===", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Code Sent===", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                OTPViewModel.mVerificationId = verificationId;
                OTPViewModel.mToken = token;

                // ...
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

    }

    @Override
    public OTPViewModel getViewModel() {
        return OTPViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_otp;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public String getOpt() {
        return optview.getOTP();
    }

    @Override
    public void openSinupuActivity() {
        OTPViewModel.getMap().put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
        OTPViewModel.getMap().put(Constants.NetworkParameters.IsEnabled, Constants.NetworkParameters.Yes);
        startActivity(new Intent(this, RegistrationAct.class).putExtra(Constants.EXTRA_Data, OTPViewModel.getMap()));
        finish();
    }

    @Override
    public void openHomeActivity() {
        startActivity(new Intent(this, GetReadyAct.class));
        finish();
    }

    @Override
    public void FinishAct() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void resendOtp() {
        PhoneAuth(phoneWithCountry);
    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public void startTimer(int resendtimer) {
        new CountDownTimer(resendtimer * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                textTimer.setText(getTranslatedString(R.string.text_resendin) + " " + checkDigit(time) + " " + getTranslatedString(R.string.text_seconds));
                time--;
            }

            public void onFinish() {
                textTimer.setText(getTranslatedString(R.string.txt_resend_code));
                time = 120;
            }
        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
