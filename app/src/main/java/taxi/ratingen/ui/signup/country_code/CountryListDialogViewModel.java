package taxi.ratingen.ui.signup.country_code;


import android.view.View;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

public class CountryListDialogViewModel extends BaseNetwork<User, CountryListNavigator> {

    HashMap<String, String> hashMap = new HashMap<>();

    public CountryListDialogViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {

        super(gitHubService, sharedPrefence, gson);
    }

    @Override
    public HashMap<String, String> getMap() {
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("country_list")) {
            getmNavigator().countryResponse(response.countryListModel);
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.toString());
    }

    public void getCountryListApi() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            getCountryList();
        } else getmNavigator().showNetworkMessage();

    }

    public void onClickBackimg(View v) {
        getmNavigator().dismissDialog();
    }

}
