package taxi.ratingen.ui.drawerscreen.historydetails;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import androidx.databinding.library.baseAdapters.BR;

import dagger.android.HasAndroidInjector;
import taxi.ratingen.ui.drawerscreen.disputdialog.DisputDialogFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Bill;
import taxi.ratingen.databinding.ActivityHistoryDetailsScrnBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.adapter.AddChargeHistoryAdapter;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;

public class HistoryDetailsScrn extends BaseActivity<ActivityHistoryDetailsScrnBinding, HistoryDetViewModel> implements HistoryDetNavigator, HasAndroidInjector {
    @Inject
    HistoryDetViewModel historyDetViewModel;
    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;
    ActivityHistoryDetailsScrnBinding activityHistoryDetailsScrnBinding;
    AddChargeHistoryAdapter adapter;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyDetViewModel.setNavigator(this);
        activityHistoryDetailsScrnBinding = getViewDataBinding();
        initSetup();
    }

    @Override
    public HistoryDetViewModel getViewModel() {
        return historyDetViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history_details_scrn;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    private void initSetup() {
        setSupportActionBar(activityHistoryDetailsScrnBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        activityHistoryDetailsScrnBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            historyDetViewModel.getRequestDetails(getIntent().getExtras().getString(Constants.EXTRA_Datastrn));
        }


     /*   activityHistoryDetailsScrnBinding..toolbar.setTitle(getString(R.string.text_profile));*/


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(historyDetViewModel);

        /* automatic scroll up of history details **/
        final ScrollView sv = activityHistoryDetailsScrnBinding.historyScroll;
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofInt(sv, "scrollY", 0,
                        (int) getResources().getDimension(R.dimen._120sdp)).setDuration(5000);
                animator.start();
            }
        }, 200);
    }

    /** sets adapter for additional charges {@link androidx.recyclerview.widget.RecyclerView} **/
    public void setRecyclerAdapter(String currency,List<Bill.AdditionalCharge> list){
        Log.d("keys---","historyActivity-"+list.size());
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new AddChargeHistoryAdapter(currency,(ArrayList<Bill.AdditionalCharge>) list,this);
        activityHistoryDetailsScrnBinding.recyclerAddChargesHistory.setLayoutManager(mLayoutManager);
        activityHistoryDetailsScrnBinding.recyclerAddChargesHistory.setAdapter(adapter);
    }

    /** returns the reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    /** finishes {@link HistoryDetailsScrn} with result **/
    @Override
    public void setResultnFinish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_Data, "Done");
        setResult(Constants.HISTORYLISTSETRESULT, intent);
        finish();
    }

    /** gets icon for from to markers by given resource id **/
    @Override
    public BitmapDescriptor getMarkerIcon(int drawID) {
        return CommonUtils.getBitmapDescriptor(getBaseAct(), drawID);
    }

    /** opens dispute dialog when clicked **/
    @Override
    public void openDisputeDialog(String id,String token,String requestID){
        DisputDialogFragment fragment = DisputDialogFragment.newInstance(requestID);
        fragment.show(getSupportFragmentManager(), DisputDialogFragment.TAG);
    }

    /** show/hide dispute button **/
    public void showDisputeButton(boolean enableDisputeButton) {
        if(historyDetViewModel!=null)
            historyDetViewModel.isDisputeAvailable.set(enableDisputeButton);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }

}
