package taxi.ratingen.ui.otpscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityOtpBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.getReady.GetReadyAct;
import taxi.ratingen.ui.registration.RegistrationAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.PhoneAuthProvider;

import javax.inject.Inject;

public class OTPActivity extends BaseActivity<ActivityOtpBinding, OTPViewModel> implements OTPNavigator {

    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    OTPViewModel OTPViewModel;
    ActivityOtpBinding activityOtpBinding;
    OtpView optview;
    int time = 120;
    TextView textTimer;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String phoneWithCountry = "";
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpBinding = getViewDataBinding();
        optview = activityOtpBinding.optCustomview;
        optview.setOTPFilledObservable(getViewModel().isOTPDone);
        OTPViewModel.setNavigator(this);
        OTPViewModel.startTimerTwoMinuts();
        startTimer(time);
        getGCMDeviceToken();
        textTimer = findViewById(R.id.timertxt);
        if (getIntent() != null) {
            OTPViewModel.phoneNumber.set(getIntent().getStringExtra(Constants.phoneNum));
            OTPViewModel.UUIDValue.set(getIntent().getStringExtra(Constants.uuidValue));
            OTPViewModel.RegOrLogin.set(getIntent().getStringExtra(Constants.regOrLogin));
            OTPViewModel.CountryId.set(getIntent().getStringExtra(Constants.countryId));
            OTPViewModel.CountryPrefix.set(getIntent().getStringExtra(Constants.phonePrefix));
        }

        activityOtpBinding.backImg.setOnClickListener(view -> finish());
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
    public void openSignUpActivity() {
        finish();
        startActivity(new Intent(this, RegistrationAct.class)
                .putExtra(Constants.phoneNum, OTPViewModel.phoneNumber.get())
                .putExtra(Constants.uuidValue, OTPViewModel.UUIDValue.get())
                .putExtra(Constants.countryId, OTPViewModel.CountryId.get())
                .putExtra(Constants.phonePrefix, OTPViewModel.CountryPrefix.get()));
    }

    @Override
    public void openHomeActivity() {
        finish();
        startActivity(new Intent(this, GetReadyAct.class));
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
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public void openResendAlert() {
        final BottomSheetDialog dialog_resend = new BottomSheetDialog(this);
        View child = getLayoutInflater().inflate(R.layout.dialog_resend, null);
        dialog_resend.setContentView(child);
        TextView resend_phone = dialog_resend.findViewById(R.id.resend_txt_num);
        TextView resendAt = dialog_resend.findViewById(R.id.resend_txt);
        Button resend = dialog_resend.findViewById(R.id.resend_butt);
        Button cancelButton = dialog_resend.findViewById(R.id.cancel_butt);

        resendAt.setText(getTranslatedString(R.string.txt_resend_to));
        resend.setText(getTranslatedString(R.string.txt_resend_code));
        cancelButton.setText(getTranslatedString(R.string.txt_cancel));

        resend_phone.setText(OTPViewModel.phoneNumber.get());
        resend.setOnClickListener(view -> {
            OTPViewModel.ResendOtpApi();
            dialog_resend.dismiss();
        });
        cancelButton.setOnClickListener(view -> {
            OTPViewModel.resendClicked.set(false);
            dialog_resend.dismiss();
        });
        dialog_resend.setCancelable(false);
        dialog_resend.setCanceledOnTouchOutside(false);
        dialog_resend.show();
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
