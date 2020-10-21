package taxi.ratingen.ui.drawerscreen.ridescreen.eta;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.DialogEtaBinding;
import taxi.ratingen.ui.base.BaseDialog;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 12/6/17.
 */

public class Etadialog extends BaseDialog implements EtaNavigator{

    private String TAG="ETAdialog";
    private static final String ARG_PARAM1 = "param1";
    private BaseResponse mParam1;

    @Inject
    EtaViewModel etaViewModel;

    /** Call this method to create {@link Etadialog} **/
    public static Etadialog newInstance(BaseResponse baseResponse) {
        Etadialog fragment = new Etadialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PARAM1,baseResponse);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (BaseResponse)getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogEtaBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_eta, container, false);
        View view = binding.getRoot();

        AndroidSupportInjection.inject(this);
        //AndroidInjection.inject(this);
        binding.setViewModel(etaViewModel);
        etaViewModel.setNavigator(this);
        etaViewModel.setValues(mParam1);
        return view;

    }

    /** Shows {@link Etadialog} fragment when this method is called **/
    public void show(FragmentManager fragmentManager) {
            super.show(fragmentManager, TAG);
        }

        /** Dismisses {@link Etadialog} fragment when this method is called **/
    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

}
