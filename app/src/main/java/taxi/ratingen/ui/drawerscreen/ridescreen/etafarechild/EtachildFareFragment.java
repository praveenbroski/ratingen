package taxi.ratingen.ui.drawerscreen.ridescreen.etafarechild;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.FragmentEtachildFareBinding;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.etaparent.ETAParent;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class EtachildFareFragment extends BaseFragment<FragmentEtachildFareBinding, EtachildFareViewModel> implements EtachildFareNavigator {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    ETAParent eTAParent;
    private BaseResponse mParam1;
    private TypeNew mParam2;
    private Route mParam3;
    public static final String TAG = "EtachildFareFragment";

    public EtachildFareFragment() {
        // Required empty public constructor
    }

    FragmentEtachildFareBinding fragmentEtachildFareBinding;

    @Inject
    EtachildFareViewModel etachildFareViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getSerializable(ARG_PARAM1) != null)
                mParam1 = (BaseResponse) getArguments().getSerializable(ARG_PARAM1);
            else if (getArguments().getSerializable(ARG_PARAM2) != null) {
                mParam2 = (TypeNew) getArguments().getSerializable(ARG_PARAM2);
                mParam3 = (Route) getArguments().getSerializable(ARG_PARAM3);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /** Factory method to create {@link EtachildFareFragment}
     * @param param1 Response model(Parameter 1)
     * @param param2 Parameter 2 **/
    public static EtachildFareFragment newInstance(BaseResponse param1, String param2) {
        EtachildFareFragment fragment = new EtachildFareFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static EtachildFareFragment newInstance(TypeNew param1, Route routeParam, String param2) {
        EtachildFareFragment fragment = new EtachildFareFragment();
        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param1);
        args.putSerializable(ARG_PARAM3, routeParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentEtachildFareBinding = getViewDataBinding();
        etachildFareViewModel.setNavigator(this);

        if (mParam1 != null)
            etachildFareViewModel.setValues(mParam1);
        else if (mParam2 != null)
            etachildFareViewModel.setTypeValues(mParam2, mParam3);
    }

    @Override
    public EtachildFareViewModel getViewModel() {
        return etachildFareViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_etachild_fare;
    }

    /** Dismisses {@link EtachildFareFragment} **/
    @Override
    public void dismissDialog() {
        sendResult(ETAParent.ETAParent_REQUEST_CODE, "dismiss");
    }

    /** Shows Fare info dialog **/
    @Override
    public void FareInfoClicked() {
        sendResult(ETAParent.ETAParent_REQUEST_CODE, "iFareInfoClicked");
    }

    /** Sends result to previous dialog
     * @param REQUEST_CODE Code to identify request
     * @param mes String message **/
    private void sendResult(int REQUEST_CODE, String mes) {
             if (getActivity().getSupportFragmentManager().
                findFragmentByTag(RideConfirmationFragment.TAG) != null)
            if (getActivity().getSupportFragmentManager().
                    findFragmentByTag(RideConfirmationFragment.TAG).getChildFragmentManager().findFragmentByTag(ETAParent.TAG) != null) {
                eTAParent = (ETAParent) getActivity().getSupportFragmentManager().
                        findFragmentByTag(RideConfirmationFragment.TAG).getChildFragmentManager().findFragmentByTag(ETAParent.TAG);
                eTAParent.Communicator(mes);
            }
    }

}
