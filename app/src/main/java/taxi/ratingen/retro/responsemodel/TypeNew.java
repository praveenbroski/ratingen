package taxi.ratingen.retro.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class TypeNew extends BaseResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type_id")
    @Expose
    private String typeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("preferred_payment")
    @Expose
    private Integer preferredPayment;
    @SerializedName("unit")
    @Expose
    private Integer unit;
    @SerializedName("payment_type")
    @Expose
    private List<String> paymentType = null;
    @SerializedName("zoneTypePrice")
    @Expose
    private ZoneTypePrice zoneTypePrice;
    private final static long serialVersionUID = 1439292603652012899L;

    public String getZoneId() {
        return id;
    }

    public void setZoneId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getIsAcceptShareRide() {
        return is_accept_share_ride;
    }

    public void setIsAcceptShareRide(Integer isAcceptShareRide) {
        this.is_accept_share_ride = isAcceptShareRide;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getPreferredPayment() {
        return preferredPayment;
    }

    public void setPreferredPayment(Integer preferredPayment) {
        this.preferredPayment = preferredPayment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public String getUnitInWords() {
        return unit_in_words;
    }

    public void setUnitInWords(String unitInWords) {
        this.unit_in_words = unitInWords;
    }

    public List<String> getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(List<String> paymentType) {
        this.paymentType = paymentType;
    }

    public ZoneTypePrice getZoneTypePrice() {
        return zoneTypePrice;
    }

    public void setZoneTypePrice(ZoneTypePrice zoneTypePrice) {
        this.zoneTypePrice = zoneTypePrice;
    }

    public static class ZoneTypePrice implements Serializable {
        @SerializedName("data")
        @Expose
        private List<TypePrice> data = null;
        private final static long serialVersionUID = -7562449826741856307L;

        public List<TypePrice> getData() {
            return data;
        }

        public void setData(List<TypePrice> data) {
            this.data = data;
        }

    }

    public static class  TypePrice implements Serializable {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("type_id")
        @Expose
        private Object typeId;
        @SerializedName("price_type")
        @Expose
        private Integer priceType;
        @SerializedName("admin_service_fee_type")
        @Expose
        private Integer adminServiceFeeType;
        @SerializedName("admin_service_fee")
        @Expose
        private Integer adminServiceFee;
        @SerializedName("base_price")
        @Expose
        private Integer basePrice;
        @SerializedName("price_per_distance")
        @Expose
        private Integer pricePerDistance;
        @SerializedName("base_distance")
        @Expose
        private Integer baseDistance;
        @SerializedName("price_per_time")
        @Expose
        private Integer pricePerTime;
        @SerializedName("waiting_charge")
        @Expose
        private Integer waitingCharge;
        @SerializedName("cancellation_fee")
        @Expose
        private Integer cancellationFee;
        @SerializedName("custom_select_driver_fee")
        @Expose
        private String customSelectDriverFee;
        @SerializedName("drop_out_of_zone_fee")
        @Expose
        private Integer dropOutOfZoneFee;
        @SerializedName("free_waiting_time")
        @Expose
        private Integer freeWaitingTime;
        @SerializedName("driver_saving_percentage")
        @Expose
        private String driverSavingPercentage;
        private final static long serialVersionUID = 4682506625754315667L;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getTypeId() {
            return typeId;
        }

        public void setTypeId(Object typeId) {
            this.typeId = typeId;
        }

        public Integer getPriceType() {
            return priceType;
        }

        public void setPriceType(Integer priceType) {
            this.priceType = priceType;
        }

        public Integer getAdminServiceFeeType() {
            return adminServiceFeeType;
        }

        public void setAdminServiceFeeType(Integer adminServiceFeeType) {
            this.adminServiceFeeType = adminServiceFeeType;
        }

        public Integer getAdminServiceFee() {
            return adminServiceFee;
        }

        public void setAdminServiceFee(Integer adminServiceFee) {
            this.adminServiceFee = adminServiceFee;
        }

        public Integer getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(Integer basePrice) {
            this.basePrice = basePrice;
        }

        public Integer getPricePerDistance() {
            return pricePerDistance;
        }

        public void setPricePerDistance(Integer pricePerDistance) {
            this.pricePerDistance = pricePerDistance;
        }

        public Integer getBaseDistance() {
            return baseDistance;
        }

        public void setBaseDistance(Integer baseDistance) {
            this.baseDistance = baseDistance;
        }

        public Integer getPricePerTime() {
            return pricePerTime;
        }

        public void setPricePerTime(Integer pricePerTime) {
            this.pricePerTime = pricePerTime;
        }

        public Integer getWaitingCharge() {
            return waitingCharge;
        }

        public void setWaitingCharge(Integer waitingCharge) {
            this.waitingCharge = waitingCharge;
        }

        public Integer getCancellationFee() {
            return cancellationFee;
        }

        public void setCancellationFee(Integer cancellationFee) {
            this.cancellationFee = cancellationFee;
        }

        public String getCustomSelectDriverFee() {
            return customSelectDriverFee;
        }

        public void setCustomSelectDriverFee(String customSelectDriverFee) {
            this.customSelectDriverFee = customSelectDriverFee;
        }

        public Integer getDropOutOfZoneFee() {
            return dropOutOfZoneFee;
        }

        public void setDropOutOfZoneFee(Integer dropOutOfZoneFee) {
            this.dropOutOfZoneFee = dropOutOfZoneFee;
        }

        public Integer getFreeWaitingTime() {
            return freeWaitingTime;
        }

        public void setFreeWaitingTime(Integer freeWaitingTime) {
            this.freeWaitingTime = freeWaitingTime;
        }

        public String getDriverSavingPercentage() {
            return driverSavingPercentage;
        }

        public void setDriverSavingPercentage(String driverSavingPercentage) {
            this.driverSavingPercentage = driverSavingPercentage;
        }

    }

    public Boolean isselected;
    public String etaPrice = "NA";
    public String etaTime = "NA";

    public void setIsSelected(Boolean isSelected) {
        this.isselected = isSelected;
    }

    public Boolean getIsSelected() {
        return isselected;
    }

    public void setEtaPrice(String etaPrice) {
        this.etaPrice = etaPrice;
    }

    public String getEtaPrice() {
        return etaPrice;
    }

    public void setEtaTime(String etaTime) {
        this.etaTime = etaTime;
    }

    public String getEtaTime() {
        return etaTime;
    }

}
