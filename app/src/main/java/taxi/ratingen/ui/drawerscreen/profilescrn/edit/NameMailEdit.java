package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.library.baseAdapters.BR;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityNameMailEditBinding;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.signup.country_code.CountryListDialog;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class NameMailEdit extends BaseActivity<ActivityNameMailEditBinding, NameMailModel> implements NameMailNavigator, HasAndroidInjector {

    public static final String TAG = "NameMailEdit";
    @Inject
    NameMailModel nameMailModel;

    ActivityNameMailEditBinding activityNameMailBinding;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    String CountryCode, countryShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNameMailBinding = getViewDataBinding();
        nameMailModel.setNavigator(this);
        setup();
    }

    private void setup() {
        setSupportActionBar(activityNameMailBinding.layoutToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        activityNameMailBinding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.back_nav);
        activityNameMailBinding.layoutToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        Bundle bundle = getIntent().getExtras();
        nameMailModel.mode.set(bundle.getInt(Constants.EXTRA_MODE));
        nameMailModel.extraData.set(bundle.getString(Constants.EXTRA_VALUE));
        if (nameMailModel.extraData.get().length() > 0) {
            nameMailModel.isSubmitEnable.set(true);
        }
        if (nameMailModel.mode.get() == 3)
            nameMailModel.getCountryListApi();
    }

    @Override
    public NameMailModel getViewModel() {
        return nameMailModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_name_mail_edit;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void clearText() {
        activityNameMailBinding.etUpdateVal.setText("");
    }

    @Override
    public void finishAct() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void SendBroadcast() {
        Intent intent = new Intent(Constants.ProfileBroastcast);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void openCountryDialog(ArrayList<CountryListModel> list) {
        CountryListDialog newInstance = CountryListDialog.newInstance(list, 1);
        newInstance.show(getSupportFragmentManager());
    }

    @Override
    public void openOtpPage(String s) {

    }

    public void onCountrySelected(String flag, String code, String name, String countryId, String iso2) {
        CountryCode = code;
        this.countryShort = iso2;
        nameMailModel.countryFlag.set(flag);
        nameMailModel.Countrycode.set(code);
        nameMailModel.CountryId.set(countryId);
        nameMailModel.CountryShort.set(iso2);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

}
