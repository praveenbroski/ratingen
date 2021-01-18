package taxi.ratingen.retro.base;

import android.text.TextUtils;

import taxi.ratingen.retro.responsemodel.ClientObject;
import taxi.ratingen.retro.responsemodel.CountryListModel;
import taxi.ratingen.retro.responsemodel.Result;
import taxi.ratingen.retro.responsemodel.TripRegisteredDetails;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.ui.topdriver.TopDriverModel;
import taxi.ratingen.ui.wallethistory.CancelledListModel;
import taxi.ratingen.ui.wallethistory.WalletHistModel;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import taxi.ratingen.retro.responsemodel.Car;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.retro.responsemodel.FAQModel;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.retro.responsemodel.UserSo;
import taxi.ratingen.utilz.SharedPrefence;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 9/27/17.
 */

/**
 * contains commonly used API response parameters
 **/

public class BaseResponse implements Serializable {


    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;

    @SerializedName("request_id")
    @Expose
    public String request_id;

    @SerializedName("status_code")
    @Expose
    public int statusCode;

    @SerializedName("token_type")
    @Expose
    public String tokenType;

    @SerializedName("expires_in")
    @Expose
    public Integer expiresIn;

    @SerializedName("access_token")
    @Expose
    public String accessToken;

    @SerializedName("error_message")
    @Expose
    public String errorMessage;
    @SerializedName("success_message")
    @Expose
    public String successMessage;
    @SerializedName("custom_select_driver_fee")
    @Expose
    public double DriverAddCharges;

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("corporate")
    @Expose
    public int isCorporate = 0;
    @SerializedName("user")
    public User user;
    @SerializedName("payment")
    @Expose
    public List<Payment> payment = null;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("expirySoon")
    @Expose
    public String expirySoon;
    @SerializedName("client_token")
    @Expose
    public String client_token;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("earned")
    @Expose
    public String earned;
    @SerializedName("spent")
    @Expose
    public String spent;
    @SerializedName("balance")
    @Expose
    public String balance;
    @SerializedName("amount_added")
    @Expose
    public String amount_added;
    @SerializedName("amount_balance")
    @Expose
    public String amount_balance;
    @SerializedName("amount_spent")
    @Expose
    public String amount_spent;
    @SerializedName("favid")
    @Expose
    public Integer favid;

    @SerializedName("promo_code_queue")
    @Expose
    public PromoCodeQueue promoCodeQueue;

    @SerializedName("type_name")
    @Expose
    public String type_name;
    @SerializedName("types")
    @Expose
    public List<Type> types = null;

    @SerializedName("type_list")
    @Expose
    public List<TypeNew> typeList = null;

    @SerializedName("distancee")
    @Expose
    public Double distancee = null;
    @SerializedName("ride_fare")
    @Expose
    public Float ride_fare = null;

    @SerializedName("drop_out_of_zone_fee")
    @Expose
    public String drop_out_of_zone_fee;


    @SerializedName("tax_amount")
    @Expose
    public Float tax_amount = null;

    @SerializedName("waiting_charge")
    @Expose
    public Float waiting_charge = null;

    @SerializedName("base_price")
    @Expose
    public Float baseprice = null;
    @SerializedName("price_per_distance")
    @Expose
    public Float price_per_distance = null;
    @SerializedName("distance_price")
    @Expose
    public Float distance_price = null;
    @SerializedName("time_price")
    @Expose
    public Float time_price = null;
    @Expose
    public String unit_in_words;
    @SerializedName("price_per_time")
    @Expose
    public Float price_per_time = null;
    @SerializedName("currency")
    @Expose
    public String currency = null;
    @SerializedName("is_accept_share_ride")
    @Expose
    public int is_accept_share_ride = 0;
    @SerializedName("driver_arival_estimation")
    @Expose
    public String driver_arival_estimation = "";
    @SerializedName("share_ride_details")
    @Expose
    public ShareRideDetails share_ride_details = null;

    @SerializedName("base_share_ride_price")
    @Expose
    public double share_ride_price = 0;

    @SerializedName("driver_current_zone")
    @Expose
    public int driver_current_zone;

    @SerializedName("driver_type_id")
    @Expose
    public int driver_type_id;

    @SerializedName("is_private_key_trip")
    @Expose
    public boolean is_private_key_trip;

    @SerializedName("show_price")
    @Expose
    public int show_price;


    @SerializedName("wallet")
    @Expose
    private WalletHistModel wallet;

    @SerializedName("total")
    @Expose
    public Float total = null;
    @SerializedName("approximate_value")
    public Integer approximate_value;
    @SerializedName("min_amount")
    public Double min_amount;
    @SerializedName("max_amount")
    public Double max_amount;
    @SerializedName("lat")
    @Expose
    public Float lat = null;
    @SerializedName("lng")
    @Expose
    public Float lng = null;
    @SerializedName("bearing")
    @Expose
    public Float bearing = null;
    @SerializedName("trip_start")
    @Expose
    public Integer trip_start = null;
    @SerializedName("waiting_time")
    public String waiting_time = "0.0";
    @SerializedName("favplace")
    @Expose
    public List<Favplace> favplace = null;
    @SerializedName("cars")
    @Expose
    public List<Car> cars = null;
    @SerializedName("sos")
    @Expose
    public List<So> sos = null;
    @SerializedName("user_sos")
    @Expose
    public List<UserSo> userSos = null;
    @SerializedName("complaint_list")
    @Expose
    public List<ComplaintList> complaintList = null;
    @Expose
    public List<FAQModel> faq_list;
    @Expose
    public String admin_key;

    @SerializedName("request")
    @Expose
    public Request request;

//    @Expose
//    @SerializedName("data")
//    public DataObject data;

    @Expose
    public Object data;

    @Expose
    public Result result;

    @SerializedName("history")
    @Expose
    public List<taxi.ratingen.retro.responsemodel.history> history;


    @SerializedName("cancelled_trip_list")
    @Expose
    public List<CancelledListModel> cancelledTripList = null;

    @SerializedName("promo_available")
    @Expose
    public int promo_available;

    @SerializedName("promo_min_amount")
    public Double promo_min_amount;
    @SerializedName("promo_max_amount")
    public Double promo_max_amount;

    @SerializedName("total_after_promo")
    public Double total_after_promo;

    @SerializedName("exist_user")
    public boolean exist_user;

    @SerializedName("show_cancellation_reason")
    public boolean show_cancellation_reason;

    @SerializedName("notification_list")
    @Expose
    private List<NotificationList> notificationList = null;

    @SerializedName("cancelled_dashboard")
    @Expose
    private CancelledDashboard cancelledDashboard;

    @SerializedName("trip_registered_details")
    @Expose
    private TripRegisteredDetails tripRegisteredDetails;

    public WalletHistModel getWallet() {
        return wallet;
    }

    public void setWallet(WalletHistModel wallet) {
        this.wallet = wallet;
    }


    @SerializedName("Topdrivers")
    @Expose
    private List<TopDriverModel> Topdrivers = null;

    public List<Car> updatedcar;

    public List<Car> getCars() {
        return cars;
    }

    public List<So> getSos() {
        return sos;
    }

    @Expose
    public List<ReasonCancel> reason;
    @Expose
    public ClientObject client;

    @SerializedName("users")
    @Expose
    public List<String> users = null;
    @SerializedName("driver")
    public Car driver = null;

    @Expose
    @SerializedName("default_selected_type")
    public int default_selected_type;

    @SerializedName("notification")
    @Expose
    public List<CountryListModel> countryListModel;

    public ArrayList<CountryListModel> getCountryList() {
        if (countryListModel == null)
            return null;
        ArrayList<CountryListModel> list = new ArrayList<>();
        list.addAll(countryListModel);
        return list;
    }

    public List<NotificationList> getNotificationList() {
        return notificationList;
    }

    public static class ReasonCancel {
        @Expose
        public String id;
        @Expose
        public String reason;

        public ReasonCancel(String s, String others) {
            id = s;
            reason = others;
        }
    }

    public void setUpdatedcar(List<Car> updatedcar) {

        this.updatedcar = updatedcar;
    }

    public CancelledDashboard getCancelledDashboard() {
        return cancelledDashboard;
    }

    public List<taxi.ratingen.retro.responsemodel.history> getHistory() {
        return history;
    }

    public List<ComplaintList> getComplaintList() {
        return complaintList;
    }

    public List<UserSo> getUserSos() {
        return userSos;
    }

    public List<Car> getUpdatedcar() {
        return updatedcar;
    }

    public User getUser() {
        return user;
    }

    public List<Payment> getPayment() {
        return payment;
    }

    public List<Type> getTypes() {
        return types;
    }

    public List<TypeNew> getNewTypes() {
        return typeList;
    }

    public List<Favplace> getFavplace() {
        return favplace;
    }

    public void setFavplace(List<Favplace> favplace) {
        this.favplace = favplace;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public List<TopDriverModel> getDrivers() {
        return Topdrivers;
    }

    public void setDrivers(List<TopDriverModel> drivers) {
        this.Topdrivers = drivers;
    }

    public TripRegisteredDetails getTripRegisteredDetails() {
        return tripRegisteredDetails;
    }

    public static class OptionsDeserilizer implements JsonDeserializer<BaseResponse> {
        @Override
        public BaseResponse deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BaseResponse options = new Gson().fromJson(json, BaseResponse.class);
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("request")) {
                JsonElement elem = jsonObject.get("request");
                if (elem != null && !elem.isJsonNull()) {
                    String valuesString = elem.getAsJsonObject().toString();
                    if (!TextUtils.isEmpty(valuesString)) {
                        Request values = new Gson().fromJson(valuesString, new TypeToken<Request>() {
                        }.getType());
                        options.setRequest(values);
                    }
                }
            }


            return options;
        }

      /*  @Override
        public Offer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BaseResponse options = new Gson().fromJson(json, Options.class);
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("option_value")) {
                JsonElement elem = jsonObject.get("option_value");
                if (elem != null && !elem.isJsonNull()) {
                    String valuesString = elem.getAsString();
                    if (!TextUtils.isEmpty(valuesString)){
                        List<OptionValue> values = new Gson().fromJson(valuesString, new TypeToken<ArrayList<OptionValue>>() {}.getType());
                        options.setOptionValues(values);
                    }
                }
            }
            return options ;
        }*/
    }


    public class NotificationList {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("sub_title")
        @Expose
        private String subTitle;
        @SerializedName("has_redirect_url")
        @Expose
        private Integer hasRedirectUrl;
        @SerializedName("redirect_url")
        @Expose
        private String redirectUrl;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("is_read")
        @Expose
        private Integer isRead;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public Integer getHasRedirectUrl() {
            return hasRedirectUrl;
        }

        public void setHasRedirectUrl(Integer hasRedirectUrl) {
            this.hasRedirectUrl = hasRedirectUrl;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Integer getIsRead() {
            return isRead;
        }

        public void setIsRead(Integer isRead) {
            this.isRead = isRead;
        }

        public String getDate() {
            return date;
        }
    }

    @SerializedName("errors")
    Map<String, List<String>> errors;

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public class DataObject {
        @Expose
        public TranslationModel en;
        @Expose
        public TranslationModel es;
        @Expose
        public TranslationModel fr;
        @Expose
        public TranslationModel ar;
        @Expose
        public TranslationModel ja;
        @Expose
        public TranslationModel ko;
        @Expose
        public TranslationModel pt;
        @Expose
        public TranslationModel zh;

    }

    public void saveLanguageTranslations(SharedPrefence sharedPrefence, Gson gson, DataObject dataObject) {
        JSONObject map = null;
        List<String> languages = new ArrayList<>();
        try {
            map = new JSONObject(gson.toJson(dataObject));
            Iterator<String> iterator = map.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                sharedPrefence.savevalue(key, map.get(key).toString());
                languages.add(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPrefence.savevalue(SharedPrefence.LANGUAGES, gson.toJson(languages) + "");
    }
}
