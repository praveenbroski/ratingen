package taxi.ratingen.ui.drawerscreen.refferalscreen;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 11/10/17.
 */

public interface RefferalNavigator extends BaseView {

    void OpenShareRefferal(String code);
    BaseActivity getAttachedContext();
    void logout();
}
