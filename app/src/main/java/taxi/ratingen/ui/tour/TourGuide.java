package taxi.ratingen.ui.tour;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.TourGuideBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.PageTransformer;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.ViewPagerAdapter;

import javax.inject.Inject;

//import org.jsoup.Jsoup;


/**
 * Created by naveen on 8/24/17.
 */

public class TourGuide extends BaseActivity<TourGuideBinding, TourGuideViewModel> implements TourGuideNavigator {
    @Inject
    TourGuideViewModel mViewmodel;

    @Inject
    SharedPrefence sharedPrefence;
    TourGuideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        mViewmodel.setNavigator(this);

        binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setPageTransformer(true, new PageTransformer());
        binding.tabLay.setupWithViewPager(binding.viewPager, true);


        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("pageNo==", "pageNO" + position);

                if (position == 3) {
                    mViewmodel.skipDisable.set(true);
                    mViewmodel.forwardtxt.set("Finish");
                } else {
                    mViewmodel.skipDisable.set(false);
                    mViewmodel.forwardtxt.set(mViewmodel.translationModell.txt_next);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public TourGuideViewModel getViewModel() {
        return mViewmodel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.tour_guide;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public BaseActivity getAttachedContext() {
        return this;
    }

    @Override
    public void forwardClick() {
        if (binding.viewPager.getCurrentItem() == 3)
            movetoSignup();
        binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);

    }

    @Override
    public void onClickSkip() {
        movetoSignup();
    }


    private void movetoSignup() {
        startActivity(new Intent(this, SignupActivity.class));
//        startActivity(new Intent(this, DrawerAct.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}