package taxi.ratingen.ui.companyvalidation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.nplus.countrylist.CountryCodeChangeListener;
import com.nplus.countrylist.CountryUtil;
import taxi.ratingen.R;
import taxi.ratingen.databinding.DialogCompanyKeyBinding;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseDialog;
import taxi.ratingen.ui.companyvalidation.CompanyValidationActivity;
import taxi.ratingen.ui.signup.country_code.CountryListDialog;
import taxi.ratingen.utilz.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by root on 12/28/17.
 */

public class DialogCompanyValidation extends BaseDialog
        implements DialogComanyNavigator, CountryCodeChangeListener {
    public static final String TAG = "DialogCompanyValidation";
    DialogCompanyKeyBinding binding;
    @Inject
    DialogCompanyViewModel viewModel;
    private CountryUtil mCountryUtil;
    String CountryCode, countryShort;
    TextView empty_key;
    Button okbutton, alert_btn;
    Context context;

    public static DialogCompanyValidation newInstance(String key) {
        DialogCompanyValidation fragment = new DialogCompanyValidation();
        Bundle args = new Bundle();
//        args.putString(Constants.IntentExtras.COMPANY_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_company_key, container, false);
        View view = binding.getRoot();
        AndroidSupportInjection.inject(this);
        viewModel.setNavigator(this);
        binding.setViewModel(viewModel);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getCurrentCountry();
        setCancelable(false);
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

        mCountryUtil = new CountryUtil(getActivity(), Constants.COUNTRY_CODE);
        mCountryUtil.initUI(binding.editCountryCodeSignup, this, binding.imgFlagSignup);
        mCountryUtil.initCodes(getActivity());
    }

    /** initializes country code picker  **/
    @Override
    public void setCurrentCountryCode(String countryCode) {
        Constants.COUNTRY_CODE = countryCode;
        mCountryUtil = new CountryUtil(getAttachedContext(), countryCode);
        mCountryUtil.initUI(binding.editCountryCodeSignup, binding.imgFlagSignup);
        mCountryUtil.initCodes(getAttachedContext());
    }

    /** shows company key validity remaining days **/
    @Override
    public void continueUsage(String expirysoon) {
        showmessage(expirysoon);
    }

    /** shows alert dialog with given string as msg **/
    @Override
    public void showmessage(String expirysoon) {
        final Dialog dialog = new Dialog(getAttachedContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.company_key_alert_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.empty_key);
        textView.setText(expirysoon);
        TextView button = dialog.findViewById(R.id.alert_btn);

        button.setOnClickListener(v -> {
            openDrawerActivity();
            dialog.cancel();
            DialogCompanyValidation.this.dismiss();

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /** snippet to open drawer activity **/
    @Override
    public void openDrawerActivity() {
        if (getActivity() != null && getActivity() instanceof CompanyValidationActivity)
            ((CompanyValidationActivity) getActivity()).openDrawerActivity();
    }

    @Override
    public void openCountryDialog(ArrayList<CountryListModel> countryListModels) {
        CountryListDialog newInstance = CountryListDialog.newInstance(countryListModels, 2);
        newInstance.setTargetFragment(this, Constants.CC_SELECTED_CODE);
        newInstance.show(getBaseActivity().getSupportFragmentManager());
    }

    /** gets current context **/
    @Override
    public BaseActivity getAttachedContext() {
        return getBaseActivity();
    }

   /* *//** triggered when country code was chosen in country code picker **//*
    @Override
    public void onCountryCodeChanged(String countryCode, String countryShort,String countryLong) {
        CountryCode = countryCode;
        this.countryShort = countryShort;
    }*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CC_SELECTED_CODE) {
            String code = data.getStringExtra(Constants.CountryCode);
            viewModel.countrycode.set(code);
        }
    }

    @Override
    public void onCountryCodeChanged(String countryCode, String countryShort, String countryLong) {
        viewModel.countrycode.set(countryCode);
    }

}
