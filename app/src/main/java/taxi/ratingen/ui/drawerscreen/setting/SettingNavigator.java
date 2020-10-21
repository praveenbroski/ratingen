package taxi.ratingen.ui.drawerscreen.setting;

import taxi.ratingen.ui.base.BaseActivity;

import java.util.List;

/**
 * Created by root on 12/20/17.
 */

public interface SettingNavigator {
    BaseActivity getAtachedContext();

    void langguageItems(List<String> items);

    void refreshScreen();

    void setSelectedLanguage(String s);
}
