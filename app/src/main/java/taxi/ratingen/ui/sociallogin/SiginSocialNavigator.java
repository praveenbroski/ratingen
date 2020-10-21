package taxi.ratingen.ui.sociallogin;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.HashMap;

/**
 * Created by root on 10/10/17.
 */

public interface SiginSocialNavigator extends BaseView {

    void Facebookclicked();
    void gplusclicked();
    void OpenRegistration(HashMap<String, String> hm);
    void OpenDrawerAct();
    BaseActivity getBaseAct();

}
