package taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog;

import android.app.Activity;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 12/21/17.
 */

public interface WaitProgressNavigator extends BaseView {

    void dismissDialog();

    String getRequestid();

    BaseActivity getBaseAct();

}
