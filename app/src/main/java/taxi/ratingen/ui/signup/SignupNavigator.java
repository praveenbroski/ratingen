package taxi.ratingen.ui.signup;

import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.ArrayList;

/**
 * Created by root on 10/7/17.
 */

public interface SignupNavigator extends BaseView {

    void openOtpActivity(boolean isSignup);
    void openSocialActivity();
    void openAlertToLogin();
    void HideKeyBoard();
    String getCountryCode();
    String getCountryShortName();
    void setCountryCode(String flat);

    void openOtpPage(boolean b, String s, String iso2);
    BaseActivity getBaseAct();
    void openCountryDialog(ArrayList<CountryListModel> countryListModel);

}
