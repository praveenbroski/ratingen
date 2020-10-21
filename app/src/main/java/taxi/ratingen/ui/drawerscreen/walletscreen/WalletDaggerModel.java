package taxi.ratingen.ui.drawerscreen.walletscreen;

import androidx.recyclerview.widget.LinearLayoutManager;

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
 * Created by root on 11/10/17.
 */
@Module
public class WalletDaggerModel {

    @Provides
    LinearLayoutManager provideLinearLayoutManage(WalletAct walletAct) {
        return new LinearLayoutManager(walletAct);
    }

    @Provides
    VisaCardAdapter PaginationAdapterAdapter(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence,
                                             HashMap<String,String> hashMap) {
        return new VisaCardAdapter(new ArrayList<Payment>(),gitHubService,sharedPrefence,hashMap);
    }
}
