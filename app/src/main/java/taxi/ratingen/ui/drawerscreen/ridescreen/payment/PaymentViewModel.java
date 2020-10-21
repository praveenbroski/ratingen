package taxi.ratingen.ui.drawerscreen.ridescreen.payment;

import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

import javax.inject.Inject;

public class PaymentViewModel extends BaseNetwork<BaseResponse, PaymentNavigator> {

    @Inject
    SharedPrefence sharedPrefence;

    HashMap<String, String> hashMap;

    @Inject
    Gson gson;

    public PaymentViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    public void onCashClick(View v) {
        getmNavigator().onCashClick();
    }

    public void onWalletClick(View v) {
        getmNavigator().onWalletClick();
    }

    public void onCardClick(View v) {
        getmNavigator().onCardClick();
    }

}
