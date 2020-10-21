package taxi.ratingen.ui.splash;

import androidx.databinding.ObservableBoolean;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class SplashViewModel extends BaseNetwork<BaseResponse, SplashNavigator> {
    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableBoolean isLoaad = new ObservableBoolean(false);


    @Inject
    public SplashViewModel(@Named(Constants.ourApp) GitHubService gitHubService
            , SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;

    }


    /**
     * @param taskId
     * @param response holds the response getting the language.
     */
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        // setIsLoading(false);
        isLoaad.set(false);
        if (response.success) {
            if (response.data != null) {
                response.saveLanguageTranslations(sharedPrefence, gson, response.data);
            }
            getmNavigator().startRequestingPermissions();
        }

    }

    /**
     * @param taskId
     * @param e      holds the exception messages from Api.
     */
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        //setIsLoading(false);
        isLoaad.set(false);

        getmNavigator().showMessage(e.getMessage());
        getmNavigator().startRequestingPermissions();
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    /**
     * Api call for getting the languages.
     */
    public void getLanguagees() {
        if (getmNavigator().isNetworkConnected()) {
            // setIsLoading(true);
            isLoaad.set(true);
            getTranslations();
        } else
            getmNavigator().showNetworkMessage();
    }
}
