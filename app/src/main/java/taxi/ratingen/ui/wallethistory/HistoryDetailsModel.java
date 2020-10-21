package taxi.ratingen.ui.wallethistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryDetailsModel {

    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("request_id")
    @Expose
    private Object requestId;

    @SerializedName("symbol")
    @Expose
    private String symbol;

    public String getAmount() {
        return amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getRequestId() {
        return requestId;
    }

    public void setRequestId(Object requestId) {
        this.requestId = requestId;
    }
}


