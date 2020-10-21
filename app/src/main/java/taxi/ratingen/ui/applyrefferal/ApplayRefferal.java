package taxi.ratingen.ui.applyrefferal;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityApplayRefferalBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

public class ApplayRefferal extends BaseActivity<ActivityApplayRefferalBinding,ApplyRefferalViewModel> implements ApplyRefferalNavigator{
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    ApplyRefferalViewModel refferalViewModel;
    ActivityApplayRefferalBinding activityApplayRefferalBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityApplayRefferalBinding=getViewDataBinding();
        refferalViewModel.setNavigator(this);

    }

    @Override
    public ApplyRefferalViewModel getViewModel() {
        return refferalViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_applay_refferal;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void OpenDrawerActivity() {
        startActivity(new Intent(this, DrawerAct.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }


    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onResume() {
        super.onResume();
        DrawerAct.activityToBeRefre.set(false);
    }
}
