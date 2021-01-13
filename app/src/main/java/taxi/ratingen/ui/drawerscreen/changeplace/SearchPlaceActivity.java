package taxi.ratingen.ui.drawerscreen.changeplace;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.databinding.ActivitySearchPlaceBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.favorites.FavoriteFragment;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class SearchPlaceActivity extends BaseActivity<ActivitySearchPlaceBinding, SearchPlaceViewModel> implements SearchPlaceNavigator, HasAndroidInjector {
    @Inject
    SearchPlaceViewModel placeApiViewModel;
    @Inject
    SharedPrefence sharedPrefence;
    SearchPlaceAdapter adapter;
    ActivitySearchPlaceBinding activityPlaceApiBinding;
    LinearLayoutManager mLayoutManager;
    LatLng latLng;
    String titleSearh;

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlaceApiBinding = getViewDataBinding();
        placeApiViewModel.setNavigator(this);
        if (getIntent() != null) {
            if (getIntent().getParcelableExtra(Constants.EXTRA_LAT_LNG) != null)
                latLng = getIntent().getParcelableExtra(Constants.EXTRA_LAT_LNG);
            if (getIntent().getStringExtra(Constants.EXTRA_SEARCH_TYPE) != null)
                titleSearh = getIntent().getStringExtra(Constants.EXTRA_SEARCH_TYPE);
            placeApiViewModel.title.set(titleSearh);
            if (getIntent().getStringExtra(Constants.EXTRA_IS_PICKUP) != null)
                placeApiViewModel.isPickup.set(getIntent().getStringExtra(Constants.EXTRA_IS_PICKUP).equals("1"));
            if (getIntent().getStringExtra(Constants.EXTRA_PICK_ADDRESS) != null)
                placeApiViewModel.pickAddress.set(getIntent().getStringExtra(Constants.EXTRA_PICK_ADDRESS));
            if (getIntent().getStringExtra(Constants.EXTRA_DROP_ADDRESS) != null)
                placeApiViewModel.dropAddress.set(getIntent().getStringExtra(Constants.EXTRA_DROP_ADDRESS));
        }
        Setup();
    }

    /** setting adapter to places API search result recycler **/
    private void Setup() {
        adapter = new SearchPlaceAdapter(new ArrayList<Favplace>(), this, placeApiViewModel.isPickup.get());
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activityPlaceApiBinding.ACPlaceRecyclerView.setLayoutManager(mLayoutManager);
        activityPlaceApiBinding.ACPlaceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityPlaceApiBinding.ACPlaceRecyclerView.setAdapter(adapter);

        placeApiViewModel.GetFavListData();

        activityPlaceApiBinding.navFavBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public SearchPlaceViewModel getViewModel() {
        return placeApiViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_place;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fav_container);
        if (currentFragment != null) {
            onFragmentDetached(currentFragment.getTag());
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentDetached(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        Objects.requireNonNull(this)
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(fragment)
                .commit();
    }

    /** populating {@link Favplace} list from API **/
    @Override
    public void addList(List<Favplace> favplace) {

        adapter.addList(favplace);

    }

    /** showing circular progress bar to indicate loading **/
    @Override
    public void showProgress(boolean show) {
        if (show)
            placeApiViewModel.setIsLoading(true);
        else
            placeApiViewModel.setIsLoading(false);
    }

    /** show/hide clear button on search box **/
    @Override
    public void showclearButton(boolean show) {
        if (show) {
            if (activityPlaceApiBinding.ACPlaceClear.getVisibility() == View.GONE)
                activityPlaceApiBinding.ACPlaceClear.setVisibility(View.VISIBLE);
        } else {
            if (activityPlaceApiBinding.ACPlaceClear.getVisibility() == View.VISIBLE)
                activityPlaceApiBinding.ACPlaceClear.setVisibility(View.GONE);
        }

    }

    /** terminates place search activity **/
    @Override
    public void FinishAct() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrawerAct.activityToBeRefre.set(false);
    }

    /** get start location {@link LatLng} **/
    @Override
    public LatLng getLatLngForStart() {
        return latLng;
    }

    /** called when clear button in search box is tapped **/
    @Override
    public void clearBtn() {
        placeApiViewModel.pickAddress.set("");
    }
    /** called when clear button in search box is tapped **/
    @Override
    public void clearDropBtn() {
        placeApiViewModel.dropAddress.set("");
    }

    @Override
    public void openFavorites() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .add(R.id.fav_container, FavoriteFragment.newInstance(0), FavoriteFragment.TAG)
                .commit();
    }

    @Override
    public void showPickClearButton(boolean show) {
        if (show) {
            if (activityPlaceApiBinding.ACPickClear.getVisibility() == View.GONE)
                activityPlaceApiBinding.ACPickClear.setVisibility(View.VISIBLE);
        } else {
            if (activityPlaceApiBinding.ACPickClear.getVisibility() == View.VISIBLE)
                activityPlaceApiBinding.ACPickClear.setVisibility(View.GONE);
        }
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}
