/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package taxi.ratingen.retro.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import taxi.ratingen.R;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;
import taxi.ratingen.utilz.exception.CustomException;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by amitshekhar on 10/07/17.
 */

public abstract class BaseBindingDialog<T extends ViewDataBinding, V> extends DialogFragment implements BaseView {

    private BaseActivity mActivity;
    private V mViewModel;
    private T mBinding;
    private View mRootView;
    private Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getBaseActivity().getLayoutInflater(), getLayoutId(), container, false);
        mRootView = mBinding.getRoot();
        return mRootView;
    }

    public T getmBinding() {
        return mBinding;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity mActivity = (BaseActivity) context;
            this.mActivity = mActivity;
            mActivity.onFragmentAttached();
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        performDependencyInjection();
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.getWindow().setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
//        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public void setCanceledOnTouchOutside(boolean canceled) {
        getDialog().setCanceledOnTouchOutside(canceled);
        getDialog().setCancelable(canceled);
    }

    //Show the Dialog as fullscreen which extends the BaseDialog
    public void setDialogFullSCreen() {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        getDialog().setCanceledOnTouchOutside(false);
    }

    //Inject or initiate the Injections
    private void performDependencyInjection() {
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = getViewModel();
        mBinding.setVariable(getBindingVariable(), mViewModel);
        mBinding.executePendingBindings();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    //Chceck the internet is connected or not which extends BaseDialog
    public boolean isNetworkConnected() {
        boolean result = mActivity.isNetworkConnected();
        if (!result)
            showMessage(mActivity.getTranslatedString(R.string.txt_NoInternet));
        return mActivity != null && mActivity.isNetworkConnected();
    }

    //Hide keyboard
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    //Show the Dialog method by calling this method
    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
//        transaction.commitAllowingStateLoss();
        transaction.addToBackStack(null);
        try {
            show(transaction, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean isSavedInstanceState;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    //Dismiss the dialog based on corressponding dialog TAG
    public void dismissDialog(String tag) {
        dismiss();
        getBaseActivity().onFragmentDetached(tag);
    }

    //Displaying the messages based on ID
    public void showMessage(int resId) {
        if (mActivity != null)
            Toast.makeText(mActivity, mActivity.getTranslatedString(resId), Toast.LENGTH_SHORT).show();
    }

    //Display the Exception messages
    public void showMessage(CustomException e) {
        if (mActivity != null)
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    //Display the toast based on messages
    public void showMessage(String message) {
        if (mActivity != null)
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

     // Displaying the message through snackbar
    public void showSnackBar(String message) {
        if (mBinding == null) {
            mBinding = getDataBinding();
        }
        if (mBinding != null) {
            Snackbar snackbar = Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    public void showSnackBar(@NonNull View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    //Show the network message if internet not connceted
    public void showNetworkMessage() {
        if (mActivity != null)
            Toast.makeText(mActivity, mActivity.getTranslatedString(R.string.txt_NoInternet), Toast.LENGTH_SHORT).show();
    }

    //Show the alert if the Company key is empty
    public void showAlert(String errorMessage) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new Dialog(mActivity);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.company_key_empty_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView) mDialog.findViewById(R.id.empty_key)).setText(errorMessage);
        mDialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        mDialog.show();
    }

    public abstract V getViewModel();

    public abstract T getDataBinding();


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

}
