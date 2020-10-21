package taxi.ratingen.ui.drawerscreen.mapscrn;

import android.content.Context;


import com.google.android.gms.maps.model.LatLng;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 11/13/17.
 */

public interface MapScrnNavigator extends BaseView {

    void DropCardClicked();

    void PickCardClicked();

    void PickAddressClicked();

    void DropAddressClicked();

    void HideNshowToolbar(boolean b);

    void addcarList(List<Type> types);

    BaseActivity getBaseAct();

    Context getAttachedcontext();

    void RideLaterClicked();

    void RideNowClicked();

    void HideNShowFavTitleEdit(boolean hid);

    void HideNshowFavLayout(boolean hid);

    void ChangeOldLayoutParams();

    Type GetSelectedCarObj();

    void openTripFragment(Request request);

    void openRequestLocation();

    void onConfirmation(String pickupAddress, LatLng pickupLatLng, String droAddredss, LatLng dropLatLng, LatLng currentLatlng, String value);

    void onConfirmation(String pickupAddress, LatLng pickupLatLng, LatLng currentLatlng, String value);

    void notifyNoDriverMessage(String tripData);

    void openScannerPage();

    void rideLaterClick(String s, LatLng pickupLatLng, String s1, LatLng dropLatLng, LatLng currLatlng);

    void openLaterAlert(Integer id);

    void openRideLaterAlert(Request request);

    boolean checkLocationPermission();

    void requestLocationPermission();
}
