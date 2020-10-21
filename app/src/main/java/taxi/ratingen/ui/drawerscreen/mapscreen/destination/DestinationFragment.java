package taxi.ratingen.ui.drawerscreen.mapscreen.destination;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import taxi.ratingen.R;
import taxi.ratingen.databinding.DestinationFragmentBinding;
import taxi.ratingen.retro.responsemodel.MarkerModel;
import taxi.ratingen.ui.CaptureQR;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.changeplace.SearchPlaceActivity;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class DestinationFragment extends BaseFragment<DestinationFragmentBinding, DestinationViewModel> implements DestinationNavigator, OnMapReadyCallback {

    public static final String TAG = "DestinationFragment";
    private static final String ParampickupLatlng = "pickupLatlng";
    private static final String ParamcurrentLatlng = "currentLatlng";
    private static final String ParampickupAddr = "pickupAddr";
    private static final String ParamDropAddr = "ParamDropAddr";
    private static final String SCANCONTENT = "scanContent";
    private static final String RIDETYPE = "ridetype";
    private static final String DRIVER_PINS = "driverPins";
    private static final String DRIVER_DATAS = "driverDatas";

    private LatLng crLatlng, pickUpLatlng;
    private String pickupAddress, scanContent;
    private int ride_type = 1;
    private boolean isViaPrivateKey = false;
    public HashMap<String, Marker> driverPins;
    public HashMap<String, MarkerOptions> driverPinOptions;
    public HashMap<String, String> driverDatas;

    @Inject
    DestinationViewModel destinationViewModel;

    DestinationFragmentBinding destFragmentBinding;

    @Inject
    SharedPrefence sharedPrefence;

    public GoogleMap googleMap;
    EditText privateKeyText;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        destFragmentBinding = getViewDataBinding();
//        destinationViewModel.setNavigator(this);
//        Setup();
//    }

    public DestinationFragment() {

    }

    public static DestinationFragment newInstance() {
        DestinationFragment fragment = new DestinationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(LatLng pickLatLng, String pickAddress) {
        DestinationFragment fragment = new DestinationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParampickupLatlng, pickLatLng);
        args.putString(ParampickupAddr, pickAddress);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(LatLng pickLatLng, String pickAddress, LatLng currentLatLng, String dropAddress, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        DestinationFragment fragment = new DestinationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParampickupLatlng, pickLatLng);
        args.putString(ParampickupAddr, pickAddress);
        args.putParcelable(ParamcurrentLatlng, currentLatLng);
        args.putString(ParamDropAddr, dropAddress);
        args.putString(SCANCONTENT, value);
        args.putInt(RIDETYPE, 1);
        ArrayList<MarkerModel> markerModels = new ArrayList<>();
        for (String key: driverPins.keySet()) {
            Marker marker = driverPins.get(key);
            MarkerModel markerModel = new MarkerModel();
            markerModel.setLat(marker.getPosition().latitude);
            markerModel.setLng(marker.getPosition().longitude);
            markerModel.setRotation(marker.getRotation());
            markerModel.setId(key);
            markerModels.add(markerModel);
        }
        args.putSerializable(DRIVER_PINS, markerModels);
        args.putSerializable(DRIVER_DATAS, driverDatas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            destinationViewModel.pickLatLng = getArguments().getParcelable(ParampickupLatlng);
            destinationViewModel.pickAddress.set(getArguments().getString(ParampickupAddr));
            destinationViewModel.dropAddress.set(getArguments().getString(ParamDropAddr));
            scanContent = getArguments().getString(SCANCONTENT);
            ride_type = getArguments().getInt(RIDETYPE);
            isViaPrivateKey = (!CommonUtils.IsEmpty(scanContent));
            HashMap<String, MarkerOptions> markerMap = new HashMap<>();
            if (getArguments().getSerializable(DRIVER_PINS) != null) {
//                driverPins = (HashMap<String, Marker>) getArguments().getSerializable(DRIVER_PINS);
                ArrayList<MarkerModel> markerModels = (ArrayList<MarkerModel>) getArguments().getSerializable(DRIVER_PINS);
                BitmapDescriptor bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                for (MarkerModel mm: markerModels) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(mm.getLat(), mm.getLng())).anchor(0.5f, 0.5f).rotation((float) mm.getRotation()).icon(bitmapDescriptorFactory);
                    markerMap.put(mm.getId(), markerOptions);
                }
            }
            this.driverPinOptions = markerMap;
            if (getArguments().getSerializable(DRIVER_DATAS) != null)
                driverDatas = (HashMap<String, String>) getArguments().getSerializable(DRIVER_DATAS);

            Log.v("fatal_log", driverDatas.toString());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        destFragmentBinding = getViewDataBinding();
        destinationViewModel.setNavigator(this);

        setup();
    }

    private void setup() {
        destFragmentBinding.toolbar.setNavigationOnClickListener(v -> {
            if (getBaseActivity() != null)
                getBaseActivity().onFragmentDetached(DestinationFragment.TAG);
        });
        destFragmentBinding.toolbar.setTitle("Destination");

        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView)).getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getBaseActivity() != null && getBaseActivity() instanceof DrawerAct) {
//            ((DrawerAct) getBaseActivity()).activityDrawerBinding.layoutActionbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getBaseActivity() != null && getBaseActivity() instanceof DrawerAct) {
//            ((DrawerAct) getBaseActivity()).activityDrawerBinding.layoutActionbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public DestinationViewModel getViewModel() {
        return destinationViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.destination_fragment;
    }

    @Override
    public void openSearchDestination() {
        Intent intent = new Intent(getContext(), SearchPlaceActivity.class);
        intent.putExtra(Constants.EXTRA_IS_PICKUP, "0");
        intent.putExtra(ParampickupAddr, destinationViewModel.pickAddress.get());
        intent.putExtra(Constants.EXTRA_PICK_ADDRESS, destinationViewModel.pickAddress.get());
        intent.putExtra(Constants.EXTRA_DROP_ADDRESS, destinationViewModel.dropAddress.get());
        intent.putExtra(Constants.EXTRA_SEARCH_TYPE, getAttachedContext().getTranslatedString(R.string.txt_EnterDrop));
        getBaseActivity().startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
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
    public void onClickConfirmation(String pickAddress, LatLng pickLatLng, String dropAddress, LatLng dropLatLng, LatLng currentLatLng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        if (getBaseActivity() != null && getBaseActivity() instanceof DrawerAct) {
            ((DrawerAct) getBaseActivity()).NeedConfirmation(pickAddress, pickLatLng, dropAddress, dropLatLng, currentLatLng, value, driverPins, driverDatas);
        }

    }

    @Override
    public void onClickConfirmation(String pickAddress, LatLng pickLatLng, LatLng currentLatLng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        if (getBaseActivity() != null && getBaseActivity() instanceof DrawerAct) {
            ((DrawerAct) getBaseActivity()).NeedConfirmation(pickAddress, pickLatLng, currentLatLng, value, driverPins, driverDatas);
        }
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
                destinationViewModel.qrScanned(privateKeyText.getText().toString());
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

    /**
     * Private key ride QR code scanning result
     *
     * @param contents
     **/
    public void scanContentResult(String contents) {
        destinationViewModel.qrScanned(contents);
        //  privateKeyText.setText(contents);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        destinationViewModel.buildGoogleApiClient(googleMap);

        googleMap.getUiSettings().setCompassEnabled(false);

        if (checkPlayServices()) {
            if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        if (sharedPrefence.GetBoolean(SharedPrefence.MAPTYPE)) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //  tripFragViewModel.mapType.set(true);
        } else {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //  tripFragViewModel.mapType.set(false);
        }
        HashMap<String, Marker> markerHashMap = new HashMap<>();
        for (String key: driverPinOptions.keySet()) {
            MarkerOptions markerOptions = driverPinOptions.get(key);
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setVisible(false);
            markerHashMap.put(key, marker);
        }
        this.driverPins = markerHashMap;
        destinationViewModel.setPins(pickUpLatlng, pickupAddress, googleMap, scanContent, driverPins, driverDatas);
    }

    /** check if GooglePlayServices available or not **/
    public boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getBaseActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getBaseActivity(), resultCode, Constants.PLAY_SERVICES_REQUEST).show();
            } else {
                showMessage(getAttachedContext().getTranslatedString(R.string.DeviceNotSupport));
            }
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            if (data != null && data.hasExtra(Constants.EXTRA_Data)) {
                destinationViewModel.dropAddress.set(data.getStringExtra(Constants.EXTRA_Data));
                destinationViewModel.getLocationFromAddress(data.getStringExtra(Constants.EXTRA_Data), false);
            }
        }
    }

}
