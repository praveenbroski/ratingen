package taxi.ratingen.ui.signup.country_code;


import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

public interface CountryListNavigator extends BaseView {

    void countryResponse(List<CountryListModel> countryListModel);

    void dismissDialog();

    void clickedItem(String flag, String code, String name, String ID, String iso2);

}
