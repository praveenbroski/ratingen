package taxi.ratingen.ui.drawerscreen.payment;

import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

/**
 * Created by root on 11/7/17.
 */

public interface PaymentNavigator extends BaseView{

    BaseActivity getBaseAct();
    void addList(List<Payment> payments);
    void OpenWalletScreen();


    void logout();

    void openCardFrag();

    void openPaymentUI(String checkoutId);
}
