package taxi.ratingen.ui.drawerscreen.support;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentSupportPageBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;

import javax.inject.Inject;


public class SupportFrag extends BaseFragment<FragmentSupportPageBinding, SupportFragViewModel> implements SupportFragNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SupportFrag";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentSupportPageBinding supportPageBinding;

    @Inject
    SupportFragViewModel supportFragViewModel;

    public SupportFrag() {
        // Required empty public constructor
    }

    /** Call this factory method to create new instance of {@link SupportFrag}
     * @param param1 String parameter 1
     * @param param2 String parameter 2 **/
    public static SupportFrag newInstance(String param1, String param2) {
        SupportFrag fragment = new SupportFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        supportPageBinding = getViewDataBinding();
        supportFragViewModel.setNavigator(this);
        getActivity().setTitle(getAttachedContext().getTranslatedString(R.string.txt_support));

//        supportFragViewModel.GetRefferalDetails();
    }


    @Override
    public SupportFragViewModel getViewModel() {
        return supportFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_support_page;
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /** Call this method to logout **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

}
