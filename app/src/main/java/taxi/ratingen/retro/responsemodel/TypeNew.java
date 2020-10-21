package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class TypeNew extends BaseResponse implements Parcelable {

    @SerializedName("id")
    @Expose
    public Integer zoneId;

    @SerializedName("type_id")
    @Expose
    public Integer typeId;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("payment_type")
    @Expose
    public List<String> paymentType;

    @SerializedName("preferred_payment")
    @Expose
    public Integer preferredPayment;

    @SerializedName("icon")
    @Expose
    public String icon;

    @SerializedName("capacity")
    @Expose
    public Integer capacity;

//    @SerializedName("is_accept_share_ride")
//    @Expose
//    private Boolean isAcceptShareRide;

    @SerializedName("active")
    @Expose
    public Integer active;

    @SerializedName("unit")
    @Expose
    public Integer unit;

    @SerializedName("type_price")
    public List<TypePrice> typePrices;

    public class TypePrice implements Serializable {

        @SerializedName("id")
        public Integer id;

        @SerializedName("zone_type_id")
        public Integer zoneTypeId;

        @SerializedName("price_type")
        public Integer priceType;

        @SerializedName("admin_service_fee_type")
        public Integer adminServiceFeeType;

        @SerializedName("admin_service_fee")
        public Integer adminServiceFee;

        @SerializedName("base_price")
        public Integer basePrice;

        @SerializedName("base_distance")
        public Integer baseDistance;

        @SerializedName("price_per_distance")
        public double pricePerDistance;

        @SerializedName("price_per_time")
        public Integer pricePerTime;

        @SerializedName("waiting_charge")
        public Integer waitingCharge;

        @SerializedName("cancellation_fee")
        public String cancellationFee;

        @SerializedName("custom_select_driver_fee")
        public String customSelectDriverFee;

        @SerializedName("drop_out_of_zone_fee")
        public Integer dropOutOfZoneFee;

        @SerializedName("created_at")
        public String createdAt;

        @SerializedName("updated_at")
        public String updatedAt;

        @SerializedName("deleted_at")
        public String deletedAt;

        @SerializedName("free_waiting_time")
        public Integer freeWaitingTime;

        @SerializedName("driver_saving_percentage")
        public String driverSavingPercentage;

    }

    public Boolean isselected;
    public String etaPrice = "NA";
    public String etaTime = "NA";

    protected TypeNew(Parcel in) {
        zoneId = in.readInt();
        typeId = in.readInt();
        name = in.readString();
        paymentType = in.createStringArrayList();
        preferredPayment = in.readInt();
        icon = in.readString();
        capacity = in.readInt();
//        isAcceptShareRide = in.readBoolean();
        active = in.readInt();
        unit = in.readInt();
    }

    public static final Creator<TypeNew> CREATOR = new Creator<TypeNew>() {
        @Override
        public TypeNew createFromParcel(Parcel in) {
            return new TypeNew(in);
        }

        @Override
        public TypeNew[] newArray(int size) {
            return new TypeNew[size];
        }
    };

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getPaymentType() {
        return paymentType;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

//    public boolean getIsAcceptShareRide() {
//        return isAcceptShareRide;
//    }

    public Integer getIsActive() {
        return active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(zoneId);
        parcel.writeInt(typeId);
        parcel.writeString(name);
        parcel.writeStringList(paymentType);
        parcel.writeInt(preferredPayment);
        parcel.writeString(icon);
        parcel.writeInt(capacity);
//        parcel.writeBoolean(isAcceptShareRide);
        parcel.writeInt(active);
        parcel.writeInt(unit);
    }

}
