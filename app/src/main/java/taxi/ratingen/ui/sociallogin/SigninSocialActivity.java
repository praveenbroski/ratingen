package taxi.ratingen.ui.sociallogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

import javax.inject.Inject;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivitySocialSigninBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.registration.RegistrationAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

public class SigninSocialActivity extends BaseActivity<ActivitySocialSigninBinding,SiginSocialViewModel> implements GoogleApiClient.OnConnectionFailedListener, SiginSocialNavigator {

    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    SiginSocialViewModel siginSocialViewModel;
    ActivitySocialSigninBinding activitySocialSigninBinding;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        siginSocialViewModel.setNavigator(this);
        activitySocialSigninBinding =getViewDataBinding();
        Setup();
    }

    private void Setup() {
        generateKeyHash();
        setSupportActionBar(activitySocialSigninBinding.socialToolbar.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        activitySocialSigninBinding.socialToolbar.toolbar.setTitle(getTranslatedString(R.string.Txt_title_SocialLogin));


        activitySocialSigninBinding.socialToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public SiginSocialViewModel getViewModel() {
        return siginSocialViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_social_signin;
    }
    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Called when facebook login is clicked **/
    @Override
    public void Facebookclicked() {
        if (siginSocialViewModel != null)
            siginSocialViewModel.setIsLoading(true);
    }

    /** Called when Google login is clicked **/
    @Override
    public void gplusclicked() {
        if (siginSocialViewModel != null)
            siginSocialViewModel.setIsLoading(true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
    }

    /** Called to open {@link RegistrationAct} **/
    @Override
    public void OpenRegistration(HashMap<String,String> hm) {
        Intent intent=new Intent(this, RegistrationAct.class);
        intent.putExtra("Data_Social",hm);
        startActivity(intent);
    }

    @Override
    public void OpenDrawerAct() {
        startActivity(new Intent(this, DrawerAct.class));
    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (siginSocialViewModel != null)
            siginSocialViewModel.setIsLoading(false);
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            siginSocialViewModel.handleSignInResult(result);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
