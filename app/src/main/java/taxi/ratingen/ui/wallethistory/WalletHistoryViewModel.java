package taxi.ratingen.ui.wallethistory;

import androidx.databinding.ObservableField;

import android.view.View;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

public class WalletHistoryViewModel extends BaseNetwork<BaseResponse, WalletHistoryNavigator> {
    HashMap<String, String> map = new HashMap<>();
    ObservableField<String> id = new ObservableField<>();
    ObservableField<String> token = new ObservableField<>();

    public ObservableField<String> totalAmount = new ObservableField<>("0.0");
    public ObservableField<String> paidAmount = new ObservableField<>("0.0");
    public ObservableField<String> netAmount = new ObservableField<>("0.0");

    int clickHandle = 0;
    public ObservableField<Boolean> tabClick = new ObservableField<>(false);

    public ObservableField<Boolean> allClick = new ObservableField<>(true);
    public ObservableField<Boolean> cancelClick = new ObservableField<>(true);

    int pageNo;

    @Inject
    WalletHistoryViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           SharedPrefence sharedPrefence,
                           Gson gson) {
        super(gitHubService, sharedPrefence, gson);
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    /**
     * @param taskId
     * @param response this holds the user wallet history details.
     */
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
//        String json1 = "{\n" +
//                "  \"success\": true,\n" +
//                "  \"success_message\": \"User Wallet Successfully\",\n" +
//                "  \"wallet\": {\n" +
//                "    \"currency_symbol\": \"$\",\n" +
//                "    \"history\": [\n" +
//                "        {\n" +
//                "          \"amount\": \"150\",\n" +
//                "          \"type\": \"Request Spend\",\n" +
//                "          \"date\": \"2020-04-15  11:25\",\n" +
//                "          \"request_id\": \"REQ0001\",\n" +
//                "          \"symbol\": \"-\"\n" +
//                "        }, \n" +
//                "        {\n" +
//                "          \"amount\": \"175\",\n" +
//                "          \"type\": \"Request Spend\",\n" +
//                "          \"date\": \"2020-04-17  15:43\",\n" +
//                "          \"request_id\": \"REQ0002\",\n" +
//                "          \"symbol\": \"-\"\n" +
//                "        }\n" +
//                "      ]\n" +
//                "  }\n" +
//                "}";
//
//        String json2 = "{\n" +
//                "  \"success\": true,\n" +
//                "  \"success_message\": \"cancelled_trip_list\",\n" +
//                "  \"cancelled_dashboard\": {\n" +
//                "    \"total_cancellation\": \"150\",\n" +
//                "    \"total_paid\": \"125\",\n" +
//                "    \"net\": \"275\"\n" +
//                "  },\n" +
//                "  \"cancelled_trip_list\": [\n" +
//                "      {\n" +
//                "        \"id\": 1,\n" +
//                "        \"request_id\": \"REQ00003\",\n" +
//                "        \"cancellation_fee\": \"14\",\n" +
//                "        \"is_paid\": 1,\n" +
//                "        \"currency_symbol\": \"$\",\n" +
//                "        \"timezone\": \"+530\",\n" +
//                "        \"date\": \"2020-04-18  18:48\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 2,\n" +
//                "        \"request_id\": \"REQ00004\",\n" +
//                "        \"cancellation_fee\": \"17\",\n" +
//                "        \"is_paid\": 0,\n" +
//                "        \"currency_symbol\": \"$\",\n" +
//                "        \"timezone\": \"+530\",\n" +
//                "        \"date\": \"2020-04-19  19:18\"\n" +
//                "      }\n" +
//                "    ]\n" +
//                "}";
//
//        if (response.successMessage.equalsIgnoreCase("User Wallet Successfully")) {
//            response = CommonUtils.getSingleObject(json1, BaseResponse.class);
//        } else if (response.successMessage.equalsIgnoreCase("cancelled_trip_list")) {
//            response = CommonUtils.getSingleObject(json2, BaseResponse.class);
//        }

        if (response.successMessage.equalsIgnoreCase("User Wallet Successfully")) {
            if (response.getWallet().getHistory() != null && response.getWallet().getHistory().size() > 0)
                getmNavigator().listWalletHistory(response.getWallet().getHistory(), response.getWallet().getCurrencySymbol());
            else if (pageNo <= 1)
                getmNavigator().noHistoryFound();
            else getmNavigator().stopRecycle();
        } else if (response.successMessage.equalsIgnoreCase("cancelled_trip_list")) {
            totalAmount.set(response.getCancelledDashboard().getTotalCancellation());
            paidAmount.set(response.getCancelledDashboard().getTotalPaid());
            netAmount.set(response.getCancelledDashboard().getNet());
            if (response.cancelledTripList != null && response.cancelledTripList.size() > 0)
                getmNavigator().cancelledTripList(response.cancelledTripList);
            else
                getmNavigator().noHistoryFound();
        }

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    /**
     * @param currentPage holds the page number of the list.
     * @param clickHandle this shows the All HistoryList or Cancellation HistoryList.
     */
    public void setUp(int currentPage, int clickHandle) {
        map.clear();
        map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        map.put(Constants.NetworkParameters.id, id.get());
        map.put(Constants.NetworkParameters.token, token.get());
        pageNo = currentPage;
        if (clickHandle == 1) {
            map.put(Constants.NetworkParameters.page, "" + currentPage);
            userWalletHistory(map);
        } else
            userCancellHistory(map);
    }

    /**
     * @param v is UI view and click defines to get the HistoryList.
     */
    public void allClick(View v) {
        if (allClick.get()) {
            getmNavigator().allClick();
            clickHandle = 0;
            tabClick.set(false);
            allClick.set(false);
            cancelClick.set(true);
            setUp(1, 1);

        }

    }

    /**
     * @param v is UI view and click defines to get the cancellation HistoryList.
     */
    public void cancellClick(View v) {
        if (cancelClick.get()) {
            getmNavigator().canclClick();
            clickHandle = 1;
            tabClick.set(true);
            cancelClick.set(false);
            allClick.set(true);
            setUp(1, 0);
        }

    }
}
