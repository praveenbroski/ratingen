package taxi.ratingen.ui.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivitySignupBinding;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.login.LoginActivity;
import taxi.ratingen.ui.otpscreen.OTPActivity;
import taxi.ratingen.ui.signup.country_code.CountryListDialog;
import taxi.ratingen.ui.sociallogin.SigninSocialActivity;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

/**
 * Created by naveen on 8/24/17.
 */

public class
SignupActivity extends BaseActivity<ActivitySignupBinding, SignupViewModel> implements SignupNavigator, HasAndroidInjector {
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    SignupViewModel loginViewModel;
    ActivitySignupBinding activitySignupBinding;
    EditText edt_text;
    private static final int RC_APP_UPDATE = 8100;
    private static final String TAG = "MainActivity";
    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;
    private AppUpdateManager mAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = getViewDataBinding();
        loginViewModel.setNavigator(this);
        Setup();
        activitySignupBinding.signupEmailorPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activitySignupBinding.scrollRegistration.scrollTo(0, activitySignupBinding.scrollRegistration.getBottom());
                    }
                }, 500);

                return false;
            }
        });
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, SignupActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                Log.e(TAG, "Update: Installed");
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (sharedPrefence.Getvalue(SharedPrefence.DEFAULT_COUNTRY) != null)
//            Constants.COUNTRY_CODE = sharedPrefence.Getvalue(SharedPrefence.DEFAULT_COUNTRY);

    }

    /**
     * Retrieves FCM device token,
     * Get current country code
     * **/
    private void Setup() {
        getGCMDeviceToken();
//        if (sharedPrefence.Getvalue(SharedPrefence.DEFAULT_COUNTRY) != null)
//            Constants.COUNTRY_CODE = sharedPrefence.Getvalue(SharedPrefence.DEFAULT_COUNTRY);
//        else
//        loginViewModel.getCurrentCountry();
        loginViewModel.getCountryListApi();
//        mCountryUtil = new CountryUtil(SignupActivity.this, Constants.COUNTRY_CODE);
//        mCountryUtil.initUI(activitySignupBinding.editCountryCodeSignup, this, activitySignupBinding.signupFlag);
//        mCountryUtil.initCodes(SignupActivity.this);
        edt_text = activitySignupBinding.signupEmailorPhone;
    }


    @Override
    public void setCountryCode(String countryCode) {

    }
    @Override
    public void openCountryDialog(ArrayList<CountryListModel> list) {
        CountryListDialog newInstance = CountryListDialog.newInstance(list, 0);
        newInstance.show(getSupportFragmentManager());
    }

    /** Opens {@link OTPActivity} when called
     * @param b boolean parameter
     * @param s String parameter **/
    @Override
    public void openOtpPage(boolean b, String s, String iso2) {

//        startActivity(new Intent(this, GetReadyAct.class));
        startActivity(new Intent(this, OTPActivity.class)
                .putExtra(Constants.isLogin, b)
                .putExtra(Constants.PhonewithCountry, s).putExtra(Constants.Country,  iso2));

    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public SignupViewModel getViewModel() {
        return loginViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Opens {@link OTPActivity} when called
     * @param isSignup boolean parameter **/
    @Override
    public void openOtpActivity(boolean isSignup) {
        startActivity(new Intent(this, OTPActivity.class).putExtra(Constants.EXTRA_Data, loginViewModel.getMap()));
    }

    /** Opens {@link SigninSocialActivity} when called **/
    @Override
    public void openSocialActivity() {
        startActivity(new Intent(this, SigninSocialActivity.class));
    }

    /** Opens {@link LoginActivity} to login with password **/
    public void openSignInPwdActivity() {

        loginViewModel.getMap().put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
        startActivity(new Intent(this, LoginActivity.class).putExtra(Constants.EXTRA_Data, loginViewModel.getMap()));
//        finish();
    }

    /** Called to hide soft keyboard **/
    @Override
    public void HideKeyBoard() {
        hideKeyboard();
    }


    String CountryCode, countryShort;

    /** Get currently selected country code **/
    @Override
    public String getCountryCode() {
        return CountryCode != null ? (CountryCode.replaceAll(" ", "")) : "";
    }

    /** Returns short country code **/
    @Override
    public String getCountryShortName() {
        return countryShort;
    }


    /** When a phone number is already registered while trying to sign up this shows {@link AlertDialog} to ask if the user want to sign in **/
    @Override
    public void openAlertToLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setMessage(R.string.text_user_exists_alert);
//        builder.setTitle("");
        builder.setPositiveButton(R.string.txt_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                openSignInPwdActivity();
            }
        }).setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void onCountrySelected(String flag, String code, String name, String countryId, String iso2) {
        CountryCode = code;
        this.countryShort = iso2;
        loginViewModel.countryFlag.set(flag);
        loginViewModel.Countrycode.set(code);
        loginViewModel.CountryId.set(countryId);
        loginViewModel.CountryShort.set(iso2);
    }
    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}