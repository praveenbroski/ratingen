package taxi.ratingen.ui.applyrefferal;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 12/8/17.
 */

public class ApplyRefferalViewModel extends BaseNetwork<BaseResponse,ApplyRefferalNavigator> {

    public ObservableField<String> Refferalcode;

    GitHubService gitHubService;

    @Inject
    HashMap<String,String> hashMap;

    SharedPrefence sharedPrefence;
    Gson gson;

    @Inject
    public ApplyRefferalViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                  SharedPrefence sharedPrefence,Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        this.gitHubService=gitHubService;
        this.sharedPrefence=sharedPrefence;
        this.gson=gson;
        Refferalcode=new ObservableField<>();
    }

    /** Sign up promo code skip **/
    public void onclickskip(View view){
        getmNavigator().OpenDrawerActivity();
    }

    /** Sign up promo code apply **/
    public void onclickApply(View view){
        if(TextUtils.isEmpty(Refferalcode.get()))
            return;
        if(getmNavigator().isNetworkConnected())
        ApplyRefferalNetwork();
        else
            getmNavigator().showNetworkMessage();
    }

    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        if (response.successMessage.equalsIgnoreCase("Referral_Added_Successfully")) {
            getmNavigator().OpenDrawerActivity();
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id,sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token,sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put("referral_code", (String) ((ObservableField) Refferalcode).get());
        return hashMap;
    }

}
