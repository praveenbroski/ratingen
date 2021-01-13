package taxi.ratingen.ui.drawerscreen.mapscreen;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.ArrayAdapter;

import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.ui.companyvalidation.dialog.DialogCompanyViewModel;
import taxi.ratingen.ui.drawerscreen.disputdialog.DisputeDialogViewModel;
import taxi.ratingen.ui.drawerscreen.favorites.FavoriteViewModel;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.AddFavViewModel;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap.PickFromMapViewModel;
import taxi.ratingen.ui.drawerscreen.notification.NotificationListViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.payment.PaymentMethod;
import taxi.ratingen.ui.drawerscreen.ridescreen.payment.PaymentMethodAdapter;
import taxi.ratingen.ui.drawerscreen.ridescreen.payment.PaymentViewModel;
import taxi.ratingen.ui.drawerscreen.tripcanceleddialog.TripCanceledViewModel;
import com.google.gson.Gson;
import taxi.ratingen.retro.GitHubMapService;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.ComplaintList;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.retro.responsemodel.history;
import taxi.ratingen.ui.drawerscreen.billdialog.BillDialogViewModel;
import taxi.ratingen.ui.drawerscreen.canceldialog.CancelDialogViewModel;
import taxi.ratingen.ui.drawerscreen.complaint.ComplaiintFrag;
import taxi.ratingen.ui.drawerscreen.complaint.ComplaintViewModel;
import taxi.ratingen.ui.drawerscreen.faq.FaqFragmentViewModel;
import taxi.ratingen.ui.drawerscreen.feedback.FeedbackViewModel;
import taxi.ratingen.ui.drawerscreen.history.adapter.HistoryAdapter;
import taxi.ratingen.ui.drawerscreen.history.HistoryListFrag;
import taxi.ratingen.ui.drawerscreen.history.HistoryListViewModel;
import taxi.ratingen.ui.drawerscreen.mapscrn.adapter.CarsTypesAdapter;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrn;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrnViewModel;
import taxi.ratingen.ui.drawerscreen.payment.adapter.VisaCardAdapter;
import taxi.ratingen.ui.drawerscreen.payment.PaymentFrag;
import taxi.ratingen.ui.drawerscreen.payment.PaymentFragViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.eta.EtaViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.etabasefare.EtachildBaseViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.etafarechild.EtachildFareViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideFragViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitingProgressViewModel;
import taxi.ratingen.ui.drawerscreen.sos.adapter.SosAdapter;
import taxi.ratingen.ui.drawerscreen.sos.SosFragViewModel;
import taxi.ratingen.ui.drawerscreen.sos.SosFragment;
import taxi.ratingen.ui.drawerscreen.setting.SettingFragViewModel;
import taxi.ratingen.ui.drawerscreen.support.SupportFragViewModel;
import taxi.ratingen.ui.drawerscreen.tripscreen.TripFragViewModel;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.EmptyViewModel;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.socket.client.Socket;

/**
 * Created by root on 10/11/17.
 */

@Module
public class MapDaggerModel {

    //dagger doesn't create instance implicitly for fragments..
    @Provides
    MapFragmentViewModel provideMapFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     @Named(Constants.googleMap) GitHubMapService gitHubgoogle,
                                                     SharedPrefence sharedPrefence, Gson gson, HashMap<String, String> hashMap) {
        return new MapFragmentViewModel(gitHubService, gitHubgoogle, sharedPrefence, gson, hashMap);
    }


    @Provides
    MapScrnViewModel provideMapScrnViewModel(@Named(Constants.ourApp) GitHubService gitHubService, @Named(Constants.googleMap) GitHubMapService gitHubgoogle,
                                             Socket socket, Gson gson, SharedPrefence sharedPrefence, HashMap<String, String> hashMap) {
        return new MapScrnViewModel(gitHubService, gitHubgoogle, socket, gson, sharedPrefence, hashMap);
    }

    @Provides
    PaymentFragViewModel providePaymentFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence,
                                                     HashMap<String, String> hashMap,
                                                     Gson gson) {
        return new PaymentFragViewModel(gitHubService, sharedPrefence, hashMap, gson);
    }


    @Provides
    RideFragViewModel provideRideFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                               @Named(Constants.googleMap) GitHubMapService gitHubMapService,
                                               SharedPrefence sharedPrefence,
                                               Socket socket,
                                               HashMap<String, String> hashMap,
                                               Context context,
                                               Gson gson) {
        return new RideFragViewModel(gitHubService, sharedPrefence, hashMap, context, gitHubMapService, socket, gson);
    }

    @Provides
    RideConfirmationViewModel provideRideConfirmationViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                               @Named(Constants.googleMap) GitHubMapService gitHubMapService,
                                                               SharedPrefence sharedPrefence,
                                                               HashMap<String, String> hashMap,
                                                               Context context,
                                                               Gson gson) {
        return new RideConfirmationViewModel(gitHubService, sharedPrefence, hashMap, context, gitHubMapService, gson);
    }

    @Provides
    EtaViewModel provideEtaViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                     SharedPrefence sharedPrefence, Gson gson) {
        return new EtaViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    BillDialogViewModel provideBillDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                   SharedPrefence sharedPrefence, Gson gson) {
        return new BillDialogViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    CancelDialogViewModel provideCancelDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                       HashMap<String, String> Map,
                                                       SharedPrefence sharedPrefence,
                                                       Gson gson) {
        return new CancelDialogViewModel(gitHubService, Map, sharedPrefence, gson);
    }


    @Provides
    EtachildFareViewModel provideEtachildFareViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                       SharedPrefence sharedPrefence, Gson gson) {
        return new EtachildFareViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    EtachildBaseViewModel provideEtachildBaseViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                       SharedPrefence sharedPrefence, Gson gson) {
        return new EtachildBaseViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    WaitingProgressViewModel provideWaitingProgressViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                             SharedPrefence sharedPrefence,
                                                             HashMap<String, String> hashMap,
                                                             Gson gson) {
        return new WaitingProgressViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }

    @Provides
    TripFragViewModel provideTripFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService, @Named(Constants.googleMap) GitHubMapService gitHubMapService, SharedPrefence sharedPrefence, HashMap<String, String> hashMap, Socket socket, Gson gson) {
        return new TripFragViewModel(gitHubService, gitHubMapService, hashMap, sharedPrefence, socket, gson);
    }

    @Provides
    FeedbackViewModel provideFeedbackViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                               SharedPrefence sharedPrefence,
                                               HashMap<String, String> hashMap,
                                               Gson gson) {
        return new FeedbackViewModel(gitHubService, sharedPrefence, hashMap, gson);
    }

    @Provides
    SettingFragViewModel provideSettingFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence, Gson gson) {
        return new SettingFragViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    HistoryListViewModel provideHistoryListViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     HashMap<String, String> hashMap,
                                                     SharedPrefence sharedPrefence,
                                                     Gson gson) {
        return new HistoryListViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }

    @Provides
    ArrayAdapter SosArrayAdapter(ComplaiintFrag complaiintFrag) {


        return new ArrayAdapter(complaiintFrag.getContext(), android.R.layout.simple_list_item_1, new ArrayList<ComplaintList>());
    }

    @Provides
    ComplaintViewModel provideComplaintViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                 HashMap<String, String> hashMap,
                                                 SharedPrefence sharedPrefence,
                                                 Gson gson) {
        return new ComplaintViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }

    @Provides
    SosAdapter SosAdapterAdapter() {
        return new SosAdapter(new ArrayList<So>());
    }


    @Provides
    HistoryAdapter HistoryAdapterAdapter(HistoryListFrag historyListFrag) {
        return new HistoryAdapter(new ArrayList<history>(), historyListFrag.getBaseActivity());
    }

    @Provides
    VisaCardAdapter PaginationAdapterAdapter(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence,
                                             HashMap<String, String> hashMap) {
        return new VisaCardAdapter(new ArrayList<Payment>(), gitHubService, sharedPrefence, hashMap);
    }

    @Provides
    @Named("Payment")
    LinearLayoutManager provideLinearLayoutManage(PaymentFrag paymentFrag) {
        return new LinearLayoutManager(paymentFrag.getBaseActivity());
    }

    @Provides
    @Named("Sos")
    LinearLayoutManager provideSosFragmentLinearLayoutManage(SosFragment sosFragment) {
        return new LinearLayoutManager(sosFragment.getBaseActivity());
    }


    @Provides
    @Named("HistoryList")
    LinearLayoutManager provideHistoryLinearLayoutManage(HistoryListFrag historyListFrag) {
        return new LinearLayoutManager(historyListFrag.getBaseActivity());
    }

  /*  @Provides
    SosFragViewModel provideEmptyViewModel() {
        return new SosFragViewModel();
    }*/

    @Provides
    EmptyViewModel provideEmptyViewModel() {
        return new EmptyViewModel();
    }

    @Provides
    @Named("MapScrn")
    LinearLayoutManager provideLinearLdayoutManage(MapScrn mapScrn) {
        return new LinearLayoutManager(mapScrn.getBaseActivity());
    }

//    @Provides
//    CarsTypesAdapter CarsTypesAdapterAdapter(RideConfirmationFragment mapScrn) {
//        return new CarsTypesAdapter(mapScrn, new ArrayList<TypeNew>());
//    }

    @Provides
    CarsTypesAdapter CarsTypesAdapterAdapter(RideConfirmationFragment mapScrn) {
        return new CarsTypesAdapter(mapScrn, new ArrayList<Type>());
    }

    @Provides
    SosFragViewModel provideSosFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                             SharedPrefence sharedPrefence,
                                             HashMap<String, String> hashMap, Gson gson) {
        return new SosFragViewModel(gitHubService, sharedPrefence, hashMap, gson);
    }

    @Provides
    FaqFragmentViewModel provideFaqFragmentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence,
                                                     HashMap<String, String> hashMap, Gson gson) {
        return new FaqFragmentViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    SupportFragViewModel provideSupportFragViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                     SharedPrefence sharedPrefence,
                                                     HashMap<String, String> hashMap, Gson gson) {
        return new SupportFragViewModel(gitHubService, sharedPrefence, hashMap, gson);
    }

    @Provides
    DisputeDialogViewModel provideDisputeDialogViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                         SharedPrefence sharedPrefence,
                                                         HashMap<String, String> hashMap, Gson gson) {
        return new DisputeDialogViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }


    @Provides
    TripCanceledViewModel provideTripCanceledViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                       SharedPrefence sharedPrefence,
                                                       Gson gson) {
        return new TripCanceledViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    NotificationListViewModel NotificationListViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                           SharedPrefence sharedPrefence,
                                                           Gson gson) {
        return new NotificationListViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    DialogCompanyViewModel provideDialogCompanyViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                         @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                                                         SharedPrefence sharedPrefence,
                                                         HashMap<String, String> hashMap, Gson gson) {
        return new DialogCompanyViewModel(gitHubService, gitHubCountryCode, sharedPrefence, gson);
    }

    @Provides
    FavoriteViewModel provFavoriteViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                                    SharedPrefence sharedPrefence,
                                                    HashMap<String, String> hashMap, Gson gson) {
        return new FavoriteViewModel(gitHubService, hashMap, sharedPrefence, gson);
    }

    @Provides
    AddFavViewModel proAddFavViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                       @Named(Constants.googleMap) GitHubMapService mapService,
                                          SharedPrefence sharedPrefence,
                                          HashMap<String, String> hashMap, Gson gson) {
        return new AddFavViewModel(gitHubService, mapService, hashMap, sharedPrefence, gson);
    }

    @Provides
    PaymentViewModel providePaymentViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                             SharedPrefence sharedPrefence,
                                             HashMap<String, String> hashMap, Gson gson) {
        return new PaymentViewModel(gitHubService, sharedPrefence, gson);
    }

    @Provides
    PaymentMethodAdapter providePaymentAdapter(PaymentMethod paymentMethod) {
        return new PaymentMethodAdapter(paymentMethod, new ArrayList<taxi.ratingen.retro.responsemodel.PaymentMethod>());
    }

    @Provides
    PickFromMapViewModel providePickFromMapViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                            @Named(Constants.googleMap) GitHubMapService mapService,
                                            SharedPrefence sharedPrefence,
                                            HashMap<String, String> hashMap, Gson gson) {
        return new PickFromMapViewModel(gitHubService, mapService, hashMap, sharedPrefence, gson);
    }

}
