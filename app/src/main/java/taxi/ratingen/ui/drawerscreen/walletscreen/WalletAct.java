package taxi.ratingen.ui.drawerscreen.walletscreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityWalletBinding;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.addcard.AddCardFrag;
import taxi.ratingen.ui.drawerscreen.payment.adapter.VisaCardAdapter;
import taxi.ratingen.ui.wallethistory.WalletHistoryAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class WalletAct extends BaseActivity<ActivityWalletBinding, WalletViewModel> implements WalletNavigator, HasAndroidInjector {

    @Inject
    WalletViewModel walletViewModel;

    ActivityWalletBinding activityWalletBinding;

    @Inject
    VisaCardAdapter adapter;

    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWalletBinding = getViewDataBinding();
        walletViewModel.setNavigator(this);
        Setup();
    }

    /** Set adapter to walletRecyclerView, Calls getWalletDetails & FetchCardNumbers APIs **/
    private void Setup() {
        setSupportActionBar(activityWalletBinding.walletToolbar);
        setTitle(getTranslatedString(R.string.txt_wallet));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        activityWalletBinding.walletToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //   activityWalletBinding.walletToolbar.setTitle(getResources().getString(R.string.txt_wallet));

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        activityWalletBinding.walletRecyclerView.setLayoutManager(mLayoutManager);
        activityWalletBinding.walletRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityWalletBinding.walletRecyclerView.setAdapter(adapter);
//        activityWalletBinding.walletHistory.setText(getTranslatedString(R.string.txt_wallet_history));
        activityWalletBinding.walletHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .add(R.id.Container, WalletHistoryFrag.newInstance("", ""), WalletHistoryFrag.TAG)
                        .commit();*/

                startActivity(new Intent(WalletAct.this, WalletHistoryAct.class).putExtra("id", sharedPrefence.Getvalue(SharedPrefence.ID)).putExtra("token", sharedPrefence.Getvalue(SharedPrefence.TOKEN)));

            }
        });

        activityWalletBinding.addNewCard.setOnClickListener(v -> {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = fragmentManager.findFragmentByTag(AddCardFrag.TAG);
//            if (getSupportFragmentManager().findFragmentByTag(AddCardFrag.TAG) != null)
//                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.add_card_container, AddCardFrag.newInstance(), AddCardFrag.TAG)
                    .commit();
        });

        walletViewModel.getWalletDetails();
        walletViewModel.FetchCardNumbers();

        Intent intent = new Intent();
        setResult(Constants.WALLETSETRESULT, intent);

    }

    @Override
    public WalletViewModel getViewModel() {
        return walletViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Called when any of the price showed in the wallet screen was selected and sets selector **/
    @Override
    public void PrizeClicked(int i) {
        switch (i) {
            case 5:
                activityWalletBinding.AWHundread.setBackgroundResource(R.drawable.prizeselected);
                activityWalletBinding.AWTwoHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWFiveHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWThousand.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWHundread.setTextColor(Color.WHITE);
                activityWalletBinding.AWTwoHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWFiveHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWThousand.setTextColor(Color.BLACK);
                break;

            case 10:
                activityWalletBinding.AWHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWTwoHundread.setBackgroundResource(R.drawable.prizeselected);
                activityWalletBinding.AWFiveHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWThousand.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWTwoHundread.setTextColor(Color.WHITE);
                activityWalletBinding.AWFiveHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWThousand.setTextColor(Color.BLACK);
                break;

            case 20:
                activityWalletBinding.AWHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWTwoHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWFiveHundread.setBackgroundResource(R.drawable.prizeselected);
                activityWalletBinding.AWThousand.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWTwoHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWFiveHundread.setTextColor(Color.WHITE);
                activityWalletBinding.AWThousand.setTextColor(Color.BLACK);
                break;

            case 30:
                activityWalletBinding.AWHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWTwoHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWFiveHundread.setBackgroundResource(R.drawable.prizeunselected);
                activityWalletBinding.AWThousand.setBackgroundResource(R.drawable.prizeselected);
                activityWalletBinding.AWHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWTwoHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWFiveHundread.setTextColor(Color.BLACK);
                activityWalletBinding.AWThousand.setTextColor(Color.WHITE);
                break;
        }

    }

    /** Populate {@link List} with {@link Payment} response models to update data in adapter **/
    @Override
    public void addList(List<Payment> payments) {
        adapter.addItems(payments);
    }

    /** Returns the selected card's ID when clicked **/
    @Override
    public Integer getCardId() {
        return adapter.getSelectedCardId();
    }

    /** Returns the reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return this;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.add_card_container);
        if (currentFragment != null) {
            onFragmentDetached(currentFragment.getTag());
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentDetached(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        Objects.requireNonNull(this)
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(fragment)
                .commit();
    }

}
