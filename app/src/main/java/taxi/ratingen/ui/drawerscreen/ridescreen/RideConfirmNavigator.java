package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.content.Context;

import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.NewRequestModel;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.TripRegisteredDetails;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 11/21/17.
 */

public interface RideConfirmNavigator extends BaseView {
    void goback();

    //    public void ShowDropLayout(boolean hide);
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

    void addCarList(List<Type> types);

    BaseActivity getBaseAct();

    Type GetSelectedCarObj();

    void onClickRideType(boolean isAcceptShare);

    void RideLaterClicked();

    void notifyNoDriverMessage();

    // void scheduleSucess(List<TopDriverModel> driverList, String s, String reqId, String requestId, String id, String token, double latitude, double longitude);

    void scheduleSucess(String type_id, String req_id, String user_id, String user_token, double latitude, double longitude);

    void setUpPayment(List<String> payment);

    void openAlert(String currency, double driverAddCharges);

    void onclickpromoCode(String id);

    void promoAvail();

    void openBlockedAlert();

    void openTripRegisteredAlert(TripRegisteredDetails tripRegisteredDetails);

    void onClickTripSchedule();

    void onClickNotesToDriver();

    void setNoDrivers();

    boolean isAddedInAct();

    void setRouteData(Route routeDest);

    void openTripFragment(Request request);

    void openTripFragment(NewRequestModel request);

    void promoCodeSet(String bookedID);

    void refreshTypesAdapter(String etaPrice, String etaTime);

    void refreshTypesAdapter(List<Type> types);

}
