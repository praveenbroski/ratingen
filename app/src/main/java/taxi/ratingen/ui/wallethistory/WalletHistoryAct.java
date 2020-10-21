package taxi.ratingen.ui.wallethistory;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.databinding.ActivityWalletHistBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

public class WalletHistoryAct extends BaseActivity<ActivityWalletHistBinding, WalletHistoryViewModel> implements WalletHistoryNavigator {

    @Inject
    WalletHistoryViewModel walletHistoryViewModel;
    ActivityWalletHistBinding walletHistBinding;
    @Inject
    SharedPrefence sharedPrefence;
    LinearLayoutManager linearLayoutManager, linearLayoutManager1;
    TranslationModel translationModel;
    String Fromid, Fromtoken;
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 1000;
    private int currentPage = PAGE_START;
    WalletHistAdapter adapter;
    CancellationAdapter cancellationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walletHistoryViewModel = getViewModel();
        walletHistBinding = getViewDataBinding();
        walletHistoryViewModel.setNavigator(this);


        setSupportActionBar(walletHistBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTranslatedString(R.string.txt_wallet_history));
//
//        linearLayoutManager = new LinearLayoutManager(this);
//        walletHistBinding.walletHistoryRecycle.setLayoutManager(linearLayoutManager);
//        adapter = new WalletHistAdapter();
//        walletHistBinding.walletHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
//        walletHistBinding.walletHistoryRecycle.setAdapter(adapter);


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager1 = new LinearLayoutManager(this);

        adapter = new WalletHistAdapter();
        cancellationAdapter = new CancellationAdapter();

        walletHistBinding.walletHistoryRecycle.addOnScrollListener(recyclerViewOnScrollListener);
        //    walletHistBinding.cancelRecycler.addOnScrollListener(cancelledRecycleListener);

        walletHistBinding.walletHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
        walletHistBinding.walletHistoryRecycle.setLayoutManager(linearLayoutManager);
        walletHistBinding.walletHistoryRecycle.setAdapter(adapter);

        walletHistBinding.cancelRecycler.setItemAnimator(new DefaultItemAnimator());
        walletHistBinding.cancelRecycler.setLayoutManager(linearLayoutManager1);
        walletHistBinding.cancelRecycler.setAdapter(cancellationAdapter);

        if (getIntent() != null) {
            Fromid = getIntent().getStringExtra("id");
            Fromtoken = getIntent().getStringExtra("token");
            walletHistoryViewModel.id.set(Fromid);
            walletHistoryViewModel.token.set(Fromtoken);
        }

        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = new Gson().fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);
        }

        walletHistoryViewModel.setUp(currentPage, 1);
    }


    /**
     * This detects the state  Recycler items scroll.
     */
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    currentPage += 1;
                    walletHistoryViewModel.setUp(currentPage, 1);
                }
            }
        }
    };

//    private RecyclerView.OnScrollListener cancelledRecycleListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = linearLayoutManager1.getChildCount();
//            int totalItemCount = linearLayoutManager1.getItemCount();
//            int firstVisibleItemPosition = linearLayoutManager1.findFirstVisibleItemPosition();
//
//            if (!isLoading && !isLastPage) {
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0
//                        && totalItemCount >= PAGE_SIZE) {
//                    currentPage += 1;
//                    walletHistoryViewModel.setUp(currentPage, 0);
//                }
//            }
//        }
//    };

    @Override
    public WalletHistoryViewModel getViewModel() {
        return walletHistoryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_hist;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /*
     * @param history is a list of history of the drivers.
     * @param currencySymbol is a currency symbol of client's country.
     */
    @Override
    public void listWalletHistory(List<HistoryDetailsModel> history, String currencySymbol) {
        isLoading = true;
        walletHistBinding.noItemFound.setVisibility(View.GONE);
        walletHistBinding.walletHistoryRecycle.setVisibility(View.VISIBLE);
        walletHistBinding.cancelRecycler.setVisibility(View.GONE);
        adapter.addAll(history, currencySymbol);
    }

    /**
     * this method defines when the history is not available.
     */
    @Override
    public void noHistoryFound() {
        walletHistBinding.noItemFound.setVisibility(View.VISIBLE);
        walletHistBinding.walletHistoryRecycle.setVisibility(View.GONE);
        walletHistBinding.cancelRecycler.setVisibility(View.GONE);
//        walletHistBinding.noItemFound.setText(translationModel.text_wallethistory_empty);
        isLoading = false;
    }

    /**
     * This method defines getting all the history list.
     */
    @Override
    public void allClick() {
        currentPage = 1;
        adapter.historyList.clear();
    }

    /**
     * This method defines cancelled trip history.
     */
    @Override
    public void canclClick() {
        cancellationAdapter.cancelledTripList.clear();
        currentPage = 1;
    }

    /**
     * @param cancelledTripList holds the canccelled list items.
     */
    @Override
    public void cancelledTripList(List<CancelledListModel> cancelledTripList) {
        isLoading = false;
        walletHistBinding.noItemFound.setVisibility(View.GONE);
        walletHistBinding.walletHistoryRecycle.setVisibility(View.GONE);
        walletHistBinding.cancelRecycler.setVisibility(View.VISIBLE);
        cancellationAdapter.addAll(cancelledTripList, translationModel.txt_unbilled);
    }

    /**
     * this method defines to stop the reccyle scrolls
     */
    @Override
    public void stopRecycle() {
        isLoading = true;
    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    /**
     * @return the view, that handle the back pressed.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }
}
