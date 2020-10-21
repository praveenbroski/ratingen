package taxi.ratingen.ui.getstarted;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 10/11/17.
 */

public interface GetStartedNavigator extends BaseView{
    BaseActivity getAttachedContext();
    void startMovingtoSignup();

    void langguageItems(List<String> items);

    void setSelectedLanguage(String s);
}
