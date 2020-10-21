package taxi.ratingen.ui.drawerscreen.mapscreen.destination;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import taxi.ratingen.ui.base.BaseView;

import java.util.HashMap;

public interface DestinationNavigator extends BaseView {

    void openSearchDestination();

    Context getAttachedContext();

    boolean checkLocationPermission();

    void openRequestLocation();

    void requestLocationPermission();

    void onClickConfirmation(String pickAddress, LatLng pickLatLng, String dropAddress, LatLng dropLatLng, LatLng currentLatLng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas);

    void openScannerPage();

    void onClickConfirmation(String s, LatLng pickupLatLng, LatLng currentLatlng, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas);
}
