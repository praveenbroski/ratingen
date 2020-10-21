package taxi.ratingen.ui.drawerscreen.favorites;

import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

public interface FavoriteNavigator extends BaseView {

    BaseActivity getAttachedContext();

    void logout();

    void goBack();

    void addFavClicked();

    void addList(List<Favplace> favPlaces);

    void deleteFavClicked(Favplace favplace);

}
