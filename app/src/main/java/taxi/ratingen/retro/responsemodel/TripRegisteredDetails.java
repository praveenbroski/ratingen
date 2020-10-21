package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripRegisteredDetails {
    @SerializedName("trip_registered_at")
    @Expose
    private String tripRegisteredAt;
    @SerializedName("trip_period_start_at")
    @Expose
    private String tripPeriodStartAt;
    @SerializedName("trip_period_end_at")
    @Expose
    private String tripPeriodEndAt;

    public String getTripRegisteredAt() {
        return tripRegisteredAt;
    }

    public void setTripRegisteredAt(String tripRegisteredAt) {
        this.tripRegisteredAt = tripRegisteredAt;
    }

    public String getTripPeriodStartAt() {
        return tripPeriodStartAt;
    }

    public void setTripPeriodStartAt(String tripPeriodStartAt) {
        this.tripPeriodStartAt = tripPeriodStartAt;
    }

    public String getTripPeriodEndAt() {
        return tripPeriodEndAt;
    }

    public void setTripPeriodEndAt(String tripPeriodEndAt) {
        this.tripPeriodEndAt = tripPeriodEndAt;
    }
}
