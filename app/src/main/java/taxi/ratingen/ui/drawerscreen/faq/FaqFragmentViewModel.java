package taxi.ratingen.ui.drawerscreen.faq;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

/**
 * Created by root on 12/21/17.
 */

public class FaqFragmentViewModel extends BaseNetwork<BaseResponse, FaqNavigator> {

    SharedPrefence prefence;
    GitHubService apiService;
    public ObservableBoolean isFaqAvailabe = new ObservableBoolean(false);

    public FaqFragmentViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
//        DataBindingUtil.setDefaultComponent(new MyDataComponent(this));
        this.prefence = sharedPrefence;
        this.apiService = gitHubService;
    }

    /**
     * Getting FAQ list from API
     */
    public void setUpData() {
        if (getmNavigator().isNetworkConnected()) {
            mIsLoading.set(true);
            getFaq(getMap());
        }
    }

    /**
     * called when the API call is successful
     **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("user_faq_list")) {
            if (response.faq_list != null && response.faq_list.size() > 0) {
                isFaqAvailabe.set(true);
                getmNavigator().loadFaQItems(response.faq_list);
            } else
                isFaqAvailabe.set(false);
        }
    }

    /**
     * called when API call fails
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            if (getmNavigator().getAttachedContext() != null) {
                getmNavigator().logout();
                getmNavigator().showMessage(getmNavigator().getAttachedContext().getTranslatedString(R.string.text_already_login));
            }
        }else if (e.getCode() == Constants.ErrorCode.NO_FAQ ) {
           isFaqAvailabe.set(false);
        } else
            getmNavigator().showMessage(e.getMessage());
        e.printStackTrace();
    }

    /**
     * gives a {@link HashMap} with query parameters required for API call
     **/
    @Override
    public HashMap<String, String> getMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, prefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, prefence.Getvalue(SharedPrefence.TOKEN));
        if (sharedPrefence.Getvalue(SharedPrefence.LATITUDE) != null && sharedPrefence.Getvalue(SharedPrefence.LONGITUDE) != null) {
            hashMap.put(Constants.NetworkParameters.latitude, sharedPrefence.Getvalue(SharedPrefence.LATITUDE));
            hashMap.put(Constants.NetworkParameters.longitude, sharedPrefence.Getvalue(SharedPrefence.LONGITUDE));
        }
        hashMap.put(Constants.NetworkParameters.type, "1");

//        hashMap.put(Constants.NetworkParameters.id, "10");
//        hashMap.put(Constants.NetworkParameters.token, "$2y$10$s5ZTFymwRLF0b9ZPgJ2EyOGbCvy5PeYwcmhw3IcYwOrG6qkQ.gIdS");
//        hashMap.put(Constants.NetworkParameters.latitude, "11.2805");
//        hashMap.put(Constants.NetworkParameters.longitude, "77.5989");
//        hashMap.put(Constants.NetworkParameters.type, "1");

        return hashMap;
    }

}
