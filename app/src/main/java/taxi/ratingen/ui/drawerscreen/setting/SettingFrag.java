package taxi.ratingen.ui.drawerscreen.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import taxi.ratingen.R;
import taxi.ratingen.databinding.FragmentSettingBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.getstarted.LanguageAdapter;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.List;

import javax.inject.Inject;



public class SettingFrag extends BaseFragment<FragmentSettingBinding, SettingFragViewModel> implements SettingNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "SettingFrag";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Inject
    SettingFragViewModel settingFragViewModel;

    FragmentSettingBinding fragmentSettingBinding;

    public SettingFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFrag newInstance(String param1, String param2) {
        SettingFrag fragment = new SettingFrag();
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
        fragmentSettingBinding = getViewDataBinding();
        settingFragViewModel.setNavigator(this);
        settingFragViewModel.setValues();
        getActivity().setTitle(getAtachedContext().getTranslatedString(R.string.txt_Setting));


    }

    @Override
    public SettingFragViewModel getViewModel() {
        return settingFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    Dialog dialog;

    /** Set available languages to choose one from it
     * @param items Language list **/
    @Override
    public void langguageItems(List<String> items) {
        dialog = new Dialog(getAtachedContext());
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lang_dialog);

        RecyclerView recyclerView = dialog.findViewById(R.id.lang_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getAtachedContext(), LinearLayoutManager.VERTICAL, false));
        LanguageAdapter languageAdapter = new LanguageAdapter(items,this);
        recyclerView.setAdapter(languageAdapter);
        dialog.show();
    }

    /** Called when a language is selected from the available language list
     * @param s Selected language **/
    @Override
    public void setSelectedLanguage(String s) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        settingFragViewModel.txt_Language_update.set(s);
        String loc;
        if (s.contains("pt")) {
            loc = "pt";
        } else if (s.contains("ar")) {
            loc = "ar";
        } else if (s.equalsIgnoreCase("zh")) {
            loc = "zh";
        } else if (s.contains("fr")) {
            loc = "fr";
        } else if (s.contains("es")) {
            loc = "es";
        } else if (s.contains("ja")) {
            loc = "ja";
        } else if (s.contains("ko")) {
            loc = "ko";
        } else {
            loc = "en";
        }
        settingFragViewModel.sharedPrefence.savevalue(SharedPrefence.LANGUANGE, loc);
        refreshScreen();
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getAtachedContext() {
        return getBaseActivity();
    }

    /** Restarts {@link DrawerAct} after selecting new language **/
    @Override
    public void refreshScreen() {
        if (getActivity() != null)
            getActivity().finish();
        Intent intent = new Intent(getActivity(), DrawerAct.class);
        getActivity().startActivity(intent);
    }

}
