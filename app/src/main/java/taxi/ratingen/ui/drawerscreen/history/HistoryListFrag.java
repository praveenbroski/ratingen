package taxi.ratingen.ui.drawerscreen.history;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.history;
import taxi.ratingen.databinding.FragmentHistoryListBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.history.adapter.HistoryAdapter;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.PaginationScrollListener;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;


public class HistoryListFrag extends BaseFragment<FragmentHistoryListBinding, HistoryListViewModel> implements HistoryListNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "HistoryListFrag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Inject
    HistoryListViewModel historyListViewModel;


    @Inject
    @Named("HistoryList")
    LinearLayoutManager mLayoutManager;


    @Inject
    HistoryAdapter adapter;


    FragmentHistoryListBinding fragmentHistoryListBinding;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isScheduledLastPage = false;
    private boolean isCompletedLastPage = false;
    private boolean isCancelledLastPage = false;
    private boolean isdeleted = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 1000;
    private int currentScheduledPage = PAGE_START;
    private int currentCompletedPage = PAGE_START;
    private int currentCancelledPage = PAGE_START;


    public HistoryListFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryListFrag newInstance(String param1, String param2) {
        HistoryListFrag fragment = new HistoryListFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentHistoryListBinding = getViewDataBinding();
        historyListViewModel.setNavigator(this);
        getActivity().setTitle(getAttachedContext().getTranslatedString(R.string.text_title_History));
        Setup();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.isItemClick = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getBaseActivity().HideNshowToolbar(false);
    }

    @Override
    public HistoryListViewModel getViewModel() {
        return historyListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_history_list;
    }

    /** sets history list adapter with the data from API call **/
    private void Setup() {
        fragmentHistoryListBinding.toolbar.setNavigationOnClickListener(v -> goBack());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentHistoryListBinding.recyclerViewHistory.setLayoutManager(mLayoutManager);
        fragmentHistoryListBinding.recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
        fragmentHistoryListBinding.recyclerViewHistory.setAdapter(adapter);

        fragmentHistoryListBinding.recyclerViewHistory.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                if (historyListViewModel.scheduledClick.get()) {
                    currentScheduledPage += 1;
                    historyListViewModel.fetchData(currentScheduledPage);
                } else if (historyListViewModel.completedClick.get()) {
                    currentCompletedPage += 1;
                    historyListViewModel.fetchData(currentCompletedPage);
                } else if (historyListViewModel.cancelledClick.get()) {
                    currentCancelledPage += 1;
                    historyListViewModel.fetchData(currentCancelledPage);
                }
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                if (historyListViewModel.scheduledClick.get()) {
                    return isScheduledLastPage;
                } else if (historyListViewModel.completedClick.get()) {
                    return isCompletedLastPage;
                } else if (historyListViewModel.cancelledClick.get()) {
                    return isCancelledLastPage;
                } else {
                    return false;
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        fragmentHistoryListBinding.txtNoData.setText(getBaseActivity().getTranslatedString(R.string.txt_no_upcoming_rides));
        if (historyListViewModel.scheduledClick.get()) {
            historyListViewModel.fetchData(currentScheduledPage);
        } else if (historyListViewModel.completedClick.get()) {
            historyListViewModel.fetchData(currentCompletedPage);
        } else if (historyListViewModel.cancelledClick.get()) {
            historyListViewModel.fetchData(currentCancelledPage);
        }
        getBaseActivity().HideNshowToolbar(true);
    }

    /** populates history list with data from API **/
    @Override
    public void addItem(List<history> histories) {
        adapter.addItem(histories, isdeleted);
        isdeleted = false;

        if (historyListViewModel.scheduledClick.get()) {
            if (currentScheduledPage <= TOTAL_PAGES) adapter.addLoadingFooter();
            else isScheduledLastPage = true;
        } else if (historyListViewModel.completedClick.get()) {
            if (currentCompletedPage <= TOTAL_PAGES) adapter.addLoadingFooter();
            else isCompletedLastPage = true;
        } else if (historyListViewModel.cancelledClick.get()) {
            if (currentCancelledPage <= TOTAL_PAGES) adapter.addLoadingFooter();
            else isCancelledLastPage = true;
        }
    }

    /** removes bottom loading circular progress bar **/
    @Override
    public void Dostaff() {
        adapter.removeLoadingFooter();
        isLoading = false;
    }

    /** notifies when paging reached it's last page **/
    @Override
    public void MentionLastPage() {
        if (historyListViewModel.scheduledClick.get()) {
            if (currentScheduledPage != 1) {
                adapter.removeLoadingFooter();
                isLoading = false;
                isScheduledLastPage = true;
            }
        } else if (historyListViewModel.completedClick.get()) {
            if (currentCompletedPage != 1) {
                adapter.removeLoadingFooter();
                isLoading = false;
                isCompletedLastPage = true;
            }
        } else if (historyListViewModel.cancelledClick.get()) {
            if (currentCancelledPage != 1) {
                adapter.removeLoadingFooter();
                isLoading = false;
                isCancelledLastPage = true;
            }
        }
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(HistoryListFrag.this)
                .commit();
    }

    /** logs out the user **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

    @Override
    public void fetchHistoryList() {
        adapter.clearList();
        currentScheduledPage = PAGE_START;
        currentCompletedPage = PAGE_START;
        currentCancelledPage = PAGE_START;
        isScheduledLastPage = false;
        isCompletedLastPage = false;
        isCancelledLastPage = false;
        if (historyListViewModel.scheduledClick.get()) {
            fragmentHistoryListBinding.txtNoData.setText(getBaseActivity().getTranslatedString(R.string.txt_no_upcoming_rides));
            historyListViewModel.fetchData(currentScheduledPage);
        } else if (historyListViewModel.completedClick.get()) {
            fragmentHistoryListBinding.txtNoData.setText(getBaseActivity().getTranslatedString(R.string.txt_no_completed_rides));
            historyListViewModel.fetchData(currentCompletedPage);
        } else if (historyListViewModel.cancelledClick.get()) {
            fragmentHistoryListBinding.txtNoData.setText(getBaseActivity().getTranslatedString(R.string.txt_no_cancelled_rides));
            historyListViewModel.fetchData(currentCancelledPage);
        }
    }

    /** returns the reference for {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /** callback for results returned from previous page **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.HISTORYLISTSETRESULT) {
            if (data.getExtras().getString(Constants.EXTRA_Data).equalsIgnoreCase("Done")) {
                isdeleted = true;
                if (historyListViewModel.scheduledClick.get()) {
                    currentScheduledPage = PAGE_START;
                    historyListViewModel.fetchData(currentScheduledPage);
                } else if (historyListViewModel.completedClick.get()) {
                    currentCompletedPage = PAGE_START;
                    historyListViewModel.fetchData(currentCompletedPage);
                } else if (historyListViewModel.cancelledClick.get()) {
                    currentCancelledPage = PAGE_START;
                    historyListViewModel.fetchData(currentCancelledPage);
                }

            }
        }
    }

}
