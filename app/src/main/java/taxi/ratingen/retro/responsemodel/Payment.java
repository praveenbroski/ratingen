package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

/**
 * Created by root on 11/6/17.
 */

public class Payment extends BaseResponse {

    @SerializedName("card_id")
    @Expose
    public Integer cardId;

    @SerializedName("last_number")
    @Expose
    public String lastNumber;

    @SerializedName("card_type")
    @Expose
    public String cardType;

    @SerializedName("is_default")
    @Expose
    public Boolean isDefault;

    @SerializedName("preferred_payment_type")
    public Integer preferred_payment_type;

    @SerializedName("checkout_id")
    @Expose
    public String checkoutId;

}
