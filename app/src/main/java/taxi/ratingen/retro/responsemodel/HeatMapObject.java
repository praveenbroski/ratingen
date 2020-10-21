package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.SerializedName;

public class HeatMapObject {

    @SerializedName("pick_latitude")
    public String pick_latitude;

    @SerializedName("pick_longitude")
    public String pick_longitude;

    @SerializedName("distance")
    public String distance;

    @SerializedName("id")
    public String id;
}
