package taxi.ratingen.ui.drawerscreen.historydetails;

import com.google.android.gms.maps.model.BitmapDescriptor;
import taxi.ratingen.retro.responsemodel.Bill;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 1/5/18.
 */

public interface HistoryDetNavigator extends BaseView {
    BaseActivity getBaseAct();
    void setResultnFinish();
    BitmapDescriptor getMarkerIcon(int drawID);

    void setRecyclerAdapter(String currency, List<Bill.AdditionalCharge> additionalCharge);

    void openDisputeDialog(String getvalue, String getvalue1, String requestID);
}
