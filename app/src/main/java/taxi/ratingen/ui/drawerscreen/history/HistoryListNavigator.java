package taxi.ratingen.ui.drawerscreen.history;

import java.util.List;

import taxi.ratingen.retro.responsemodel.history;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

/**
 * Created by root on 1/4/18.
 */

public interface HistoryListNavigator extends BaseView {
    void addItem(List<history> histories);
    void Dostaff();
    void MentionLastPage();

    BaseActivity getAttachedContext();

    void logout();

    void fetchHistoryList();

}
