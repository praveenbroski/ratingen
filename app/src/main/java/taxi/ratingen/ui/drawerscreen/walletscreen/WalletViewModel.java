package taxi.ratingen.ui.drawerscreen.walletscreen;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import taxi.ratingen.utilz.CommonUtils;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 11/3/17.
 */

public class WalletViewModel extends BaseNetwork<Payment, WalletNavigator> {


    public ObservableField<String> SelectedPrize = new ObservableField<>("5");
    public ObservableField<String> WalletPrize = new ObservableField<>();
    public ObservableField<String> CurrencySymbol = new ObservableField<>();

    @Inject
    HashMap<String, String> hashMap;

    SharedPrefence sharedPrefence;
    public ObservableBoolean IsReclyer = new ObservableBoolean(false);


    @Inject
    public WalletViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;

    }

    /** Click listener for price **/
    public void OnclickThousand(View view) {
        SelectedPrize.set("30");
        getmNavigator().PrizeClicked(30);
    }

    /** Click listener for price **/
    public void OnclickFivehundread(View view) {
        SelectedPrize.set("20");
        getmNavigator().PrizeClicked(20);
    }

    /** Click listener for price **/
    public void OnclickTwohundread(View view) {
        SelectedPrize.set("10");
        getmNavigator().PrizeClicked(10);
    }

    /** Click listener for price **/
    public void Onclickhundread(View view) {
        SelectedPrize.set("5");
        getmNavigator().PrizeClicked(5);
    }

    /** Custom {@link BindingAdapter} method to set font of the {@link TextView}
     * @param textView {@link TextView} whose font needed to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Custom {@link BindingAdapter} method to set font of the {@link EditText}
     * @param textView {@link EditText} whose font needed to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Custom {@link BindingAdapter} method to set font of the {@link Button}
     * @param textView {@link Button} whose font needed to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Click listener for the add button. Calls AddWalletAmtNetwork API to add selected/entered amount **/
    public void onclickAdd(View view) {
        getmNavigator().showMessage(translationModel.txt_this_feature_unvailabe_demo);
        return;
        /*if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(SharedPrefence.ID, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(SharedPrefence.TOKEN, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            hashMap.put(Constants.NetworkParameters.card_id, "" + getmNavigator().getCardId());
            hashMap.put(Constants.NetworkParameters.amount, SelectedPrize.get());
            AddWalletAmtNetwork();

        } else {
            getmNavigator().showNetworkMessage();
        }
*/
    }

    /** Callback method for successful API calls
     * @param taskId Id of the API task
     * @param response {@link Payment} response model **/
    @Override
    public void onSuccessfulApi(long taskId, Payment response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("Get_card_list")) {

            if (response.getPayment() != null && response.getPayment().size() != 0) {
                getmNavigator().addList(response.getPayment());
                IsReclyer.set(true);
            } else
                IsReclyer.set(false);
        } else if (response.successMessage.equalsIgnoreCase("Added Wallet Successfully")) {
            getmNavigator().showMessage(response.successMessage);
            WalletPrize.set(CommonUtils.doubleDecimalFromat(Double.valueOf(response.amount_balance)));
            CurrencySymbol.set(response.currency);
            SelectedPrize.set("");
        } else if (response.successMessage.equalsIgnoreCase("Get_Wallet_Amount")) {
            WalletPrize.set(CommonUtils.doubleDecimalFromat(Double.valueOf(response.amount_balance)));
            CurrencySymbol.set(response.currency);

        }
    }

    /** Callback method for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if(getmNavigator()!=null)
                getmNavigator().refreshCompanyKey();
        }
    }

    /** Returns {@link HashMap} with query parameters required for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** Calls API to get card numbers **/
    public void FetchCardNumbers() {

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(SharedPrefence.ID, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(SharedPrefence.TOKEN, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            GetCardListNetwork();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /** Calls API to get details of the wallet **/
    public void getWalletDetails() {
        CurrencySymbol.set(getmNavigator().getAttachedContext().getTranslatedString(R.string.Rs));
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(SharedPrefence.ID, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(SharedPrefence.TOKEN, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            GetwalletNetwork();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }
}
