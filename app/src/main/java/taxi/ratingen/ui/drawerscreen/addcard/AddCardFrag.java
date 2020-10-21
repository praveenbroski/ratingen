package taxi.ratingen.ui.drawerscreen.addcard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.databinding.AddCardLayBinding;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.payment.PaymentFrag;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class AddCardFrag extends BaseFragment<AddCardLayBinding, AddCardViewModel> implements AddCardNavigator {

    public static final String TAG = "AddCardFrag";
    AddCardLayBinding addCardLayBinding;
    @Inject
    AddCardViewModel addCardViewModel;

    @Override
    public AddCardViewModel getViewModel() {
        return addCardViewModel;
    }

    public static AddCardFrag newInstance() {
        return new AddCardFrag();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addCardLayBinding = getViewDataBinding();
        addCardViewModel.setNavigator(this);

        addCardLayBinding.toolbar.setNavigationOnClickListener(v -> goBack());
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.add_card_lay;
    }

    /** opens {@link PaymentFrag} after add card API call **/
    @Override
    public void openPaymentFrag(List<Payment> payment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(AddCardFrag.TAG);
        if (getActivity().getSupportFragmentManager().findFragmentByTag(AddCardFrag.TAG) != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.Container, PaymentFrag.newInstance("",""), PaymentFrag.TAG)
                .commit();
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(AddCardFrag.this)
                .commit();
    }

}
