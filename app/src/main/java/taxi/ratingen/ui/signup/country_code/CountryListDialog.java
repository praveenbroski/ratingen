package taxi.ratingen.ui.signup.country_code;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import taxi.ratingen.R;
import taxi.ratingen.databinding.CountryListBinding;

import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.drawerscreen.profilescrn.edit.NameMailEdit;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CountryListDialog extends BaseDialog implements CountryListNavigator {

    private static final String TAG = "CountryListDialog";
    public List<CountryListModel> countryList;
    @Inject
    CountryListDialogViewModel countryListDialogViewModel;
    CountryListBinding countryListBinding;
    CountryListAdapter countryListAdapter;
    LinearLayoutManager linearLayoutManager;
    int mode;

    public static CountryListDialog newInstance(ArrayList<CountryListModel> countryList, int mode) {
        CountryListDialog countryListDialog = new CountryListDialog();
        Bundle args = new Bundle();
        if (countryList != null)
            args.putParcelableArrayList(Constants.IntentExtras.COUNTRY_LIST, countryList);
        args.putInt(Constants.EXTRA_MODE, mode);
        countryListDialog.setArguments(args);
        return countryListDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelableArrayList(Constants.IntentExtras.COUNTRY_LIST) != null) {
            countryList = getArguments().getParcelableArrayList(Constants.IntentExtras.COUNTRY_LIST);
        }

        mode = getArguments().getInt(Constants.EXTRA_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        countryListBinding = DataBindingUtil.inflate(
                inflater, R.layout.country_list, container, false);
        View view = countryListBinding.getRoot();
        AndroidSupportInjection.inject(this);
        countryListDialogViewModel.setNavigator(this);
        countryListBinding.setViewModel(countryListDialogViewModel);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDialogFullSCreen();

        if (countryList == null)
            countryListDialogViewModel.getCountryListApi();
        else
            countryResponse(countryList);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getDialog().getWindow().setStatusBarColor(getResources().getColor(R.color.clr_F2F2F2));
//        }

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);

        View decorView = getDialog().getWindow().getDecorView();
        int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
        systemUiVisibilityFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        // getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void countryResponse(List<CountryListModel> data) {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        countryListAdapter = new CountryListAdapter(getActivity(), data, this);
        countryListBinding.recylerview.setLayoutManager(linearLayoutManager);
        countryListBinding.recylerview.setAdapter(countryListAdapter);
        countryListBinding.searchEdit.addTextChangedListener(countryListAdapter);
    }

    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

    @Override
    public void clickedItem(String flag, String code, String name, String countryId, String iso2) {
//        Intent intent = new Intent(Constants.INTENT_ADD_TASK);
//        intent.putExtra(Constants.FLAG, flag);
//        intent.putExtra(Constants.CountryCode, code);
//        intent.putExtra(Constants.CountryID, countryId);
//        getActivity().sendBroadcast(intent);

        if (mode == 0) {
            ((SignupActivity) getActivity()).onCountrySelected(flag, code, name, countryId, iso2);
            dismiss();
        } else if (mode == 1) {
            ((NameMailEdit) getActivity()).onCountrySelected(flag, code, name, countryId, iso2);
            dismiss();
        }

    }

    @Override
    public void refreshCompanyKey() {

    }
}
