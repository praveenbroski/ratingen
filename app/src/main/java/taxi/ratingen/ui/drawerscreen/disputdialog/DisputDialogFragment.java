package taxi.ratingen.ui.drawerscreen.disputdialog;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.databinding.DialogDisputeBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.drawerscreen.historydetails.HistoryDetailsScrn;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 12/20/17.
 */

public class DisputDialogFragment extends BaseDialog implements DisputeDialogeNavigator {

    DialogDisputeBinding binding;
    @Inject
    DisputeDialogViewModel viewModel;
    public static String param = "PARAM";
    public static String TAG = "DisputDialogFragment";


    public DisputDialogFragment() {
        // Required empty public constructor
    }

    public static DisputDialogFragment newInstance(String requestID) {
        Bundle args = new Bundle();
        args.putString(DisputDialogFragment.param, requestID);
        DisputDialogFragment fragment = new DisputDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_dispute, container, false);
        View view = binding.getRoot();
        AndroidSupportInjection.inject(this);
        viewModel.setNavigator(this);
        binding.setViewModel(viewModel);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getComplaintList(getArguments().getString(param));
        setDialogFullSCreen();
    }

    List<ComplaintList> complaintLists;

    /** populates dispute types list **/
    @Override
    public void addList(final List<ComplaintList> complaintLists) {
        this.complaintLists = complaintLists;
        if (binding != null) {
            ArrayAdapter<ComplaintList> adapter = new ArrayAdapter<ComplaintList>(getActivity(), android.R.layout.simple_list_item_1, complaintLists);
            binding.spinTitileComplaints.setAdapter(adapter);
            binding.spinTitileComplaints.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    for (ComplaintList complaintLi : complaintLists) {
                        if (complaintLi.title != null)
                            if (complaintLi.title.equalsIgnoreCase(((TextView) view).getText().toString())) {
                                viewModel.SelectedId = String.valueOf(complaintLi.id);
                                Log.d(TAG, "keysSelected-" + complaintLi.title);
                                break;
                            }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /** returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity GetContext() {
        return getBaseActivity();
    }

    /** returns the ID of selected reason from the spinner **/
    @Override
    public String getSelectedItemID() {
        String result = null;
        if (binding.spinTitileComplaints != null) {
            if (binding.spinTitileComplaints.getSelectedItemPosition() > -1 && complaintLists != null && complaintLists.size() > 0) {
                if (complaintLists.get(binding.spinTitileComplaints.getSelectedItemPosition()) != null) {
                    result = complaintLists.get(binding.spinTitileComplaints.getSelectedItemPosition()).id + "";
                }
            }
        }
        return result;
    }

    /** closes the dispute dialog **/
    @Override
    public void dismissDialog(boolean enableDisputeButton) {
        getDialog().dismiss();
        if (getActivity() != null)
            ((HistoryDetailsScrn) getActivity()).showDisputeButton(enableDisputeButton);
    }
}
