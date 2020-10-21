package taxi.ratingen.ui.login;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 10/9/17.
 */

public interface LoginNavigator extends BaseView {

    void OpenDrawerActivity();
    void OpenForgotActivity();
    void OpenSocialActivity();
    void OpenCountrycodevisible();
    void OpenCountrycodeINvisible();
    String getCountryCode();
    void setCountryFlag(String flag);
    BaseActivity getBaseAct();

}
