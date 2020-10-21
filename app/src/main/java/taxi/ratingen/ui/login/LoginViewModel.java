package taxi.ratingen.ui.login;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.graphics.Typeface;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.CountryCodeModel;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 10/9/17.
 */

public class LoginViewModel extends BaseNetwork<User, LoginNavigator> {


    @Inject
    Context context;


    SharedPrefence sharedPrefence;


    Gson gson;

    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> EmailorPhone = new ObservableField<>();
    public ObservableField<String> Countrycode = new ObservableField<>("");

    @Inject
    HashMap<String, String> Map;
    GitHubCountryCode gitHubCountryCodeService;

    @Inject
    public LoginViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                          @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCodeService,
                          SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubCountryCodeService = gitHubCountryCodeService;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;

    }


    public void onclickForgot(View view) {
        getmNavigator().OpenForgotActivity();
    }


    public void onClickSocial(View view) {
        getmNavigator().OpenSocialActivity();
    }

    public void onclicklogin(View view) {
        if (CommonUtils.IsEmpty(EmailorPhone.get()))
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Email)); //change later
        else if (CommonUtils.IsEmpty(password.get()))
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Password));
        else if (password.get().length() < 8)
            getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_PasswordLength));
        else {
            setIsLoading(true);
            Map.clear();
            Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            Map.put(Constants.NetworkParameters.login_by, Constants.NetworkParameters.android);
            Map.put(Constants.NetworkParameters.device_token,sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
            Map.put(Constants.NetworkParameters.password, password.get());
            Map.put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.manual);
            if (EmailorPhone.get().contains("@") || EmailorPhone.get().contains(".co") || EmailorPhone.get().contains(".com"))
                Map.put(Constants.NetworkParameters.username, EmailorPhone.get().replace(" ", "")); //change later
            else
                Map.put(Constants.NetworkParameters.username, getmNavigator().getCountryCode().replace(" ", "") +
                        CommonUtils.removeFirstZeros(EmailorPhone.get().replace(" ", "").replace("-", "")));
          /*  if(Map.containsKey(Constants.NetworkParameters.phonenumber)&&!CommonUtils.IsEmpty(Constants.NetworkParameters.phonenumber))
            Map.put(Constants.NetworkParameters.username,Map.get(Constants.NetworkParameters.phonenumber));*/
            LoginNetworkcall();

        }


    }

    public void onUsernameChanged(Editable e) {
        EmailorPhone.set(e.toString());
        if (e.toString().contains("@") || e.toString().contains(".co") || e.toString().contains(".com")) {
            getmNavigator().OpenCountrycodeINvisible();

        } else {

            if (e.toString().matches("[a-zA-Z]")) //only alphanumeric
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().isEmpty())
                getmNavigator().OpenCountrycodeINvisible();
            else if (e.toString().matches("[0-9]+"))//only number
                getmNavigator().OpenCountrycodevisible();
        }
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

    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCodeService.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null && response.body() != null) {
                           // Constants.COUNTRY_CODE = response.body().toString();
                            Constants.COUNTRY_CODE = "IN";
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCountryFlag(Constants.COUNTRY_CODE);
                }
            });
        }
    }

    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);
        if (response.success) {
            if (!CommonUtils.IsEmpty(response.successMessage) && response.successMessage.equalsIgnoreCase("User_Logged")) {
//                getmNavigator().showMessage(context.getString(R.string.Toast_UserLogged));
                String userstring = gson.toJson(response.getUser());
                if (response.isCorporate == 1)
                    sharedPrefence.saveBoolean(SharedPrefence.IS_CORPORATE_USER, true);
                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.TOKEN, response.getUser().token);
                sharedPrefence.savevalue(SharedPrefence.ID, "" + response.getUser().id);
                getmNavigator().OpenDrawerActivity();
            }

        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {

            getmNavigator().refreshCompanyKey();
        }else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {

            getmNavigator().refreshCompanyKey();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        Map.clear();
        Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        return Map;
    }
}
