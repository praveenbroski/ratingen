package taxi.ratingen.ui.drawerscreen.promoscrn;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 1/3/18.
 */

public interface PromoNavigator extends BaseView {
    void setResult();
    void setResult(String bookedId);
    BaseActivity getBaseAct();

}
