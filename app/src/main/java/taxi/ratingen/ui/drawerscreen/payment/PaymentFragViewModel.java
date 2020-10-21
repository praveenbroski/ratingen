package taxi.ratingen.ui.drawerscreen.payment;

import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 11/3/17.
 */

public class PaymentFragViewModel extends BaseNetwork<Payment, PaymentNavigator> {

    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    String Client_token = null;
    List<Payment> paymentList = new ArrayList<>();
    public ObservableBoolean IsReclyer = new ObservableBoolean(false);
    public ObservableBoolean prefCash = new ObservableBoolean(false);
    public ObservableBoolean prefCard = new ObservableBoolean(false);
    public ObservableBoolean prefWallet = new ObservableBoolean(false);

    public PaymentFragViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, HashMap<String, String> hashMap, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
    }

    /** Adds client_id & client_token to the {@link HashMap} used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** Called when add money to wallet clicked. Used to recharge user's wallet **/
    public void onclickAddMoney(View view) {
        if (paymentList != null && paymentList.size() > 0) {
            getmNavigator().OpenWalletScreen();
        } else {
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Toast_AddCard));
        }
    }

    /** User for adding a new card to saved cards **/
    public void onclickAddcard(View view) {
        getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.txt_this_feature_unvailabe_demo));
//        getmNavigator().openCardFrag();
//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(Client_token).disablePayPal();
//        getmNavigator().getBaseAct().startActivityForResult(dropInRequest.getIntent(view.getContext()), Constants.BRAINTREE_REQUEST_CODE);
    }

    /** Called to set currently selected default payment method
     * @param paymentType ID of the payment type **/
    public void setPrefferedPayment(int paymentType) {
        switch (paymentType) {
            case 1:
                prefCard.set(false);
                prefWallet.set(false);
                prefCash.set(true);
                break;
            case 0:
                prefCard.set(true);
                prefWallet.set(false);
                prefCash.set(false);
                break;
            case 2:
                prefCard.set(false);
                prefWallet.set(true);
                prefCash.set(false);
                break;
        }
    }

    /** Callback when API call was successful
     * @param taskId Id of the task
     * @param response Response of the API **/
    @Override
    public void onSuccessfulApi(long taskId, Payment response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Client_token")) {
            Client_token = response.client_token;
        } else if (response.successMessage.equalsIgnoreCase("Preferred Payment Type Updated")
                || response.successMessage.equalsIgnoreCase("Preferred Payment Type Listed")) {
            if (response.preferred_payment_type != null) {
                sharedPrefence.saveInt(SharedPrefence.PREFFERED_PAYMENT, response.preferred_payment_type);
                setPrefferedPayment(response.preferred_payment_type);
            }
        } else if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Card_Added_Successfully") ||
                response.successMessage.equalsIgnoreCase("Get_card_list")) {

            if (response.getPayment() != null && response.getPayment().size() != 0) {
                paymentList = response.getPayment();
                getmNavigator().addList(response.getPayment());
                IsReclyer.set(true);
            } else {
                IsReclyer.set(false);
                paymentList.clear();
            }
        } else if (response.successMessage.equalsIgnoreCase("checkout_generated_successfully")) {
            getmNavigator().openPaymentUI(response.checkoutId);
        }
    }

    /** Callback when API fails
     * @param taskId Id of the task
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getBaseAct() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getBaseAct().getTranslatedString(R.string.text_already_login));
            }
        }else  if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if(getmNavigator()!=null)
                getmNavigator().refreshCompanyKey();
        }
    }

    /** Calls API to get client_id **/
    public void GetClient() {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        GetClientNetwork();
    }

    /** Calls API to get preferred payment type **/
    public void GetYourPrefferdPayment() {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        getSetDefaultPayment(hashMap);
    }

    /** Calls API to choose a new preferred payment type
     * @param paymentType ID of payment type **/
    public void changePrefferedPayment(/*View view,*/int paymentType) {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.payment_type, paymentType + "");
        setIsLoading(true);
        getSetDefaultPayment(hashMap);
    }

    /** Calls API to fetch saved card details **/
    public void FetchCardNumbers() {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(SharedPrefence.ID, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(SharedPrefence.TOKEN, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        GetCardListNetwork();
    }

    /** Calls API to add new card
     * @param resoucePath **/
    public void addCard(String resoucePath) {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.url, resoucePath);
        addCardNetwork();
    }

    /** Calls API to get checkout ID **/
    public void RequestIdApi() {
        setIsLoading(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.amount, "10");
        ReqIdApi(hashMap);
    }

}
