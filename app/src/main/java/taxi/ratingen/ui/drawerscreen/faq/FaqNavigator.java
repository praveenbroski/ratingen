package taxi.ratingen.ui.drawerscreen.faq;

import taxi.ratingen.retro.responsemodel.FAQModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by naveen on 13/11/17.
 */

public interface FaqNavigator extends BaseView {
    void loadFaQItems(List<FAQModel> faqModelList);
    BaseActivity getAttachedContext();

    void logout();
}
