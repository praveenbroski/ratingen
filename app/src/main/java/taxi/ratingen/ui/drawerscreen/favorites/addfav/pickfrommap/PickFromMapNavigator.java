package taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

public interface PickFromMapNavigator extends BaseView {

    BaseActivity getAttachedContext();

    void goBack();

    void openSearchDestination();

    boolean checkLocationPermission();

    void requestLocationPermission();

    void openRequestLocation();

    void selectPlaceClicked();

}
