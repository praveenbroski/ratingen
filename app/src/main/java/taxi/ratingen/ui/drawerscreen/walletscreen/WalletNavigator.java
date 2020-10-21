package taxi.ratingen.ui.drawerscreen.walletscreen;

import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 11/3/17.
 */

public interface WalletNavigator extends BaseView{

    void PrizeClicked(int i);
    void addList(List<Payment> payments);
    Integer getCardId();
    BaseActivity getAttachedContext();
}
