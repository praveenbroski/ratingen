package taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taxi.ratingen.R;
import taxi.ratingen.databinding.DialogWaitProgressBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseDialog;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 12/21/17.
 */

public class WaitProgressDialog extends BaseDialog implements WaitProgressNavigator {

    private static final String ARG_PARAM1 = "param1";
    public static final String TAG = "WaitProgressDialog";
    private String mParam1;
    @Inject
    WaitingProgressViewModel waitingProgressViewModel;

    /**
     * Factory method to show {@link WaitProgressDialog}
     *
     * @param requestid ID of the current request
     **/
    public static WaitProgressDialog newInstance(String requestid) {
        WaitProgressDialog fragment = new WaitProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, requestid);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.DismissDialog));*/

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogWaitProgressBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_wait_progress, container, false);
        View view = binding.getRoot();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AndroidSupportInjection.inject(this);

        binding.setViewModel(waitingProgressViewModel);
        setCancelable(false);
        waitingProgressViewModel.setNavigator(this);

        binding.slideMenu.setOnSlideCompleteListener(slideToActView -> waitingProgressViewModel.cancelTrip());

        return view;


    }

    /**
     * Displays {@link WaitProgressDialog} fragment
     **/
    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    /**
     * Dismissed {@link WaitProgressDialog} fragment
     **/
    @Override
    public void dismissDialog() {
        setCancelable(true);
        ((BaseActivity) getActivity()).removeWaitProgressDialog();
//        startActivity(new Intent(getActivity(), DrawerAct.class));
    }

    /**
     * Returns current request ID
     **/
    @Override
    public String getRequestid() {
        return mParam1;
    }

    /**
     * Returns reference for {@link Activity}
     **/
    @Override
    public BaseActivity getBaseAct() {
        return getBaseActivity();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          /*  if(waitProgressDialog!=null){
                waitProgressDialog=null;
                waitProgressDialog.dismissDialog();
            }*/
        }
    };

}
