package taxi.ratingen.ui.drawerscreen.changeplace;

import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by root on 11/30/17.
 */

public interface SearchPlaceNavigator extends BaseView {
    void addList(List<Favplace> favplace);
    void showProgress(boolean show);
    void showclearButton(boolean show);
    void FinishAct();


    LatLng getLatLngForStart();

    void clearBtn();

    void openFavorites();

    void showPickClearButton(boolean b);

    void clearDropBtn();
}
