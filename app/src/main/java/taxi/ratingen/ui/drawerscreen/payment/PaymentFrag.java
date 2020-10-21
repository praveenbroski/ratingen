package taxi.ratingen.ui.drawerscreen.payment;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.databinding.FragmentPaymentBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.payment.adapter.VisaCardAdapter;
import taxi.ratingen.ui.drawerscreen.walletscreen.WalletAct;
import taxi.ratingen.utilz.Constants;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;


public class PaymentFrag extends BaseFragment<FragmentPaymentBinding, PaymentFragViewModel> implements PaymentNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "PaymentFrag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentPaymentBinding fragmentPaymentBinding;

    @Inject
    @Named("Payment")
    LinearLayoutManager mLayoutManager;

    @Inject
    PaymentFragViewModel paymentFragViewModel;

    @Inject
    VisaCardAdapter adapter;
    boolean firstTime = true;

    public PaymentFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFrag newInstance(String param1, String param2) {
        PaymentFrag fragment = new PaymentFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentFrag newInstance(List<Payment> payment) {
        return new PaymentFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getBaseAct().getTranslatedString(R.string.txt_Payment));

        fragmentPaymentBinding = getViewDataBinding();
        paymentFragViewModel.setNavigator(this);
        Setup();
    }

    /**
     * Setting adapter to payment recyclerView
     * Call API to get default payment method
     * Call API to validate client token
     * Call API to get saved card details
     * **/
    private void Setup() {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentPaymentBinding.paymentRecyclerView.setLayoutManager(mLayoutManager);
        fragmentPaymentBinding.paymentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fragmentPaymentBinding.paymentRecyclerView.setAdapter(adapter);
        paymentFragViewModel.GetYourPrefferdPayment();
        paymentFragViewModel.GetClient();
        paymentFragViewModel.FetchCardNumbers();
    }

    @Override
    public PaymentFragViewModel getViewModel() {
        return paymentFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_payment;
    }

    @Override
    public BaseActivity getBaseAct() {
        return getBaseActivity();
    }

    /** Populate payments(cards) list
     * @param payments **/
    @Override
    public void addList(List<Payment> payments) {
        adapter.addItems(payments);
    }

    /** Opens {@link WalletAct} wallet screen **/
    @Override
    public void OpenWalletScreen() {
        getBaseActivity().startActivityForResult(new Intent(getBaseActivity(), WalletAct.class), Constants.WALLETSETRESULT);
    }

    /** Callback to get {@link Intent} data from previous screens if it has any
     * @param requestCode
     * @param resultCode
     * @param data **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.BRAINTREE_REQUEST_CODE) {
            // commented by ramesh
            /*DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            PaymentMethodNonce payDetails = result.getPaymentMethodNonce();*/
            //  paymentFragViewModel.addCard(payDetails);
        } else if (resultCode == Constants.WALLETSETRESULT) {
            paymentFragViewModel.FetchCardNumbers();
        } else {
           /* String resourcePath = data.getStringExtra(
                    CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
            Log.e("resourcePathpayment==", "resourcePath==" + resourcePath);*/
            if (firstTime) {
              //  paymentFragViewModel.addCard(resourcePath);
                firstTime = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        firstTime = true;
    }

    /** Logs out the user **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

    /** Gets checkout ID by calling API **/
    @Override
    public void openCardFrag() {
        paymentFragViewModel.RequestIdApi();
        /*if (getActivity() != null) {
            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.disallowAddToBackStack();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.Container, AddCardFrag.newInstance(), AddCardFrag.TAG)
                    .commitAllowingStateLoss();
        }*/

    }

    /** Opens UI of the payment gateway proceed with payment **/
    @Override
    public void openPaymentUI(String checkoutId) {
        Set<String> paymentBrands = new LinkedHashSet<>();
        paymentBrands.add("VISA");
        paymentBrands.add("MASTER");
       /* CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutId, paymentBrands, Connect.ProviderMode.TEST);
        checkoutSettings.setShopperResultUrl("companyname://result");
        checkoutSettings.setStorePaymentDetailsMode(CheckoutStorePaymentDetailsMode.PROMPT);
        Intent intent = checkoutSettings.createCheckoutActivityIntent(getActivity());
        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);*/
    }

}
