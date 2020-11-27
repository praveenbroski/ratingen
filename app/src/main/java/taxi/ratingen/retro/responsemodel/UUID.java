package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import taxi.ratingen.retro.base.BaseResponse;

public class UUID extends BaseResponse {

    @SerializedName("uuid")
    @Expose
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
