package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DriverData implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    public int id;
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
    public String profile_picture;
    @SerializedName("vehicle_type_id")
    @Expose
    public String vehicle_type_id;
    @SerializedName("vehicle_type_name")
    @Expose
    public String vehicle_type_name;
    @SerializedName("car_make")
    @Expose
    public String car_make;
    @SerializedName("car_model")
    @Expose
    public String car_model;
    @SerializedName("car_make_name")
    @Expose
    public String car_make_name;
    @SerializedName("car_model_name")
    @Expose
    public String car_model_name;
    @SerializedName("car_color")
    @Expose
    public String car_color;
    @SerializedName("car_number")
    @Expose
    public String car_number;
    @SerializedName("rating")
    @Expose
    public double rating;
    @SerializedName("no_of_ratings")
    @Expose
    public int no_of_ratings;

    public DriverData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        mobile = in.readString();
        profile_picture = in.readString();
        vehicle_type_id = in.readString();
        vehicle_type_name = in.readString();
        car_make = in.readString();
        car_model = in.readString();
        car_make_name = in.readString();
        car_model_name = in.readString();
        car_color = in.readString();
        car_number = in.readString();
        rating = in.readDouble();
        no_of_ratings = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(profile_picture);
        dest.writeString(vehicle_type_id);
        dest.writeString(vehicle_type_name);
        dest.writeString(car_make);
        dest.writeString(car_model);
        dest.writeString(car_make_name);
        dest.writeString(car_model_name);
        dest.writeString(car_color);
        dest.writeString(car_number);
        dest.writeDouble(rating);
        dest.writeInt(no_of_ratings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverData> CREATOR = new Creator<DriverData>() {
        @Override
        public DriverData createFromParcel(Parcel in) {
            return new DriverData(in);
        }

        @Override
        public DriverData[] newArray(int size) {
            return new DriverData[size];
        }
    };

}
