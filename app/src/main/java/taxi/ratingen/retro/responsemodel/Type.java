package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

import java.util.List;

public class Type extends BaseResponse implements Parcelable {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("zone_id")
    @Expose
    public Integer id;
    @SerializedName("type_id")
    @Expose
    public String type_id;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("payment_type")
    @Expose
    public List<String> paymenttype;
    @SerializedName("drivers")
    public List<Car> drivers = null;
    @SerializedName("preferred_payment")

    public Integer preferred_payment;

    public Boolean isselected;
    public String IsFrom;
    public String dateform;
    private LatLng droplatlng;
    private LatLng picklatlng;
    private String dropAddress;
    private String pickAddress;
    private String scanContent;
    public String etaPrice = "NA";
    public String etaTime = "NA";

    protected Type(Parcel in) {
        name = in.readString();
        duration = in.readString();
        paymenttype = in.createStringArrayList();
        dateform = in.readString();
        droplatlng = in.readParcelable(LatLng.class.getClassLoader());
        picklatlng = in.readParcelable(LatLng.class.getClassLoader());
        dropAddress = in.readString();
        pickAddress = in.readString();
        scanContent = in.readString();
        IsFrom = in.readString();
    }

    public static final Creator<Type> CREATOR = new Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel in) {
            return new Type(in);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

    public void setdate(String format) {
        dateform = format;
    }

    public void setIsFrom(String isFrom) {
        IsFrom = isFrom;
    }

    public String getIsFrom() {
        return IsFrom;
    }

    public void setDroplatlng(LatLng droplatlng) {
        this.droplatlng = droplatlng;
    }

    public void setpicklatlng(LatLng picklatlng) {
        this.picklatlng = picklatlng;
    }

    public String getDateform() {
        return dateform;
    }

    public LatLng getPicklatlng() {
        return picklatlng;
    }

    public LatLng getDroplatlng() {
        return droplatlng;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public void setpickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public void setScanContent(String scanContent) {
        this.scanContent = scanContent;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public String getScanContent() {
        return scanContent;
    }


    public List<String> getPaymenttype() {
        return paymenttype;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(duration);
        parcel.writeStringList(paymenttype);
        parcel.writeString(dateform);
        parcel.writeParcelable(droplatlng, i);
        parcel.writeParcelable(picklatlng, i);
        parcel.writeString(dropAddress);
        parcel.writeString(pickAddress);
        parcel.writeString(IsFrom);
    }


}