package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
public class MarkerModel implements Serializable {

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("lng")
    @Expose
    private double lng;

    @SerializedName("rotation")
    @Expose
    private float rotation;

    @SerializedName("id")
    @Expose
    private String id;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public float getRotation() {
        return rotation;
    }

    public String getId() {
        return id;
    }

}