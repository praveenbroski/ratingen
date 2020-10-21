package taxi.ratingen.ui.drawerscreen.favorites.addfav;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

public interface AddFavNavigator extends BaseView {

    BaseActivity getAttachedContext();

    void logout();

    void goBack();

    void openPickFromMapFragment();

    void goBackRefresh();
}
