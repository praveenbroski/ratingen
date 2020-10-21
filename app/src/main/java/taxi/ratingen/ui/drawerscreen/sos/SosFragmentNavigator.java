package taxi.ratingen.ui.drawerscreen.sos;

import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.ui.base.BaseView;
import taxi.ratingen.ui.drawerscreen.DrawerAct;

import java.util.List;

/**
 * Created by root on 3/8/18.
 */

public interface SosFragmentNavigator extends BaseView {
    void setSosList(List<So> listso);
    DrawerAct getAttachedContext();

}
