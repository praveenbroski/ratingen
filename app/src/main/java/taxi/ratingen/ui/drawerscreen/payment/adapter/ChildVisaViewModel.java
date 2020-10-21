package taxi.ratingen.ui.drawerscreen.payment.adapter;

import android.content.DialogInterface;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 11/3/17.
 */
@BindingMethods({
        @BindingMethod(type = android.widget.ImageView.class,
                attribute = "app:srcCompat",
                method = "setImageDrawable")})
public class ChildVisaViewModel extends BaseNetwork<Payment, ChildVisaViewModel.ChidItemViewModelListener> {

    ChidItemViewModelListener mListener;
    Payment payment;
    public ObservableField<String> lastNumber = new ObservableField<>();
    public ObservableBoolean isDefault = new ObservableBoolean();
    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;

    public ChildVisaViewModel(Payment pay, ChidItemViewModelListener listener, GitHubService gitHubService,
                              SharedPrefence sharedPrefence, HashMap<String, String> hashMap) {
        super(gitHubService);
        this.payment = pay;
        mListener = listener;
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
        isDefault.set(payment.isDefault);
        lastNumber.set(payment.lastNumber);
    }

    /** Click listener for visa item  **/
    public void onItemTickClick() {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.card_id, "" + payment.cardId);
        if (mListener.isNetworkConnected())
            changeDefaultCardNetwork();
        else
            mListener.showNetworkMessage();
    }

    /** Called when clicking delete button **/
    public void onItemTrashClick() {
        ShowTrashAlertDialog();
    }

    /** Callback for successful API call
     * @param taskId
     * @param response **/
    @Override
    public void onSuccessfulApi(long taskId, Payment response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("default_changed")) {
            if (response.getPayment() != null && response.getPayment().size() != 0)
                mListener.PopulateData(response.getPayment());
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Card_Deleted_Successfully")) {
            if (response.getPayment() != null && response.getPayment().size() != 0) {
                mListener.PopulateData(response.getPayment());
//                mListener.showSnackBar(mListener.getAttachedView().getContext().getString(R.string.Toast_card_delete));
                mListener.showSnackBar(mListener.getBaseActivity().getTranslatedString(R.string.Toast_card_delete));
            } else
                mListener.ClearList();

        }
    }

    /** Callback for failed API call
     * @param taskId
     * @param e **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        mListener.showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if(mListener!=null)
                mListener.refreshCompanyKey();
        }
    }

    /** Returns {@link HashMap} with query parameters used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    public interface ChidItemViewModelListener extends BaseView {
        void PopulateData(List<Payment> payment);

        void ClearList();

        View getAttachedView();

        BaseActivity getBaseActivity();
    }

    /** Confirmation dialog for card deletion **/
    public void ShowTrashAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mListener.getAttachedView().getContext());
        builder.setMessage(R.string.text_desc_delete);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setIsLoading(true);
                hashMap.clear();
                hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
                hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                hashMap.put(Constants.NetworkParameters.card_id, "" + payment.cardId);
                if (mListener.isNetworkConnected())
                    DeleteCardNetwork();
                else
                    mListener.showNetworkMessage();

            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
