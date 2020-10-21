package taxi.ratingen.retro.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import taxi.ratingen.retro.base.BaseResponse;

public class PaymentMethod extends BaseResponse {

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

    public Boolean isSelected;

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public void setLastNumber(String lastNumber) {
        this.lastNumber = lastNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public void setPreferred_payment_type(Integer preferred_payment_type) {
        this.preferred_payment_type = preferred_payment_type;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

}