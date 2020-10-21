package taxi.ratingen.ui.topdriver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TopDriverModel implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("car_model")
    @Expose
    private String carModel;

    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("meta")
    @Expose
    private Integer meta;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("review")
    @Expose
    private String review;


    @SerializedName("driver_type")
    @Expose
    private String driverType;


    public String getDriverType() {
        return driverType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfilePic() {
        return profilePic;
    }


    public String getCarModel() {
        return carModel;
    }


    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }


    public String getReview() {
        return review;
    }


}
