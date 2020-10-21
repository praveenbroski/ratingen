package taxi.ratingen.ui.getReady;

import android.view.View;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class GetReadyViewModel extends BaseNetwork<BaseResponse, GetReadyNavigator> {

    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableField<String> txt_Language_update = new ObservableField<>("");
    public ObservableField<String> txt_country_update = new ObservableField<>("");
    public boolean isLanguageChanged = false;

    @Inject
    public GetReadyViewModel(@Named(Constants.ourApp) GitHubService gitHubService
            , SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;

    }


    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    public void getStarted(View v){
        getmNavigator().startMovingtoSignup();
    }
}
