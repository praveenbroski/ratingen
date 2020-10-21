package taxi.ratingen.ui.drawerscreen.ridescreen.etabasefare;


import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;

import androidx.fragment.app.Fragment;


import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.FragmentEtachildBaseFareBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.etaparent.ETAParent;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class EtachildBaseFare extends BaseFragment<FragmentEtachildBaseFareBinding, EtachildBaseViewModel> implements EtachildBaseNavigator {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ETAParent eTAParent;
    private BaseResponse mParam1;
    private String mParam2;
    public static final String TAG = "EtachildBaseFare";

    public EtachildBaseFare() {
        // Required empty public constructor
    }

    FragmentEtachildBaseFareBinding fragmentEtachildBaseFareBinding;

    @Inject
    EtachildBaseViewModel etachildBaseViewModel;

    /** Factory method to create {@link EtachildBaseFare}
     * @param param1 Parameter 1
     * @param param2 Parameter 2 **/
    public static EtachildBaseFare newInstance(BaseResponse param1, String param2) {
        EtachildBaseFare fragment = new EtachildBaseFare();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (BaseResponse) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentEtachildBaseFareBinding = getViewDataBinding();
        etachildBaseViewModel.setNavigator(this);

        etachildBaseViewModel.setValues(mParam1,getActivity());
    }

    @Override
    public EtachildBaseViewModel getViewModel() {
        return etachildBaseViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_etachild_base_fare;
    }

    /** Called to dismiss dialog **/
    @Override
    public void dismissDialog() {
        sendResult(ETAParent.ETAParent_REQUEST_CODE, "dismiss");
    }
    /** Shows base fare info when info button clicked **/
    @Override
    public void FareonClicked() {
        sendResult(ETAParent.ETAParent_REQUEST_CODE, "iBaseInfoclicked");
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getBaseAct() {
        return getBaseActivity();
    }

    /** Sends result to previous screen
     * @param mes String data that has to be sent **/
    private void sendResult(int REQUEST_CODE, String mes) {
       /* Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_Data, mes);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);*/

       if(getActivity().getSupportFragmentManager().
               findFragmentByTag(RideConfirmationFragment.TAG) !=null)
        if (getActivity().getSupportFragmentManager().
                findFragmentByTag(RideConfirmationFragment.TAG).getChildFragmentManager().findFragmentByTag(ETAParent.TAG) != null) {
            eTAParent = (ETAParent) getActivity().getSupportFragmentManager().
                    findFragmentByTag(RideConfirmationFragment.TAG).getChildFragmentManager().findFragmentByTag(ETAParent.TAG);
            eTAParent.Communicator(mes);
        }
    }
}
