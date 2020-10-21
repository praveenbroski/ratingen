package taxi.ratingen.ui.drawerscreen.faq;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;


import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.FAQModel;
import taxi.ratingen.databinding.FragmentFaqBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by root on 12/20/17.
 */

public class FaqFragment extends BaseFragment<FragmentFaqBinding, FaqFragmentViewModel> implements FaqNavigator {
    public static final String TAG = "FaqFragment";
    @Inject
    FaqFragmentViewModel oneViewModel;
    FragmentFaqBinding binding;
    @Inject
    SharedPrefence sharedPrefence;
    FAQAdapter adapter;

    public FaqFragment() {
        // Required empty public constructor
    }

    public static FaqFragment newInstance() {
        FaqFragment fragment = new FaqFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        oneViewModel.setNavigator(this);
        oneViewModel.setUpData();
        getBaseActivity().setTitle(getAttachedContext().getString(R.string.text_faq));

        binding.toolbar.setNavigationOnClickListener(v -> {
            goBack();
        });
        getBaseActivity().HideNshowToolbar(true);
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(FaqFragment.this)
                .commit();
    }

    @Override
    public FaqFragmentViewModel getViewModel() {
        return oneViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_faq;
    }

    /** populates {@link FAQModel} from API **/
    @Override
    public void loadFaQItems(List<FAQModel> faqModelList) {
        if (faqModelList != null) {
            adapter = new FAQAdapter(faqModelList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getAttachedContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.recyclerFaqItem.setLayoutManager(layoutManager);
            binding.recyclerFaqItem.setAdapter(adapter);
        }
    }

    /** returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /** logs out the user **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getBaseActivity().HideNshowToolbar(false);
    }

}
