package taxi.ratingen.ui.getstarted;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.app.MyApp;
import taxi.ratingen.databinding.ActivityGetStartedBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.ui.tour.TourGuide;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.language.LanguageUtil;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

//import org.jsoup.Jsoup;


/**
 * Created by naveen on 8/24/17.
 */

public class GetStartedScreen extends BaseActivity<ActivityGetStartedBinding, GetStartedViewModel> implements GetStartedNavigator {
    @Inject
    GetStartedViewModel emptyViewModel;

    @Inject
    SharedPrefence sharedPrefence;
    String language = "";
    ActivityGetStartedBinding binding;
//    private CountryUtil mCountryUtil;

    //    GetVersionCode getVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGCMDeviceToken();
        binding = getViewDataBinding();
        emptyViewModel.setNavigator(this);
        configureLanguage();
//        mCountryUtil = new CountryUtil(GetStartedScreen.this, Constants.COUNTRY_CODE);
//        mCountryUtil.initUIDisableEdit(this, this, binding.layoutSelectCountry);
        Resources resources = MyApp.getmContext().getResources();
        Configuration config = resources.getConfiguration();

        //get phone language
        if (config.locale.toString().contains("_")) {
            String[] phoneLang = config.locale.toString().split("_");
            Log.e("Phonelang===", phoneLang[0]);
            language = phoneLang[0];
        } else language = config.locale.toString();

        //  emptyViewModel.getLanguagees();

        emptyViewModel.selectLanguage();

    }

    /**
     * Sets language of the app
     **/
    private void configureLanguage() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)))
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, "en");

        String localLanguageType = sharedPrefence.Getvalue(SharedPrefence.LANGUANGE);

        if ("ar".equals(localLanguageType)) {
            Locale locale = Locale.getDefault();
            Locale.setDefault(new Locale("ar"));
            LanguageUtil.changeLanguageType(this, locale);
        } else if (!TextUtils.isEmpty(localLanguageType)) {
            Locale locale = Locale.getDefault();
            Locale.setDefault(new Locale(localLanguageType));
            LanguageUtil.changeLanguageType(this, locale);
        } else {
            LanguageUtil.changeLanguageType(this, Locale.ENGLISH);
        }
    }

    @Override
    public GetStartedViewModel getViewModel() {
        return emptyViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_started;
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

    Dialog dialog;

    /**
     * Sets adapter to language selector dialog
     **/
    @Override
    public void langguageItems(List<String> items) {
      /*  dialog = new Dialog(this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lang_dialog);*/

        binding.langRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        LanguageAdapter languageAdapter = new LanguageAdapter(getAttachedContext(), items, this);
        binding.langRecycler.setAdapter(languageAdapter);
    }

    /**
     * Set selected language to app
     **/
    @Override
    public void setSelectedLanguage(String s) {
        emptyViewModel.txt_Language_update.set(s);
        String loc;
        language = s;
        if (language.contains("pt")) {
            loc = "pt";
        } else if (language.contains("ar")) {
            loc = "ar";
        } else if (language.equalsIgnoreCase("Chinese")) {
            loc = "zh";
        } else if (language.contains("fr")) {
            loc = "fr";
        } else if (language.contains("es")) {
            loc = "es";
        } else if (language.contains("ja")) {
            loc = "ja";
        } else if (language.contains("ko")) {
            loc = "ko";
        } else {
            loc = "en";
        }
        sharedPrefence.savevalue(SharedPrefence.LANGUANGE, loc);
    }

    /**
     * Callback for results from previous screen
     *
     * @param requestCode ID of the request
     * @param resultCode  ID of the result
     * @param data        {@link Intent} with data from previous screen
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     /*   if (!CommonUtils.isGpscheck(SplashScreen.this)) {
            CommonUtils.ShowGpsDialog(SplashScreen.this);

        } else */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGranted(Constants.Array_permissions)) {
            requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        } else
            initiateNaviagation();
    }

    /**
     * Opens {@link SignupActivity} after language configuration was complete
     **/
    private void initiateNaviagation() {
        sharedPrefence.saveBoolean(SharedPrefence.GetStartedScrnLoaded, true);
        startActivity(new Intent(GetStartedScreen.this, TourGuide.class));
        finish();
    }


}