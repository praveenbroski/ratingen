package taxi.ratingen.ui.drawerscreen.favorites.addfav;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.braintreepayments.api.visacheckout.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentAddFavBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.favorites.FavoriteFragment;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap.PickFromMapFragment;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.Objects;

import javax.inject.Inject;

public class AddFavFragment extends BaseFragment<FragmentAddFavBinding, AddFavViewModel> implements AddFavNavigator {

    public static final String TAG = "AddFavFragment";

    @Inject
    AddFavViewModel viewModel;
    FragmentAddFavBinding binding;
    @Inject
    SharedPrefence sharedPrefence;

    int flag = 0;

    public AddFavFragment() {

    }

    public static AddFavFragment newInstance(int flag) {
        AddFavFragment fragment = new AddFavFragment();
        Bundle args = new Bundle();
        args.putInt("mParam1", flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flag = getArguments().getInt("mParam1");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

//        setup();
    }

    @Override
    public AddFavViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_fav;
    }

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
                .remove(AddFavFragment.this)
                .commit();
    }

    @Override
    public void goBackRefresh() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(AddFavFragment.this)
                .commit();
        if (getActivity().getSupportFragmentManager() != null
                && getActivity().getSupportFragmentManager().findFragmentByTag(FavoriteFragment.TAG) != null)
            ((FavoriteFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FavoriteFragment.TAG)).refreshFavList();
    }

    @Override
    public void openPickFromMapFragment() {
        if (flag == 0)
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.fav_container, PickFromMapFragment.newInstance(viewModel), PickFromMapFragment.TAG)
                    .commit();
        else
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, PickFromMapFragment.newInstance(viewModel), PickFromMapFragment.TAG)
                    .commit();
    }

}
