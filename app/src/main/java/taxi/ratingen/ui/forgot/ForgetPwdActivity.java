package taxi.ratingen.ui.forgot;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;

//import com.nplus.countrylist.CountryUtil;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityForgotBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

import taxi.ratingen.ui.signup.SignupActivity;

public class ForgetPwdActivity extends BaseActivity<ActivityForgotBinding, ForgotViewModel> implements ForgotNavigator {

    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    ForgotViewModel forgotViewModel;
//    private CountryUtil mCountryUtil;
    ActivityForgotBinding activityForgotBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotBinding = getViewDataBinding();
        forgotViewModel.setNavigator(this);
        forgotViewModel.getCurrentCountry();
        Setup();

    }

    /** Initializes {@link androidx.appcompat.widget.Toolbar} **/
    private void Setup() {
        setSupportActionBar(activityForgotBinding.forgotToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        activityForgotBinding.forgotToolbar.toolbar.setTitle(getTranslatedString(R.string.Txt_Forgot));
        activityForgotBinding.forgotToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /** Initializes country code selector dialog
     * @param flag Default country **/
    @Override
    public void setCountryFlag(String flag) {
//        mCountryUtil = new CountryUtil(ForgetPwdActivity.this, Constants.COUNTRY_CODE);
//        mCountryUtil.initUI(activityForgotBinding.forgotCountrycode, activityForgotBinding.forgotFlag);
//        mCountryUtil.initCodes(ForgetPwdActivity.this);
    }

    @Override
    public ForgotViewModel getViewModel() {
        return forgotViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Finishes current activity to re-open {@link SignupActivity} **/
    @Override
    public void openLoginActivity() {
      /*  startActivity(new Intent(this, SignupActivity.class));*/
        finish();
    }

    /** Hides country code selector **/
    @Override
    public void OpenCountrycodeINvisible() {
        activityForgotBinding.forgotCountrycode.setVisibility(View.GONE);
        activityForgotBinding.forgotFlag.setVisibility(View.GONE);
    }

    /** Shows country code selector **/
    @Override
    public void OpenCountrycodevisible() {
        activityForgotBinding.forgotCountrycode.setVisibility(View.VISIBLE);
        activityForgotBinding.forgotFlag.setVisibility(View.VISIBLE);
    }

    /** Returns currently selected country code **/
    @Override
    public String getCountryCode() {
        return activityForgotBinding.forgotCountrycode.getText().toString();
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
