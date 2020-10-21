package taxi.ratingen.ui.drawerscreen.support;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 11/10/17.
 */

public interface SupportFragNavigator extends BaseView {
    BaseActivity getAttachedContext();

    void logout();
}
