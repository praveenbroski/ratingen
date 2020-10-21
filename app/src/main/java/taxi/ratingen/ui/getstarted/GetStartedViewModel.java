package taxi.ratingen.ui.getstarted;

import android.view.View;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class GetStartedViewModel extends BaseNetwork<BaseResponse, GetStartedNavigator> {

    SharedPrefence sharedPrefence;
    Gson gson;
    public ObservableField<String> txt_Language_update = new ObservableField<>("");
    public ObservableField<String> txt_country_update = new ObservableField<>("");
    public boolean isLanguageChanged = false;

    @Inject
    public GetStartedViewModel(@Named(Constants.ourApp) GitHubService gitHubService
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


    public void getLanguagees() {

    }

    /**
     * Click listener for starting navigation to {@link SignupActivity}
     **/
    public void initiateNavigation(View v) {
        // if(isLanguageChanged)

        if (!txt_Language_update.get().isEmpty())
            getmNavigator().startMovingtoSignup();
        else getmNavigator().showMessage(translationModel.txt_choose_language);
    }


    /**
     * Click listener for choose language
     **/
    public void selectLanguage() {
        /*isLanguageChanged=true;
        showAlertDialog(getmNavigator().getAttachedContext());*/
        final List<String> items = new ArrayList<>();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUAGES) != null&&!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUAGES))) {
            for (String key : gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.LANGUAGES), String[].class))
                switch (key) {
                    case "en":
                        items.add("English" + " (en)");
                        break;
                    case "ar":
                        items.add("العربية" + " (ar)");
                        break;
                    case "fr":
                        items.add("française" + " (fr)");
                        break;
                    case "es":
                        items.add("Español" + " (es)");
                        break;
                    case "ja":
                        items.add("日本語" + " (ja)");
                        break;
                    case "ko":
                        items.add("한국어" + " (ko)");
                        break;
                    case "pt":
                        items.add("português" + " (pt)");
                        break;
                    case "zh":
                        items.add("中文" + " (zh)");
                        break;
                }
        }

        getmNavigator().langguageItems(items);

    }

    public void selectCountry(View v) {
//        showAlertDialog(getmNavigator().getAttachedContext());
    }

}
