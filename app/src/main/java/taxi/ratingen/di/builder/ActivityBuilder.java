/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package taxi.ratingen.di.builder;

import taxi.ratingen.ui.applyrefferal.ApplayRefferal;
import taxi.ratingen.ui.companyvalidation.CompanyValidationActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.changeplace.SearchPlaceActivity;
import taxi.ratingen.ui.drawerscreen.historydetails.HistoryDetailsScrn;
import taxi.ratingen.ui.drawerscreen.mapscreen.MapDaggerModel;
import taxi.ratingen.ui.drawerscreen.mapscreen.MapFragmentProvider;
import taxi.ratingen.ui.drawerscreen.mapscreen.destination.DestinationFragment;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiAct;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiDaggerModel;
import taxi.ratingen.ui.drawerscreen.profilescrn.ProfileAct;
import taxi.ratingen.ui.drawerscreen.profilescrn.edit.NameMailEdit;
import taxi.ratingen.ui.drawerscreen.promoscrn.PromoAct;
import taxi.ratingen.ui.drawerscreen.ridescreen.payment.PaymentMethod;
import taxi.ratingen.ui.drawerscreen.walletscreen.WalletAct;
import taxi.ratingen.ui.drawerscreen.walletscreen.WalletDaggerModel;
import taxi.ratingen.ui.forgot.ForgetPwdActivity;
import taxi.ratingen.ui.getReady.GetReadyAct;
import taxi.ratingen.ui.getstarted.GetStartedScreen;
import taxi.ratingen.ui.login.LoginActivity;
import taxi.ratingen.ui.nodriveralert.NoDriverAct;
import taxi.ratingen.ui.optionalscreen.OptionalAct;
import taxi.ratingen.ui.otpscreen.OTPActivity;
import taxi.ratingen.ui.otpscreen.OtpDaggerModel;
import taxi.ratingen.ui.registration.RegistrationAct;
import taxi.ratingen.ui.registration.RegistrationDaggerModel;
import taxi.ratingen.ui.signup.SignupActivity;
import taxi.ratingen.ui.signup.country_code.CCFragmentProvider;
import taxi.ratingen.ui.sociallogin.SigninSocialActivity;
import taxi.ratingen.ui.splash.SplashScreen;
import taxi.ratingen.ui.tour.TourGuide;
import taxi.ratingen.ui.wallethistory.WalletHistoryAct;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Here bind the subcomponent of Activitie's and
 **/

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = CCFragmentProvider.class)
    abstract SignupActivity bindSignupActivity();

    @ContributesAndroidInjector()
    abstract SplashScreen bindSplashScreen();

    @ContributesAndroidInjector()
    abstract TourGuide bindTourGuide();

    @ContributesAndroidInjector(modules = OtpDaggerModel.class)
    abstract OTPActivity bindOTPActivity();

    @ContributesAndroidInjector(modules = RegistrationDaggerModel.class)
    abstract RegistrationAct bindRegistrationAct();

    @ContributesAndroidInjector()
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector()
    abstract OptionalAct bindOptionalAct();

    @ContributesAndroidInjector()
    abstract ForgetPwdActivity bindForgetPwdActivity();

    @ContributesAndroidInjector()
    abstract SigninSocialActivity bindSigninSocialActivity();

    @ContributesAndroidInjector(modules = MapFragmentProvider.class)
    abstract DrawerAct bindDrawerActActivity();

    @ContributesAndroidInjector(modules = {WalletDaggerModel.class, CCFragmentProvider.class})
    abstract WalletAct bindWalletActActivity();


    @ContributesAndroidInjector(modules = PlaceApiDaggerModel.class)
    abstract PlaceApiAct bindPlaceApiActActivity();


    @ContributesAndroidInjector()
    abstract ApplayRefferal bindPApplayRefferalActivity();

    @ContributesAndroidInjector()
    abstract ProfileAct bindProfileActActivity();

    @ContributesAndroidInjector(modules = CCFragmentProvider.class)
    abstract NameMailEdit bindNameMailEdit();

    @ContributesAndroidInjector()
    abstract PromoAct bindPromoActActivity();

    @ContributesAndroidInjector(modules = MapFragmentProvider.class)
    abstract HistoryDetailsScrn bindHistoryDetailsScrn();


    @ContributesAndroidInjector(modules = {PlaceApiDaggerModel.class, MapFragmentProvider.class})
    abstract SearchPlaceActivity bindSearchPlaceActivity();

    @ContributesAndroidInjector(modules = MapFragmentProvider.class)
    abstract NoDriverAct bindNoNoDriverAct();

    @ContributesAndroidInjector()
    abstract WalletHistoryAct provideWalletHistoryAct();

    @ContributesAndroidInjector(modules = {MapFragmentProvider.class, CCFragmentProvider.class})
    abstract CompanyValidationActivity bindCompanyValidationActivity();

    @ContributesAndroidInjector()
    abstract GetStartedScreen bindGetStartedScreen();

    @ContributesAndroidInjector()
    abstract GetReadyAct bindGetReadyAct();

    @ContributesAndroidInjector()
    abstract DestinationFragment bindDestinationFragment();

    @ContributesAndroidInjector(modules = MapDaggerModel.class)
    abstract PaymentMethod bindPaymentMethod();

}
