package taxi.ratingen.ui.drawerscreen.profilescrn.edit;

import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseView;

import java.util.ArrayList;

public interface NameMailNavigator extends BaseView {

    void clearText();

    void finishAct();

    void SendBroadcast();

    void openCountryDialog(ArrayList<CountryListModel> countryListModels);

    void openOtpPage(String s);

}
