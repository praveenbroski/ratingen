package taxi.ratingen.ui.wallethistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletHistModel {

    @SerializedName("total_earned")
    @Expose
    private String totalEarned;
    @SerializedName("total_spend")
    @Expose
    private String totalSpend;
    @SerializedName("total_balance")
    @Expose
    private String totalBalance;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;

    @SerializedName("history")
    @Expose
    private List<HistoryDetailsModel> history = null;

    public String getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(String totalEarned) {
        this.totalEarned = totalEarned;
    }

    public String getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(String totalSpend) {
        this.totalSpend = totalSpend;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public List<HistoryDetailsModel> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryDetailsModel> history) {
        this.history = history;
    }


}
