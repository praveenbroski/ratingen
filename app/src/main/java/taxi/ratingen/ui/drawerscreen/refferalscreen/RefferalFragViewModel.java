package taxi.ratingen.ui.drawerscreen.refferalscreen;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 11/3/17.
 */

public class RefferalFragViewModel extends BaseNetwork<User, RefferalNavigator> {

    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public ObservableField<String> code = new ObservableField<>();
    public ObservableField<String> Prize = new ObservableField<>("0");
    public ObservableField<String> CurrencySymbol = new ObservableField<>();

    public RefferalFragViewModel(GitHubService gitHubService,
                                 SharedPrefence sharedPrefence,
                                 HashMap<String, String> hashMap, Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
    }

    /** Custom {@link BindingAdapter} function to set custom font for {@link TextView}
     * @param fontName Name of the font
     * @param textView {@link TextView} whose font needs to be changed **/
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Custom {@link BindingAdapter} function to set custom font for {@link EditText}
     * @param fontName Name of the font
     * @param textView {@link EditText} whose font needs to be changed **/
    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Custom {@link BindingAdapter} function to set custom font for {@link Button}
     * @param fontName Name of the font
     * @param textView {@link Button} whose font needs to be changed **/
    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response Response model **/
    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);
        if (response.successMessage != null && response.successMessage.equalsIgnoreCase("Get_Referral_code")) {
            code.set(response.code);
            CurrencySymbol.set(response.currency);
            Prize.set(response.earned);
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_already_login));
            }
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }else
        getmNavigator().showMessage(e);
    }

    /** Adds and returns {@link HashMap} with client_id & client_token used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    /** Called to open share {@link android.content.Intent} **/
    public void onclickShare(View view) {
      getmNavigator().OpenShareRefferal(code.get());
    }

    /** Calls API to get details of referral code **/
    public void GetRefferalDetails() {
        CurrencySymbol.set(getmNavigator().getAttachedContext().getTranslatedString(R.string.Rs));
        if (getmNavigator().isNetworkConnected()) {

            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            GetRefferalDetailsNetwork();


        } else {
            getmNavigator().showNetworkMessage();
        }

    }
}
