package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.content.Context;

import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 11/21/17.
 */

public interface RideNavigator extends BaseView {

    void goback();
    void ShowDropLayout(boolean hide);
    void DropCardClicked();
    void SelectedPaymentType(String a);
    BaseActivity GetBaseAct();
    void ShowPaymentProgress(boolean hide);
    void onClickPayment(Type type);
    void onClickNofSeat(ShareRideDetails shareRideDetails, String cr);
    void ShowWaitingDialog(String id);
    Context getAttachedcontext();
    boolean isAttached();
    void setCorporateUser(boolean isCorporateUser);

    void openETADialog(BaseResponse baseResponse);

    void logoutApp();
}
