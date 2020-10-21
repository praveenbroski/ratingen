package taxi.ratingen.ui.drawerscreen.favorites;

import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteViewModel extends BaseNetwork<BaseResponse, FavoriteFragment> {

    HashMap<String, String> hashMap;
    SharedPrefence sharedPrefence;

    public ObservableBoolean mIsLoading = new ObservableBoolean(false);

    public FavoriteViewModel(GitHubService gitHubService, HashMap<String, String> hashMap, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.hashMap = hashMap;
        this.sharedPrefence = sharedPrefence;
    }

    public void getFavListData() {
        mIsLoading.set(true);

        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));

        if (getmNavigator().isNetworkConnected())
            GetFavListNetworkcall();
        else
            getmNavigator().showNetworkMessage();
    }

    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        mIsLoading.set(false);
        if (response.successMessage.equalsIgnoreCase("Favorite_Added_Successfully")) {
            if (response.getFavplace().size() > 0) {
                response.getFavplace().get(0).IsFavTit = true;
                getmNavigator().addList(response.getFavplace());
                String favList = gson.toJson(response);
                sharedPrefence.savevalue(SharedPrefence.FAVLIST, favList);
            }
        } else if (response.successMessage.equalsIgnoreCase("Favorite_Deleted_Successfully")) {
            getFavListData();
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        mIsLoading.set(false);
        if (e.getCode() != Constants.ErrorCode.EMPTY_FAV_LIST) {
            getmNavigator().showMessage(e);
        }else if (e.getCode() == Constants.ErrorCode.EMPTY_FAV_LIST) {
            getmNavigator().showMessage(e);
            sharedPrefence.savevalue(SharedPrefence.FAVLIST, "");
            getmNavigator().addList(new ArrayList<>());
        }else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().showMessage(e);
            getmNavigator().refreshCompanyKey();
        }
    }

    public void clickBack(View v) {
        getmNavigator().goBack();
    }

    public void clickAdd(View v) {
        getmNavigator().addFavClicked();
    }

    public void deleteFav(Favplace favplace) {
        mIsLoading.set(true);
        hashMap.clear();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        hashMap.put(Constants.NetworkParameters.favid, "" + favplace.Favid);
        DeleteFavNetworkcall();
    }

}
