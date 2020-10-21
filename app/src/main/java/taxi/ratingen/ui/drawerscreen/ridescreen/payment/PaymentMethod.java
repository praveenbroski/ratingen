package taxi.ratingen.ui.drawerscreen.ridescreen.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityPaymentBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class PaymentMethod extends BaseActivity<ActivityPaymentBinding, PaymentViewModel> implements PaymentNavigator, HasAndroidInjector {

    @Inject
    PaymentViewModel paymentViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    ActivityPaymentBinding activityPaymentBinding;

    LinearLayoutManager mLayoutManager;

    @Inject
    PaymentMethodAdapter adapter;

    ArrayList<String> mParam1;

    LinearLayout PB_Cashlayout, PB_Cardlayout, PB_walletlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentBinding = getViewDataBinding();
        paymentViewModel.setNavigator(this);
        activityPaymentBinding.toolbar.setNavigationOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_Data, "");
            setResult(RESULT_OK, data);
            finish();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.EXTRA_PAYMENT_BUNDLE)) {
            mParam1 = bundle.getStringArrayList(Constants.EXTRA_PAYMENT_BUNDLE);
            Log.v("fatal_log", mParam1.toString());
        }

//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        activityPaymentBinding.paymentMethodRecycler.setLayoutManager(mLayoutManager);
//        activityPaymentBinding.paymentMethodRecycler.setItemAnimator(new DefaultItemAnimator());
//        activityPaymentBinding.paymentMethodRecycler.setAdapter(adapter);
//
//        com.user.client.retro.responsemodel.PaymentMethod paymentMethod = new com.user.client.retro.responsemodel.PaymentMethod();
//        paymentMethod.setCardId(1);
//        paymentMethod.setLastNumber("1234");
//        paymentMethod.setCardType("Visa");
//        paymentMethod.setDefault(false);
//        paymentMethod.setPreferred_payment_type(0);
//        paymentMethod.setCheckoutId("4");
//        paymentMethod.setSelected(false);
//
//        List<com.user.client.retro.responsemodel.PaymentMethod> paymentMethods = new ArrayList<>();
//        paymentMethods.add(paymentMethod);

//        adapter.addList(paymentMethods);

        initializeView();
    }

    private void initializeView() {
        PB_Cashlayout = activityPaymentBinding.llPayCash;
        PB_Cardlayout = activityPaymentBinding.llPayCard;
        PB_walletlayout = activityPaymentBinding.llPayWallet;

        if (mParam1 != null && mParam1.size() > 0) {
            if (mParam1.contains("cash")) {
                PB_Cashlayout.setVisibility(View.VISIBLE);
            } else {
                PB_Cashlayout.setVisibility(View.GONE);
//                PB_Cardlayout.setVisibility(View.GONE);
//                PB_walletlayout.setVisibility(View.GONE);
            }
            if (mParam1.contains("card")) {
                PB_Cardlayout.setVisibility(View.VISIBLE);
            } else {
                PB_Cardlayout.setVisibility(View.GONE);
                /*PB_Cashlayout.setVisibility(View.GONE);
                PB_walletlayout.setVisibility(View.GONE);*/
            }

            if (mParam1.contains("wallet")) {
                PB_walletlayout.setVisibility(View.VISIBLE);
            } else {
                PB_walletlayout.setVisibility(View.GONE);
//                PB_Cashlayout.setVisibility(View.GONE);
//                PB_Cardlayout.setVisibility(View.GONE);
            }if (mParam1.contains("all")) {
                PB_Cashlayout.setVisibility(View.VISIBLE);
                PB_Cardlayout.setVisibility(View.VISIBLE);
                PB_walletlayout.setVisibility(View.VISIBLE);
            }

        } else {
            PB_Cashlayout.setVisibility(View.VISIBLE);
            PB_Cardlayout.setVisibility(View.VISIBLE);
            PB_walletlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public PaymentViewModel getViewModel() {
        return paymentViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Override
    public void onCashClick() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_Data, "cash");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onWalletClick() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_Data, "wallet");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onCardClick() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_Data, "card");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_Data, "");
        setResult(RESULT_OK, data);
        finish();
    }

}
