package taxi.ratingen.ui.drawerscreen.billdialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentBillDialogBinding;
import taxi.ratingen.retro.responsemodel.Bill;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.adapter.AddChargeBillAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class BillDialogFragment extends BaseDialog implements BillDialogNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "BillDialogFragment";

    // TODO: Rename and change types of parameters
    private Request mParam1;
    private String mParam2;

    AddChargeBillAdapter adapter;
    LinearLayoutManager mLayoutManager;
    @Inject
    BillDialogViewModel billDialogViewModel;
    FragmentBillDialogBinding binding;
    public BillDialogFragment() {
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
    public static BillDialogFragment newInstance(Request param1, String param2) {
        BillDialogFragment fragment = new BillDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Request) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_bill_dialog, container, false);
        View view = binding.getRoot();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width, height);
        AndroidSupportInjection.inject(this);
        billDialogViewModel.setNavigator(this);
        binding.setViewModel(billDialogViewModel);

        return view;

    }

    /** sets {@link AddChargeBillAdapter} if additional charges are available **/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billDialogViewModel.setBillDetails(mParam1);
        binding.setViewModel(billDialogViewModel);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        if(mParam1!=null&&mParam1.bill!=null&&mParam1.bill.additionalCharge!=null) {
            binding.recyclerAddCharges.setLayoutManager(mLayoutManager);
            adapter = new AddChargeBillAdapter(mParam1.bill.currency,(ArrayList<Bill.AdditionalCharge>) mParam1.bill.additionalCharge, (DrawerAct) getActivity());
            Log.d("keys","size--"+mParam1.bill.additionalCharge.size());
            binding.recyclerAddCharges.setAdapter(adapter);
            billDialogViewModel.isAddnlChargeAvailable.set(mParam1.bill.additionalCharge.size()>0);
        }else{
            billDialogViewModel.isAddnlChargeAvailable.set(false);
        }
    }

    /** shows {@link BillDialogFragment} **/
    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    /** dismisses the {@link BillDialogFragment} **/
    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

    /** gets reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

}
