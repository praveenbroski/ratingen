package taxi.ratingen.ui.drawerscreen.notification;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

public class NotificationListViewModel extends BaseNetwork<BaseResponse, NotificationListNavigator> {

    SharedPrefence sharePref;
    HashMap<String, String> hashMap = new HashMap();

    public ObservableBoolean noItemFound = new ObservableBoolean(false);

    public NotificationListViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        sharePref = sharedPrefence;
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("user_notification_list")) {
            getmNavigator().openNotifications(response.getNotificationList());
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
    }

    public void notificationApi() {
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharePref.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharePref.Getvalue(SharedPrefence.TOKEN));
            getNotificationAPi(hashMap);
        }

    }
}
