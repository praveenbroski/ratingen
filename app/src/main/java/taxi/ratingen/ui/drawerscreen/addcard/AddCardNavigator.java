package taxi.ratingen.ui.drawerscreen.addcard;

import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.base.BaseView;

import java.util.List;

public interface AddCardNavigator extends BaseView {
    void openPaymentFrag(List<Payment> payment);
}
