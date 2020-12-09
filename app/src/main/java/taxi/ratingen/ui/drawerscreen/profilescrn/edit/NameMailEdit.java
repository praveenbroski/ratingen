package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityNameMailEditBinding;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.otpscreen.OTPActivity;
import taxi.ratingen.ui.signup.country_code.CountryListDialog;
import taxi.ratingen.utilz.CommonUtils;
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

    ArrayList<CountryListModel> countryListModels = new ArrayList<>();

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
        int mode = bundle.getInt(Constants.EXTRA_MODE);

        nameMailModel.mode.set(mode);
        if (bundle.getString(Constants.EXTRA_VALUE) != null) {
            nameMailModel.extraData.set(bundle.getString(Constants.EXTRA_VALUE));
            if (mode == 3)
                nameMailModel.extraMobile.set(bundle.getString(Constants.EXTRA_VALUE));
            if (nameMailModel.extraData.get().length() > 0) {
                nameMailModel.isSubmitEnable.set(true);
            }
        }
        if (nameMailModel.mode.get() == 3)
            nameMailModel.getCountryListApi();

        nameMailModel.txtTitle.set(mode == 0 ? getTranslatedString(R.string.txt_update_name)
                : mode == 1 ? getTranslatedString(R.string.txt_update_last_name)
                : mode == 2 ? getTranslatedString(R.string.txt_update_email)
                : mode == 3 ? getTranslatedString(R.string.txt_update_phone) : "");

        nameMailModel.txtDescription.set(mode == 0 ? getTranslatedString(R.string.txt_update_user_name_desc)
                : mode == 1 ? getTranslatedString(R.string.txt_update_user_last_name_desc)
                : mode == 2 ? getTranslatedString(R.string.txt_update_email_desc)
                : mode == 3 ? getTranslatedString(R.string.txt_update_phone_desc) : "");
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
    public void openOtpPage(String uuid_value, Integer toRegOrLog, String phoneNum, String countryId, String countryPrefix) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra(Constants.uuidValue, uuid_value);
        intent.putExtra(Constants.regOrLogin, "" + toRegOrLog);
        intent.putExtra(Constants.phoneNum, phoneNum);
        intent.putExtra(Constants.countryId, countryId);
        intent.putExtra(Constants.phonePrefix, countryPrefix);
        startActivityForResult(intent, Constants.REQUEST_MOBILE_VALIDATE);
    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public void countryResponse(Object data) {
        String strCountryBase = CommonUtils.ObjectToString(data);
        BaseResponse countryBase = new Gson().fromJson(strCountryBase, BaseResponse.class);
        String countryArray = CommonUtils.arrayToString((ArrayList<Object>) countryBase.data);
        countryListModels.addAll(CommonUtils.stringToArray(countryArray, CountryListModel[].class));
        for (int i = 0; i < countryListModels.size(); i++) {
            if (countryListModels.get(i).name.equalsIgnoreCase(Constants.DefaultcountryName)) {
                Constants.CountryID = countryListModels.get(i).id + "";
                Constants.CLICKEDCOUNTRYCODE = countryListModels.get(i).dialCode;
                nameMailModel.countryCode.set(Constants.CLICKEDCOUNTRYCODE);
                nameMailModel.CountryId.set(countryListModels.get(i).id + "");
                nameMailModel.countryShort.set(countryListModels.get(i).code);
                nameMailModel.countryFlag.set(countryListModels.get(i).flag);
                break;
            }
        }
    }

    @Override
    public View getBaseView() {
        return activityNameMailBinding.rlBase;
    }

    public void onCountrySelected(String flag, String code, String name, String countryId, String iso2) {
        CountryCode = code;
        this.countryShort = iso2;
        nameMailModel.countryFlag.set(flag);
        nameMailModel.countryCode.set(code);
        nameMailModel.CountryId.set(countryId);
        nameMailModel.countryShort.set(iso2);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_MOBILE_VALIDATE) {
                nameMailModel.callMobileUpdate();
            }
        }
    }

}
