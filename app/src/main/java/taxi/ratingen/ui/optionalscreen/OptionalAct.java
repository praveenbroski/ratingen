package taxi.ratingen.ui.optionalscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityOptionalBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.login.LoginActivity;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

public class OptionalAct extends BaseActivity<ActivityOptionalBinding,OptionalViewModel> implements OptionalNavigator {

    @Inject
    OptionalViewModel optionalViewModel;

    @Inject
    SharedPrefence sharedPrefence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        optionalViewModel.setNavigator(this);
    }

    @Override
    public OptionalViewModel getViewModel() {
        return optionalViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_optional;
    }
    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Opens {@link LoginActivity} **/
    @Override
    public void openLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /** Opens {@link SignupActivity} **/
    @Override
    public void openSigupScreen() {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
