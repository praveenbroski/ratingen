package taxi.ratingen.ui.tour;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/11/17.
 */

public interface TourGuideNavigator extends BaseView{
    BaseActivity getAttachedContext();

    void forwardClick();

    void onClickSkip();
}
