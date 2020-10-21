package taxi.ratingen.ui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityLoginBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.forgot.ForgetPwdActivity;
import taxi.ratingen.ui.optionalscreen.OptionalAct;
import taxi.ratingen.ui.sociallogin.SigninSocialActivity;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements LoginNavigator {
    @Inject
    LoginViewModel loginViewModel;
    ActivityLoginBinding activityLoginBinding;
    @Inject
    SharedPrefence sharedPrefence;
    ScrollView sc_view;
    EditText login_emailorPhone;
//    private boolean isScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = getViewDataBinding();
        loginViewModel.setNavigator(this);
        Setup();

        login_emailorPhone = findViewById(R.id.login_emailorPhone);
        sc_view = findViewById(R.id.sc_view);


        login_emailorPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sc_view.scrollTo(0, sc_view.getBottom());
                    }
                }, 500);

                return false;
            }
        });
    }


    private void Setup() {
        activityLoginBinding.txtEnterSocialLogin.setText(Build.VERSION.SDK_INT >= 24 ? Html.fromHtml("or Enter " + getTranslatedString(R.string.text_social_media) + "", Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml("or Enter " + getTranslatedString(R.string.text_social_media) + ""));
        getGCMDeviceToken();
        setSupportActionBar(activityLoginBinding.loginToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
//        loginViewModel.getCurrentCountry();
        loginViewModel.getCountryList();
        activityLoginBinding.loginToolbar.toolbar.setTitle(getTranslatedString(R.string.text_log_in));
        activityLoginBinding.loginToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, OptionalAct.class));
                finish();
            }
        });
    }

    @Override
    public void setCountryFlag(String flag) {

    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public LoginViewModel getViewModel() {
        return loginViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void OpenDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class));
        finish();

    }

    @Override
    public void OpenForgotActivity() {
        startActivity(new Intent(this, ForgetPwdActivity.class));
    }

    @Override
    public void OpenSocialActivity() {
        startActivity(new Intent(this, SigninSocialActivity.class));
    }


    @Override
    public void OpenCountrycodevisible() {
        activityLoginBinding.loginCountrycode.setVisibility(View.VISIBLE);
        activityLoginBinding.loginFlag.setVisibility(View.VISIBLE);

    }

    @Override
    public void OpenCountrycodeINvisible() {
        activityLoginBinding.loginCountrycode.setVisibility(View.GONE);
        activityLoginBinding.loginFlag.setVisibility(View.GONE);
    }

    @Override
    public String getCountryCode() {
        return activityLoginBinding.loginCountrycode.getText().toString();
    }

  /*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_forgt_pwd_signin:
                startActivity(new Intent(this,ForgetPwdActivity.class));
                break;
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, OptionalAct.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
