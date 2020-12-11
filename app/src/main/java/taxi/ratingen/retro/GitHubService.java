package taxi.ratingen.retro;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by root on 9/25/17.
 */

public interface GitHubService {

    @GET(Constants.URL.CC_URL)
    Call<BaseResponse> getCountryList();

    @FormUrlEncoded
    @POST(Constants.URL.LoginURL)
    Call<User> Logincall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.TokenGeneratorURL)
    Call<BaseResponse> TokenGenerator(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(Constants.URL.otpsendURL)
    Call<User> SendOtpNetork(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.addwalletURL)
    Call<Payment> AddWalletNetork(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ClienttokenURL)
    Call<Payment> GetClient(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.PREFFERED_PAYMENT)
    Call<Payment> GetPaymentModeSelected(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.addcardURL)
    Call<Payment> addCard(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.cardlistURL)
    Call<Payment> GetCardList(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.getwalletURL)
    Call<Payment> Getwallet(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.carddefaultURL)
    Call<Payment> changeDefaultCard(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.deletecardURL)
    Call<Payment> DeleteCard(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ETAURL)
    Call<BaseResponse> ETACall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ETA_NEW)
    Call<BaseResponse> newETACall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.getTypes)
    Call<BaseResponse> TypesCall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.createrequestURl)
//    @POST(Constants.URL.createRequestNewURL)
    Call<BaseResponse> CreateRequest(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.createrequestLaterURl)
    Call<BaseResponse> CreateRequestLater(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.otpvalidate)
    Call<User> OtpVerfiycall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.LoginByOTPURL)
    Call<User> loginOtpVerfiycall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ISRegistered)
    Call<User> IsRegisteredSocial(@Field(Constants.NetworkParameters.social_unique_id) String socialid);

    @Multipart
    @POST(Constants.URL.SignUpURL)
    Call<User> SignupCall(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST(Constants.URL.otpResend)
    Call<User> OtpResendcall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.Forgoturl)
    Call<User> Forgotcall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.referralcheckUrl)
    Call<BaseResponse> ApplyRefferal(@FieldMap Map<String, String> options);

    @GET(Constants.URL.requestInProgressURL)
    Call<BaseResponse> getRequestInpro(@Header("Authorization") String bearer);

    @FormUrlEncoded
    @POST(Constants.URL.historyListURL)
    Call<BaseResponse> GetHistorycall(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.AddFavurl)
    Call<User> AddFav(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.Reviewurl)
    Call<BaseResponse> ReviewNetwork(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CANCEL_REASON_LIST_URL)
    Call<BaseResponse> ListCancel(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.Requestcancelurl)
    Call<BaseResponse> RequestCancel(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ListFavurl)
    Call<User> GetFavList(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.GetreferralURL)
    Call<User> GetRefferal(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SUPPORT)
    Call<User> sendSupport(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SendCompliantURL)
    Call<BaseResponse> sendComplaint(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.DeletefavURL)
    Call<BaseResponse> DeleteFav(@FieldMap Map<String, String> options);

    @GET(Constants.URL.CompliantsURL + "/{" + Constants.NetworkParameters.lat + "}/{" + Constants.NetworkParameters.lng + "}")
    Call<BaseResponse> GetCompliantList(@Header("Authorization") String bearer,
                                        @Path(Constants.NetworkParameters.lat) String lat,
                                        @Path(Constants.NetworkParameters.lng) String lng);

    @FormUrlEncoded
    @POST(Constants.URL.PromoURL)
    Call<BaseResponse> ApplyPromo(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.ridelatercancelURL)
    Call<BaseResponse> Schedulecancel(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.historySingleURL)
    Call<BaseResponse> getSingleHistory(@FieldMap Map<String, String> options);


    @Multipart
    @POST(Constants.URL.ProfileURL)
    Call<User> ProfileCall(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file,
            @Header("Authorization") String bearer);

  /*  @FormUrlEncoded
    @POST(Constants.URL.userProfileretrive)
    Call<BaseResponse> getUserProfile(@FieldMap Map<String, String> options);
*/

    @FormUrlEncoded
    @POST(Constants.URL.FAQ_LIST)
    Call<BaseResponse> getFaqList(@FieldMap Map<String, String> options);


    @GET(Constants.URL.TRANSLATIONS_DOC)
    Call<BaseResponse> getTranslationsDoc();

    @FormUrlEncoded
    @POST(Constants.URL.DriverList)
    Call<BaseResponse> getTopDriverList(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.singleDriver)
    Call<BaseResponse> driverSingle(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.walletHistory)
    Call<BaseResponse> GetWalletHistory(@FieldMap Map<String, String> options);

    @GET(Constants.URL.ZoneSOSUrl)
    Call<BaseResponse> getZoneSos(@Header("Authorization") String bearer);

    @GET(Constants.URL.userProfileRetrieve)
    Call<BaseResponse> GetUserProfile(@Header("Authorization") String bearer);

    @FormUrlEncoded
    @POST(Constants.URL.changeLocationInRide)
    Call<BaseResponse> changeAddressInRide(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.changeLocationInRideDrop)
    Call<BaseResponse> changeAddressInRideDrop(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.topratedConfirm)
    Call<BaseResponse> topratedConfirm(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CANCELLATION_LIST)
    Call<BaseResponse> getCancelationlist(@FieldMap Map<String, String> options);


    @FormUrlEncoded
    @POST(Constants.URL.PROMOCODE_ETA)
    Call<BaseResponse> getPrmocode(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.PROMOCODE_TRIP)
    Call<BaseResponse> getPrmocodeTrip(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.RESCHEDULE_TRIP)
    Call<BaseResponse> reScheduleTrip(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.USER_WALLET_HISTORY)
    Call<BaseResponse> userWalletHistory(@FieldMap Map<String, String> options);


    @FormUrlEncoded
    @POST(Constants.URL.USER_CANCELLATION_HISTORY)
    Call<BaseResponse> userCancellHistory(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.USER_BLOCKED)
    Call<BaseResponse> userBlocked(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CHECKOUTID)
    Call<Payment> getCheckId(@FieldMap Map<String, String> map);
    @FormUrlEncoded
    @POST(Constants.URL.REQUEST_COMPANY_KEY)
    Call<User> getCompanyKey(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.CHECK_COMPANY_KEY_STATUS)
    Call<User> checkCompanyKeyStatus(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.NotificationURL)
    Call<BaseResponse> getNotificationAPi(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.VALIDATE_MOBILE)
    Call<BaseResponse> getValidateMobile(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SEND_OTP)
    Call<BaseResponse> sendOTP(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.SEND_LOGIN_OTP)
    Call<BaseResponse> loginOTP(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.REGISTER_OTP_VALIDATE)
    Call<BaseResponse> registerOtpValidate(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST(Constants.URL.LOGIN)
    Call<BaseResponse> userLogin(@FieldMap Map<String, String> options);

    @Multipart
    @POST(Constants.URL.USER_REGISTER)
    Call<BaseResponse> userRegister(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @POST(Constants.URL.LOGOUT)
    Call<BaseResponse> logoutCall(@Header("Authorization") String bearer);

}
