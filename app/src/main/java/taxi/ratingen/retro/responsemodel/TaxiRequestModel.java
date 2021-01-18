package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import taxi.ratingen.retro.base.BaseResponse;

public class TaxiRequestModel extends BaseResponse implements Serializable {

    @SerializedName("result")
    @Expose
    public Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result implements Serializable {

        @SerializedName("data")
        @Expose
        public ResultData resultData;

    }

    public static class ResultData implements Serializable {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("request_number")
        @Expose
        public String requestNumber;
        @SerializedName("request_otp")
        @Expose
        public Integer requestOtp;
        @SerializedName("is_later")
        @Expose
        public Integer isLater;
        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("trip_start_time")
        @Expose
        public String tripStartTime;
        @SerializedName("arrived_at")
        @Expose
        public String arrivedAt;
        @SerializedName("accepted_at")
        @Expose
        public String acceptedAt;
        @SerializedName("completed_at")
        @Expose
        public String completedAt;
        @SerializedName("is_driver_started")
        @Expose
        public Integer isDriverStarted;
        @SerializedName("is_driver_arrived")
        @Expose
        public Integer isDriverArrived;
        @SerializedName("is_trip_start")
        @Expose
        public Integer isTripStart;
        @SerializedName("total_distance")
        @Expose
        public Double totalDistance;
        @SerializedName("total_time")
        @Expose
        public Integer totalTime;
        @SerializedName("is_completed")
        @Expose
        public Integer isCompleted;
        //        @SerializedName("is_cancelled")
//        @Expose
//        public Integer isCancelled;
        @SerializedName("cancel_method")
        @Expose
        public String cancelMethod;
        @SerializedName("payment_opt")
        @Expose
        public String paymentOpt;
        @SerializedName("is_paid")
        @Expose
        public Integer isPaid;
        @SerializedName("user_rated")
        @Expose
        public Integer userRated;
        @SerializedName("driver_rated")
        @Expose
        public Integer driverRated;
        @SerializedName("unit")
        @Expose
        public String unit;
        @SerializedName("zone_type_id")
        @Expose
        public String zoneTypeId;
        @SerializedName("vehicle_type_name")
        @Expose
        public String vehicleTypeName;
        @SerializedName("pick_lat")
        @Expose
        public Double pickLat;
        @SerializedName("pick_lng")
        @Expose
        public Double pickLng;
        @SerializedName("drop_lat")
        @Expose
        public Double dropLat;
        @SerializedName("drop_lng")
        @Expose
        public Double dropLng;
        @SerializedName("pick_address")
        @Expose
        public String pickAddress;
        @SerializedName("drop_address")
        @Expose
        public String dropAddress;
        @SerializedName("requested_currency_code")
        @Expose
        public String requestedCurrencyCode;

        @SerializedName("driverDetail")
        @Expose
        public DriverDetail driverDetail;

        @SerializedName("userDetail")
        @Expose
        public UserDetail userDetail;

        @SerializedName("requestBill")
        @Expose
        public BillDetail billDetail;

    }

    public static class RequestData implements Serializable {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("request_number")
        @Expose
        public String requestNumber;
        @SerializedName("request_otp")
        @Expose
        public Integer requestOtp;
        @SerializedName("is_later")
        @Expose
        public Integer isLater;
        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("trip_start_time")
        @Expose
        public String tripStartTime;
        @SerializedName("arrived_at")
        @Expose
        public String arrivedAt;
        @SerializedName("accepted_at")
        @Expose
        public String acceptedAt;
        @SerializedName("completed_at")
        @Expose
        public String completedAt;
        @SerializedName("is_driver_started")
        @Expose
        public Integer isDriverStarted;
        @SerializedName("is_driver_arrived")
        @Expose
        public Integer isDriverArrived;
        @SerializedName("is_trip_start")
        @Expose
        public Integer isTripStart;
        @SerializedName("total_distance")
        @Expose
        public Integer totalDistance;
        @SerializedName("total_time")
        @Expose
        public Integer totalTime;
        @SerializedName("is_completed")
        @Expose
        public Integer isCompleted;
        @SerializedName("is_cancelled")
        @Expose
        public Integer isCancelled;
        @SerializedName("cancel_method")
        @Expose
        public String cancelMethod;
        @SerializedName("payment_opt")
        @Expose
        public String paymentOpt;
        @SerializedName("is_paid")
        @Expose
        public Integer isPaid;
        @SerializedName("user_rated")
        @Expose
        public Integer userRated;
        @SerializedName("driver_rated")
        @Expose
        public Integer driverRated;
        @SerializedName("unit")
        @Expose
        public String unit;
        @SerializedName("zone_type_id")
        @Expose
        public String zoneTypeId;
        @SerializedName("vehicle_type_name")
        @Expose
        public String vehicleTypeName;
        @SerializedName("pick_lat")
        @Expose
        public Double pickLat;
        @SerializedName("pick_lng")
        @Expose
        public Double pickLng;
        @SerializedName("drop_lat")
        @Expose
        public Double dropLat;
        @SerializedName("drop_lng")
        @Expose
        public Double dropLng;
        @SerializedName("pick_address")
        @Expose
        public String pickAddress;
        @SerializedName("drop_address")
        @Expose
        public String dropAddress;
        @SerializedName("requested_currency_code")
        @Expose
        public String requestedCurrencyCode;
        @SerializedName("driverDetail")
        @Expose
        public DriverDetail driverDetail;

        public LatLng getPickupLatLng() {
            return new LatLng(pickLat, pickLng);
        }

        public LatLng getDropLatLng() {
            return new LatLng(dropLat, dropLng);
        }
    }

    public static class DriverDetail implements Serializable {

        @SerializedName("data")
        @Expose
        public DriverData driverData;

    }

    public static class DriverData implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("profile_picture")
        @Expose
        public String profilePicture;
        @SerializedName("active")
        @Expose
        public Boolean active;
        @SerializedName("approve")
        @Expose
        public Boolean approve;
        @SerializedName("available")
        @Expose
        public Boolean available;
        @SerializedName("uploaded_document")
        @Expose
        public Boolean uploadedDocument;
        @SerializedName("service_location_id")
        @Expose
        public String serviceLocationId;
        @SerializedName("vehicle_type_id")
        @Expose
        public String vehicleTypeId;
        @SerializedName("vehicle_type_name")
        @Expose
        public String vehicleTypeName;
        @SerializedName("car_make")
        @Expose
        public String carMake;
        @SerializedName("car_model")
        @Expose
        public String carModel;
        @SerializedName("car_make_name")
        @Expose
        public String carMakeName;
        @SerializedName("car_model_name")
        @Expose
        public String carModelName;
        @SerializedName("car_color")
        @Expose
        public String carColor;
        @SerializedName("car_number")
        @Expose
        public String carNumber;
        @SerializedName("rating")
        @Expose
        public Float rating;
        @SerializedName("no_of_ratings")
        @Expose
        public Integer noOfRatings;
        @SerializedName("latitude")
        @Expose
        public Double latitude;
        @SerializedName("longitude")
        @Expose
        public Double longitude;
        @SerializedName("currency_symbol")
        @Expose
        public String currency_symbol;

    }

    public static class UserDetail implements Serializable {

        @SerializedName("data")
        @Expose
        public UserData userData;

    }

    public static class UserData implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("last_name")
        @Expose
        public Object lastName;
        @SerializedName("username")
        @Expose
        public Object username;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("profile_picture")
        @Expose
        public String profilePicture;
        @SerializedName("active")
        @Expose
        public Integer active;
        @SerializedName("email_confirmed")
        @Expose
        public Integer emailConfirmed;
        @SerializedName("mobile_confirmed")
        @Expose
        public Integer mobileConfirmed;
        @SerializedName("last_known_ip")
        @Expose
        public String lastKnownIp;
        @SerializedName("last_login_at")
        @Expose
        public String lastLoginAt;
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("no_of_ratings")
        @Expose
        public Object noOfRatings;
        @SerializedName("onTripRequest")
        @Expose
        public Object onTripRequest;
        @SerializedName("metaRequest")
        @Expose
        public MetaRequest metaRequest;

    }

    public static class BillDetail implements Serializable {

        @SerializedName("data")
        @Expose
        public BillData billData;

    }

    public static class BillData implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("base_price")
        @Expose
        public Double basePrice;
        @SerializedName("base_distance")
        @Expose
        public Integer baseDistance;
        @SerializedName("price_per_distance")
        @Expose
        public Double pricePerDistance;
        @SerializedName("distance_price")
        @Expose
        public Double distancePrice;
        @SerializedName("price_per_time")
        @Expose
        public Double pricePerTime;
        @SerializedName("time_price")
        @Expose
        public Double timePrice;
        @SerializedName("waiting_charge")
        @Expose
        public Double waitingCharge;
        @SerializedName("cancellation_fee")
        @Expose
        public Double cancellationFee;
        @SerializedName("service_tax")
        @Expose
        public Double serviceTax;
        @SerializedName("service_tax_percentage")
        @Expose
        public Integer serviceTaxPercentage;
        @SerializedName("promo_discount")
        @Expose
        public Double promoDiscount;
        @SerializedName("admin_commision")
        @Expose
        public Double adminCommision;
        @SerializedName("driver_commision")
        @Expose
        public Double driverCommision;
        @SerializedName("total_amount")
        @Expose
        public Double totalAmount;
        @SerializedName("requested_currency_code")
        @Expose
        public String requestedCurrencyCode;
        @SerializedName("requested_currency_symbol")
        @Expose
        public String getRequestedCurrencySymbol;
        @SerializedName("admin_commision_with_tax")
        @Expose
        public Double adminCommisionWithTax;
        @SerializedName("referral_amount")
        @Expose
        public Double referral_amount;

    }

    public static class MetaRequest implements Serializable, Parcelable {

        @SerializedName("data")
        @Expose
        public RequestData requestData;

        protected MetaRequest(Parcel in) {
        }

        public static final Creator<MetaRequest> CREATOR = new Creator<MetaRequest>() {
            @Override
            public MetaRequest createFromParcel(Parcel in) {
                return new MetaRequest(in);
            }

            @Override
            public MetaRequest[] newArray(int size) {
                return new MetaRequest[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

    }

}
