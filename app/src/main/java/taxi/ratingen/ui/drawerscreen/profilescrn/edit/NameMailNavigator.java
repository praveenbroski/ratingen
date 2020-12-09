package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import android.view.View;

import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.ArrayList;

public interface NameMailNavigator extends BaseView {

    void clearText();

    void finishAct();

    void SendBroadcast();

    void openCountryDialog(ArrayList<CountryListModel> countryListModels);

    void openOtpPage(String uuid_value, Integer toRegOrLog, String phoneNum, String countryId, String countryPrefix);

    BaseActivity getBaseAct();

    void countryResponse(Object data);

    View getBaseView();

}
