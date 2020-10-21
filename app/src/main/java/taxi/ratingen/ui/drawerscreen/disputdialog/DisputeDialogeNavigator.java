package taxi.ratingen.ui.drawerscreen.disputdialog;

import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by naveen on 13/11/17.
 */

public interface DisputeDialogeNavigator extends BaseView {

    void addList(List<ComplaintList> complaintLists);

    BaseActivity GetContext();

    String getSelectedItemID();

    void dismissDialog(boolean enableDisputeButton);
}
