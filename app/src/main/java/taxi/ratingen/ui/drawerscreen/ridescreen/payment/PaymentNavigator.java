package taxi.ratingen.ui.drawerscreen.ridescreen.payment;

import taxi.ratingen.ui.base.BaseView;

public interface PaymentNavigator extends BaseView {

    void onCashClick();

    void onWalletClick();

    void onCardClick();

}
