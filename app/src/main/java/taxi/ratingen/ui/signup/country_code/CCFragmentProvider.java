package taxi.ratingen.ui.signup.country_code;

import taxi.ratingen.ui.drawerscreen.addcard.AddCardFrag;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class CCFragmentProvider {

    @ContributesAndroidInjector(modules = CCDaggerModel.class)
    abstract CountryListDialog  provideCountryListDialog();
    @ContributesAndroidInjector(modules = CCDaggerModel.class)
    abstract AddCardFrag provideAddCardFrag();
}
