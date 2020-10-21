package taxi.ratingen.ui.companyvalidation.dialog;

import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.ArrayList;

/**
 * Created by root on 12/28/17.
 */

public interface DialogComanyNavigator extends BaseView {
    BaseActivity getAttachedContext();
    void setCurrentCountryCode(String countryCode);

    void continueUsage(String expirysoon);

    void showAlert(String errorMessage);

    void showmessage(String expirysoon);

    void openDrawerActivity();

    void openCountryDialog(ArrayList<CountryListModel> countryListModels);

}
