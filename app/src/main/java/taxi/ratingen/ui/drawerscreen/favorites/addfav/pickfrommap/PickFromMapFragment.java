package taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.databinding.library.baseAdapters.BR;

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
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentPickFromMapBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.changeplace.SearchPlaceActivity;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.AddFavViewModel;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.Objects;

import javax.inject.Inject;

public class PickFromMapFragment extends BaseFragment<FragmentPickFromMapBinding, PickFromMapViewModel> implements PickFromMapNavigator, OnMapReadyCallback {

    public static final String TAG = "PickFromMapFragment";

    private static final String ParampickupAddr = "pickupAddr";

    @Inject
    PickFromMapViewModel viewModel;
    FragmentPickFromMapBinding binding;
    @Inject
    SharedPrefence sharedPrefence;

    public GoogleMap googleMap;

    public static AddFavViewModel addFavViewModel;

    public PickFromMapFragment() {

    }

    public static PickFromMapFragment newInstance(AddFavViewModel viewModel) {
        addFavViewModel = viewModel;
        PickFromMapFragment fragment = new PickFromMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        setup();
    }

    private void setup() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (getBaseActivity() != null)
                getBaseActivity().onFragmentDetached(PickFromMapFragment.TAG);
        });
        binding.toolbar.setTitle("Select location");

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView)).getMapAsync(this);
    }

    @Override
    public PickFromMapViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_pick_from_map;
    }

    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(PickFromMapFragment.this)
                .commit();
    }

    @Override
    public void openSearchDestination() {
        viewModel.getMap().clear();
        viewModel.getMap().put(Constants.EXTRA_IS_PICKUP, "0");
        viewModel.getMap().put(ParampickupAddr, viewModel.mAddress.get());
        viewModel.getMap().put(Constants.Extra_identity, getAttachedContext().getTranslatedString(R.string.txt_EnterDrop));
        startActivityForResult(new Intent(getContext(), SearchPlaceActivity.class)
                .putExtra(Constants.EXTRA_Data, viewModel.getMap()), Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public boolean checkLocationPermission() {
        return (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !getBaseActivity().checkGranted(Constants.Array_permissions)) {
            getBaseActivity().requestPermissionsSafely(Constants.Array_permissions, Constants.REQUEST_PERMISSION);
        }
    }

    @Override
    public void openRequestLocation() {
        displayLocationSettingsRequest(getActivity());
    }

    @Override
    public void selectPlaceClicked() {
        addFavViewModel.mAddress.set(viewModel.mAddress.get());
        goBack();
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        viewModel.buildGoogleApiClient(googleMap);

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
                viewModel.mAddress.set(data.getStringExtra(Constants.EXTRA_Data));
                viewModel.getLocationFromAddress(data.getStringExtra(Constants.EXTRA_Data));
            }
        }
    }

}
