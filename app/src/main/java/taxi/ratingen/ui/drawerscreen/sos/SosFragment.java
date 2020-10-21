package taxi.ratingen.ui.drawerscreen.sos;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.databinding.FragmentSosBinding;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.sos.adapter.SosAdapter;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;


public class SosFragment extends BaseFragment<FragmentSosBinding, SosFragViewModel> implements SosFragmentNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SosFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentSosBinding fragmentSosBinding;
    @Inject
    @Named("Sos")
    LinearLayoutManager mLayoutManager;
    @Inject
    Gson gson;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    SosFragViewModel emptyViewModel;

    @Inject
    SosAdapter adapter;

    public SosFragment() {
        // Required empty public constructor
    }
    /** Use this factory method to create {@link SosFragment}
     * @param param1 String parameter 1
     * @param param2 String parameter 2 **/
    public static SosFragment newInstance(String param1, String param2) {
        SosFragment fragment = new SosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentSosBinding = getViewDataBinding();
        emptyViewModel.setNavigator(this);
        getActivity().setTitle(getAttachedContext().getTranslatedString(R.string.txt_sos));

        Setup();
    }

    /** Set {@link SosAdapter} to recyclerSos.
     * Call API to get sos from server **/
    private void Setup() {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentSosBinding.recyclerSos.setLayoutManager(mLayoutManager);
        fragmentSosBinding.recyclerSos.setItemAnimator(new DefaultItemAnimator());
        fragmentSosBinding.recyclerSos.setAdapter(adapter);
        emptyViewModel.getZoneSos();

        fragmentSosBinding.toolbar.setNavigationOnClickListener(v -> {
            goBack();
        });

        getBaseActivity().HideNshowToolbar(true);
    }

    public void goBack() {
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .remove(SosFragment.this)
                .commit();
    }

    @Override
    public SosFragViewModel getViewModel() {
        return emptyViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sos;
    }

    /** Call this method to populate {@link So} data model {@link List} on {@link SosAdapter}
     * @param listso {@link List} with {@link So} data models **/
    @Override
    public void setSosList(List<So> listso) {
        if(listso!=null){
            adapter.addList(listso);
        }
    }

    /** Returns reference of {@link DrawerAct} **/
    @Override
    public DrawerAct getAttachedContext() {
        return (DrawerAct) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getBaseActivity().HideNshowToolbar(false);
    }

}