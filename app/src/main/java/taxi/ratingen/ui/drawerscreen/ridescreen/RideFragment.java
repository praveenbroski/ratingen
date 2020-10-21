package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.databinding.FragmentRideBinding;

import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiAct;
import taxi.ratingen.ui.drawerscreen.ridescreen.etaparent.ETAParent;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;

import javax.inject.Inject;


public class RideFragment extends BaseFragment<FragmentRideBinding, RideFragViewModel> implements OnMapReadyCallback, RideNavigator {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "RideFragment";

    // TODO: Rename and change types of parameters
    private Type mParam1;
    private String mParam2;

    @Inject
    RideFragViewModel rideFragViewModel;


    Gson gson;

    FragmentRideBinding fragmentRideBinding;
    Animation bottomTotop, topToBottom;
    PaymentBottomSheet paymentBottomSheet;
    ChooseSeatBottomSheet chooseSeatBottomSheet;
    WaitProgressDialog waitProgressDialog;

    public RideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RideFragment newInstance(String param1, String param2) {
        RideFragment fragment = new RideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static RideFragment newInstance(Type param1, String param2) {
        RideFragment fragment = new RideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.PushWaitingorAcceptByDriver));
        if (getArguments() != null) {
            mParam1 = (Type) getArguments().getSerializable(ARG_PARAM1);
            Log.d(TAG, "keys--" + mParam1.preferred_payment);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gson = new GsonBuilder()
                .registerTypeAdapter(BaseResponse.class, new BaseResponse.OptionsDeserilizer())
                .create();


        rideFragViewModel.setNavigator(this);
        fragmentRideBinding = getViewDataBinding();
        fragmentRideBinding.FRDropCard.setUseCompatPadding(true);
        fragmentRideBinding.FRDropCard.setPreventCornerOverlap(false);
        // fragmentRideBinding.FRProgressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.ride_map)).getMapAsync(this);


    }

    @Override
    public RideFragViewModel getViewModel() {
        return rideFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ride;
    }

    /** This callback is called when {@link GoogleMap} loading is completed
     * @param googleMap Reference of {@link GoogleMap} **/
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setPadding(0, 410, 0, 310);
        rideFragViewModel.setPins(mParam1, googleMap);
// Setting up preffered Payment while selection got from Types and While Changing from Payment Screen
        if (rideFragViewModel.sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER)) {
            rideFragViewModel.is_CorporateUser.set(true);
            setCorporateUser(true);
        } else {
            if (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT) != -1 && mParam1.getPaymenttype() != null)
                switch (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT)) {
                    case 0:
                        if (mParam1.getPaymenttype().contains("card") || mParam1.getPaymenttype().contains("all")) {
                            fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                            fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_card));
                            rideFragViewModel.paymentOption.set("0");
                        }
                        break;
                    case 1:
                        if (mParam1.getPaymenttype().contains("cash") || mParam1.getPaymenttype().contains("all")) {
                            fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                            fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_cash));
                            rideFragViewModel.paymentOption.set("1");
                            break;
                        }
                }
        }

    }

    /** Closes current screen and displays previous screen **/
    @Override
    public void goback() {
        getBaseActivity().onFragmentDetached(RideFragment.TAG);
    }

    /** Toggle visibility of views **/
    @Override
    public void ShowDropLayout(boolean hide) {
        if (hide) {
            fragmentRideBinding.FRPaymentLayout.setVisibility(View.GONE);
            fragmentRideBinding.FREnterDropLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentRideBinding.FRPaymentLayout.setVisibility(View.VISIBLE);
            fragmentRideBinding.FREnterDropLayout.setVisibility(View.GONE);
        }
    }

    /** Called when drop location card is clicked. Open {@link PlaceApiAct} to search address **/
    @Override
    public void DropCardClicked() {
        /*AutoCompleteIntent();*/
        rideFragViewModel.getMap().clear();
        rideFragViewModel.getMap().put(Constants.Extra_identity, GetBaseAct().getTranslatedString(R.string.txt_EnterDrop));
        getBaseActivity().startActivityForResult(new Intent(getContext(), PlaceApiAct.class).putExtra(Constants.EXTRA_Data, rideFragViewModel.getMap()), Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE);

    }

    /** Set selected payment type like card, cash or wallet
     * @param a Payment type **/
    @Override
    public void SelectedPaymentType(String a) {
        switch (a) {
            case "card":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_card));

                break;
            case "cash":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_cash));
                break;

            case "Wallet":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_wallet);
                fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_wallet));
                break;
            default:
                break;
        }
    }

    /** Gives reference of {@link BaseActivity} **/
    @Override
    public BaseActivity GetBaseAct() {
        return getBaseActivity();
    }

    /** Show/Hide payment progress bar **/
    @Override
    public void ShowPaymentProgress(boolean hide) {
        if (hide) {
            fragmentRideBinding.FRPrizelayout.setVisibility(View.GONE);
            fragmentRideBinding.FRProgressBar.setVisibility(View.VISIBLE);
        } else {
            fragmentRideBinding.FRPrizelayout.setVisibility(View.VISIBLE);
            fragmentRideBinding.FRProgressBar.setVisibility(View.GONE);
        }
    }

    /** {@link BroadcastReceiver} to receive details about ride confirmation or no driver message **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("RideFragment","RideFrfag");

            if (waitProgressDialog != null) {
                WaitProgressDialog errorDialog = (WaitProgressDialog) getChildFragmentManager()
                        .findFragmentByTag(WaitProgressDialog.TAG);
                try {
                    if (errorDialog != null) {
                        errorDialog.dismiss();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                /* waitProgressDialog.dismissDialog();*/
                waitProgressDialog = null;
            }

            if (intent.hasExtra(Constants.EXTRA_Data)) {

                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = gson.fromJson(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null)
                    getBaseActivity().NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);

                goback();
            } else {
                Toast.makeText(getBaseActivity(), GetBaseAct().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
            }

        }
    };

    /** Shows Payment selection bottom sheet
     * @param type {@link Type} data model **/
    @Override
    public void onClickPayment(Type type) {
        paymentBottomSheet = new PaymentBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.EXTRA_Data, (ArrayList<String>) type.getPaymenttype());
        paymentBottomSheet.setArguments(bundle);
        paymentBottomSheet.setTargetFragment(this, Constants.BOTTOMSHEETCALLBACK);
        paymentBottomSheet.show(getBaseActivity().getSupportFragmentManager(), "BottomSheet Fragment");
    }

    /** Opens seat selection bottom sheet when using share ride
     * @param shareRideDetails {@link ShareRideDetails data model}
     * @param cr String  parameter **/
    @Override
    public void onClickNofSeat(ShareRideDetails shareRideDetails, String cr) {
        chooseSeatBottomSheet = new ChooseSeatBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("Crcy", cr);
        bundle.putString("Fare1", CommonUtils.doubleDecimalFromat(shareRideDetails.one_seat_total) + "");
        bundle.putString("Fare2", CommonUtils.doubleDecimalFromat(shareRideDetails.two_seat_total) + "");
        chooseSeatBottomSheet.setArguments(bundle);
        chooseSeatBottomSheet.setTargetFragment(this, Constants.BOTTOMSHEETCALLBACK);
        chooseSeatBottomSheet.show(getBaseActivity().getSupportFragmentManager(), "BottomSheet_Fragment");

    }

    /** Shows Waiting for Driver progress dialog({@link WaitProgressDialog}) after creating a request
     * @param id Id of the booking request **/
    @Override
    public void ShowWaitingDialog(String id) {
        waitProgressDialog = new WaitProgressDialog();

        WaitProgressDialog.newInstance(id).show(this.getChildFragmentManager());
    }

    /** Returns current {@link Context} **/
    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    /** Returns true/false using added or not **/
    @Override
    public boolean isAttached() {
        return isAdded();
    }

    /** Opens {@link ETAParent} to show the trip fare details **/
    @Override
    public void openETADialog(BaseResponse baseResponse) {
        ETAParent.newInstance(baseResponse).show(getChildFragmentManager());
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomTotop = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top);
        topToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.top_bottom);
        fragmentRideBinding.FRBottomlayout.startAnimation(bottomTotop);

        fragmentRideBinding.FRDropCard.startAnimation(topToBottom);
        fragmentRideBinding.FRRelativeone.startAnimation(topToBottom);
        fragmentRideBinding.rideTool.startAnimation(topToBottom);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void AutoCompleteIntent() {
        Intent intent = null;
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().
                    setTypeFilter(Place.TYPE_COUNTRY).setCountry("IN").build();
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(getBaseActivity());
            getBaseActivity().startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    /** Callback function where the results & data from the previous screen comes
     * @param requestCode Code of the request made
     * @param resultCode Code of the result returned
     * @param data {@link Intent} with data from previous screen **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE) {
            //Intially Pickcard is foreground and guess user has been selected drop in ridefrgament tn we have to refect them &change dropcard foreground in Mapfragment too..
            if (data != null) {
                getBaseActivity().setResultToDropAddress(data != null ? data.getStringExtra(Constants.EXTRA_Data) : "");
                rideFragViewModel.DropProceed.set(false);
                rideFragViewModel.DropAddress.set(data != null ? data.getStringExtra(Constants.EXTRA_Data) : "");
            }


        } else if (requestCode == Constants.BOTTOMSHEETCALLBACK) {
            switch (data.getStringExtra(Constants.EXTRA_Data)) {
                case "card":
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                    fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_card));
                    rideFragViewModel.paymentOption.set("0");
                    break;
                case "cash":

                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                    fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_cash));
                    rideFragViewModel.paymentOption.set("1");
                    break;

                case "wallet":
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_wallet);
                    fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.txt_wallet));
                    rideFragViewModel.paymentOption.set("2");
                    break;
                case "Corporate":
                    fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_grey);
                    fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.text_corporate));
                    break;

                case "one_user":
                    rideFragViewModel.NofSeat.set("Myself");
                    rideFragViewModel.currentShareETA(1);
                    break;
                case "two_user":
                    rideFragViewModel.NofSeat.set("Pair");
                    rideFragViewModel.currentShareETA(2);
                    break;
                default:
                    break;
            }
        }

    }

    /** Set if the current user is corporate user or not by incoming boolean parameter
     * @param isCorporateUser true/false based on the user is corporate or not **/
    @Override
    public void setCorporateUser(boolean isCorporateUser) {
        fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_corporate);
        fragmentRideBinding.FPPaymentTXt.setText(GetBaseAct().getTranslatedString(R.string.text_corporate));
        rideFragViewModel.paymentOption.set("4");
    }

    /** Dismissed Waiting dialog **/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*getBaseActivity().onFragmentDetached(RideFragment.TAG);*/
//        System.out.println("++++++DestroyView+++");
        if (waitProgressDialog != null) {
            WaitProgressDialog errorDialog = (WaitProgressDialog) getChildFragmentManager()
                    .findFragmentByTag(WaitProgressDialog.TAG);
            try {
                if (errorDialog != null) {
                    errorDialog.dismiss();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            /* waitProgressDialog.dismissDialog();*/
            waitProgressDialog = null;
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    /** Logs out the current user **/
    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

}
