package taxi.ratingen.ui.drawerscreen.placeapiscreen;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.adapter.PlaceandFavAdapter;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 11/30/17.
 */
@Module
public class PlaceApiDaggerModel {

    @Provides
    LinearLayoutManager provideLinearLayoutManage(PlaceApiAct placeApiAct) {
        return new LinearLayoutManager(placeApiAct);
    }

    @Provides
    PlaceandFavAdapter PlaceandFavAdapterAdapter(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence,
                                                 HashMap<String,String> hashMap, Gson gson, PlaceApiAct placeApiAct) {
        return new PlaceandFavAdapter(new ArrayList<Favplace>(),gitHubService,sharedPrefence,hashMap,gson,placeApiAct);
    }

    @Provides
    @Named("HashMapData")
    static HashMap<String, String> provideData(PlaceApiAct placeApiAct) {
        return placeApiAct.Bindabledata;
    }

}
