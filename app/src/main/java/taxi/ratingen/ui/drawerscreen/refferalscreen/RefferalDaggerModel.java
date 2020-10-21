package taxi.ratingen.ui.drawerscreen.refferalscreen;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.ui.drawerscreen.payment.adapter.VisaCardAdapter;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 11/7/17.
 */
@Module
public class RefferalDaggerModel {

    @Provides
    RefferalFragViewModel provideRefferalFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence,
                                                       HashMap<String,String> hashMap,
                                                       Gson gson) {
        return new RefferalFragViewModel(gitHubService,sharedPrefence,hashMap,gson);
    }

    @Provides
    VisaCardAdapter PaginationAdapterAdapter(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence,
                                             HashMap<String,String> hashMap) {
        return new VisaCardAdapter(new ArrayList<Payment>(),gitHubService,sharedPrefence,hashMap);
    }


    @Provides
    LinearLayoutManager provideLinearLayoutManage(RefferalCodeFrag refferalCodeFrag) {
        return new LinearLayoutManager(refferalCodeFrag.getBaseActivity());
    }
}
