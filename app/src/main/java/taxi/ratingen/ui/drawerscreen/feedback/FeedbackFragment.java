package taxi.ratingen.ui.drawerscreen.feedback;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.databinding.FragmentFeedbackBinding;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.billdialog.BillDialogFragment;
import taxi.ratingen.utilz.Constants;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends BaseFragment<FragmentFeedbackBinding, FeedbackViewModel> implements FeedbackNavigator {
    public static final String TAG = "FeedbackFragment";
    @Inject
    FeedbackViewModel viewModel;
    FragmentFeedbackBinding layoutBinding;
    Request model;
    static boolean isCorporate;

    public static FeedbackFragment newInstance(Request param1, boolean isCorporate) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_Data, param1);
        args.putBoolean(Constants.EXTRA_IS_CORPORATE, isCorporate);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            model = (Request) getArguments().getSerializable(Constants.EXTRA_Data);
        isCorporate = getArguments().getBoolean(Constants.EXTRA_IS_CORPORATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutBinding = getViewDataBinding();
        viewModel.setNavigator(this);
        viewModel.setUserDetails(model);
        if (model.bill != null)
            if (!isCorporate && model.bill.show_bill == 1)
                BillDialogFragment.newInstance(model, "").show(this.getChildFragmentManager());


        Drawable drawable1 = layoutBinding.ratingUserFeedback.getProgressDrawable();
        drawable1.setColorFilter(Color.parseColor("#FFAA00"), PorterDuff.Mode.SRC_ATOP);

        Drawable drawable2 = layoutBinding.ratingBar.getProgressDrawable();
        drawable2.setColorFilter(Color.parseColor("#FFAA00"), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public FeedbackViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_feedback;
    }

    /**
     * returns the current {@link Context}
     **/
    @Override
    public Context getAttachedContext() {
        return getBaseActivity();
    }

    @Override
    public void ShowHomeFragment() {
        getBaseActivity().NeedHomeFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBaseActivity().changeTripnFeedback();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().HideNshowToolbar(true);
    }

}
