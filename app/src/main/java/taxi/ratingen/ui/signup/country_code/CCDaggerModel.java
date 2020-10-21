package taxi.ratingen.ui.signup.country_code;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.ui.drawerscreen.addcard.AddCardViewModel;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class CCDaggerModel {

    @Provides
    CountryListDialogViewModel provideCountryListDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                                 SharedPrefence sharedPrefence, Gson gson) {
        return new CountryListDialogViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    AddCardViewModel provideAddCardViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                             SharedPrefence sharedPrefence,
                                             HashMap<String, String> hashMap, Gson gson) {
        return new AddCardViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }
}
