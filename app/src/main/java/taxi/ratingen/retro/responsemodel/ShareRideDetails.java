package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

public class ShareRideDetails extends BaseResponse  {

    @SerializedName("one_seat_share_price")
    @Expose
    public double one_seat_share_price = 0;
    @SerializedName("one_seat_total")
    @Expose
    public double one_seat_total = 0;
    @SerializedName("one_seat_tax_amount")
    @Expose
    public double one_seat_tax_amount = 0;


    @SerializedName("two_seat_share_price")
    @Expose
    public double two_seat_share_price = 0;

    @SerializedName("two_seat_tax_amount")
    @Expose
    public double two_seat_tax_amount = 0;
    @SerializedName("two_seat_total")
    @Expose
    public double two_seat_total = 0;


}
