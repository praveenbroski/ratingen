package taxi.ratingen.ui.drawerscreen.complaint;

import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 12/20/17.
 */

public interface ComplaintNavigator extends BaseView {

    void addList(List<ComplaintList> complaintLists);

    BaseActivity GetContext();

    void DisableSpinner(Boolean status);

    void logout();

    BaseActivity getBaseAct();

}
