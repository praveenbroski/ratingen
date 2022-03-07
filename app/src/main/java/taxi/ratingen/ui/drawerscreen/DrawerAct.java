package taxi.ratingen.ui.drawerscreen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.library.baseAdapters.BR;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.braintreepayments.api.dropin.DropInActivity;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityDrawerBinding;
import taxi.ratingen.databinding.NavHeaderDrawerBinding;
import taxi.ratingen.retro.responsemodel.Driver;
import taxi.ratingen.retro.responsemodel.DriverData;
import taxi.ratingen.retro.responsemodel.NewRequestModel;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.PayTest;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.complaint.ComplaiintFrag;
import taxi.ratingen.ui.drawerscreen.faq.FaqFragment;
import taxi.ratingen.ui.drawerscreen.favorites.FavoriteFragment;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.AddFavFragment;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap.PickFromMapFragment;
import taxi.ratingen.ui.drawerscreen.feedback.FeedbackFragment;
import taxi.ratingen.ui.drawerscreen.history.HistoryListFrag;
import taxi.ratingen.ui.drawerscreen.mapscreen.MapFragment;
import taxi.ratingen.ui.drawerscreen.mapscreen.destination.DestinationFragment;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrn;
import taxi.ratingen.ui.drawerscreen.notification.NotificationlistFrag;
import taxi.ratingen.ui.drawerscreen.payment.PaymentFrag;
import taxi.ratingen.ui.drawerscreen.profilescrn.ProfileAct;
import taxi.ratingen.ui.drawerscreen.refferalscreen.RefferalCodeFrag;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.drawerscreen.setting.SettingFrag;
import taxi.ratingen.ui.drawerscreen.sos.SosFragment;
import taxi.ratingen.ui.drawerscreen.support.SupportFrag;
import taxi.ratingen.ui.drawerscreen.tripscreen.TripFragment;
import taxi.ratingen.ui.drawerscreen.walletscreen.WalletAct;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class DrawerAct extends BaseActivity<ActivityDrawerBinding, DrawerViewModel>
        implements NavigationView.OnNavigationItemSelectedListener, DrawerNavigator, HasAndroidInjector,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "DrawerAct";
    public static ObservableBoolean activityToBeRefre = new ObservableBoolean(true);
    @Inject
    DrawerViewModel drawerViewModel;

    @Inject
    SharedPrefence sharedPrefence;

    AppUpdater appUpdater;

    @Inject
    Gson gson;

    private boolean isTriporFeedback;
    AlertDialog alertDialog = null;

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;

    FragmentManager fragmentManager;
    private DrawerLayout mDrawer;
    boolean doubleBackToExitPressedOnce = false;
    private GoogleApiClient mGoogleApiClient;

    private NavigationView mNavigationView;

    public ActivityDrawerBinding activityDrawerBinding;

    /**
     * Registers {@link BroadcastReceiver}s
     * Initializes {@link GoogleApiClient}
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDrawerBinding = getViewDataBinding();
        drawerViewModel.setNavigator(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.ProfileBroastcast));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRemoveWaitingDialog,
                new IntentFilter(Constants.BroadcastsActions.RemoveWaitingDialog));

        fragmentManager = getSupportFragmentManager();
        buildGoogleApiClient();
        setUp();
    }


    @Override
    public DrawerViewModel getViewModel() {
        return drawerViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_drawer;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /**
     * Phone back button click callback. Handles fragments navigation
     **/
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(RideConfirmationFragment.TAG);
        Fragment destFragment = fragmentManager.findFragmentByTag(DestinationFragment.TAG);
//        Fragment fragment1 = fragmentManager.findFragmentByTag(RideFragment.TAG);
        boolean isChanged = false;
        if (fragment == null && destFragment == null) {
            for (Fragment f : getSupportFragmentManager().getFragments()) {
                if (f != null)
                    switch (f.getTag()) {
                        case TripFragment.TAG:
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                            isChanged = true;
                            break;
                        case SosFragment.TAG:
                            startActivity(new Intent(this, DrawerAct.class));
                            getSupportFragmentManager().beginTransaction().remove(f).commit();
                            isChanged = true;
                            break;
                        case MapScrn.TAG:
                        case FeedbackFragment.TAG:
//                            setTitle(getTranslatedString(R.string.app_name));
                            break;
                        case SettingFrag.TAG:
                        case ComplaiintFrag.TAG:
                        case HistoryListFrag.TAG:
                        case PaymentFrag.TAG:
                        case RefferalCodeFrag.TAG:
                        case FaqFragment.TAG:
                        case FavoriteFragment.TAG:
                        case AddFavFragment.TAG:
                        case PickFromMapFragment.TAG:
                        case SupportFrag.TAG:
                        case NotificationlistFrag.TAG:
                            getSupportFragmentManager().beginTransaction().remove(f).commit();
                            isChanged = true;
                            break;
                    }
            }

            if (!isChanged) {
                if (doubleBackToExitPressedOnce) {
                    //  super.onBackPressed();
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                    //    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getTranslatedString(R.string.text_double_tap_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        } else {
            if (fragment != null) {
                onFragmentDetached(RideConfirmationFragment.TAG);
            } else {
                onFragmentDetached(DestinationFragment.TAG);
            }
        }

    }

    /**
     * Unregisters {@link BroadcastReceiver}s
     **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdater != null)
            appUpdater.stop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRemoveWaitingDialog);
    }

    /**
     * Initializes {@link Menu}
     *
     * @param menu {@link Menu}
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return false;
    }

    /**
     * Initializes {@link DrawerLayout}
     **/
    private void setUp() {
        isTriporFeedback = false;
        mDrawer = activityDrawerBinding.drawerLayout;
//        mappBarlayout = activityDrawerBinding.menuHam;
        mNavigationView = activityDrawerBinding.navView;
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideKeyboard();
                drawerViewModel.GetProfileRatings();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        setupNavMenu();
        getGCMDeviceToken();
        drawerViewModel.GetFavList();
        drawerViewModel.RequestInProNetwork();
        drawerViewModel.setupProfile();
    }

    /**
     * Callback when a {@link MenuItem} is clicked
     *
     * @param item {@link MenuItem}
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback for {@link MenuItem} click in {@link DrawerLayout} menus
     **/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = fragmentManager.findFragmentByTag(TripFragment.TAG);
        Fragment fragment1 = fragmentManager.findFragmentByTag(FeedbackFragment.TAG);
        if (fragment != null && fragment instanceof TripFragment) {
            isTriporFeedback = true;
        } else if (fragment1 != null && fragment1 instanceof FeedbackFragment) {
            isTriporFeedback = true;
        }
        if (id == R.id.nav_Home) {
            drawerViewModel.RequestInProNetwork();
        } else if (id == R.id.nav_logout) {
            drawerViewModel.ShowLogoutAlertDialog(this);
        } else if (id == R.id.nav_Wallet) {
            // startActivity(new Intent(DrawerAct.this, WalletHistoryAct.class).putExtra("id", sharedPrefence.Getvalue(SharedPrefence.ID)).putExtra("token", sharedPrefence.Getvalue(SharedPrefence.TOKEN)));

            startActivityForResult(new Intent(this, WalletAct.class), Constants.WALLETSETRESULT);
        } else if (id == R.id.nav_Referralcode) {
            openRefferalFragment();
        } else if (id == R.id.nav_Payment) {
            if (!isTriporFeedback) {
                openPaymentFrgament();
            }
        } else if (id == R.id.nav_Sos) {
            openSOSFragment();
        } else if (id == R.id.nav_Complaints) {
            openComplaintsFragment();
        } else if (id == R.id.nav_Setting) {
            if (!isTriporFeedback)
                openSettingFragment();
        } else if (id == R.id.nav_History) {
            openHistoryFragment();
        } else if (id == R.id.nav_faq) {
            openFaqFragment();
        } else if (id == R.id.nav_Profile) {
            startActivity(new Intent(this, ProfileAct.class));
        } else if (id == R.id.nav_Favs) {
            openFavsFragment();
        } else if (id == R.id.nav_Notifications) {
            openNotificationsFragment();
        } else if (id == R.id.nav_about) {
            openAboutFragment();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFavsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, FavoriteFragment.newInstance(1), FavoriteFragment.TAG)
                .commit();
    }

    private void openNotificationsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, NotificationlistFrag.newInstance(), FavoriteFragment.TAG)
                .commit();
    }

    private void openAboutFragment() {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .disallowAddToBackStack()
//                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
//                .add(R.id.Container, NotificationlistFrag.newInstance(), FavoriteFragment.TAG)
//                .commit();

        startActivity(new Intent(this, PayTest.class));
    }

    /**
     * Opens {@link HistoryListFrag}
     **/
    private void openHistoryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, HistoryListFrag.newInstance("", ""), HistoryListFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link FaqFragment}
     **/
    private void openFaqFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, FaqFragment.newInstance(), FaqFragment.TAG)
                .commit();
    }

    /**
     * Opens {@link SettingFrag}
     **/
    private void openSettingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, SettingFrag.newInstance("", ""), SettingFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link ComplaiintFrag}
     **/
    private void openComplaintsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, ComplaiintFrag.newInstance("", ""), ComplaiintFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link SosFragment}
     **/
    private void openSOSFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, SosFragment.newInstance("", ""), SosFragment.TAG)
                .commit();
    }

    /**
     * Opens {@link PaymentFrag}
     **/
    private void openPaymentFrgament() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, PaymentFrag.newInstance("", ""), PaymentFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link RefferalCodeFrag}
     **/
    private void openRefferalFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, RefferalCodeFrag.newInstance("", ""), RefferalCodeFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link SupportFrag}
     **/
    private void openSupportFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, SupportFrag.newInstance("", ""), SupportFrag.TAG)
                .commit();
    }

    /**
     * Opens {@link MapScrn}
     **/
    private void openMapFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .replace(R.id.Container, MapFragment.newInstance("", "", ""), MapFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * Sets up {@link NavigationView} {@link MenuItem}'s translated labels and listener
     **/
    private void setupNavMenu() {
        NavHeaderDrawerBinding navHeaderDrawerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_drawer, activityDrawerBinding.navView, false);
        activityDrawerBinding.navView.addHeaderView(navHeaderDrawerBinding.getRoot());
        navHeaderDrawerBinding.setViewModel(drawerViewModel);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUANGE).equalsIgnoreCase("ar"))
            navHeaderDrawerBinding.name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        else navHeaderDrawerBinding.name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Home).setTitle(getTranslatedString(R.string.txt_booking_page));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Profile).setTitle(getTranslatedString(R.string.txt_my_profile));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_History).setTitle(getTranslatedString(R.string.txt_my_rides));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Favs).setTitle(getTranslatedString(R.string.txt_Fav_side_menu));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Wallet).setTitle(getTranslatedString(R.string.txt_wall_pay));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Notifications).setTitle(getTranslatedString(R.string.txt_noitification));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Complaints).setTitle(getTranslatedString(R.string.txt_support));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Referralcode).setTitle(getTranslatedString(R.string.text_title_Referralcode));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Sos).setTitle(getTranslatedString(R.string.txt_sos));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Setting).setTitle(getTranslatedString(R.string.txt_Setting));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_faq).setTitle(getTranslatedString(R.string.text_faq));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_about).setTitle(getTranslatedString(R.string.txt_about_us));
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_logout).setTitle(getTranslatedString(R.string.txt_logout));

        /* hiding unwanted menus */
//        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_History).setVisible(false);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Favs).setVisible(false);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Wallet).setVisible(false);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Notifications).setVisible(false);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Complaints).setVisible(false);
//        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_about).setVisible(false);
//        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Sos).setVisible(false);
        activityDrawerBinding.navView.getMenu().findItem(R.id.nav_faq).setVisible(false);
    }

    /**
     * Code snippet to open {@link MapScrn} fragment
     **/
    public void showMapFragment() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, MapFragment.newInstance("", "", drawerViewModel.Imageurl.get()), MapFragment.TAG)
                        .commitAllowingStateLoss();
            }
        }, 100);

//        cancelDialogFrag.newInstance("25","").show(getSupportFragmentManager());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .disallowAddToBackStack()
//                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
//                        .replace(R.id.Container, MapScrn.newInstance("", ""), MapScrn.TAG)
//                        .commitAllowingStateLoss();
//            }
//        }, 100);
    }

    /**
     * Connection successful callback for {@link GoogleApiClient}. Here we initialize {@link LocationRequest} to get location updates
     **/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
           /* Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);*/
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);


        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Initializes {@link GoogleApiClient}
     **/
    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void lockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Call this method to lock unlock the drawer
     **/
    public void unlockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Called when logout is clicked
     **/
    @Override
    public void logout() {
        sharedPrefence.saveBoolean(SharedPrefence.IS_CORPORATE_USER, false);
        sharedPrefence.savevalue(SharedPrefence.USERDETAILS, null);
        sharedPrefence.savevalue(SharedPrefence.SOSLIST, null);
        sharedPrefence.savevalue(SharedPrefence.LATITUDE, null);
        sharedPrefence.savevalue(SharedPrefence.LONGITUDE, null);
        sharedPrefence.savevalue(SharedPrefence.AccessToken, null);
        openOptionalActivity();
    }

    /**
     * Opens {@link SignupActivity} after logout was complete
     **/
    @Override
    public void openOptionalActivity() {
        startActivity(new Intent(DrawerAct.this, SignupActivity.class));
        finish();
    }

    /**
     * Called to open {@link MapScrn} fragment
     **/
    @Override
    public void ShowMapFragment() {
        showMapFragment();
    }

    /**
     * Called to open {@link FeedbackFragment}
     *
     * @param request     {@link Request} model
     * @param isCorporate boolean parameter
     **/
    @Override
    public void ShowFeedbackFragment(Request request, boolean isCorporate) {
        NeedFeedbackFragment(request, isCorporate);
    }

    @Override
    public void ShowFeedbackFragment(TaxiRequestModel.ResultData resultData, boolean isCorporate) {
        NeedFeedbackFragment(resultData, isCorporate);
    }

    @Override
    public void notifyNoDriverMessage(String reqId) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.LATER_NO_DRIVER);
        intentBroadcast.putExtra("req_id", reqId);
        intentBroadcast.putExtra("from", "push");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
    }

    /**
     * Called to open {@link TripFragment}
     *
     * @param request {@link Request} model
     * @param driver  {@link Driver} model
     **/
    @Override
    public void ShowTripFragment(Request request, Driver driver) {
        NeedTripFragment(request, driver);
    }

    @Override
    public void ShowTripFragment(NewRequestModel request) {
        NeedTripFragment(request);
    }

    @Override
    public void showTripFragment(TaxiRequestModel.ResultData request, TaxiRequestModel.DriverData driver) {
        NeedTripFragment(request, driver);
    }

    /**
     * Returns reference of {@link BaseActivity}
     **/
    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    /**
     * Callback function to receive results from previous pages
     *
     * @param requestCode Id of the request
     * @param resultCode  Id of the result
     * @param data        {@link Intent} with data from previous pages
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHANGE_ADDRESS) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(TripFragment.TAG);
            if (fragment instanceof TripFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.REQUEST_CODE_ENABLING_GOOGLE_LOCATION) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(MapScrn.TAG);
            if (fragment instanceof MapScrn)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.BRAINTREE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(PaymentFrag.TAG);
                if (fragment instanceof PaymentFrag)
                    fragment.onActivityResult(requestCode, resultCode, data);

            }
        } else if (requestCode == Constants.WALLETSETRESULT) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(PaymentFrag.TAG);
            if (fragment instanceof PaymentFrag)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.PROMOSETRESULT) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(TripFragment.TAG);
            if (fragment instanceof TripFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.HISTORYLISTSETRESULT) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(HistoryListFrag.TAG);
            if (fragment instanceof HistoryListFrag)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE || requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE_DROPADD_FROMRIDEFRG) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(MapFragment.TAG);
            if (fragment instanceof MapFragment)
                fragment.onActivityResult(requestCode, resultCode, data);

            Fragment destFragment = fragmentManager.findFragmentByTag(DestinationFragment.TAG);
            if (destFragment instanceof DestinationFragment)
                destFragment.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == Constants.RIDE_PROMO_RESULT) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(RideConfirmationFragment.TAG);
            if (fragment instanceof RideConfirmationFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(RideFragment.TAG);
            if (fragment instanceof RideFragment)
                fragment.onActivityResult(requestCode, resultCode, data);

        } /*else if (resultCode == CheckoutActivity.RESULT_OK) {
            Transaction transaction = data.getParcelableExtra(
                    CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);

            *//* Check the transaction type. *//*
            if (transaction.getTransactionType() == TransactionType.SYNC) {
                *//* Check the status of synchronous transaction. *//*
                Log.e("Sync==", "EnteringSync==");
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(PaymentFrag.TAG);
                if (fragment instanceof PaymentFrag)
                    fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                *//* Asynchronous transaction is processed in the onNewIntent(). *//*
            }

        }*/ else if (requestCode == Constants.REQUESTCODE_QR) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = "";
                if (scanningResult.getContents() != null) {
                    DestinationFragment fragment = (DestinationFragment) getSupportFragmentManager().findFragmentByTag(DestinationFragment.TAG);
                    assert fragment != null;
                    fragment.scanContentResult(scanningResult.getContents());
                }
            } else {
                Toast.makeText(DrawerAct.this, "Nothing scanned", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode != RESULT_CANCELED) {
            if (data != null)
                if (data.getSerializableExtra(DropInActivity.EXTRA_ERROR) != null)
                    if (data.getSerializableExtra(DropInActivity.EXTRA_ERROR) != null)
                        showMessage(((Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR)).getMessage());
        }

    }

    /**
     * Called to remove currently visible fragment when back button is pressed
     *
     * @param tag TAG of the fragment to identify
     **/
    public void onFragmentDetached(String tag) {
        if (!tag.equalsIgnoreCase("ETAdialog") || !tag.equalsIgnoreCase("FeedbackFragment")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                fragmentManager
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .remove(fragment)
//                        .commitNow();
                        .commitAllowingStateLoss();
                unlockDrawer();
                HideNshowToolbar(false);
                MapScrn MapFragment = (MapScrn) fragmentManager.findFragmentByTag(MapScrn.TAG);
                if (MapFragment != null)
                    MapFragment.showBottomlayout();
            }
        }
    }

    /**
     * Replace current fragment with {@link FeedbackFragment}
     *
     * @param request     {@link Request} model
     * @param isCorporate true/false
     **/
    public void NeedFeedbackFragment(Request request, boolean isCorporate) {
//        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.disallowAddToBackStack();
//        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.replace(R.id.Container, FeedbackFragment.newInstance(request, isCorporate), FeedbackFragment.TAG)
//                .commitAllowingStateLoss();
    }

    /**
     * Replace current fragment with {@link FeedbackFragment}
     *
     * @param requestData     {@link Request} model
     * @param isCorporate true/false
     **/
    public void NeedFeedbackFragment(TaxiRequestModel.ResultData requestData, boolean isCorporate) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.Container, FeedbackFragment.newInstance(requestData, isCorporate), FeedbackFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * Sets isTriporFeedback to false
     **/
    @Override
    public void changeTripnFeedback() {
        isTriporFeedback = false;
    }

    /**
     * Replaces current fragment with {@link TripFragment} if user was in trip
     *
     * @param type {@link Type} data model
     **/
    public void NeedRideFragment(final Type type) {
        HideNshowToolbar(true);
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_NONE);
        transaction.add(R.id.CoordinatorLayout, RideFragment.newInstance(type, ""), RideFragment.TAG)
                .commit();
    }

    /**
     * Opens {@link RideConfirmationFragment} to confirm booking
     *
     * @param pickup        Pickup location string
     * @param pickupLat     {@link LatLng} of the pickup location
     * @param drop          Drop location string
     * @param dropLat       {@link LatLng} of the drop location
     * @param currentLatlng {@link LatLng} of the current location
     * @param value         String parameter
     **/
    public void NeedConfirmation(String pickup, LatLng pickupLat, String drop, LatLng dropLat, LatLng currentLatlng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        HideNshowToolbar(true);
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_NONE);
        transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.newInstance(pickup, pickupLat, drop, dropLat, currentLatlng, value, driverPins, driverDatas), RideConfirmationFragment.TAG)
                .commit();
    }

    /**
     * Opens {@link RideConfirmationFragment} to confirm booking
     *
     * @param pickup        Pickup location string
     * @param pickupLat     {@link LatLng} of the pickup location
     * @param currentLatlng {@link LatLng} of the current location
     * @param value         String parameter
     **/
    public void NeedConfirmation(String pickup, LatLng pickupLat, LatLng currentLatlng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        HideNshowToolbar(true);
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_NONE);
        transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.newInstance(pickup, pickupLat, currentLatlng, value, driverPins, driverDatas), RideConfirmationFragment.TAG)
                .commit();
    }

    /**
     * Call this to open {@link TripFragment}
     *
     * @param request {@link Request} model
     * @param driver  {@link Driver} model
     **/
    @Override
    public void NeedTripFragment(Request request, Driver driver) {
//        /* if(!CommonUtils.isAppIsInBackground(DrawerAct.this)) {*/
//        /*if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(TripFragment.TAG) != null)
//            return;*/
//        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.disallowAddToBackStack();
//        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_NONE);
//        transaction.replace(R.id.Container, TripFragment.newInstance(request, driver), TripFragment.TAG)
//                .commitAllowingStateLoss();
//        /* }*/
    }

    @Override
    public void NeedTripFragment(NewRequestModel request) {
        stopTypesTimerNow();
        drawerViewModel.RequestInProNetwork();
    }

    public void NeedTripFragment(TaxiRequestModel.ResultData request, TaxiRequestModel.DriverData driver) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.disallowAddToBackStack();
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.Container, TripFragment.newInstance(request, driver), TripFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * Call this to open {@link MapScrn} fragment
     **/
    @Override
    public void NeedHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .replace(R.id.Container, MapFragment.newInstance("", "", ""), MapFragment.TAG)
                .commitAllowingStateLoss();
//                .commit();
    }

    /**
     * Sets result to {@link Intent} with address on places search
     **/
    @Override
    public void setResultToDropAddress(String Address) {
        onActivityResult(Constants.REQUEST_CODE_AUTOCOMPLETE_DROPADD_FROMRIDEFRG, 15, new Intent().putExtra(Constants.EXTRA_Data, Address));
    }

    /**
     * {@link BroadcastReceiver} to setup profile data
     **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawerViewModel.setupProfile();
        }
    };

    /**
     * {@link BroadcastReceiver} to remove {@link WaitProgressDialog} if showing
     **/
    private BroadcastReceiver mRemoveWaitingDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeWaitProgressDialog();
        }
    };

    /**
     * Enable/Disable corporate user and set {@link NavigationView} {@link MenuItem}'s visibility according to it
     *
     * @param isCorporate boolean variable to decide if user is corporate or not
     **/
    @Override
    public void enableCorporateUser(boolean isCorporate) {
//        sharedPrefence.saveBoolean(SharedPrefence.IS_CORPORATE_USER, isCorporate);
        if (activityDrawerBinding != null && activityDrawerBinding.navView != null && activityDrawerBinding.navView.getMenu() != null)
            if (activityDrawerBinding.navView.getMenu().findItem(R.id.nav_History) != null &&
                    activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Referralcode) != null &&
                    activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Wallet) != null &&
                    activityDrawerBinding.navView.getMenu().findItem(R.id.nav_faq) != null &&
                    activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Payment) != null) {
//                activityDrawerBinding.navView.getMenu().findItem(R.id.nav_History).setVisible(!isCorporate);
                activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Referralcode);
//                activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Wallet).setVisible(!isCorporate);
                //  activityDrawerBinding.navView.getMenu().findItem(R.id.nav_Payment).setVisible(!isCorporate);
//                activityDrawerBinding.navView.getMenu().findItem(R.id.nav_faq).setVisible(!isCorporate);
            }
    }

    /**
     * Call this to show ride later alert
     *
     * @param request {@link Request} model
     * @param driver  {@link Driver} model
     **/
    @Override
    public void openRideLaterAlert(TaxiRequestModel.ResultData request, TaxiRequestModel.DriverData driver) {
        Toast.makeText(this, "LaterReceieved..!!", Toast.LENGTH_SHORT).show();
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ride_latrer_alert, viewGroup, false);
        TextView content = dialogView.findViewById(R.id.alert_content);
        if (getApplicationContext() != null)
            content.setText(getTranslatedString(R.string.txt_schedule_trip_accepted));
        else
            content.setText(getTranslatedString(R.string.txt_schedule_trip_accepted));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogView.findViewById(R.id.submit).setOnClickListener(v -> {
            alertDialog.dismiss();
            NeedTripFragment(request, driver);
        });
        alertDialog.show();
    }

    @Override
    public void openRideLaterAlert(NewRequestModel request) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ride_latrer_alert, viewGroup, false);
        TextView content = dialogView.findViewById(R.id.alert_content);
        content.setText(getBaseAct().getTranslatedString(R.string.txt_schedule_trip_accepted));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogView.findViewById(R.id.submit).setOnClickListener(v -> {
            alertDialog.dismiss();
            NeedTripFragment(request);
        });
        alertDialog.show();
    }

    /**
     * Check for app update
     * Other fragment transactions
     **/
    @Override
    protected void onResume() {
        super.onResume();

//        showMapFragment();

//        appUpdateCheck();

        Constants.ACTIVITY_OPENEND_ALRDY = false;


        if (activityToBeRefre.get())
            if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(MapScrn.TAG) != null)
                ((MapScrn) getSupportFragmentManager().findFragmentByTag(MapScrn.TAG)).refreshCurrentPositions();
    }

    /**
     * Call this to check if new version of the app is available for the app in store
     **/
    public void appUpdateCheck() {
        appUpdater = new AppUpdater(this).setDisplay(Display.DIALOG);
        appUpdater.setButtonDismiss("");
        appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY);
        appUpdater.setButtonUpdate(getTranslatedString(R.string.txt_update));
        appUpdater.setDialogDescriptionWhenUpdateAvailable(getApplicationContext() != null ? getTranslatedString(R.string.txt_update_desc) : getTranslatedString(R.string.txt_update_desc));
        appUpdater.setTitleOnUpdateAvailable(getApplicationContext() != null ? getTranslatedString(R.string.txt_update_avail) : getTranslatedString(R.string.txt_update_desc));
        appUpdater.setButtonDoNotShowAgain(null);
        appUpdater.setCancelable(false);
        appUpdater.start();
    }


    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void openCloseDrawer() {
        if (!mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onClickNotification() {

       /* Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash by HiveDriver"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/

        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, NotificationlistFrag.newInstance(), NotificationlistFrag.TAG)
                .commit();
    }

    @Override
    public void setImageURL(String url) {
        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
        if (fragment != null)
            fragment.setProfileImage(url);
    }

     @Override
     public void stopTypesTimerNow() {
         MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
         if (fragment != null)
             fragment.stopTypesTimer();
     }

}
