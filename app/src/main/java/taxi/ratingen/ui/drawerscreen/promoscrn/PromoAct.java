package taxi.ratingen.ui.drawerscreen.promoscrn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityPromoBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

public class PromoAct extends BaseActivity<ActivityPromoBinding, PromoViewModel> implements PromoNavigator {

    ActivityPromoBinding activityPromoBinding;

    @Inject
    PromoViewModel promoViewModel;
    @Inject
    SharedPrefence sharedPrefence;

    String isRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPromoBinding = getViewDataBinding();
        promoViewModel.setNavigator(this);
        if (getIntent().getStringExtra("isRide").equalsIgnoreCase("0"))
            promoViewModel.requestid.set(getIntent().getExtras().getString(Constants.EXTRA_Datastrn));
        else {
            promoViewModel.typeId = getIntent().getIntExtra("typeId", 0);
        }

        promoViewModel.isRide.set(getIntent().getStringExtra("isRide"));

        isRide = getIntent().getStringExtra("isRide");

        Setup();

    }

    /** {@link androidx.appcompat.widget.Toolbar} setup **/
    private void Setup() {
        setSupportActionBar(activityPromoBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        activityPromoBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityPromoBinding.toolbar.setTitle(getTranslatedString(R.string.Txt_title_Promocode));
    }

    @Override
    public PromoViewModel getViewModel() {
        return promoViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_promo;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Set promo code result to Intent Result **/
    @Override
    public void setResult() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_Data, "Applied");
        setResult(Constants.PROMOSETRESULT, intent);
        finish();
    }

    /** Set promo code result to Intent Result with Booking ID **/
    @Override
    public void setResult(String bookedId) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_Data, "Applied");
        intent.putExtra("BookedID", bookedId);
        setResult(Constants.RIDE_PROMO_RESULT, intent);
        finish();
    }

    /** Gives reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

}
