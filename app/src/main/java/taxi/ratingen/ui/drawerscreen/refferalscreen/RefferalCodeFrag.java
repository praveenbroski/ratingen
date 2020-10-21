package taxi.ratingen.ui.drawerscreen.refferalscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentRefferalCodeBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;

import javax.inject.Inject;


public class RefferalCodeFrag extends BaseFragment<FragmentRefferalCodeBinding, RefferalFragViewModel> implements RefferalNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "RefferalCodeFrag";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentRefferalCodeBinding fragmentRefferalCodeBinding;

    @Inject
    RefferalFragViewModel refferalFragViewModel;

    public RefferalCodeFrag() {
        // Required empty public constructor
    }

    /** Call this factory method to create {@link RefferalCodeFrag}
     * @param param1 Parameter 1
     * @param param2 Parameter 2 **/
    public static RefferalCodeFrag newInstance(String param1, String param2) {
        RefferalCodeFrag fragment = new RefferalCodeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentRefferalCodeBinding = getViewDataBinding();
        refferalFragViewModel.setNavigator(this);
        refferalFragViewModel.GetRefferalDetails();
        getActivity().setTitle(getAttachedContext().getTranslatedString(R.string.text_title_Referral));
    }

    @Override
    public RefferalFragViewModel getViewModel() {
        return refferalFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_refferal_code;
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

    /** Open Share {@link Intent} to share promo code **/
    @Override
    public void OpenShareRefferal(String code) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getAttachedContext().getTranslatedString(R.string.text_play_url) + getActivity().getPackageName() +
                         "\n " + getAttachedContext().getTranslatedString(R.string.text_sharequote)
                        + getAttachedContext().getTranslatedString(R.string.text_note_referral)
                        + code);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getAttachedContext().getTranslatedString(R.string.text_share_ref)));
    }

    /** Logs out current user **/
    @Override
    public void logout() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

}
