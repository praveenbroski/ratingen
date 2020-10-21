package taxi.ratingen.ui.nodriveralert;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

public interface NoDriverNavigator extends BaseView {
    void reScheduleSucess();

    void onClickNO();

    void closeWaitingDialog();

    BaseActivity getBaseAct();
}
