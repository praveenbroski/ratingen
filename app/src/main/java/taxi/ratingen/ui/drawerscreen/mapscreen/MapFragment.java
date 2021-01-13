package taxi.ratingen.ui.drawerscreen.mapscreen;


import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentMapBinding;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.CaptureQR;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.changeplace.SearchPlaceActivity;
import taxi.ratingen.ui.drawerscreen.mapscreen.adapter.PlacesApiAdapter;
import taxi.ratingen.ui.drawerscreen.mapscreen.destination.DestinationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.drawerscreen.tripcanceleddialog.TripCanceledDialogFrag;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**/
public class MapFragment extends BaseFragment<FragmentMapBinding, MapFragmentViewModel> implements MapNavigator, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static final String TAG = "MapFragment";
    private static final String ParampickupAddr = "pickupAddr";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private PlacesApiAdapter placesApiAdapter;

    @Inject
    MapFragmentViewModel mapFragmentViewModel;

    LatLng loc = null;
    public FragmentMapBinding fragmentMapBinding;

    public GoogleMap googleMap;
    EditText privateKeyText;
    String scanContent = "";
    AlertDialog alertDialog = null;

    HashMap<String, Marker> driverPins = new HashMap<>();
    HashMap<String, String> driverDatas = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2, String imageUrl) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            if (mParam3 != null) {
                mapFragmentViewModel.ImageUrl.set(mParam3);
            }
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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mProfileReceiver, new IntentFilter(Constants.ProfileUpdate));
        fragmentMapBinding = getViewDataBinding();
//        fragmentMapBinding.mapView.onCreate(savedInstanceState);
        mapFragmentViewModel.setNavigator(this);

        Setup();
    }

    /** initializes {@link MapsInitializer}
     * initializes {@link com.google.android.gms.common.api.GoogleApiClient}
     * sets auto complete adapter for places api editText
     * **/
    private void Setup() {
        mapFragmentViewModel.IsIdle.set(true);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapFragmentViewModel.SetSocketListener();
        mapFragmentViewModel.startTypesTimer();
        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView)).getMapAsync(this);

        // we can't use dagger injection here..Because We can't activity instance in fragment...
        placesApiAdapter = new PlacesApiAdapter(getBaseActivity(), this, R.layout.autocompleteitem);
//        fragmentMapBinding.AutocompleteEdit.setAdapter(placesApiAdapter);

        if (!CommonUtils.isGpscheck(getActivity())) {
            MapFragmentViewModel.isLocationAvailable.set(false);
            openRequestLocation();
        } else
            MapFragmentViewModel.isLocationAvailable.set(true);

        mapFragmentViewModel.setupProfile();
    }

    @Override
    public MapFragmentViewModel getViewModel() {
        return mapFragmentViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            getBaseActivity().HideNshowToolbar(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        fragmentMapBinding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        fragmentMapBinding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mDriverChanged);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mTripCanceled);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mProfileReceiver);
//        if (fragmentMapBinding.mapView != null)
//            fragmentMapBinding.mapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFragmentViewModel.stopTypesTimer();
//        fragmentMapBinding.mapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        fragmentMapBinding.mapView.onLowMemory();
    }

    /** check if GooglePlayServices available or not **/
    public boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getBaseActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getBaseActivity(), resultCode,
                        Constants.PLAY_SERVICES_REQUEST).show();
            } else {

                showMessage(getAttachedContext().getTranslatedString(R.string.DeviceNotSupport));


            }
            return false;
        }
        return true;
    }

    /** returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    //change later using api with retrofit ..
    /** get {@link android.location.Location} object from given address **/
    private LatLng getLocationFromAddress(final String place) {

        Geocoder gCoder = new Geocoder(getActivity());
        try {
            final List<Address> list = gCoder.getFromLocationName(place, 1);
            if (list != null && list.size() > 0) {
                loc = new LatLng(list.get(0).getLatitude(), list.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return loc;
    }

    /** return places list for given search term **/
    @Override
    public ArrayList<String> getPlaceList(String s) {
        if (s != null && !s.isEmpty())
            return mapFragmentViewModel.autocomplete(s);

        return null;
    }

    /** call reverse geo-coding i.e., {@link android.location.Location} object from address string **/
    @Override
    public LatLng getReverseGeocode(String s) {
        return getLocationFromAddress(s);
    }

    /** get address string from autocomplete **/
    @Override
    public void getAutoCompletedAddress(String s) {
//        if (s != null && !s.isEmpty())
//            fragmentMapBinding.AutocompleteEdit.setText(s);
    }

    @Override
    public boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
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

    @Override
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Constants.Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        }
    }

    @Override
    public void openDestinationFragment(String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
//        getBaseActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .disallowAddToBackStack()
//                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
//                .add(R.id.destination_container, DestinationFragment.newInstance(mapFragmentViewModel.mMapLatLng.get(), mapFragmentViewModel.address.get(), mapFragmentViewModel.mMapLatLng.get(), "", driverPins, driverDatas), DestinationFragment.TAG)
//                .commit();

        this.driverPins = driverPins;
        this.driverDatas = driverDatas;

        Intent intent = new Intent(getContext(), SearchPlaceActivity.class);
        intent.putExtra(Constants.EXTRA_IS_PICKUP, "0");
        intent.putExtra(ParampickupAddr, mapFragmentViewModel.address.get());
        intent.putExtra(Constants.EXTRA_PICK_ADDRESS, mapFragmentViewModel.address.get());
//        intent.putExtra(Constants.EXTRA_DROP_ADDRESS, destinationViewModel.dropAddress.get());
        intent.putExtra(Constants.EXTRA_SEARCH_TYPE, getAttachedContext().getTranslatedString(R.string.txt_EnterDrop));
        getBaseActivity().startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public void pickAddressClicked() {
        Intent intent = new Intent(getContext(), SearchPlaceActivity.class);
        intent.putExtra(Constants.EXTRA_IS_PICKUP, "1");
        intent.putExtra(Constants.EXTRA_PICK_ADDRESS, mapFragmentViewModel.address.get());
        intent.putExtra(Constants.EXTRA_SEARCH_TYPE, getAttachedContext().getTranslatedString(R.string.txt_EnterPick));
        getBaseActivity().startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public void openScannerPage() {
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.qr_user_input, null);
        privateKeyText = view.findViewById(R.id.qrEnter);

        Button submit = view.findViewById(R.id.submit_butt);
        TextView qrTExt = view.findViewById(R.id.qr_scan);
        qrTExt.setText(getBaseActivity().getTranslatedString(R.string.txt_scan_qrcode));
        privateKeyText.setHint(getBaseActivity().getTranslatedString(R.string.txt_enter_privatekey));
        submit.setText(getBaseActivity().getTranslatedString(R.string.text_submit));

        submit.setOnClickListener(v -> {
            if (!privateKeyText.getText().toString().isEmpty()) {
                mapFragmentViewModel.qrScanned(privateKeyText.getText().toString());
                dialog.dismiss();
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Enter Private Key", Toast.LENGTH_SHORT).show();

            }
        });

        qrTExt.setOnClickListener(v -> {
            dialog.dismiss();
            IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
            scanIntegrator.setPrompt("Scan");
            scanIntegrator.setBeepEnabled(true);
            //The following line if you want QR code
            scanIntegrator.setCaptureActivity(CaptureQR.class);
            scanIntegrator.setOrientationLocked(true);
            scanIntegrator.setBarcodeImageEnabled(true);
            scanIntegrator.initiateScan();
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void openSideMenu() {
        if(getBaseActivity()!=null)
            ((DrawerAct)getBaseActivity()).openCloseDrawer();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapFragmentViewModel.buildGoogleApiClient(googleMap);

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
        changeMapStyle();
    }

    /**
     * change style and appearance of {@link GoogleMap} with style json file
     **/
    private void changeMapStyle() {
//        if (googleMap == null)
//            return;
//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json);
//        googleMap.setMapStyle(style);
    }

    /**
     * Constants.REQUEST_CODE_ENABLING_GOOGLE_LOCATION
     * request of Enabling GPS using google api
     * used delay to get location request
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            if (data != null && data.hasExtra(Constants.EXTRA_IS_PICKUP)) {
                if (data.getBooleanExtra(Constants.EXTRA_IS_PICKUP, false)) {
                    if (data.hasExtra(Constants.EXTRA_Data)) {
                        mapFragmentViewModel.isFromAutoComplete.set(true);
                        mapFragmentViewModel.address.set(data.getStringExtra(Constants.EXTRA_Data));
                        mapFragmentViewModel.getLocationFromAddress(mapFragmentViewModel.address.get());
                    }
                } else {
                    if (data.hasExtra(Constants.EXTRA_Data)) {
                        String dropAddress = data.getStringExtra(Constants.EXTRA_Data);

                        getBaseActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .disallowAddToBackStack()
                                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                                .add(R.id.destination_container, DestinationFragment.newInstance(mapFragmentViewModel.mMapLatLng.get(), mapFragmentViewModel.address.get(), mapFragmentViewModel.mMapLatLng.get(), dropAddress, "", driverPins, driverDatas), DestinationFragment.TAG)
                                .commit();
                    }
                }
            }
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager =getBaseActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    @Override
    public void addcarList(List<Type> types) {
//        adapter.addList(types);
    }

    @Override
    public String GetSelectedCarObj() {
        return null;
    }

    @Override
    public void openRideLaterAlert(Request request) {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.ride_latrer_alert, viewGroup, false);
        TextView content = dialogView.findViewById(R.id.alert_content);
        content.setText(getAttachedContext().getTranslatedString(R.string.txt_schedule_trip_accepted));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogView.findViewById(R.id.submit).setOnClickListener(v -> {
            alertDialog.dismiss();
            openTripFragment(request);
        });
        alertDialog.show();
    }

    @Override
    public void openTripFragment(Request request) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).removeWaitProgressDialog();
        if (request != null && getBaseActivity() != null) {
            getBaseActivity().NeedTripFragment(request, request.driver);
            getBaseActivity().onFragmentDetached(RideConfirmationFragment.TAG);

        }
    }

    @Override
    public void notifyNoDriverMessage(String tripData) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager();
            if (getActivity().getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG) != null) {
                Intent intent = new Intent(Constants.PushWaitingorAcceptByDriver);
                intent.putExtra(Constants.EXTRA_Data, tripData);
                LocalBroadcastManager.getInstance(getAttachedContext()).sendBroadcast(intent);
            }
        }
    }

    public void restartTypesTimer() {
        if (mapFragmentViewModel != null)
            mapFragmentViewModel.startTypesTimer();
    }

    private BroadcastReceiver mProfileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mapFragmentViewModel.setupProfile();
        }
    };

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
//                    if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
//                        // Toast.makeText(getActivity(), getString(R.string.text_accepted), Toast.LENGTH_SHORT).show();
//                    } else
                        getBaseActivity().NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);
                } else if (baseResponse.successMessage != null &&
                        (baseResponse.successMessage.contains("no driver") || baseResponse.successMessage.contains("no_driver") || baseResponse.successMessage.contains("no_driver_found"))) {
                    Toast.makeText(getBaseActivity(), getBaseActivity().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

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

    public void setProfileImage(String url) {
        mapFragmentViewModel.ImageUrl.set(url);
    }

    public void stopTypesTimer() {
        mapFragmentViewModel.stopTypesTimer();
    }

}
