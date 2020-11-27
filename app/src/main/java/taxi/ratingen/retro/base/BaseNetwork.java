package taxi.ratingen.retro.base;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;

import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatDelegate;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 9/27/17.
 */

/**
 * Here is where all API calls connected with view and githubService
 **/
public abstract class BaseNetwork<T extends BaseResponse, N> implements Basecallback<T> {

    public GitHubService gitHubService;
    private N mNavigator;
    public HashMap<String, RequestBody> requestbody = new HashMap<>();
    public MultipartBody.Part body = null;
    /*public  ObservableInt mCurrentTaskId = new ObservableInt(-1);*/
    public Integer mCurrentTaskId = -1;
    public ObservableBoolean mIsLoading = new ObservableBoolean(false);
    public TranslationModel translationModel;
    public SharedPrefence sharedPrefence;
    public Gson gson;

    /**
     * @param gitHubService is intiating the api parameter.
     */
    public BaseNetwork(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * @param gitHubService  object of GithubService class for api.
     * @param sharedPrefence object of SharedPreference and to used Every Viewmodel class.
     * @param gson           object of Gson and this is for getting the Translation Model.
     */
    public BaseNetwork(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = gson.fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);
        }
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * Api call for user login.
     */
    public void LoginNetworkcall() {
        gitHubService.Logincall(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    public void getCountryList() {
        setIsLoading(true);
        gitHubService.getCountryList().enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to verify the otp while login.
     */
    public void loginOtpVerification() {
        setIsLoading(true);
        gitHubService.loginOtpVerfiycall(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call for forgot password.
     */
    public void ForgotNetworkcall() {
        gitHubService.Forgotcall(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call for Applying refferal code.
     */
    public void ApplyRefferalNetwork() {
        gitHubService.ApplyRefferal(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to get User trip details and blocked status.
     */
    public void getRequestInProgressNetwork() {
        setIsLoading(true);
        gitHubService.getRequestInpro(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to get the history details of user.
     */
    public void GetHistoryNetworkCall() {
        gitHubService.GetHistorycall(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * APi call to Add the Address as favourite.
     */
    public void AddFavNetworkcall() {
        gitHubService.AddFav(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call to Apply promocode.
     */
    public void PromoCodeNetworkcall() {
        gitHubService.ApplyPromo(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call for cancelling the scedule ride.
     */
    public void SchedulecancelNetwork() {
        gitHubService.Schedulecancel(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * api call to get single history details.
     */
    public void getSingleHistoryNetwork() {
        gitHubService.getSingleHistory(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call for giving feedback of driver.
     */
    public void reviewDriverNetwork() {
        gitHubService.ReviewNetwork(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to cancel the trip request.
     */
    public void RequestcancelNetwork() {
        gitHubService.RequestCancel(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call for Requesting the cancellation list.
     */
    public void RequestCancelReasonList() {
        gitHubService.ListCancel(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call for getting the FavouriteList.
     */
    public void GetFavListNetworkcall() {
        setIsLoading(false);
        gitHubService.GetFavList(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call Register the complaint.
     */
    public void SendComplaintNetwork() {
        gitHubService.sendComplaint(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call get the Referal code details.
     */
    public void GetRefferalDetailsNetwork() {
        gitHubService.GetRefferal(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call to sedn support feedback.
     */
    public void sendSupportFeedback() {
        gitHubService.sendSupport(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call to delete the Favourite Address.
     */
    public void DeleteFavNetworkcall() {
        gitHubService.DeleteFav(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to get the complaints.
     */
    public void getComplaintsNetwork() {
        gitHubService.GetCompliantList(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to check if the new user or not.
     */
    public void checkIsRegistered() {
        gitHubService.IsRegisteredSocial(getMap().get(Constants.NetworkParameters.social_unique_id)).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call to send OTP.
     */
    public void sendOtpCall() {
        gitHubService.SendOtpNetork(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call to add wallet Amount.
     */
    public void AddWalletAmtNetwork() {
        gitHubService.AddWalletNetork(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * unused Api
     */
    public void GetClientNetwork() {
        gitHubService.GetClient(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * For Setting and Getting Preferred Payment
     * Mode same API with different Parameters
     * For Set give @param payment_type
     * For Get selected payment @param payment_type not required
     */
    public void getSetDefaultPayment(HashMap<String, String> map) {
        gitHubService.GetPaymentModeSelected(map).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Api call to add a card
     */
    public void addCardNetwork() {
        gitHubService.addCard(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Api call to list the card details.
     */
    public void GetCardListNetwork() {
        gitHubService.GetCardList(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Api call to get the Wallet amount
     */
    public void GetwalletNetwork() {
        gitHubService.Getwallet(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Api call to get the default choose card.
     */
    public void changeDefaultCardNetwork() {
        gitHubService.changeDefaultCard(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * APi call to delete the Saved card.
     */
    public void DeleteCardNetwork() {
        gitHubService.DeleteCard(getMap()).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Api call to get the ETA for trip.
     */
    public void getETANetworkcall() {
//        setIsLoading(true);
        gitHubService.ETACall(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to get the ETA for trip.
     */
    public void getNewETANetworkCall() {
//        setIsLoading(true);
        gitHubService.newETACall(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * API call to get types data for firebase
     */
    public void getTypesNetworkCall() {
        gitHubService.TypesCall(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to make the Trip request.
     */
    public void CreateRequestNetwork() {
        gitHubService.CreateRequest(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to create the ride later request.
     */
    public void CreateRequestLaterNetwork() {
        gitHubService.CreateRequestLater(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to call to register the user.
     */
    public void SignupNetworkcall() {
        gitHubService.SignupCall(requestbody, body).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call get the Emergency contact list.
     */
    public void getSosList() {
        gitHubService.getZoneSos(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call for getting the profile informations.
     */
    public void ProfileNetworkcall() {
        gitHubService.ProfileCall(requestbody, body).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameter to  get the faq list.
     */
    public void getFaq(HashMap<String, String> hashMap) {
        gitHubService.getFaqList(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap to update the location in ride.
     */
    public void updateLocationInRide(HashMap<String, String> hashMap) {
        gitHubService.changeAddressInRide(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameter for update the Droplocation in ride.
     */
    public void updateLocationInRideDrop(HashMap<String, String> hashMap) {
        gitHubService.changeAddressInRideDrop(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api call to get the languages values
     */
    public void getTranslations() {
        setIsLoading(true);
        gitHubService.getTranslationsDoc().enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameter to get the userprofile informations.
     */
    public void getUserProfile(HashMap<String, String> hashMap) {
        gitHubService.GetUserProfile(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network paramter to get the Top driver list for schedule.
     */
    public void TopDriverList(HashMap<String, String> hashMap) {
        gitHubService.getTopDriverList(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is network paramter to get the promocode.
     */
    public void RidePromocode(HashMap<String, String> hashMap) {
        gitHubService.getPrmocode(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is network paramter to get the promocode.
     */
    public void RidePromocodeTrip(HashMap<String, String> hashMap) {
        gitHubService.getPrmocodeTrip(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameter to schedule the trip again.
     */
    public void reScheduleAPi(HashMap<String, String> hashMap) {
        gitHubService.reScheduleTrip(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameer to get the user Cancellation history.
     */
    public void userCancellHistory(HashMap<String, String> hashMap) {
        gitHubService.userCancellHistory(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * @param hashMap is a network parameter that holds the user history details.
     */
    public void userWalletHistory(HashMap<String, String> hashMap) {
        gitHubService.userWalletHistory(hashMap).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    //Api call to check user blocked state.
    public void UserBlockedApi() {
        gitHubService.userBlocked(getMap()).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    //unUsed api call
    public void ReqIdApi(HashMap<String, String> map) {
        gitHubService.getCheckId(map).enqueue((Callback<Payment>) baseModelCallBackListener);
    }

    /**
     * Requesting the company key
     */
    public void requestCompanyKey() {
        setIsLoading(true);
        gitHubService.getCompanyKey(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    /**
     * Api call for Validating the company key.
     */
    public void validateCompanyKey() {
        setIsLoading(true);
        gitHubService.checkCompanyKeyStatus(getMap()).enqueue((Callback<User>) baseModelCallBackListener);
    }

    public void getNotificationAPi(HashMap<String ,String > map) {
        setIsLoading(true);
        gitHubService.getNotificationAPi(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void validateMobile(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.getValidateMobile(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void sendRegisterOtp(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.sendOTP(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void sendLoginOtp(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.loginOTP(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void registerOtpValidate(HashMap<String, String> map) {
        setIsLoading(true);
        gitHubService.registerOtpValidate(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void getUserLoginApi(HashMap<String, String> map) {
        mCurrentTaskId = Constants.TaskId.login;
        setIsLoading(true);
        gitHubService.userLogin(map).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    public void userRegister() {
        setIsLoading(true);
        gitHubService.userRegister(requestbody, body).enqueue((Callback<BaseResponse>) baseModelCallBackListener);
    }

    /**
     * Api callback to detect the Api response whether success or failure.
     */
    private Callback<T> baseModelCallBackListener = new Callback<T>() {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().success || response.body().tokenType != null) {
                    onSuccessfulApi(mCurrentTaskId, response.body());
                } else {
                    if (response.message() != null) {
                        onFailureApi(mCurrentTaskId, new CustomException(response.message()));
                    } else {
                        String errorMsg = CommonUtils.converErrors(response.errorBody());
                        Log.e("Response==", "respp===" + errorMsg);
                        if (TextUtils.isEmpty(errorMsg))
                            errorMsg = response.message();
                        onFailureApi(mCurrentTaskId, new CustomException(errorMsg));
                    }

                }
            } else {
                String errorMsg = CommonUtils.converErrors(response.errorBody());
                Log.e("Response==", "respp111===" + errorMsg);
                if (TextUtils.isEmpty(errorMsg))
                    errorMsg = response.message();
                onFailureApi(mCurrentTaskId, new CustomException(errorMsg));
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {

            onFailureApi(mCurrentTaskId, new CustomException(501, t.getLocalizedMessage()));
        }
    };

    public abstract HashMap<String, String> getMap();

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    /**
     * @param isLoading contains whether loader is need ot not.
     */
    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    /**
     * @param navigator is the inter
     */
    public void setNavigator(N navigator) {
        this.mNavigator = navigator;
    }

    public N getmNavigator() {
        return mNavigator;
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

    /**
     * @param v hide the opened keyboard.
     */
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
