package taxi.ratingen.utilz;

/**
 * Created by root on 9/22/17.
 */

import taxi.ratingen.BuildConfig;

/**
 * Common used files
 */
public final class Constants {
    public static final int INITIAL_DELAY = 1;
    public static final int UPDATE_INTERVAL = 10;
    public static final int BOTTOMSHEETCALLBACK = 23123;
    public static final int BOTTOMSHEETRIDECALLBACK = 23124;
    public static final int CANCELTRIPCALLBACK = 23134;
    public static final int RIDE_PROMO_RESULT = 99;
    public static final int CC_SELECTED_CODE = 9795;
    public static final int REQUEST_MOBILE_VALIDATE = 8749;
    public static boolean ACTIVITY_OPENEND_ALRDY = false;
    public static String[] Array_permissions = new String[] {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    public static String[] storagePermission = new String[] {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static int SPLASH_TIME_OUT = 3000;
    public static final int REQUEST_PERMISSION = 9000;
    public static final int SELECT_FILE = 100;
    public static final int REQUEST_CAMERA = 200;
    public static final int GOOGLE_SIGN_IN = 300;
    public final static int PLAY_SERVICES_REQUEST = 1000;
    public final static int BRAINTREE_REQUEST_CODE = 500;
    public static final int REQUEST_CODE_AUTOCOMPLETE = 700;
    public static final int REQUEST_CODE_AUTOCOMPLETE_DEST = 701;
    public static final int REQUEST_CODE_ENABLING_GOOGLE_LOCATION = 400;
    public static final int REQUEST_CODE_AUTOCOMPLETE_DROPADD_FROMRIDEFRG = 1100;
    public static final float ZOOMLEVELMAP = 18f;
    public static final int REQUEST_CODE_AUTOCOMPLETEINRIDE = 800;
    public static final int REQUEST_CHANGE_ADDRESS = 900;
    public final static int WALLETSETRESULT = 600;
    public final static int PROMOSETRESULT = 8100;
    public final static int HISTORYLISTSETRESULT = 9100;

    public static final int REQUESTCODE_QR = 49374;
    public static final int REQUEST_CODE_PAYMENT_METHOD = 8692;
    public static final int REQUEST_PROFILE_UPDATE = 1589;


    public static final String EXTRA_Data = "Data";
    public static final String EXTRA_RIDE_TYPE = "extra_ride_type";
    public static final String ourApp = "ourApp";
    public static final String googleMap = "googleMap";
    public static final String countryMap = "countryMap";
    public static final String EXTRA_IS_CORPORATE = "EXTRA_IS_CORPORATE";
    public static final String EXTRA_LAT_LNG = "EXTRA_LAT_LNG";
    public static final String EXTRA_SEARCH_TYPE = "extra_search_type";
    public static final String EXTRA_PICK_LAT_LNG = "extra_pick_lat_lng";
    public static final String EXTRA_PICK_ADDRESS = "extra_pick_address";
    public static final String EXTRA_DROP_ADDRESS = "extra_drop_address";
    public static final String EXTRA_PICK_DATA = "EXTRA_PICK_DATA";
    public static final String EXTRA_PAYMENT_BUNDLE = "EXTRA_PAYMENT_BUNDLE";
    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_VALUE = "EXTRA_VALUE";
    public static final String EXTRA_DATA_LAT = "EXTRA_DATA_LAT";
    public static final String EXTRA_DATA_LNG = "EXTRA_DATA_LNG";
    public static final String EXTRA_DATA_ADDRESS = "EXTRA_DATA_ADDRESS";

    public static String EXTRA_IS_PICKUP = "EXTRA_IS_PICKUP";
    public static String Extra_identity = "identity";
    public static String RideNow = "RideNow";
    public static String RideLater = "RideLater";
    public static String PushWaitingorAcceptByDriver = "PushWaitingorAcceptByDriver";
    public static String ProfileUpdate = "ProfileUpdate";
    public static String SearchDestination = "SearchDestination";

    public static String pushRejected = "pushRejected";
    public static String driverChanged = "driverChanged";

    public static String PlaceApi_key = BuildConfig.PLACEAPI_KEY;
    public static String ProfileBroastcast = "ProfileBroastcast";
    public static String PushTripCancelled = "PushTripCancelled";
    public static String PushTripCompleted = "PushTripCompleted";
    public static String Pushbillgenerated = "Pushbillgenerated";
    public static String DismissDialog = "DismissDialog";
    public static String EXTRA_Datastrn = "EXTRA_Datastrn";
    public static String COUNTRY_CODE = "IN";
    public static String text_default_country_codes = "+91";
    public static Integer PAY_CARD = 0;
    public static Integer PAY_CASH = 1;
    public static Integer PAY_WALLET = 2;
    public final static Integer IMAGE_COMPRESSION_QUALITY = 40;
    public static String isLogin = "isLogin";
    public static String CHOOSED_COUNTRYCODE = "IN";
    public static String WAKE_LOCK_TAG = "myapp:LIMO_WAKE_LOCK_TAG";
    public static String WAKE_LOCK_TAG2 = "myapp:LIMO_WAKE_LOCK_TAG2";
    public static String PhonewithCountry = "phoneWithCountry";
    public static final String RESULT_CODE_PICK_FROM_MAP = "RESULT_CODE_PICK_FROM_MAP";
    public static final String CountryCode = "CountryCode";
    public static String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";
    public static boolean ENABLE_FIREBASE_OTP = true;
    public static String Country = "country";
    public static String CLICKEDCOUNTRYCODE = "+49";
    public static String DefaultcountryName = "Germany";
    public static String CountryID = "";
    public static String uuidValue = "UUIDval";
    public static String regOrLogin = "RegOrLogin";
    public static String phoneNum = "phoneNum";
    public static final String countryId = "COUNTRYID";
    public static String phonePrefix = "phonePrefix";

    /**
     * Api 500 Errorcode message.
     */
    public interface HttpErrorMessage {
        String INTERNAL_SERVER_ERROR = "Our server is under maintenance. We will reslove shortly!";
    }

    /**
     * Local broadcast receiever.
     */
    public interface BroadcastsActions {
        String B_REQUEST = "B_REQUEST";
        String LATER_NO_DRIVER = "LATER_NO_DRIVER";
        String RemoveWaitingDialog = "RemoveWaitingDialog";
    }

    /**
     * Error code from Api
     */
    public interface ErrorCode {
        Integer TOKEN_EXPIRED = 609;
        Integer EMPTY_FAV_LIST = 721;
        Integer TOKEN_MISMATCHED = 606;
        Integer INVALID_LOGIN = 603;
        Integer REQUEST_NOT_REGISTERED = 803;
        Integer REQUEST_ALREADY_CANCELLED = 808;
        Integer DRIVER_ALREADY_RATED = 904;
        int COMPANY_KEY_DATE_EXPIRED = 1101;
        int COMPANY_KEY_NOT_ACTIVE = 1102;
        int COMPANY_KEY_NOT_VALID = 1105;
        Integer CONTACT_ADMIN = 903;
        int COMPANY_CREDENTIALS_NOT_VALID = 1100;
        Integer NO_FAQ = 7000;
    }
    public interface IntentExtras {
        String USER_PHONE = "USER_PHONE";
        String USER_COUNTRY = "USER_COUNTRY";
        String REQUEST_DATA = "request_data";
        String COUNTRY_LIST = "country_list";
        String WAITING_TIME = "waiting_time";
        String PREVAILING_WAITING_TIME = "PrevailingWAiting";
        String PREVAILING_WAITING_SEC = "prevailing_waiting_sec";
        String CANCEL_REASON = "cancel_reason";
        String ADD_CHARGE_VALUES = "ADD_CHARGE_VALUES";
        String RIDE_OTP = "ride_otp";
        String ACCEPT_REJECT_DATA = "accept_reject_data";
        String LOCATION_DATA = "location_data";
        String LOCATION_ID = "LOCATION_ID";
        String LOCATION_LAT = "LAT";
        String LOCATION_LNG = "LNG";
        String TRIP_LAT = "TRIP_LAT";
        String TRIP_LNG = "TRIP_LNG";
        String TRIP_BEARING = "TRIP_BEARING";
        String LOCATION_BEARING = "BEARING";
    }
    /**
     * Api URL
     */
    public interface URL {
        String LoginURL = "v1/user/login";
        String TokenGeneratorURL = "v1/user/temptoken";
        String BaseURL = BuildConfig.BASE_URL;
        String SOCKET_URL = BuildConfig.SOCKET_URL;
        String profileBaseURL = BuildConfig.BASE_URL + "storage/uploads/user/profile-picture/";
        String CC_URL = "api/v1/common/countries";
        String GooglBaseURL = "https://maps.googleapis.com/";
        String COUNTRY_CODE_URL = "http://ip-api.com/";//"/json";
        String ISRegistered = "v1/user/social_unique_id_check";
        String SignUpURL = "v1/user/signup";
        String LoginByOTPURL = "v1/user/login/otp";
        String ProfileURL = "api/v1/user/profile";
        String otpvalidate = "v1/user/otpvalidate";
        String carddefaultURL = "v1/user/carddefault";
        String deletecardURL = "v1/user/deletecard";
        String ETA_URL = "api/v1/request/eta";
        String ETA_NEW = "v1/api/eta/new";
        String otpsendURL = "v1/user/sendotp";
        String createRequestURL = "api/v1/request/create";
        String createrequestLaterURl = "v1/user/ridelater";
        String createRequestNewURL = "v1/user/new/createrequest";
        String GetreferralURL = "v1/user/getreferral";
        String DeletefavURL = "v1/user/deletefav";
        String CompliantsURL = "api/v1/complaint-titles/list";
        String PromoURL = "v1/user/promocode/check";
        String ridelatercancelURL = "v1/user/ridelatercancel";
        String CANCEL_REASON_LIST_URL = "api/v1/cancallation/reasons";
        String SendCompliantURL = "v1/compliants/user";
        String addwalletURL = "v1/user/addwallet";
        String addcardURL = "v1/user/addcard";
        String historySingleURL = "v1/user/historySingle";
        String cardlistURL = "v1/user/cardlist";
        String getwalletURL = "v1/user/getwallet";
        String ClienttokenURL = "v1/application/client_token";
        String otpResend = "v1/user/resendotp";
        String Forgoturl = "v1/user/forgotpassword";
        String referralcheckUrl = "v1/user/referralcheck";
        String requestInProgressURL = "api/v1/user/requestInprogress";
        String historyListURL = "v1/user/historyList";
        String AddFavurl = "v1/user/addfav";
        String Reviewurl = "v1/user/review";
        String DirectionURLreferralcheckUrl = "maps/api/directions/json?";
        String DirectionURL = "maps/api/directions/json?";
        String RequestCancelURL = "api/v1/request/cancel";
        String ListFavURL = "api/v1/user/list-favorite-places";
        String ZoneSOSUrl = "api/v1/user/sos";
//        String ZoneSOSUrl = "v1/user/zonesos";
        String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
        String TYPE_AUTOCOMPLETE = "/autocomplete";
        String TYPE_NEAR_BY = "/nearbysearch";
        String OUT_JSON = "/json";
        String TRANSLATIONS_DOC = "api/v1/common/translation/get";
        String PREFFERED_PAYMENT = "v1/user/preferred/payment";
        String FAQ_LIST = "v1/user/get/faqlist";
        String SUPPORT = "v1/Users/support/email";
        String DriverList = "v1/driver/top/list";
        String singleDriver = "v1/user/custom/driver/select";
        String walletHistory = "v1/user/wallet/history";
        String changeLocationInRide = "v1/user/pickup/change";
        String userProfileRetrieve = "api/v1/user";
        String changeLocationInRideDrop = "v1/user/drop/change";
        String topratedConfirm = "v1/driver/top/confirm";
        String CANCELLATION_LIST = "v1/user/cancellation/list";
        String PROMOCODE_ETA = "v1/user/promocode/queue";
        String PROMOCODE_TRIP = "v1/user/promocode/check";
        String RESCHEDULE_TRIP = "v1/user/reschedule/later/trip";
        String USER_WALLET_HISTORY = "v1/user/wallet/history";
        String USER_CANCELLATION_HISTORY = "v1/user/cancellation/list";
        String USER_BLOCKED = "v1/user/decline";
        String CHECKOUTID = "v1/create/checkout";
        String REQUEST_COMPANY_KEY = "v1/client/request/token";
        String CHECK_COMPANY_KEY_STATUS = "v1/client/token/check";
        String NotificationURL ="v1/user/get/notification";
        String getTypes = "api/v1/types";
        String DRIVER_PROFILE_PIC = "storage/uploads/driver/profile/";
        String VALIDATE_MOBILE = "api/v1/user/validate-mobile";
        String SEND_OTP = "api/v1/user/register/send-otp";
        String SEND_LOGIN_OTP = "api/v1/user/login/send-otp";
        String REGISTER_OTP_VALIDATE = "api/v1/user/register/validate-otp";
        String LOGIN = "api/v1/user/login";
        String USER_REGISTER = "api/v1/user/register";
        String LOGOUT = "api/v1/logout";
    }
    /**
     * Api paramater names
     */
    public interface NetworkParameters {
        String email = "email";
        String type = "type";
        String new_password = "new_password";
        String old_password = "old_password";
        String platitude = "platitude";
        String start_connect = "start_connect";
        String plongitude = "plongitude";
        String dlongitude = "dlongitude";
        String dlatitude = "dlatitude";
        String paymentOpt = "paymentOpt";
        String payment_opt = "payment_opt";
        String dlocation = "dlocation";
        String plocation = "plocation";
        String id = "id";
        String token = "token";
        String message = "message";
        String timezone = "timezone";
        String datetime = "datetime";
        String page = "page";
        String request_id = "request_id";
        String reason = "reason";
        String promo_code = "promo_code";
        String title = "title";
        String description = "description";
        String nickName = "nickName";
        String placeId = "placeId";
        String latitude = "latitude";
        String longitude = "longitude";
        String card_id = "card_id";
        String type_id = "type_id";
        String car_id = "car_id";
        String olat = "olat";
        String olng = "olng";
        String dlat = "dlat";
        String dlng = "dlng";
        String amount = "amount";
        String payment_id = "payment_id";
        String last_number = "last_number";
        String card_type = "card_type";
        String otp_key = "otp_key";
        String firstname = "firstname";
        String lastname = "lastname";
        String phonenumber = "phone_number";
        String mobile = "mobile";
        String CountryDummy = "CountryDummy";
        String profile_pic = "profile_pic";
        String username = "username";
        String android = "android";
        String manual = "manual";
        String Yes = "Yes";
        String facebook = "facebook";
        String google = "google";
        String password = "password";
        String device_token = "device_token";
        String login_by = "login_by";
        String login_method = "login_method";
        String country_code = "country_code";
        String country = "country";
        String IsEnabled = "IsEnabled";
        String social_unique_id = "social_unique_id";
        String favid = "favid";
        String comment = "comment";
        String rating = "rating";
        String timeZone = "time_zone";
        String is_share = "is_share";
        String is_signup = "is_signup";
        String no_of_seats = "no_of_seats";
        String admin_key = "admin_key";
        String payment_type = "payment_type";
        String cancel_other_reason = "cancel_other_reason";
        String phoneNumber = "phoneNumber";
        String driver_id = "driver_id";
        String privateKey = "private_key";
        String complaint_type = "complaint_type";
        String request_type = "request_type";
        String ride_type = "ride_type";
        String vehicle_type = "vehicle_type";
        String user_type = "user_type";
        String promo_booked_id = "promo_booked_id";
        String new_flow = "new_flow";
        String CardHolderName = "card_holder_name";
        String EXP_DATE = "expiry_date";
        String CARD_NUM = "card_number";
        String CVV_NUM = "cvv";
        String TIME_TAKES = "time_takes";
        String url = "url";
        String name = "name";
        String client_id = "client_id";
        String client_token = "client_token";
        String dialcode = "dialcode";
        String company_token = "company_token";
        String gender = "gender";
        String PICK_LAT = "pick_lat";
        String PICK_LNG = "pick_lng";
        String DROP_LAT = "drop_lat";
        String DROP_LNG = "drop_lng";

        String later = "later";
        String cancelled = "cancelled";
        String driver_notes = "driver_notes";
        String disp_country_code = "disp_country_code";
        String disp_phonenumber = "disp_phonenumber";
        String UUId = "uuid";
        String OTP = "otp";
        String Role = "role";
        String Email = "email";
        String TermsCondition = "terms_condition";
        String lat = "lat";
        String lng = "lng";
        String PICK_ADDRESS = "pick_address";
        String DROP_ADDRESS = "drop_address";
        String custom_reason = "custom_reason";
        String arrived = "arrived";
        String fcm_token = "fcm_token";
    }

    public interface TaskId {
        int login = 100;
        int PROFILE_UPDATE = 101;
        int SEND_OTP = 102;
        int LOGOUT = 103;
        int SOS_LIST = 104;
        int COMPLAINTS_LIST = 105;
        int TYPES_LIST = 106;
        int ETA = 107;
    }

}
