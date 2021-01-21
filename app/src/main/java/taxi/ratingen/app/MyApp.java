package taxi.ratingen.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.izettle.payments.android.sdk.IZettleSDK;
import com.izettle.payments.android.ui.SdkLifecycle;

import dagger.android.HasAndroidInjector;
import taxi.ratingen.R;
import taxi.ratingen.ui.nodriveralert.NoDriverAct;
import taxi.ratingen.ui.topdriver.TopDriverAct;
import taxi.ratingen.di.component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;

/**
 * Created by root on 9/22/17.
 */

public class MyApp extends Application implements HasAndroidInjector, Application.ActivityLifecycleCallbacks {

    @Inject
    DispatchingAndroidInjector<Object> activityDispatchingAndroidInjector;
    private static Context mContext;

    Activity topDriverAct;
    static boolean isDriverEnabled = false;
    static boolean isNodDriveActDestroyed = true;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);

        mContext = this;
        registerActivityLifecycleCallbacks(this);
        initIZettleSDK();
    }

    private void initIZettleSDK() {
        String clientId = getString(R.string.client_id);
        String scheme = getString(R.string.redirect_url_scheme);
        String host = getString(R.string.redirect_url_host);
        String redirectUrl = scheme + "://" + host;
        IZettleSDK.Instance.init(this, clientId, redirectUrl);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new SdkLifecycle(IZettleSDK.Instance));
    }

    public static Context getmContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof TopDriverAct) {
            topDriverAct = activity;
            isDriverEnabled = true;
        }else if (activity instanceof NoDriverAct)
            isNodDriveActDestroyed=false;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof TopDriverAct) {
            topDriverAct = activity;
            Log.e("ActResumed", "Resume");
            isDriverEnabled = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("ActPaused", "Pause");

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof NoDriverAct)
            isNodDriveActDestroyed = true;
    }

    public static boolean isIsNodDriveActDestroyed() {
        return isNodDriveActDestroyed;
    }

    public static void setIsNodDriveActDestroyed(boolean isNodDriveActDestroyed) {
        MyApp.isNodDriveActDestroyed = isNodDriveActDestroyed;
    }

    public static boolean clearMethd() {
        return isDriverEnabled;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return activityDispatchingAndroidInjector;
    }
}
