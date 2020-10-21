package taxi.ratingen.ui.drawerscreen.mapscrn;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentMapScrnBinding;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.CaptureQR;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiAct;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.drawerscreen.tripcanceleddialog.TripCanceledDialogFrag;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import taxi.ratingen.ui.drawerscreen.tripscreen.TripFragment;

import static android.app.Activity.RESULT_OK;

public class MapScrn extends BaseFragment<FragmentMapScrnBinding, MapScrnViewModel> implements MapScrnNavigator, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "MapScrn";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @Inject
    MapScrnViewModel mapScrnViewModel;
    FragmentMapScrnBinding fragmentMapScrnBinding;

    public GoogleMap googleMap;

    EditText privateKeyText;
    String scanContent = "";

    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    private SimpleDateFormat mtimeFormatter = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
    private SimpleDateFormat mdateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    Animation topToBottom, BottomtoTop;

    String pickupAddress = "", dropadress = "";
    LatLng pickupLatlng, dropLatlng, currLatLng;
    AlertDialog alertDialog;

    public MapScrn() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapScrn newInstance(String param1, String param2) {
        MapScrn fragment = new MapScrn();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.PushWaitingorAcceptByDriver));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mTripCanceled, new IntentFilter(Constants.PushTripCancelled));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mDriverChanged,
                new IntentFilter(Constants.driverChanged));
        fragmentMapScrnBinding = getViewDataBinding();
        mapScrnViewModel.setNavigator(this);
        ((DrawerAct) getActivity()).unlockDrawer();
        fragmentMapScrnBinding.btnClickConfirm.setVisibility(View.GONE);

        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);
    }

    /**
     * {@link BroadcastReceiver} to get notified trip was cancelled by driver
     **/
    private BroadcastReceiver mTripCanceled = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String json = intent.getExtras().getString(Constants.EXTRA_Data);
                if (json.equalsIgnoreCase("Trip cancelled")) {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                    openTripCancelMsg();
                }
                // getBaseActivity().NeedHomeFragment();
            }
        }
    };

    /**
     * {@link BroadcastReceiver} to get notified driver has been changed
     **/
    private BroadcastReceiver mDriverChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("req_id")) {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
                String req_id = intent.getExtras().getString("req_id");
                int id = intent.getExtras().getInt("id");
                Log.e("request_id", "id==" + req_id + "id" + id);
                WaitProgressDialog.newInstance("" + id).show(getActivity().getSupportFragmentManager());
            }
        }
    };

    /**
     * shows trip cancel page when mTripCanceled broadcast received
     **/
    private void openTripCancelMsg() {
        if (getActivity().getSupportFragmentManager().findFragmentByTag(TripCanceledDialogFrag.TAG) == null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .replace(R.id.Container, TripCanceledDialogFrag.newInstance(1), TripCanceledDialogFrag.TAG)
                    .commitAllowingStateLoss();

    }

    @Override
    public void onStart() {
        super.onStart();
        mapScrnViewModel.isScreenAvailable = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mapScrnViewModel.isScreenAvailable = true;
    }

    /**
     * initial setup
     **/
    public void Setup() {
        getActivity().setTitle(getBaseAct().getTranslatedString(R.string.app_name));
        fragmentMapScrnBinding.DropCard.animate().alpha(0.7f);
        fragmentMapScrnBinding.dropDownArrow.animate().alpha(0.0f).translationY(-10.0f);
        fragmentMapScrnBinding.Pickcard.bringToFront();
        fragmentMapScrnBinding.pickupDownArrow.setVisibility(View.VISIBLE);
        MapScrnViewModel.IsIdle.set(true);
        fragmentMapScrnBinding.pickupDownArrow.bringToFront();
        fragmentMapScrnBinding.PickDotline.bringToFront();
        mapScrnViewModel.SetSocketListener();
        //  mapScrnViewModel.startTypesTimer();
        topToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.top_bottom);
        BottomtoTop = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top);

        topToBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fragmentMapScrnBinding.LinearBottom.setVisibility(View.GONE);
                fragmentMapScrnBinding.FMSEditFavtitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        BottomtoTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fragmentMapScrnBinding.LinearBottom.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (!CommonUtils.isGpscheck(getActivity())) {
            MapScrnViewModel.isLocationAvailable.set(false);
            openRequestLocation();
        } else
            MapScrnViewModel.isLocationAvailable.set(true);
    }

    @Override
    public MapScrnViewModel getViewModel() {
        return mapScrnViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map_scrn;
    }

    /**
     * triggered when drop location card is clicked
     **/
    @Override
    public void DropCardClicked() {
        fragmentMapScrnBinding.DropCard.bringToFront();
        fragmentMapScrnBinding.DropCard.animate().scaleX(1.0f).alpha(1.0f).translationY(0.0f);
        fragmentMapScrnBinding.DropTxtAddress.setVisibility(View.VISIBLE);
        fragmentMapScrnBinding.DropFav.setVisibility(View.VISIBLE);
        fragmentMapScrnBinding.DropTitleTxt.setText(getBaseAct().getTranslatedString(R.string.txt_Dropat));
        fragmentMapScrnBinding.Pickcard.animate().alpha(0.7f);
        fragmentMapScrnBinding.pickupDownArrow.animate().alpha(0.0f).setDuration(500);
        fragmentMapScrnBinding.dropDownArrow.animate().alpha(1.0f).translationY(0.0f);
        fragmentMapScrnBinding.dropDownArrow.setVisibility(View.VISIBLE);
        fragmentMapScrnBinding.dropDownArrow.bringToFront();
    }

    @Override
    public void openRequestLocation() {
        displayLocationSettingsRequest(getActivity());
    }

    /**
     * asks the user enable high accuracy gps mode if in battery saver mode
     **/
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), Constants.REQUEST_CODE_ENABLING_GOOGLE_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    /**
     * triggered when pickup location card is clicked
     **/
    @Override
    public void PickCardClicked() {
        fragmentMapScrnBinding.Pickcard.bringToFront();
        fragmentMapScrnBinding.Pickcard.animate().alpha(1.0f);
        fragmentMapScrnBinding.PickTitle.setText(getBaseAct().getTranslatedString(R.string.txt_PickupFrom));
        fragmentMapScrnBinding.dropDownArrow.animate().alpha(0.0f).translationY(-10.0f).setDuration(500);
        fragmentMapScrnBinding.DropCard.animate().alpha(0.7f);
        fragmentMapScrnBinding.dropDownArrow.animate().alpha(0.0f).setDuration(500);
        fragmentMapScrnBinding.pickupDownArrow.animate().alpha(1.0f).translationY(0.0f);
        fragmentMapScrnBinding.pickupDownArrow.bringToFront();
    }

    /**
     * opens places search API activity
     **/
    @Override
    public void PickAddressClicked() {
        mapScrnViewModel.getMap().clear();
        mapScrnViewModel.getMap().put(Constants.Extra_identity, getBaseAct().getTranslatedString(R.string.txt_EnterPick));
        getBaseActivity().startActivityForResult(new Intent(getContext(), PlaceApiAct.class).putExtra(Constants.EXTRA_Data, mapScrnViewModel.getMap()), Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    /**
     * opens places search API activity
     **/
    @Override
    public void DropAddressClicked() {
        //  AutoCompleteIntent();
        mapScrnViewModel.getMap().clear();
        mapScrnViewModel.getMap().put(Constants.Extra_identity, getBaseAct().getTranslatedString(R.string.txt_EnterDrop));
        getBaseActivity().startActivityForResult(new Intent(getContext(), PlaceApiAct.class).putExtra(Constants.EXTRA_Data, mapScrnViewModel.getMap()), Constants.REQUEST_CODE_AUTOCOMPLETE);

        //getBaseActivity().startActivity(new Intent(getContext(), PlaceApiAct.class));
    }

    private void AutoCompleteIntent() {
        Intent intent = null;
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().
                    setTypeFilter(Place.TYPE_COUNTRY).setCountry("IN").build();
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(getBaseActivity());
            getBaseActivity().startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    /**
     * show/hide toolbar and bottom layout
     **/
    @Override
    public void HideNshowToolbar(boolean b) {
        getBaseActivity().HideNshowToolbar(b);
        HideNShowBottomLayout(b);
    }

    @Override
    public void addcarList(List<Type> types) {
//        adapter.addList(types);
    }

    public void carSlected(Type type) {
        if (type != null && type.drivers != null && mapScrnViewModel != null)
            mapScrnViewModel.setDriverMarkers(type.drivers);
    }

    /**
     * returns reference of {@link BaseActivity}
     **/
    @Override
    public BaseActivity getBaseAct() {
        return ((BaseActivity) getActivity());
    }

    /**
     * show/hide bottom layout contents
     **/
    private void HideNShowBottomLayout(boolean hide) {
        if (hide) {
            fragmentMapScrnBinding.LinearBottom.animate().translationY(100).alpha(0.0f);
            fragmentMapScrnBinding.LinearBottom.setVisibility(View.GONE);

        } else {
            fragmentMapScrnBinding.LinearBottom.animate().translationY(0).alpha(1.0f).setDuration(100);
            fragmentMapScrnBinding.LinearBottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * returns current {@link Context}
     **/
    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    /**
     * shows ride later dialog when ride later button clicked
     **/
    @Override
    public void RideLaterClicked() {
        new SlideDateTimePicker.Builder(getChildFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    @Override
    public void RideNowClicked() {

//        Type type = adapter.getSelectedCar();
//        type.setIsFrom(Constants.RideNow);
//        type.setpicklatlng(MapScrnViewModel.PickupLatLng);
//        type.setDroplatlng(MapScrnViewModel.DropLatLng);
//        type.setpickAddress(mapScrnViewModel.mPickupAddress.get());
//        type.setDropAddress(mapScrnViewModel.mDropupAddress.get());
//        fragmentMapScrnBinding.Pickcard.setVisibility(View.GONE);
//        fragmentMapScrnBinding.DropCard.setVisibility(View.GONE);
//        if (!mapScrnViewModel.PickDropCard.get()) {
//            fragmentMapScrnBinding.PickDotline.setVisibility(View.GONE);
//        } else if (mapScrnViewModel.PickDropCard.get()) {
//            fragmentMapScrnBinding.DropDotline.setVisibility(View.GONE);
//        }
//
//        fragmentMapScrnBinding.LinearBottom.startAnimation(topToBottom);
//        getBaseAct().NeedRideFragment(type);
    }

    /**
     * show/hide fav. title editText
     **/
    @Override
    public void HideNShowFavTitleEdit(boolean hid) {
        if (hid)
            fragmentMapScrnBinding.FMSEditFavtitle.startAnimation(topToBottom);
        else
            fragmentMapScrnBinding.FMSEditFavtitle.setVisibility(View.GONE);

    }

    /**
     * show/hide fav. layout
     **/
    @Override
    public void HideNshowFavLayout(boolean hid) {
        if (hid) {
            fragmentMapScrnBinding.FMSFavCard.setVisibility(View.GONE);
            fragmentMapScrnBinding.FavDownArrow.setVisibility(View.GONE);//ani8mation later
            ViewGroup.LayoutParams layoutParams = fragmentMapScrnBinding.FMSGestureInit.getLayoutParams();
            layoutParams.height = 40;
            layoutParams.width = 40;
            fragmentMapScrnBinding.FMSGestureInit.setLayoutParams(layoutParams);
        } else {

            fragmentMapScrnBinding.FMSFavCard.setVisibility(View.VISIBLE);
            fragmentMapScrnBinding.FavDownArrow.setVisibility(View.VISIBLE);
        }
    }

    /**
     * change layout params of gesture image
     **/
    @Override
    public void ChangeOldLayoutParams() {
        fragmentMapScrnBinding.FMSEditFavtitle.getText().clear();
        fragmentMapScrnBinding.FMSRadioHome.setChecked(true);
        ViewGroup.LayoutParams layoutParams = fragmentMapScrnBinding.FMSGestureInit.getLayoutParams();
        layoutParams.height = 15;
        layoutParams.width = 15;
        fragmentMapScrnBinding.FMSGestureInit.setLayoutParams(layoutParams);
    }

    @Override
    public Type GetSelectedCarObj() {
        return null;
    }

    /**
     * callback to get notified that {@link GoogleMap} is loaded
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapScrnViewModel.buildGoogleApiClient(googleMap);

        googleMap.getUiSettings().setCompassEnabled(false);

        if (checkPlayServices()) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the users grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            //  googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            getBaseActivity().finish();/// Later change..have to look fragment deattach...
        }
        this.googleMap = googleMap;
        changeMapStyle();
    }

    /**
     * change style and appearance of {@link GoogleMap} with style json file
     **/
    private void changeMapStyle() {
        if (googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json);
        googleMap.setMapStyle(style);
    }

    /**
     * Constants.REQUEST_CODE_ENABLING_GOOGLE_LOCATION
     * request of Enabling GPS using google api
     * used delay to get location request
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MapScrnViewModel.PickDropProceed.set(false);
        if (requestCode == Constants.REQUEST_CODE_ENABLING_GOOGLE_LOCATION) {
            if (resultCode == RESULT_OK) {
                if (mapScrnViewModel != null)
                    mapScrnViewModel.setIsLoading(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mapScrnViewModel != null)
                            mapScrnViewModel.restartGoogleApiClient();
                    }
                }, 2000);

            } /*else {
                Toast.makeText(getActivity(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }*/
        } else if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {

            /*MapScrnViewModel.PickDropProceed.set(false);*/
            if (!MapScrnViewModel.PickDropCard.get() && data != null) {
                Log.e("PickupDaaress==", "pickAddress" + data.getStringExtra(Constants.EXTRA_Data));
                mapScrnViewModel.mPickupAddress.set(data.getStringExtra(Constants.EXTRA_Data));
                if (mapScrnViewModel.checkaddressFav(mapScrnViewModel.mPickupAddress.get())) {
                    MapScrnViewModel.PickFavImage.set(true);
                } else {
                    MapScrnViewModel.PickFavImage.set(false);
                }

            } else if (MapScrnViewModel.PickDropCard.get() && data != null) {
                Log.e("PickupDaaress==", "pickAddress11" + data.getStringExtra(Constants.EXTRA_Data));

                mapScrnViewModel.mDropupAddress.set(data.getStringExtra(Constants.EXTRA_Data));

                if (mapScrnViewModel.checkaddressFav(mapScrnViewModel.mDropupAddress.get())) {
                    MapScrnViewModel.DropFavImage.set(true);
                } else {
                    MapScrnViewModel.DropFavImage.set(false);
                }
            }


        } else if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE_DROPADD_FROMRIDEFRG) {

            if (data != null) {

                mapScrnViewModel.mDropupAddress.set(data.getStringExtra(Constants.EXTRA_Data));
                mapScrnViewModel.onclickDrop(null);

                if (mapScrnViewModel.checkaddressFav(mapScrnViewModel.mDropupAddress.get())) {
                    MapScrnViewModel.DropFavImage.set(true);
                } else {
                    MapScrnViewModel.DropFavImage.set(false);
                }
            }
        }
    }

    /**
     * listener for ride later dateTime selection
     **/
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            long different = 0;

            String currentdate = mdateFormatter.format(new Date());
            String selecteddate = mdateFormatter.format(date);

            try {
                if (mdateFormatter.parse(currentdate).compareTo(mdateFormatter.parse(selecteddate)) == 0) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.MINUTE, 59);

                    String After30Time = mtimeFormatter.format(now.getTime());
                    String Selectedtime = mtimeFormatter.format(date);


                    different = mtimeFormatter.parse(After30Time).getTime() - mtimeFormatter.parse(Selectedtime).getTime();

                    if (different < 0) {
                        androidx.fragment.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.disallowAddToBackStack();
                        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                        transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.RideLater(pickupAddress, pickupLatlng, dropadress, dropLatlng, currLatLng, mFormatter.format(date), 2), RideConfirmationFragment.TAG)
                                .commit();
                    } else {
                        showMessage(getBaseAct().getTranslatedString(R.string.Txt_Schedule_Alert));
                    }

                } else {
                    androidx.fragment.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.disallowAddToBackStack();
                    transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                    transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.RideLater(pickupAddress, pickupLatlng, dropadress, dropLatlng, currLatLng, mFormatter.format(date), 2), RideConfirmationFragment.TAG)
                            .commit();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    /**
     * dispaly bottom layout contents
     **/
    public void showBottomlayout() {
        fragmentMapScrnBinding.Pickcard.setVisibility(View.VISIBLE);
        fragmentMapScrnBinding.DropCard.setVisibility(View.VISIBLE);
        fragmentMapScrnBinding.LinearBottom.startAnimation(BottomtoTop);
        if (!MapScrnViewModel.PickDropCard.get()) {
            fragmentMapScrnBinding.PickDotline.setVisibility(View.VISIBLE);
        } else if (MapScrnViewModel.PickDropCard.get()) {
            fragmentMapScrnBinding.DropDotline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * checks if GooglePlayServices available or not
     **/
    public boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getBaseActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getBaseActivity(), resultCode,
                        Constants.PLAY_SERVICES_REQUEST).show();
            } else {

                showMessage(getBaseAct().getTranslatedString(R.string.DeviceNotSupport));


            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Setup();
        Constants.ACTIVITY_OPENEND_ALRDY = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapScrnViewModel.PickupLatLng = null;
        MapScrnViewModel.DropLatLng = null;
        mapScrnViewModel.stopTypesTimer();
        // mapScrnViewModel.DisconnectSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mDriverChanged);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTripCanceled);
    }

    /**
     * opens {@link TripFragment}
     *
     * @param request
     **/
    @Override
    public void openTripFragment(Request request) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).removeWaitProgressDialog();
        if (request != null && getBaseActivity() != null) {
            getBaseActivity().NeedTripFragment(request, request.driver);
            getBaseActivity().onFragmentDetached(RideConfirmationFragment.TAG);

        }
    }

    /**
     * Called when current location button is clicked
     **/
    public void refreshCurrentPositions() {
        MapScrnViewModel.PickDropCard.set(false);
        if (mapScrnViewModel != null)
            mapScrnViewModel.onclickCurrentLocation(null);
    }

    /**
     * called when Ride Now clicked
     *
     * @param pickupAddress
     * @param pickupLatLng
     * @param droAddredss
     * @param dropLatLng
     * @param currentLatlng
     **/
    @Override
    public void onConfirmation(String pickupAddress, LatLng pickupLatLng, String droAddredss, LatLng dropLatLng, LatLng currentLatlng, String value) {
        if (getBaseAct() != null && getBaseAct() instanceof DrawerAct) {
//            ((DrawerAct) getBaseAct()).NeedConfirmation(pickupAddress, pickupLatLng, droAddredss, dropLatLng, currentLatlng, value);
//            if (mapScrnViewModel != null)
//                mapScrnViewModel.stopTypesTimer();
        }
    }

    /**
     * Called when Ride Now clicked
     *
     * @param pickupAddress
     * @param pickupLatLng
     * @param currentLatlng
     * @param value
     **/
    @Override
    public void onConfirmation(String pickupAddress, LatLng pickupLatLng, LatLng currentLatlng, String value) {
        if (getBaseAct() != null && getBaseAct() instanceof DrawerAct) {
//            ((DrawerAct) getBaseAct()).NeedConfirmation(pickupAddress, pickupLatLng, currentLatlng, value);
//            if (mapScrnViewModel != null)
//                mapScrnViewModel.stopTypesTimer();
        }
    }

    /**
     * Show the user there are no drivers in the vicinity
     *
     * @param tripData
     **/
    @Override
    public void notifyNoDriverMessage(String tripData) {
        if (getActivity() != null && getActivity().getSupportFragmentManager() != null)
            if (getActivity().getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG) != null) {
                Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                intent.putExtra(Constants.EXTRA_Data, tripData);
                LocalBroadcastManager.getInstance(getAttachedcontext()).sendBroadcast(intent);
            }
    }

    /**
     * Opens QR code scanner to initialize ride with key
     **/
    @Override
    public void openScannerPage() {
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.qr_user_input, null);
        privateKeyText = view.findViewById(R.id.qrEnter);
       /* Button ride_later = view.findViewById(R.id.ride_later);
        Button ride_now = view.findViewById(R.id.ride_now);*/

        Button submit = view.findViewById(R.id.submit_butt);
        TextView qrTExt = view.findViewById(R.id.qr_scan);
        qrTExt.setText(getBaseAct().getTranslatedString(R.string.txt_scan_qrcode));
        privateKeyText.setHint(getBaseAct().getTranslatedString(R.string.txt_enter_privatekey));
        submit.setText(getBaseAct().getTranslatedString(R.string.text_submit));
      /*  ride_later.setText(getActivity().getString(R.string.txt_Ridelater));
        ride_now.setText(getActivity().getString(R.string.txt_RideNow));*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!privateKeyText.getText().toString().isEmpty()) {
                    mapScrnViewModel.qrScanned(privateKeyText.getText().toString());
                    dialog.dismiss();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "Enter Private Key", Toast.LENGTH_SHORT).show();

                }
            }
        });

      /*  ride_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mapScrnViewModel.qrScanned(privateKeyText.getText().toString(), 1);
            }
        });

        ride_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mapScrnViewModel.qrScanned(privateKeyText.getText().toString(), 2);
            }
        });*/

        qrTExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                scanIntegrator.setPrompt("Scan");
                scanIntegrator.setBeepEnabled(true);
                //The following line if you want QR code
                // scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                scanIntegrator.setCaptureActivity(CaptureQR.class);
                scanIntegrator.setOrientationLocked(true);
                scanIntegrator.setBarcodeImageEnabled(true);
                scanIntegrator.initiateScan();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }

    /**
     * called when ride later was clicked
     *
     * @param s
     * @param pickupLatLng
     * @param s1
     * @param dropLatLng
     * @param curLatLng
     **/
    @Override
    public void rideLaterClick(String s, LatLng pickupLatLng, String s1, LatLng dropLatLng, LatLng curLatLng) {
        this.pickupAddress = s;
        this.dropadress = s1;
        this.pickupLatlng = pickupLatLng;
        this.dropLatlng = dropLatLng;
        this.currLatLng = curLatLng;

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.layout_date_time_bottom);
        dialog.show();

        LinearLayout root = dialog.findViewById(R.id.ll_root);
        Button bCancel = dialog.findViewById(R.id.btn_date_time_cancel);
        Button bDone = dialog.findViewById(R.id.btn_date_time_done);
        final TextView tCurrent = dialog.findViewById(R.id.txt_date_time_current);
        final TimePicker timePicker = dialog.findViewById(R.id.timePicker);

        final SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm aa dd/MM/yyyy", Locale.ENGLISH);
        Date dCurrent = new Date(System.currentTimeMillis());

        tCurrent.setText(sdf1.format(dCurrent));

        Calendar cStart = Calendar.getInstance();
        cStart.setTime(dCurrent);
        Calendar cEnd = Calendar.getInstance();
        cEnd.add(Calendar.MONTH, 1);

        final Calendar cSelected = Calendar.getInstance();
        cSelected.setTime(dCurrent);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(root, R.id.calendarView)
                .range(cStart, cEnd)
                .datesNumberOnScreen(5)
                .mode(HorizontalCalendar.Mode.DAYS)
                .configure()
                .showBottomText(false)
                .formatTopText("EEEE")
                .formatMiddleText("d")
                .textSize(10f, 25f, 0f)
                .selectorColor(Color.parseColor("#FE3939"))
                .end()
                .defaultSelectedDate(cStart)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                cSelected.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                cSelected.set(Calendar.MONTH, date.get(Calendar.MONTH));
                cSelected.set(Calendar.YEAR, date.get(Calendar.YEAR));

                tCurrent.setText(sdf1.format(cSelected.getTime()));
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                cSelected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cSelected.set(Calendar.MINUTE, minute);

                tCurrent.setText(sdf1.format(cSelected.getTime()));
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dSelected = cSelected.getTime();

                String current_date = mdateFormatter.format(new Date());
                String selected_date = mdateFormatter.format(dSelected);

                try {
                    if (mdateFormatter.parse(current_date).compareTo(mdateFormatter.parse(selected_date)) == 0) {
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.MINUTE, 59);

                        String After30Time = mtimeFormatter.format(now.getTime());
                        String Selectedtime = mtimeFormatter.format(dSelected);


                        long different = mtimeFormatter.parse(After30Time).getTime() - mtimeFormatter.parse(Selectedtime).getTime();

                        if (different < 0) {
                            androidx.fragment.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.disallowAddToBackStack();
                            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                            transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.RideLater(pickupAddress, pickupLatlng, dropadress, dropLatlng, currLatLng, mFormatter.format(dSelected), 2), RideConfirmationFragment.TAG)
                                    .commit();

                            dialog.dismiss();
                        } else {
                            showMessage(getBaseAct().getTranslatedString(R.string.Txt_Schedule_Alert));
                        }

                    } else {
                        androidx.fragment.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.disallowAddToBackStack();
                        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                        transaction.add(R.id.CoordinatorLayout, RideConfirmationFragment.RideLater(pickupAddress, pickupLatlng, dropadress, dropLatlng, currLatLng, mFormatter.format(dSelected), 2), RideConfirmationFragment.TAG)
                                .commit();

                        dialog.dismiss();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

//        new SlideDateTimePicker.Builder(getChildFragmentManager())
//                .setListener(listener)
//                .setInitialDate(new Date())
//                .setMinDate(new Date())
//                //.setMaxDate(maxDate)
//                .setIs24HourTime(true)
//                //.setTheme(SlideDateTimePicker.HOLO_DARK)
//                //.setIndicatorColor(Color.parseColor("#990000"))
//                .build()
//                .show();
    }

    /**
     * notify the user about no drivers for ride later
     **/
    @Override
    public void openLaterAlert(Integer id) {
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.LATER_NO_DRIVER);
        intentBroadcast.putExtra("req_id", id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentBroadcast);
    }

    /**
     * Opens the ride later accepted confirmation dialog
     *
     * @param request
     **/
    @Override
    public void openRideLaterAlert(final Request request) {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.ride_latrer_alert, viewGroup, false);
        TextView content = dialogView.findViewById(R.id.alert_content);
        content.setText(getBaseAct().getTranslatedString(R.string.txt_schedule_trip_accepted));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                openTripFragment(request);
            }
        });
        alertDialog.show();
    }


/*    public void restartTypesTimer() {
        if (mapScrnViewModel != null)
            mapScrnViewModel.startTypesTimer();
    }*/

    /**
     * Private key ride QR code scanning result
     *
     * @param contents
     **/
    public void scanContentResult(String contents) {
        mapScrnViewModel.qrScanned(contents);
        //  privateKeyText.setText(contents);
    }

    /**
     * {@link BroadcastReceiver} to receive no driver and other messages
     **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Mapscrn", "Mapscrn");

            ((BaseActivity) getActivity()).removeWaitProgressDialog();
            ((BaseActivity) getActivity()).removeRideConfirmationFragment();
            if (intent.hasExtra(Constants.EXTRA_Data)) {

                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = CommonUtils.getSingleObject(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null) {
                    if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                        // Toast.makeText(getActivity(), getString(R.string.text_accepted), Toast.LENGTH_SHORT).show();
                    } else
                        getBaseActivity().NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);
                } else if (baseResponse.successMessage != null &&
                        (baseResponse.successMessage.contains("no driver") || baseResponse.successMessage.contains("no_driver") || baseResponse.successMessage.contains("no_driver_found"))) {
                    Toast.makeText(getBaseActivity(), getBaseAct().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    public boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Constants.Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        }
    }
}
