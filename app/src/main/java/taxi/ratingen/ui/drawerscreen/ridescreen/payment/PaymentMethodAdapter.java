package taxi.ratingen.ui.drawerscreen.ridescreen.payment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import taxi.ratingen.databinding.ChildPaymentMethodBinding;

import taxi.ratingen.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    ArrayList<taxi.ratingen.retro.responsemodel.PaymentMethod> payments;
    PaymentMethod paymentMethod;
    public Integer selectedMethodId;

    public PaymentMethodAdapter(PaymentMethod paymentMethod, ArrayList<taxi.ratingen.retro.responsemodel.PaymentMethod> payments) {
        this.paymentMethod = paymentMethod;
        this.payments = payments;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChildPaymentMethodBinding childPaymentMethodBinding = ChildPaymentMethodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChildPaymentViewHolder(childPaymentMethodBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void  addList(List<taxi.ratingen.retro.responsemodel.PaymentMethod> payments) {
        this.payments.clear();
        this.payments.addAll(payments);
        notifyDataSetChanged();
    }

    public class ChildPaymentViewHolder extends BaseViewHolder implements PaymentMethodsViewModel.PaymentMethodsViewModeListener {

        ChildPaymentMethodBinding mBinding;

        PaymentMethodsViewModel paymentMethodsViewModel;

        public ChildPaymentViewHolder(ChildPaymentMethodBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            taxi.ratingen.retro.responsemodel.PaymentMethod paymentMethod = payments.get(position);
            paymentMethodsViewModel = new PaymentMethodsViewModel(paymentMethod, this);
            mBinding.setViewModel(paymentMethodsViewModel);
            mBinding.executePendingBindings();
        }

        @Override
        public void onPaymentMethodClick(taxi.ratingen.retro.responsemodel.PaymentMethod payment) {
            Iterator it = payments.iterator();

            while (it.hasNext()) {
                taxi.ratingen.retro.responsemodel.PaymentMethod paymentMethod = (taxi.ratingen.retro.responsemodel.PaymentMethod) it.next();
                paymentMethod.isSelected = true;
            }
            notifyDataSetChanged();
        }

    }

}
