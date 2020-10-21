package taxi.ratingen.ui.tour;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class TourGuideViewModel extends BaseNetwork<BaseResponse, TourGuideNavigator> {
    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableBoolean skipDisable = new ObservableBoolean(false);
    public TranslationModel translationModell;

    public ObservableField<String> forwardtxt = new ObservableField<>("Next");

    public ObservableBoolean isLoaad = new ObservableBoolean(false);


    @Inject
    public TourGuideViewModel(@Named(Constants.ourApp) GitHubService gitHubService
            , SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.translationModell = translationModel;

    }


    /**
     * @param taskId
     * @param response holds the response getting the language.
     */
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    /**
     * @param taskId
     * @param e      holds the exception messages from Api.
     */
    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }


    public void onClickNext(View v) {
        getmNavigator().forwardClick();
    }

    public void onClickSkip(View v) {
        getmNavigator().onClickSkip();
    }

}
