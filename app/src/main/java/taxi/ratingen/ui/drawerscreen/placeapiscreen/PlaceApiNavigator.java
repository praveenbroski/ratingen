package taxi.ratingen.ui.drawerscreen.placeapiscreen;

import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 11/30/17.
 */

public interface PlaceApiNavigator extends BaseView {
    void addList(List<Favplace> favplace);
    void showProgress(boolean show);
    void showclearButton(boolean show);
    void FinishAct();


    void clearText();

    BaseActivity getAttachedContext();
}
