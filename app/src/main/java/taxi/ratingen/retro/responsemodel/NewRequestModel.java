package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import taxi.ratingen.retro.base.BaseResponse;

public class NewRequestModel extends BaseResponse implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("request_number")
    @Expose
    public String request_number;
    @SerializedName("request_otp")
    @Expose
    public int request_otp;
    @SerializedName("is_later")
    @Expose
    public Integer is_later;
    @SerializedName("user_id")
    @Expose
    public int user_id;
    @SerializedName("trip_start_time")
    @Expose
    public String trip_start_time;
    @SerializedName("arrived_at")
    @Expose
    public String arrived_at;
    @SerializedName("accepted_at")
    @Expose
    public String accepted_at;
    @SerializedName("completed_at")
    @Expose
    public String completed_at;
    @SerializedName("is_driver_started")
    @Expose
    public int is_driver_started;
    @SerializedName("is_driver_arrived")
    @Expose
    public int is_driver_arrived;
    @SerializedName("is_trip_start")
    @Expose
    public int is_trip_start;
    @SerializedName("total_distance")
    @Expose
    public double total_distance;
    @SerializedName("total_time")
    @Expose
    public String total_time;
    @SerializedName("is_completed")
    @Expose
    public int is_completed;
    @SerializedName("is_cancelled")
    @Expose
    public int is_cancelled;
    @SerializedName("cancel_method")
    @Expose
    public int cancel_method;
    @SerializedName("payment_opt")
    @Expose
    public String payment_opt;
    @SerializedName("is_paid")
    @Expose
    public int is_paid;
    @SerializedName("user_rated")
    @Expose
    public int user_rated;
    @SerializedName("driver_rated")
    @Expose
    public int driver_rated;
    @SerializedName("unit")
    @Expose
    public String unit;
    @SerializedName("zone_type_id")
    @Expose
    public String zone_type_id;
    @SerializedName("vehicle_type_name")
    @Expose
    public String vehicle_type_name;
    @SerializedName("pick_lat")
    @Expose
    public double pick_lat;
    @SerializedName("pick_lng")
    @Expose
    public double pick_lng;
    @SerializedName("drop_lat")
    @Expose
    public double drop_lat;
    @SerializedName("drop_lng")
    @Expose
    public double drop_lng;
    @SerializedName("pick_address")
    @Expose
    public String pick_address;
    @SerializedName("drop_address")
    @Expose
    public String drop_address;
    @SerializedName("requested_currency_code")
    @Expose
    public String requested_currency_code;
    @SerializedName("driverDetail")
    @Expose
    public DriverDetail driverDetail;

    public NewRequestModel(Parcel in) {
        id = in.readString();
        request_number = in.readString();
        request_otp = in.readInt();
        is_later = in.readInt();
        user_id = in.readInt();
        trip_start_time = in.readString();
        arrived_at = in.readString();
        accepted_at = in.readString();
        completed_at = in.readString();
        is_driver_started = in.readInt();
        is_driver_arrived = in.readInt();
        is_trip_start = in.readInt();
        total_distance = in.readDouble();
        total_time = in.readString();
        is_completed = in.readInt();
        is_cancelled = in.readInt();
        cancel_method = in.readInt();
        payment_opt = in.readString();
        is_paid = in.readInt();
        user_rated = in.readInt();
        driver_rated = in.readInt();
        unit = in.readString();
        zone_type_id = in.readString();
        vehicle_type_name = in.readString();
        pick_lat = in.readDouble();
        pick_lng = in.readDouble();
        drop_lat = in.readDouble();
        drop_lng = in.readDouble();
        pick_address = in.readString();
        drop_address = in.readString();
        requested_currency_code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(request_number);
        dest.writeInt(request_otp);
        dest.writeInt(is_later);
        dest.writeInt(user_id);
        dest.writeString(trip_start_time);
        dest.writeString(arrived_at);
        dest.writeString(accepted_at);
        dest.writeString(completed_at);
        dest.writeInt(is_driver_started);
        dest.writeInt(is_driver_arrived);
        dest.writeInt(is_trip_start);
        dest.writeDouble(total_distance);
        dest.writeString(total_time);
        dest.writeInt(is_completed);
        dest.writeInt(is_cancelled);
        dest.writeInt(cancel_method);
        dest.writeString(payment_opt);
        dest.writeInt(is_paid);
        dest.writeInt(user_rated);
        dest.writeInt(driver_rated);
        dest.writeString(unit);
        dest.writeString(zone_type_id);
        dest.writeString(vehicle_type_name);
        dest.writeDouble(pick_lat);
        dest.writeDouble(pick_lng);
        dest.writeDouble(drop_lat);
        dest.writeDouble(drop_lng);
        dest.writeString(pick_address);
        dest.writeString(drop_address);
        dest.writeString(requested_currency_code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewRequestModel> CREATOR = new Creator<NewRequestModel>() {
        @Override
        public NewRequestModel createFromParcel(Parcel in) {
            return new NewRequestModel(in);
        }

        @Override
        public NewRequestModel[] newArray(int size) {
            return new NewRequestModel[size];
        }
    };

    public class DriverDetail {

        @SerializedName("data")
        @Expose
        public DriverData userData;

    }

}
