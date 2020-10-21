package taxi.ratingen.ui.drawerscreen;

import taxi.ratingen.retro.responsemodel.Driver;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface DrawerNavigator extends BaseView{

    void logout();
    void openOptionalActivity();
    void ShowMapFragment();
    void ShowFeedbackFragment(Request request, boolean isCorporate);
    void ShowTripFragment(Request request, Driver driver);

    BaseActivity getBaseAct();
    /**
     * Hide history when user is Corporate user else
     * Show History
     * */
    void enableCorporateUser(boolean isCorporate);

    void openRideLaterAlert(Request request, Driver driver);

    void openCloseDrawer();

    void onClickNotification();
}
