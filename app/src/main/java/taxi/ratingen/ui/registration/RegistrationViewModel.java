package taxi.ratingen.ui.registration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

/**
 * Created by root on 10/9/17.
 */

public class RegistrationViewModel extends BaseNetwork<BaseResponse, RegistrationNavigator> {

    @Inject
    HashMap<String, String> Map;

    @Inject
    Context context;

    public ObservableField<String> countryID = new ObservableField<>();
    public ObservableField<String> userPhone = new ObservableField<>();
    public ObservableField<String> userCountryCode = new ObservableField<>();
    public ObservableField<String> userFName = new ObservableField<>();
    public ObservableField<String> userLName = new ObservableField<>();
    ObservableField<String> uuid = new ObservableField<>();
    public ObservableField<String> userEmail = new ObservableField<>();
    public ObservableBoolean isTermsAccepted = new ObservableBoolean(false);
    private HashMap<String, String> map = new HashMap<>();
    private SharedPrefence sharedPrefence;
    public ObservableField<String> bitmap_profilePicture = new ObservableField<>();

    File RealFile;

    String RealPath = null;

    @Inject
    public RegistrationViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                 @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                                 @Named("HashMapData") HashMap<String, String> stringHashMap,
                                 SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
    }

    /**Callback for successful API calls
     * @param taskId ID of the API task
     * @param response {@link BaseResponse} model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (!TextUtils.isEmpty(response.accessToken)) {
            sharedPrefence.savevalue(SharedPrefence.AccessToken, response.accessToken);
            sharedPrefence.savevalue(SharedPrefence.UserName, userFName.get() + " " + userLName.get());
            sharedPrefence.savevalue(SharedPrefence.UserEmail, userEmail.get());
            getmNavigator().OpenDrawerAct();
        }
    }

    /**Callback for failed API calls
     * @param taskId ID of the API task
     * @param e {@link CustomException} **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }

    /** Click listener for signup button. Validates form and calls sign up API **/
    public void onClickSignUp(View view) {
        if (getmNavigator().isNetworkConnected()) {
            if (isValidation()) {
                setIsLoading(true);
                requestbody.clear();
                requestbody.put(Constants.NetworkParameters.device_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN)));
                requestbody.put(Constants.NetworkParameters.login_by, RequestBody.create(MediaType.parse("text/plain"), "1"));
                requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"), countryID.get()));
                if (!CommonUtils.IsEmpty(userFName.get())&&!CommonUtils.IsEmpty(userLName.get()))
                    requestbody.put(Constants.NetworkParameters.name, RequestBody.create(MediaType.parse("text/plain"), userFName.get() + " " + userLName.get()));
                if (!CommonUtils.IsEmpty(uuid.get()))
                    requestbody.put(Constants.NetworkParameters.UUId, RequestBody.create(MediaType.parse("text/plain"), uuid.get()));

                if (!CommonUtils.IsEmpty(userEmail.get()))
                    requestbody.put(Constants.NetworkParameters.Email, RequestBody.create(MediaType.parse("text/plain"), userEmail.get()));

                if (!CommonUtils.IsEmpty(userPhone.get()))
                    requestbody.put(Constants.NetworkParameters.mobile, RequestBody.create(MediaType.parse("text/plain"), userPhone.get()));
                requestbody.put(Constants.NetworkParameters.TermsCondition, RequestBody.create(MediaType.parse("text/plain"), isTermsAccepted.get() ? "1" : "0"));


                if (RealPath != null) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), RealFile);
                    body = MultipartBody.Part.createFormData("profile", RealFile.getName(), reqFile);
                }
                userRegister();
            }
        } else getmNavigator().showNetworkMessage();
    }

    private boolean isValidation() {
        String error_message = null;
        if (CommonUtils.IsEmpty(userFName.get())) {
            error_message = translationModel.text_first_name_empty;
        } else if (CommonUtils.IsEmpty(userLName.get())) {
            error_message = translationModel.text_error_lastname;
        } else if (TextUtils.isEmpty(userEmail.get())) {
            error_message = translationModel.text_error_email;
        } else if (!CommonUtils.isEmailValid(userEmail.get())) {
            error_message = translationModel.txt_email_invalid;
        } else if (TextUtils.isEmpty(userPhone.get()))
            error_message = translationModel.txt_enter_phone;
        else if (!isTermsAccepted.get())
            error_message = translationModel.txt_accept_terms;
        if (error_message != null)
            getmNavigator().showMessage(error_message);
        return error_message == null;
    }

    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            getmNavigator().getPath(data);
        }
    }

    public void onCaptureImageResult(Intent data) {
        RealPath = CommonUtils.getImageUri((Bitmap) data.getExtras().get("data"));
        RealFile = new File(RealPath == null ? "" : RealPath);
        bitmap_profilePicture.set(RealPath);
    }

}
