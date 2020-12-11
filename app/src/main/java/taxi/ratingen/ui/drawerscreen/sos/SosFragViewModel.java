package taxi.ratingen.ui.drawerscreen.sos;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 12/20/17.
 */

public class SosFragViewModel extends BaseNetwork<BaseResponse, SosFragmentNavigator> {

    GitHubService gitHubService;
    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    Gson gson;
    public ObservableBoolean noItemFound = new ObservableBoolean(false);

    @Inject
    public SosFragViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence,
                            HashMap<String, String> hashMap, Gson gson) {
        super(gitHubService,sharedPrefence,gson);
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
        this.gson = gson;
    }

    /** Callback for successful API calls
     * @param taskId Id of the API call
     * @param response {@link BaseResponse} data model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.message.equalsIgnoreCase("success")) {
            if (mCurrentTaskId == Constants.TaskId.SOS_LIST) {
                if (response.data != null) {
                    String sosBase = gson.toJson(response.data);
                    BaseResponse baseSos = gson.fromJson(sosBase, BaseResponse.class);
                    String sosJson = gson.toJson(baseSos.data);
                    So[] sosModels = CommonUtils.IsEmpty(sosJson) ? null : CommonUtils.getSingleObject(sosJson, So[].class);
                    if (sosModels != null) {
                        if (sosModels.length > 0) {
                            noItemFound.set(false);
                            List<So> sosList = Arrays.asList(sosModels);
                            sosList = new ArrayList(sosList);
                            if (sosList != null)
                                getmNavigator().setSosList(sosList);
                        } else
                            noItemFound.set(true);
                    } else
                        noItemFound.set(true);
                } else
                    noItemFound.set(true);
            }
        }

//        if (response == null) {
//            return;
//        }
//        if (response.successMessage.equalsIgnoreCase("user sos details"))
//            if (response.getSos() != null && response.getSos().size() != 0) {
//                if (response.getUserSos() != null && response.getUserSos().size() != 0) {
//                    So so = new So();
//                    so.number = response.getUserSos().get(0).number;
//                    so.name = response.getUserSos().get(0).name;
//                    response.getSos().add(so);
//                }
//                if (response.getSos().size() > 0)
//                    sharedPrefence.savevalue(SharedPrefence.SOSLIST, gson.toJson(response));
//                getmNavigator().setSosList(response.getSos());
//            } else {
//                sharedPrefence.savevalue(SharedPrefence.SOSLIST, gson.toJson(response));
//            }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API call
     * @param e Exception msg **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (mCurrentTaskId == Constants.TaskId.SOS_LIST) {
            noItemFound.set(true);
        }
    }

    /** Returns {@link HashMap} with query parameters used in API calls **/
    @Override
    public HashMap<String, String> getMap() {
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(SharedPrefence.ID, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(SharedPrefence.TOKEN, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        if (sharedPrefence.Getvalue(SharedPrefence.LATITUDE) != null && sharedPrefence.Getvalue(SharedPrefence.LONGITUDE) != null) {
            hashMap.put(SharedPrefence.LATITUDE, sharedPrefence.Getvalue(SharedPrefence.LATITUDE));
            hashMap.put(SharedPrefence.LONGITUDE, sharedPrefence.Getvalue(SharedPrefence.LONGITUDE));
        }
        return hashMap;
    }

    /** Call this method to get SOS data from server by calling API **/
    public void getZoneSos() {
        setIsLoading(false);
        getSosList();
    }

}
