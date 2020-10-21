package taxi.ratingen.ui.drawerscreen.mapscreen;


import taxi.ratingen.ui.companyvalidation.dialog.DialogCompanyValidation;
import taxi.ratingen.ui.drawerscreen.billdialog.BillDialogFragment;
import taxi.ratingen.ui.drawerscreen.canceldialog.cancelDialogFrag;
import taxi.ratingen.ui.drawerscreen.complaint.ComplaiintFrag;
import taxi.ratingen.ui.drawerscreen.disputdialog.DisputDialogFragment;
import taxi.ratingen.ui.drawerscreen.faq.FaqFragment;
import taxi.ratingen.ui.drawerscreen.favorites.FavoriteFragment;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.AddFavFragment;
import taxi.ratingen.ui.drawerscreen.favorites.addfav.pickfrommap.PickFromMapFragment;
import taxi.ratingen.ui.drawerscreen.feedback.FeedbackFragment;
import taxi.ratingen.ui.drawerscreen.history.HistoryListFrag;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrn;
import taxi.ratingen.ui.drawerscreen.notification.NotificationlistFrag;
import taxi.ratingen.ui.drawerscreen.payment.PaymentFrag;
import taxi.ratingen.ui.drawerscreen.refferalscreen.RefferalCodeFrag;
import taxi.ratingen.ui.drawerscreen.refferalscreen.RefferalDaggerModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.eta.Etadialog;
import taxi.ratingen.ui.drawerscreen.ridescreen.etabasefare.EtachildBaseFare;
import taxi.ratingen.ui.drawerscreen.ridescreen.etafarechild.EtachildFareFragment;
import taxi.ratingen.ui.drawerscreen.ridescreen.etaparent.ETAParent;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.drawerscreen.setting.SettingFrag;
import taxi.ratingen.ui.drawerscreen.sos.SosFragment;
import taxi.ratingen.ui.drawerscreen.support.SupportFrag;
import taxi.ratingen.ui.drawerscreen.tripcanceleddialog.TripCanceledDialogFrag;
import taxi.ratingen.ui.drawerscreen.tripscreen.TripFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by root on 10/11/17.
 */
@Module
public abstract class MapFragmentProvider {
    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract MapFragment provideMapFragmentProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract PaymentFrag providePaymentFragProviderFactory();

    @ContributesAndroidInjector(modules = RefferalDaggerModel.class)
    abstract RefferalCodeFrag provideRefferalCodeFragProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract MapScrn provideMapScrnProviderFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract RideFragment provideRideFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract RideConfirmationFragment provideRideConfirmationFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract Etadialog provideEtadialogFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract BillDialogFragment provideBillDialogFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract ETAParent provideETAParentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract EtachildFareFragment provideEtachildFareFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract EtachildBaseFare provideEtachildBaseFareFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SosFragment provideSosFragmentBaseFareFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract ComplaiintFrag provideComplaiintFragBaseFareFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SettingFrag provideSettingFragBaseFareFragmentFactory();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract WaitProgressDialog provideWaitProgressDialog();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract TripFragment provideTripFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract FeedbackFragment provideFeedbackFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract HistoryListFrag provideHistoryListFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract cancelDialogFrag providecancelDialogFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract FaqFragment provideFaqFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract SupportFrag provideSupportFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract DisputDialogFragment provideDisputDialogFragment();

//    @ContributesAndroidInjector(modules = MapDaggerModel.class)
//    abstract AddCardFrag provideAddCardFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract NotificationlistFrag provideNotificationlistFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract TripCanceledDialogFrag provideTripCanceledDialogFrag();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract DialogCompanyValidation provideDialogCompanyValidation();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract FavoriteFragment provFavoriteFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract AddFavFragment provAddFavFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract PickFromMapFragment providePickFromMapFragment();

}
