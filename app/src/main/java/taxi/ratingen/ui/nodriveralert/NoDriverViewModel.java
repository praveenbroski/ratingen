package taxi.ratingen.ui.nodriveralert;

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

public class NoDriverViewModel extends BaseNetwork<BaseResponse, NoDriverNavigator> {

    int requestId;

    HashMap<String, String> hashMap = new HashMap<>();
    Gson gson;
    SharedPrefence sharedPrefence;

    @Inject
    public NoDriverViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                             SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.gitHubService = gitHubService;
    }

//    @Inject
//    public NoDriverViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
//        super(gitHubService, sharedPrefence, gson);
//    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    /**Callback for successful API calls
     * @param taskId ID of the API task
     * @param response {@link BaseResponse} model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

        if (response.successMessage.equalsIgnoreCase("user_reschedule_ride_later_trip")) {
            setIsLoading(false);
            getmNavigator().reScheduleSucess();
        }
    }

    /**Callback for failed API calls
     * @param taskId ID of the API task
     * @param e {@link CustomException} **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().closeWaitingDialog();
    }

    /** Click listener for reschedule No **/
    public void onClickNo(View v) {
        getmNavigator().onClickNO();
    }

    /** Click listener for reschedule Yes **/
    public void onClickYes(View v) {
        setIsLoading(true);
        rescheduleTripApi();
    }

    /** Calls reschedule API to try again **/
    private void rescheduleTripApi() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.request_id, "" + requestId);
        reScheduleAPi(hashMap);
    }

}
