package taxi.ratingen.ui.drawerscreen.complaint;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.databinding.FragmentComplaiintBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;


public class ComplaiintFrag extends BaseFragment<FragmentComplaiintBinding,ComplaintViewModel> implements ComplaintNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "ComplaiintFrag";

    FragmentComplaiintBinding fragmentComplaiintBinding;

    public ComplaiintFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplaiintFrag.
     */
    // TODO: Rename and change types and number of parameters

    @Inject
    ComplaintViewModel complaintViewModel;

    @Inject
    ArrayAdapter adapter;

    BaseActivity context;


    public static ComplaiintFrag newInstance(String param1, String param2) {
        ComplaiintFrag fragment = new ComplaiintFrag();
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
        context = GetContext();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        complaintViewModel.setNavigator(this);
        fragmentComplaiintBinding=getViewDataBinding();
        getActivity().setTitle(context.getTranslatedString(R.string.text_complaint));

        Setup();

        getBaseActivity().HideNshowToolbar(true);
    }

    /** sets complaints adapter **/
    private void Setup() {
        fragmentComplaiintBinding.spinTitileComplaints.setAdapter(adapter);
        complaintViewModel.getComplaintList();
        getActivity().setTitle(context.getTranslatedString(R.string.text_complaint));

        fragmentComplaiintBinding.toolbar.setNavigationOnClickListener(v -> {
            goBack();
        });

        adapter.addAll();
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(ComplaiintFrag.this)
                .commit();
    }

    @Override
    public ComplaintViewModel getViewModel() {
        return complaintViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_complaiint;
    }

    /** populates {@link ComplaintList} **/
    @Override
    public void addList(List<ComplaintList> complaintLists) {
        adapter.addAll(complaintLists);
        adapter.notifyDataSetChanged();
    }

    /** returns the reference of {@link BaseActivity} **/
    @Override
    public BaseActivity GetContext() {
        return getBaseActivity();
    }

    /** enable/disable {@link ComplaintList} spinner based on given status **/
    @Override
    public void DisableSpinner(Boolean status) {
        if (status) {
            fragmentComplaiintBinding.spinTitileComplaints.setEnabled(true);
            fragmentComplaiintBinding.btnSendComplaint.setEnabled(true);
        } else {
            fragmentComplaiintBinding.spinTitileComplaints.setEnabled(false);
            fragmentComplaiintBinding.btnSendComplaint.setEnabled(false);
        }
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
