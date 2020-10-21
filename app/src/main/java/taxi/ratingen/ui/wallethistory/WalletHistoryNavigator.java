package taxi.ratingen.ui.wallethistory;

import java.util.List;

import taxi.ratingen.ui.base.BaseActivity;

public interface WalletHistoryNavigator {
    void listWalletHistory(List<HistoryDetailsModel> history, String currencySymbol);

    void noHistoryFound();

    void allClick();

    void canclClick();

    void cancelledTripList(List<CancelledListModel> cancelledTripList);

    void stopRecycle();

    BaseActivity getBaseAct();
}
