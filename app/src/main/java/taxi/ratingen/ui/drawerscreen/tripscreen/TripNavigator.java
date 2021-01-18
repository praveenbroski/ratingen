package taxi.ratingen.ui.drawerscreen.tripscreen;

import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 12/21/17.
 */

public interface TripNavigator extends BaseView {

    void LeftNavclicked();
    void RightNavclicked();
    boolean isAddedinAct();
    void HomeScreen();
    void ShowFeedBackScreen(Request request, boolean isCorporate);
    void ShowPromoCodeScrn(String reqid);
    void ShowCancelDialog(String requedid);
    BaseActivity getbaseAct();

    void chagePickup(boolean isPickup);

    void notifyNoDriverMessage();

    void openRequestLocation();

    void sosClicked();

    void openCancelalert();

    void callTripCancel();


    void setDropValue(String from, Double pickLat, Double pickLng, Double dropLat, Double dropLng);

    void setVAlue(String from, Double pickLat, Double pickLng, Double dropLat, Double dropLng);

    void openTripCancelMsg();

    void ShowFeedBackScreen(TaxiRequestModel.ResultData metaRequest, boolean isCorporate);

}
