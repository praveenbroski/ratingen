package taxi.ratingen.ui.drawerscreen.ridescreen.payment;

import androidx.databinding.ObservableBoolean;

import taxi.ratingen.retro.responsemodel.PaymentMethod;

public class PaymentMethodsViewModel {

    PaymentMethod payment;
    public ObservableBoolean isSelected;
    public PaymentMethodsViewModel.PaymentMethodsViewModeListener mListener;

    public PaymentMethodsViewModel(PaymentMethod payment, PaymentMethodsViewModel.PaymentMethodsViewModeListener listener) {
        this.payment = payment;
        this.mListener = listener;
        isSelected = new ObservableBoolean(payment.isSelected == null ? false: payment.isSelected);
    }

    public void onPaymentMethodClick() {
        payment.isSelected = true;
        mListener.onPaymentMethodClick(payment);
    }

    public interface PaymentMethodsViewModeListener {
        void onPaymentMethodClick(PaymentMethod payment);
    }

}
