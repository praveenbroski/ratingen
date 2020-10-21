package taxi.ratingen.ui.splash;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface SplashNavigator extends BaseView{
    BaseActivity getAttachedContext();
    void startRequestingPermissions();
}
