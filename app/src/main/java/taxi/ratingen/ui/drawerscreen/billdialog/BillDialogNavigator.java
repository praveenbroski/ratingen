package taxi.ratingen.ui.drawerscreen.billdialog;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;


/**
 * Created by root on 12/28/17.
 */

public interface BillDialogNavigator extends BaseView {
    void dismissDialog();
    BaseActivity getAttachedContext();
}
