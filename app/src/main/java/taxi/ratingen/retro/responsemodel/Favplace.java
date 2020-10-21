package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

public class Favplace extends BaseResponse {

    @SerializedName("placeId")
    @Expose
    public String placeId;
    @SerializedName("id")
    @Expose
    public Integer Favid;
    @SerializedName("nickName")
    @Expose
    public String nickName;
    @SerializedName("latitude")
    @Expose
    public Float latitude;
    @SerializedName("longitude")
    @Expose
    public Float longitude;

    public boolean IsFavTit;
    public boolean IsPlaceLayout;
    public String PlaceApiOGaddress;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setFavid(Integer favid) {
        Favid = favid;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}