package taxi.ratingen.ui.companyvalidation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityCompanyValidationBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.companyvalidation.dialog.DialogCompanyValidation;
import taxi.ratingen.ui.splash.SplashScreen;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class CompanyValidationActivity extends BaseActivity<ActivityCompanyValidationBinding, CompanyValidationViewModel>
        implements CompanyValidationNavigator, HasAndroidInjector {

    @Inject
    CompanyValidationViewModel companyValidationViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    ActivityCompanyValidationBinding splashBinding;
    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = getViewDataBinding();
        Setup();
    }

    /** opens drawer activity **/
    @Override
    public void openDrawerActivity() {
        if (checkInternetEnabled()) {
            startActivity(new Intent(CompanyValidationActivity.this, SplashScreen.class));
            finish();
        } else
            showNetworkMessage();
    }

    /** show enter company key dialog **/
    @Override
    public void showRequestDialog() {
        DialogCompanyValidation.newInstance("").show(getSupportFragmentManager(), DialogCompanyValidation.TAG);
    }

    @Override
    public CompanyValidationViewModel getViewModel() {
        return companyValidationViewModel;
    }

    @Override
    public int getBindingVariable() {
        return taxi.ratingen.BR.viewModel;
    }

    /**
     * saves language
     * retrieves and saves FCM device token
     **/
    private void Setup() {
        Glide.with(this).load(R.raw.taxiappz1).into(splashBinding.imgSpl);
        companyValidationViewModel.setNavigator(this);
        companyValidationViewModel.getLanguagees();
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, SharedPrefence.EN);
        }
        getGCMDeviceToken();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_company_validation;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** code snippet to get and save FCM device token **/
    public void getGCMDeviceToken() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN))) {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( CompanyValidationActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken",newToken);
                    sharedPrefence.savevalue(SharedPrefence.FCMTOKEN, newToken);
                }
            });
        } else {
            Log.e("RefreshTokenOLD", sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetEnabled();
    }


    private void initiateNaviagation() {
        if (checkInternetEnabled()) {
            startActivity(new Intent(CompanyValidationActivity.this, SplashScreen.class));
            finish();
        } else
            showNetworkMessage();
    }

    /** gets current context **/
    @Override
    public Context getAttachedContext() {
        return this;
    }

    /** company key validity remaining days alert dialog **/
    @Override
    public void showExpiryMessage(String expirysoon) {
        final Dialog dialog = new Dialog(getAttachedContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.company_key_alert_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView) dialog.findViewById(R.id.empty_key)).setText(expirysoon);
        dialog.findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawerActivity();
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}
