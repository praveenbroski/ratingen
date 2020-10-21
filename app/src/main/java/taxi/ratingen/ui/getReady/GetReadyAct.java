package taxi.ratingen.ui.getReady;

import android.content.Intent;
import android.os.Bundle;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ReadyGoBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

//import org.jsoup.Jsoup;


/**
 * Created by naveen on 8/24/17.
 */

public class GetReadyAct extends BaseActivity<ReadyGoBinding, GetReadyViewModel> implements GetReadyNavigator {
    @Inject
    GetReadyViewModel getReadyViewModel;

    @Inject
    SharedPrefence sharedPrefence;
    ReadyGoBinding binding;
//    private CountryUtil mCountryUtil;

    //    GetVersionCode getVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        getReadyViewModel.setNavigator(this);

    }


    @Override
    public GetReadyViewModel getViewModel() {
        return getReadyViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ready_go;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public BaseActivity getAttachedContext() {
        return this;
    }

    @Override
    public void startMovingtoSignup() {
        initiateNaviagation();
    }



    /**
     * Opens {@link SignupActivity} after language configuration was complete
     **/
    private void initiateNaviagation() {
        sharedPrefence.saveBoolean(SharedPrefence.GetStartedScrnLoaded, true);
        startActivity(new Intent(GetReadyAct.this, DrawerAct.class));
        finish();
    }


}