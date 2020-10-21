package taxi.ratingen.ui.drawerscreen.canceldialog;

import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 1/29/18.
 */

public interface cancelDialogNavigator extends BaseView {

    void dismissDialog();
    BaseActivity getAttachedContext();
    void DismisswithTarget();
    void setCancelList(List<BaseResponse.ReasonCancel> list);
    String getSelectionPosition();
    void selectedOthers(boolean isOthers);
}
