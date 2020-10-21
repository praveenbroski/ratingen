package taxi.ratingen.ui.drawerscreen.canceldialog;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.CanceldialogBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.drawerscreen.tripscreen.TripFragment;
import taxi.ratingen.utilz.Constants;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 1/29/18.
 */

public class cancelDialogFrag extends BaseDialog implements cancelDialogNavigator {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "cancelDialogFrag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RadioAdapter recyclerAdapter;

    @Inject
    CancelDialogViewModel cancelDialogViewModel;
    CanceldialogBinding binding;

    public cancelDialogFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static cancelDialogFrag newInstance(String param1, String param2) {
        cancelDialogFrag fragment = new cancelDialogFrag();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.canceldialog, container, false);
        View view = binding.getRoot();
        AndroidSupportInjection.inject(this);
        cancelDialogViewModel.setNavigator(this);
        binding.setViewModel(cancelDialogViewModel);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelDialogViewModel.setvalues(mParam1);
        binding.recyclerReason.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }

    /**
     * shows {@link cancelDialogFrag}
     **/
    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    /**
     * shows cancel reasons adapter {@link RadioAdapter}
     **/
    @Override
    public void setCancelList(List<BaseResponse.ReasonCancel> list) {
        // list.add(new BaseResponse.ReasonCancel("0", "Others"));
        recyclerAdapter = new RadioAdapter(getActivity(), list, this);
        if (list != null)
            binding.recyclerReason.setAdapter(recyclerAdapter);
    }

    /**
     * get selected reason's position
     **/
    @Override
    public String getSelectionPosition() {
        return recyclerAdapter != null ? recyclerAdapter.getSelectPosition() : "";
    }

    /**
     * dismisses {@link cancelDialogFrag}
     **/
    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

    /**
     * returns the reference for {@link BaseActivity}
     **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /**
     * dismisses cancel dialog with a target destination fragment
     **/
    @Override
    public void DismisswithTarget() {
        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "Cancelled"));
        else {
            if (getActivity() != null)
                if (getActivity().getSupportFragmentManager() != null && getActivity().getSupportFragmentManager().findFragmentByTag(TripFragment.TAG) != null) {
                    getActivity().getSupportFragmentManager().findFragmentByTag(TripFragment.TAG).onActivityResult(Constants.CANCELTRIPCALLBACK, Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "Cancelled"));
                }
        }
        dismiss();
    }

    /**
     * others option selected in reasons
     **/
    @Override
    public void selectedOthers(boolean isOthers) {
        if (cancelDialogViewModel != null)
            cancelDialogViewModel.otherCancelAvaialability.set(isOthers);
    }

}
