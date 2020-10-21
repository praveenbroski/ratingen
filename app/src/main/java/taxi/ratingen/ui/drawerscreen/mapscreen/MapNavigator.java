package taxi.ratingen.ui.drawerscreen.mapscreen;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 10/11/17.
 */

public interface MapNavigator extends BaseView{

    BaseActivity getAttachedContext();

    ArrayList<String> getPlaceList(String s);

    LatLng getReverseGeocode(String s);

    void getAutoCompletedAddress(String s);

    boolean checkLocationPermission();

    void openRequestLocation();

    void requestLocationPermission();

    void openDestinationFragment(String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas);

    void addcarList(List<Type> types);

    String GetSelectedCarObj();

    void openRideLaterAlert(Request request);

    void openTripFragment(Request request);

    void notifyNoDriverMessage(String cancelled_request);

    void pickAddressClicked();

    void openScannerPage();

    void openSideMenu();
}
