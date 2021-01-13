package taxi.ratingen.ui.drawerscreen.ridescreen.etaparent;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.DialogEtaparentBinding;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.drawerscreen.ridescreen.etabasefare.EtachildBaseFare;
import taxi.ratingen.ui.drawerscreen.ridescreen.etafarechild.EtachildFareFragment;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.EmptyViewModel;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 12/18/17.
 */

public class ETAParent extends BaseDialog implements ETAParentNavigator {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static int ETAParent_REQUEST_CODE=100001;
    public static String TAG="ETAParent";
    private BaseResponse mParam1;
    private Type mParam2;
    private Route mParam3;

    @Inject
    EmptyViewModel etaParentviewModel;

    /** Factory method to create new {@link ETAParent} fragment **/
    public static ETAParent newInstance(BaseResponse baseResponse) {
        ETAParent fragment = new ETAParent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1,baseResponse);
        bundle.putInt(ARG_PARAM2, 0);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ETAParent newInstance(Type type, Route route) {
        ETAParent fragment = new ETAParent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1, type);
        bundle.putSerializable(ARG_PARAM3, route);
        bundle.putInt(ARG_PARAM2, 1);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getInt(ARG_PARAM2) == 0)
                mParam1 = (BaseResponse)getArguments().getSerializable(ARG_PARAM1);
            else if (getArguments().getInt(ARG_PARAM2) == 1) {
                mParam2 = (Type) getArguments().getSerializable(ARG_PARAM1);
                mParam3 = (Route) getArguments().getSerializable(ARG_PARAM3);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogEtaparentBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_etaparent, container, false);
        View view = binding.getRoot();

        AndroidSupportInjection.inject(this);

        binding.setViewModel(etaParentviewModel);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mParam1 != null) {
            EtachildFareFragment etachildFareFragment = EtachildFareFragment.newInstance(mParam1, "");
//        etachildFareFragment.setTargetFragment(ETAParent.this,ETAParent.ETAParent_REQUEST_CODE);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.disallowAddToBackStack().replace(R.id.fragment_content,etachildFareFragment,EtachildFareFragment.TAG).commit();
        } else if (mParam2 != null) {
            EtachildFareFragment etachildFareFragment = EtachildFareFragment.newInstance(mParam2, mParam3, "");
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.disallowAddToBackStack().replace(R.id.fragment_content,etachildFareFragment,EtachildFareFragment.TAG).commit();
        }
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    /** Dismiss {@link ETAParent} dialog **/
    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

    /** Callback to get results from previous screens
     * @param requestCode Code of the request
     * @param resultCode Result code to identify if result is OK or not
     * @param data {@link Intent} containing data from previous screen **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ETAParent_REQUEST_CODE) {
            if(data.hasExtra(Constants.EXTRA_Data)){
                switch (data.getStringExtra(Constants.EXTRA_Data)){

                    case "dismiss":
                        dismissDialog(TAG);

                        break;

                    case "iFareInfoClicked":
                        EtachildBaseFare etachildBaseFare=EtachildBaseFare.newInstance(mParam1, "");
//                        etachildBaseFare.setTargetFragment(ETAParent.this,ETAParent.ETAParent_REQUEST_CODE);


                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                      /*  transaction.setCustomAnimations(
                                R.animator.card_flip_right_in,
                                R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in,
                                R.animator.card_flip_left_out);*/
                        transaction.disallowAddToBackStack();
                        transaction.replace(R.id.fragment_content,etachildBaseFare,EtachildBaseFare.TAG).commit();

                        break;

                    case "iBaseInfoclicked":

                        EtachildFareFragment etachildFareFragment=EtachildFareFragment.newInstance(mParam1, "");
//                        etachildFareFragment.setTargetFragment(ETAParent.this,ETAParent.ETAParent_REQUEST_CODE);


                        FragmentTransaction transactifon = getChildFragmentManager().beginTransaction();
                        transactifon.disallowAddToBackStack().disallowAddToBackStack().replace(R.id.fragment_content,etachildFareFragment,EtachildFareFragment.TAG).commit();


                        break;


                }

            }

        }

    }

    /** Function to dismiss/show {@link EtachildBaseFare}, {@link EtachildFareFragment} **/
    public void Communicator(String message) {
        if (message.equalsIgnoreCase("dismiss")) {
            dismissDialog(TAG);
        } else if (message.equalsIgnoreCase("iFareInfoClicked")) {
            EtachildBaseFare etachildBaseFare = EtachildBaseFare.newInstance(mParam1, "");
//                        etachildBaseFare.setTargetFragment(getRootParentFragment(this), ETAParent.ETAParent_REQUEST_CODE);


            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                      /*  transaction.setCustomAnimations(
                                R.animator.card_flip_right_in,
                                R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in,
                                R.animator.card_flip_left_out);*/
            transaction.disallowAddToBackStack();
            transaction.replace(R.id.fragment_content, etachildBaseFare, EtachildBaseFare.TAG).commit();

        } else if (message.equalsIgnoreCase("iBaseInfoclicked")) {
            EtachildFareFragment etachildFareFragment = EtachildFareFragment.newInstance(mParam1, "");
//                        etachildFareFragment.setTargetFragment(getRootParentFragment(this), ETAParent.ETAParent_REQUEST_CODE);


            FragmentTransaction transactifon = getChildFragmentManager().beginTransaction();
            transactifon.disallowAddToBackStack().disallowAddToBackStack().replace(R.id.fragment_content, etachildFareFragment, EtachildFareFragment.TAG).commit();

        }
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
