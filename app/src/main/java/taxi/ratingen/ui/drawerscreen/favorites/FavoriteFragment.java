package taxi.ratingen.ui.drawerscreen.favorites;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.braintreepayments.api.visacheckout.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentFavoriteBinding;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.AddFavFragment;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class FavoriteFragment extends BaseFragment<FragmentFavoriteBinding, FavoriteViewModel> implements FavoriteNavigator {

    public static final String TAG = "FavoriteFragment";

    @Inject
    FavoriteViewModel viewModel;
    FragmentFavoriteBinding binding;
    @Inject
    SharedPrefence sharedPrefence;
    FavoriteAdapter adapter;

    int flag = 0;

    public FavoriteFragment() {

    }

    public static FavoriteFragment newInstance(int i) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt("mParam1", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        setup();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flag = getArguments().getInt("mParam1");
        }
    }

    private void setup() {
        /* Setting dummy data */
        List<Favplace> favPlaceList = new ArrayList<>();
        adapter = new FavoriteAdapter(favPlaceList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getAttachedContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerFavPlaces.setLayoutManager(layoutManager);
        binding.recyclerFavPlaces.setAdapter(adapter);

        viewModel.getFavListData();

        try {
            getBaseActivity().HideNshowToolbar(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FavoriteViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_favorite;
    }

    /**
     * returns reference of {@link BaseActivity}
     **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /**
     * logs out the user
     **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

    @Override
    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(FavoriteFragment.this)
                .commit();
    }

    @Override
    public void addFavClicked() {
        if (flag == 0)
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.fav_container, AddFavFragment.newInstance(flag), AddFavFragment.TAG)
                    .commit();
        else
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, AddFavFragment.newInstance(flag), AddFavFragment.TAG)
                    .commit();
    }

    @Override
    public void addList(List<Favplace> favPlaces) {
        adapter.addList(favPlaces);
    }

    @Override
    public void deleteFavClicked(Favplace favplace) {
        viewModel.deleteFav(favplace);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getBaseActivity().HideNshowToolbar(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshFavList() {
        if (viewModel != null)
            viewModel.getFavListData();
    }
}
