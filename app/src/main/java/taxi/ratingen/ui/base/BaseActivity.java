package taxi.ratingen.ui.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import taxi.ratingen.retro.responsemodel.NewRequestModel;
import taxi.ratingen.ui.companyvalidation.CompanyValidationActivity;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.nodriveralert.NoDriverAct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Driver;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.optionalscreen.OptionalAct;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.language.LanguageUtil;
import taxi.ratingen.utilz.language.MyContextWrapper;
import taxi.ratingen.utilz.NetworkUtils;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * Created by amitshekhar on 07/07/17.
 */

public abstract class BaseActivity<T extends ViewDataBinding, V> extends AppCompatActivity implements BaseFragment.Callback, BaseView {

    public static final int REQUEST_ENABEL_INTERNET = 199;
    public static final int REQUEST_ENABEL_LOCATION = 200;
    // TODO
    // this can probably depend on isLoading variable of BaseViewModel,
    // since its going to be common for all the activities
    private ProgressDialog mProgressDialog;

    private T mViewDataBinding;
    private V mViewModel;
    public String check;
//    public View mappBarlayout;
//    public Toolbar mToolbar;
    private Resources resources;

    @Inject
    SharedPrefence sharedPrefence;

    public Dialog dialog_internet;

    public HashMap<String, String> Bindabledata = new HashMap<>();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
      /*  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build());*/


        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(Constants.EXTRA_Data)) {
            Bindabledata = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.EXTRA_Data);
        }
        resources = getResources();
        performDependencyInjection();
        performDataBinding();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());

        this.mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.d("======", "attachBaseContext");
        Locale languageType = LanguageUtil.getLanguageType(this);
        super.attachBaseContext(MyContextWrapper.wrap(newBase, languageType));
    }

    /**
     * ask for run time permissions in Android 6 & above
     **/
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkGranted(String[] permissions) {

        for (String per : permissions) {

            if (checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkGranted(int[] permissions) {

        for (int per : permissions) {

            if (per != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    public void HideNshowToolbar(boolean hide) {
//        mappBarlayout.setVisibility(!hide ? View.GONE : View.VISIBLE);
    }

    /**
     * to reflect changes of Language in All screens if Arabic RTL
     */
    public void changeDiretionLanguage(Context context, Window window) {
        configureLanguage();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUANGE).equalsIgnoreCase("ar")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                window.getDecorView().setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                window.getDecorView().setTextDirection(View.TEXT_DIRECTION_LTR);
            }
        }


    }

    /**
     * configuring app language
     **/
    private void configureLanguage() {
        if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)))
            sharedPrefence.savevalue(SharedPrefence.LANGUANGE, "en");
        String localLanguageType = sharedPrefence.Getvalue(SharedPrefence.LANGUANGE);
        if ("ar".equalsIgnoreCase(localLanguageType)) {
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
    protected void onResume() {
        super.onResume();
        if (bcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(Constants.BroadcastsActions.B_REQUEST);
            LocalBroadcastManager.getInstance(this).registerReceiver(bcastReceiver, intentFilter);
        }
        IntentFilter intentFilter = new IntentFilter(Constants.BroadcastsActions.LATER_NO_DRIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(laterNoDriver, intentFilter);

        changeDiretionLanguage(this, getWindow());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(laterNoDriver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Mapscrn123", "Mapscrn123");

            removeWaitProgressDialog();
            removeRideConfirmationFragment();
            if (intent.hasExtra(Constants.EXTRA_Data)) {
                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = CommonUtils.getSingleObject(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null) {
                    if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                        //   Toast.makeText(BaseActivity.this, getTranslatedString(R.string.text_accepted), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    };

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void NeedRideFragment(final Type type) {

    }

    @Override
    public void setResultToDropAddress(String Address) {

    }

    @Override
    public void NeedTripFragment(Request request, Driver driver) {

    }

    public void NeedTripFragment(NewRequestModel request) {

    }

    public void NeedFeedbackFragment(Request request, boolean isCorporate) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentInternetFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentInternetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, intentInternetFilter);
    }

    @Override
    public void NeedHomeFragment() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(internetReceiver);
    }

    @Override
    public void changeTripnFeedback() {

    }

    public BroadcastReceiver internetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkInternetEnabled();
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * this hides soft keyboard when called
     **/
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * shows toast with translation text from string resource
     **/
    public void showMessage(int resId) {
        Toast.makeText(this, getTranslatedString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * shows toast with exception msg
     **/
    public void showMessage(CustomException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * returns true if internet connected, false if not connected
     **/
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    /**
     * shows toast with given string
     **/
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * shows circular progressbar
     **/
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    /**
     * hides circular progressbar if already showing
     **/
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * @return SharedPrefereance
     */
    public abstract SharedPrefence getSharedPrefence();

    public void performDependencyInjection() {
        AndroidInjection.inject(this);
    }

    /**
     * shows snackbar
     **/
    @Override
    public void showSnackBar(String message) {
        if (getViewModel() != null) {
            Snackbar snackbar = Snackbar.make((View) getViewModel(), message, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    /**
     * shows snackbar
     **/
    @Override
    public void showSnackBar(@NonNull View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sView = snackbar.getView();
        TextView tv = sView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    /**
     * shows network error msg
     **/
    @Override
    public void showNetworkMessage() {
        Toast.makeText(this, getTranslatedString(R.string.txt_NoInternet), Toast.LENGTH_SHORT).show();
    }

    /**
     * shows network enable dialog
     **/
    private void enableInternetDialog() {
        if (dialog_internet != null)
            if (!dialog_internet.isShowing()) {
                dialog_internet.show();
                return;
            } else
                return;

        dialog_internet = new Dialog(this);
        dialog_internet.setContentView(R.layout.dialog_enable_internet);
        if (dialog_internet.getWindow() != null) {
            dialog_internet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_internet.findViewById(R.id.btn_exit_internetialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog_internet.findViewById(R.id.btn_settings_internetdialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), REQUEST_ENABEL_INTERNET);
            }
        });
        dialog_internet.show();
        dialog_internet.setCanceledOnTouchOutside(false);
    }

    /**
     * checks if internet enabled if not shows the enable internet dialog
     **/
    public boolean checkInternetEnabled() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = true;
        result = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        if (!result)
            enableInternetDialog();
        else {
            if (dialog_internet != null)
                if (dialog_internet.isShowing()) {
                    dialog_internet.setCancelable(true);
                    dialog_internet.dismiss();
                }
        }

        return result;
    }

    /**
     * returns translated string for the given resource id
     **/
    public String getTranslatedString(int id) {
        if (resources.getResourceEntryName(id).equalsIgnoreCase("app_name") || resources.getResourceEntryName(id).equalsIgnoreCase("app_title"))
            return super.getString(id);
        else
            return getLocalizedString(resources.getResourceEntryName(id));
    }

    /**
     * returns string from translation sheet for given key
     **/
    public String getLocalizedString(String resId) {
        String result = resId;
        try {
            JSONObject jsonObject;
            if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
                jsonObject = new JSONObject(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)));
                result = jsonObject.optString(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(result))
                result = resId;
        }
        return result;

    }

    /**
     * receives broadcast if the same user logged in another device and logs out the user
     **/
    public BroadcastReceiver bcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String intentDAta = intent.getStringExtra(Constants.BroadcastsActions.B_REQUEST);
                if (!CommonUtils.IsEmpty(intentDAta)) {
                    Log.d("keys", "showingMessage:" + intentDAta);
                    BaseResponse model = CommonUtils.getSingleObject(intentDAta, BaseResponse.class);
                    if (model != null)
                        if (model.successMessage != null) {
                            if (model.successMessage.equalsIgnoreCase("another_user_loggedin"))
                                Toast.makeText(getBaseContext(), getTranslatedString(R.string.text_already_login), Toast.LENGTH_LONG).show();
                            sharedPrefence.savevalue(SharedPrefence.USERDETAILS, null);
                            sharedPrefence.savevalue(SharedPrefence.SOSLIST, null);
                            sharedPrefence.savevalue(SharedPrefence.LATITUDE, null);
                            sharedPrefence.savevalue(SharedPrefence.LONGITUDE, null);
                            startActivity(new Intent(BaseActivity.this, OptionalAct.class));
                            finish();
                        }
                }
            }
        }
    };


//    @Override
//    public Resources getResources() {
//        return new CustomResources(sharedPrefence, getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
//    }

    /**
     * generates HASH string for user registration & social media login
     **/
    public void generateKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key new ", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    /**
     * retrieves & saves Firebase Cloud Messaging device token for sending push notifications
     **/
    public void getGCMDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sharedPrefence.savevalue(SharedPrefence.FCMTOKEN, "" + token);
                        Log.e("RefreshTokenOLD", sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
                    }
                });


       /* if (CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN))) {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (!CommonUtils.IsEmpty(token)) {
                sharedPrefence.savevalue(SharedPrefence.FCMTOKEN, token);
                Log.e("RefreshToken", token);
            }
        } else {
            Log.e("RefreshTokenOLD", sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
        }*/
    }

    /**
     * receives broadcast when no driver found and shows no driver screen
     **/
    public BroadcastReceiver laterNoDriver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  if (MyApp.isIsNodDriveActDestroyed())

            if (!Constants.ACTIVITY_OPENEND_ALRDY)
                startActivity(new Intent(BaseActivity.this, NoDriverAct.class).putExtra("reqId", intent.getIntExtra("req_id", 0)));
        }
    };

    /**
     * removes waiting for driver screen
     **/
    public void removeWaitProgressDialog() {
        if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(WaitProgressDialog.TAG) != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .remove(getSupportFragmentManager().findFragmentByTag(WaitProgressDialog.TAG))
                    .commitAllowingStateLoss();
    }

    /**
     * removes confirm ride booking screen
     **/
    public void removeRideConfirmationFragment() {
        if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG) != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .remove(getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG))
                    .commitAllowingStateLoss();
    }

    /**
     * opens an activity to refresh company key
     **/
    @Override
    public void refreshCompanyKey() {
        startActivity(new Intent(this, CompanyValidationActivity.class));
        finish();
    }

    public void stopTypesTimerNow() {

    }

}

