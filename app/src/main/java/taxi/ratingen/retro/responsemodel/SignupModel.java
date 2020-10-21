package taxi.ratingen.retro.responsemodel;


import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

public class SignupModel extends BaseResponse {

//    @Expose
//    public DriverModel driver;
//    @Expose
//    public RequestModel.User user;
    @SerializedName("exist_user")
    public boolean exist_user;

}