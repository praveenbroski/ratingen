package taxi.ratingen.ui.registration;

import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 10/9/17.
 */
@Module
public class RegistrationDaggerModel {

    @Provides
    @Named("HashMapData")
    static HashMap<String, String> provideData(RegistrationAct otpActivity) {
        return otpActivity.Bindabledata;
    }
}
