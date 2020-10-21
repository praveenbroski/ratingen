package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import taxi.ratingen.retro.responsemodel.MarkerModel;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.retro.responsemodel.Route;
import taxi.ratingen.retro.responsemodel.TripRegisteredDetails;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.ui.drawerscreen.ridescreen.payment.PaymentMethod;
import taxi.ratingen.ui.topdriver.TopDriverAct;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.ShareRideDetails;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.databinding.FragRideConfirmationBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.mapscrn.adapter.CarsTypesAdapter;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiAct;
import taxi.ratingen.ui.drawerscreen.ridescreen.etaparent.ETAParent;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;


public class RideConfirmationFragment extends BaseFragment<FragRideConfirmationBinding, RideConfirmationViewModel> implements OnMapReadyCallback, RideConfirmNavigator {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ParampickupLatlng = "pickupLatlng";
    private static final String ParamdropLatlng = "dropLatlng";
    private static final String ParamcurrentLatlng = "currentLatlng";
    private static final String ParampickupAddr = "pickupAddr";
    private static final String ParamdropAddr = "dropAddr";
    private static final String SCANCONTENT = "scanContent";
    private static final String RIDELATERDATE = "rideLaterDate";

    private static final String RIDETYPE = "ridetype";

    private static final String DRIVER_PINS = "driverPins";
    private static final String DRIVER_DATAS = "driverDatas";

    public static final String TAG = "RideConfirmation";

    @Inject
    RideConfirmationViewModel rideFragViewModel;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    CarsTypesAdapter adapter;
    LinearLayoutManager mLayoutManager;
    FragRideConfirmationBinding fragmentRideBinding;
    Animation bottomTotop, topToBottom;
    PaymentBottomSheet paymentBottomSheet;
    RideTypeBottomSheet rideTypeBottomSheet;
    ChooseSeatBottomSheet chooseSeatBottomSheet;
    WaitProgressDialog waitProgressDialog;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    private SimpleDateFormat mtimeFormatter = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
    private SimpleDateFormat mdateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private boolean isViaPrivateKey = false;

    String type_id = "";
    String req_id = "";
    String user_id = "";
    String user_token = "";
    double latitude = 0.0;
    double longitude = 0.0;

    private LatLng crLatlng, dropUPLatlng, pickUpLatlng;
    private String pickupAddress, dropAddress, scanContent, dateformat;
    private int ride_type = 1;
    public HashMap<String, Marker> driverPins;
    public HashMap<String, MarkerOptions> driverPinOptions;
    public HashMap<String, String> driverDatas;

    Type getType;
    TypeNew getTypeNew;
    String bookedID = "";
    Route routeDest = new Route();

    public RideConfirmationFragment() {
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
    public static RideConfirmationFragment newInstance(String param1, String param2) {
        RideConfirmationFragment fragment = new RideConfirmationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /** Creates a new instance using the provided parameters
     * @param pickup Pickup address
     * @param pickupLat Pickup {@link android.location.Location} {@link LatLng}
     * @param drop Drop address
     * @param dropLat Drop {@link android.location.Location} {@link LatLng}
     * @param currentLat Current {@link LatLng}
     * @param value String parameter **/
    public static Fragment newInstance(String pickup, LatLng pickupLat, String drop, LatLng dropLat, LatLng currentLat, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        RideConfirmationFragment fragment = new RideConfirmationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParampickupLatlng, pickupLat);
        args.putParcelable(ParamdropLatlng, dropLat);
        args.putParcelable(ParamcurrentLatlng, currentLat);
        args.putString(ParampickupAddr, pickup);
        args.putString(ParamdropAddr, drop);
        args.putString(SCANCONTENT, value);
        args.putInt(RIDETYPE, 1);
        //        args.putSerializable(DRIVER_PINS, driverPins);
        ArrayList<MarkerModel> markerModels = new ArrayList<>();
        for (String key: driverPins.keySet()) {
            Marker marker = driverPins.get(key);
            MarkerModel markerModel = new MarkerModel();
            markerModel.setLat(marker.getPosition().latitude);
            markerModel.setLng(marker.getPosition().longitude);
            markerModel.setRotation(marker.getRotation());
            markerModel.setId(key);
            markerModels.add(markerModel);
        }
        args.putSerializable(DRIVER_PINS, markerModels);
        args.putSerializable(DRIVER_DATAS, driverDatas);

        fragment.setArguments(args);
        return fragment;
    }

    /** Creates a new instance using the provided parameters
     * @param pickup Pickup address
     * @param pickupLat Pickup {@link android.location.Location} {@link LatLng}
     * @param currentLat Current {@link LatLng}
     * @param value String parameter **/
    public static Fragment newInstance(String pickup, LatLng pickupLat, LatLng currentLat, String value, HashMap<String, Marker> driverPins, HashMap<String, String> driverDatas) {
        RideConfirmationFragment fragment = new RideConfirmationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParampickupLatlng, pickupLat);
//        args.putParcelable(ParamdropLatlng, dropLat);
        args.putParcelable(ParamcurrentLatlng, currentLat);
        args.putString(ParampickupAddr, pickup);
//        args.putString(ParamdropAddr, drop);
        args.putString(SCANCONTENT, value);
        args.putInt(RIDETYPE, 1);
        //        args.putSerializable(DRIVER_PINS, driverPins);
        ArrayList<MarkerModel> markerModels = new ArrayList<>();
        for (String key: driverPins.keySet()) {
            Marker marker = driverPins.get(key);
            MarkerModel markerModel = new MarkerModel();
            markerModel.setLat(marker.getPosition().latitude);
            markerModel.setLng(marker.getPosition().longitude);
            markerModel.setRotation(marker.getRotation());
            markerModel.setId(key);
            markerModels.add(markerModel);
        }
        args.putSerializable(DRIVER_PINS, markerModels);
        args.putSerializable(DRIVER_DATAS, driverDatas);

        fragment.setArguments(args);
        return fragment;
    }

    /** Creates a new instance using the provided parameters
     * @param pickupAddress Pickup address
     * @param pickupLatlng Pickup {@link android.location.Location} {@link LatLng}
     * @param dropadress Drop address
     * @param dropLatlng Drop {@link android.location.Location} {@link LatLng}
     * @param currLatLng Current {@link LatLng}
     * @param format String parameter
     * @param rideType Ride now/Ride later **/
    public static Fragment RideLater(String pickupAddress, LatLng pickupLatlng, String dropadress, LatLng dropLatlng, LatLng currLatLng, String format, int rideType) {

        RideConfirmationFragment fragment = new RideConfirmationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParampickupLatlng, pickupLatlng);
        args.putParcelable(ParamdropLatlng, dropLatlng);
        args.putParcelable(ParamcurrentLatlng, currLatLng);
        args.putString(ParampickupAddr, pickupAddress);
        args.putString(ParamdropAddr, dropadress);
        args.putString(RIDELATERDATE, format);
        args.putInt(RIDETYPE, rideType);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.PushWaitingorAcceptByDriver));

        if (getArguments() != null) {
            crLatlng = getArguments().getParcelable(ParamcurrentLatlng);
            if (getArguments().getParcelable(ParamdropLatlng) != null)
                dropUPLatlng = getArguments().getParcelable(ParamdropLatlng);
            pickUpLatlng = getArguments().getParcelable(ParampickupLatlng);
            pickupAddress = getArguments().getString(ParampickupAddr);
            if (getArguments().getString(ParamdropAddr) != null)
                dropAddress = getArguments().getString(ParamdropAddr);
            scanContent = getArguments().getString(SCANCONTENT);
            dateformat = getArguments().getString(RIDELATERDATE);
            ride_type = getArguments().getInt(RIDETYPE);
            isViaPrivateKey = (!CommonUtils.IsEmpty(scanContent));
            HashMap<String, MarkerOptions> markerMap = new HashMap<>();
            if (getArguments().getSerializable(DRIVER_PINS) != null) {
//                driverPins = (HashMap<String, Marker>) getArguments().getSerializable(DRIVER_PINS);
                ArrayList<MarkerModel> markerModels = (ArrayList<MarkerModel>) getArguments().getSerializable(DRIVER_PINS);
                BitmapDescriptor bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.ic_new_car);
                for (MarkerModel mm: markerModels) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(mm.getLat(), mm.getLng())).anchor(0.5f, 0.5f).rotation((float) mm.getRotation()).icon(bitmapDescriptorFactory);
                    markerMap.put(mm.getId(), markerOptions);
                }
            }
            this.driverPinOptions = markerMap;
            if (getArguments().getSerializable(DRIVER_DATAS) != null)
                driverDatas = (HashMap<String, String>) getArguments().getSerializable(DRIVER_DATAS);

            Log.v("fatal_log", driverDatas.toString());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rideFragViewModel.setNavigator(this);
        fragmentRideBinding = getViewDataBinding();

//        fragmentRideBinding.toolbar.setNavigationOnClickListener(v -> {
//            goback();
//        });

        fragmentRideBinding.backImg.setOnClickListener(v -> {
            goback();
        });

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentRideBinding.CarsRecyclerView.setLayoutManager(mLayoutManager);
        fragmentRideBinding.CarsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fragmentRideBinding.CarsRecyclerView.setAdapter(adapter);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ride_map)).getMapAsync(this);
//        rideFragViewModel.SetSocketListener();
//        rideFragViewModel.startTypesTimer();

        rideFragViewModel.rideType = ride_type;
        rideFragViewModel.dateFormat = dateformat;
        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(fragmentRideBinding.typesBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        fragmentRideBinding.bottomSheetPersistent.setOnClickListener(v -> {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public RideConfirmationViewModel getViewModel() {
        return rideFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_ride_confirmation;
    }

    /** Callback for {@link GoogleMap} loading complete
     * @param googleMap {@link GoogleMap} **/
    @Override
    public void onMapReady(final GoogleMap googleMap) {
//        googleMap.setPadding(0, 410, 0, 610);
        if (sharedPrefence.GetBoolean(SharedPrefence.MAPTYPE)) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //  tripFragViewModel.mapType.set(true);
        } else {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //  tripFragViewModel.mapType.set(false);
        }
        HashMap<String, Marker> markerHashMap = new HashMap<>();
        for (String key: driverPinOptions.keySet()) {
            MarkerOptions markerOptions = driverPinOptions.get(key);
            Marker marker = googleMap.addMarker(markerOptions);
            marker.setVisible(false);
            markerHashMap.put(key, marker);
        }
        this.driverPins = markerHashMap;
        rideFragViewModel.setPins(pickUpLatlng, dropUPLatlng, pickupAddress, dropAddress, googleMap, scanContent, driverPins, driverDatas);

    }

    /** Go back to previous screen **/
    @Override
    public void goback() {
        if (getBaseActivity() != null)
            getBaseActivity().onFragmentDetached(RideConfirmationFragment.TAG);
    }

    /** Calls {@link PlaceApiAct} to search drop location when drop location card is clicked **/
    @Override
    public void DropCardClicked() {
        rideFragViewModel.getMap().clear();
        rideFragViewModel.getMap().put(Constants.Extra_identity, getBaseAct().getTranslatedString(R.string.txt_EnterDrop));
        getBaseActivity().startActivityForResult(new Intent(getContext(), PlaceApiAct.class).putExtra(Constants.EXTRA_Data, rideFragViewModel.getMap()), Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE);

    }

    /** Sets selected payment method
     * @param a card, cash or wallet **/
    @Override
    public void SelectedPaymentType(String a) {
        switch (a) {
            case "card":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_card));

                break;
            case "cash":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_cash));
                break;

            case "Wallet":
                fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_wallet);
                fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_wallet));
                break;
            default:
                break;
        }
    }

    /** Returns a reference of {@link BaseActivity} **/
    @Override
    public BaseActivity GetBaseAct() {
        return getBaseActivity();
    }

    @Override
    public void ShowPaymentProgress(boolean hide) {
    }

    /** {@link BroadcastReceiver} to listen to about driver availability for ride **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("RideConfirmation", "RideConfirmation");

           /* if (waitProgressDialog != null) {
                WaitProgressDialog errorDialog = (WaitProgressDialog) getChildFragmentManager()
                        .findFragmentByTag(WaitProgressDialog.TAG);
                try {
                    if (errorDialog != null) {
                        errorDialog.dismissAllowingStateLoss();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                *//* waitProgressDialog.dismissDialog();*//*
                waitProgressDialog = null;
            }*/
            ((BaseActivity) getActivity()).removeWaitProgressDialog();

            /* if (MyApp.clearMethd()) {*/
            // }

            if (intent.hasExtra(Constants.EXTRA_Data)) {

                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = CommonUtils.getSingleObject(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null) {
                    if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                        //  showMessage(getActivity().getString(R.string.Txt_DriverAccepted));

                    } else {
                        if (getBaseActivity() != null)
                            getBaseActivity().NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);
                    }
                    goback();
                } else if (baseResponse.successMessage != null &&
                        (baseResponse.successMessage.contains("no driver") || baseResponse.successMessage.contains("no_driver") || baseResponse.successMessage.contains("no_driver_found"))) {
                    Toast.makeText(getBaseActivity(), getBaseAct().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
                }
            } else {
               /* if (getActivity() != null && getActivity().getSupportFragmentManager() != null && getActivity().getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG) != null)
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .disallowAddToBackStack()
                            .remove(Objects.requireNonNull(getActivity().getSupportFragmentManager().findFragmentByTag(RideConfirmationFragment.TAG)))
                            .commitAllowingStateLoss();*/
                startActivity(new Intent(getActivity(), DrawerAct.class));
            }
//            } else {
//                Toast.makeText(getBaseActivity(), getString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
//            }

        }
    };

    /** Displays payment type selection {@link com.google.android.material.bottomsheet.BottomSheetDialog} with available payment methods
     * @param type {@link Type} response model **/
    @Override
    public void onClickPayment(Type type) {
        if (type == null)
            return;

        Intent intent = new Intent(getActivity(), PaymentMethod.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.EXTRA_PAYMENT_BUNDLE, (ArrayList<String>) type.getPaymenttype());
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.BOTTOMSHEETCALLBACK);

//        paymentBottomSheet = new PaymentBottomSheet();
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList(Constants.EXTRA_Data, (ArrayList<String>) type.getPaymenttype());
//        paymentBottomSheet.setArguments(bundle);
//        paymentBottomSheet.setTargetFragment(this, Constants.BOTTOMSHEETCALLBACK);
//        paymentBottomSheet.show(getBaseActivity().getSupportFragmentManager(), "BottomSheet Fragment");

//        Intent intent = new Intent(getAttachedcontext(), PaymentMethod.class);
////        Bundle bundle = new Bundle();
////        bundle.putStringArrayList(Constants.EXTRA_Data, (ArrayList<String>) type.getPaymenttype());
////        intent.putExtra(Constants.EXTRA_PAYMENT_BUNDLE, bundle);
//        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT_METHOD);
    }

//    @Override
//    public void onClickPayment(TypeNew type) {
//        if (type == null)
//            return;
//        paymentBottomSheet = new PaymentBottomSheet();
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList(Constants.EXTRA_Data, (ArrayList<String>) type.getPaymentType());
//        paymentBottomSheet.setArguments(bundle);
//        paymentBottomSheet.setTargetFragment(this, Constants.BOTTOMSHEETCALLBACK);
//        paymentBottomSheet.show(getBaseActivity().getSupportFragmentManager(), "BottomSheet Fragment");
//
////        Intent intent = new Intent(getAttachedcontext(), PaymentMethod.class);
//////        Bundle bundle = new Bundle();
//////        bundle.putStringArrayList(Constants.EXTRA_Data, (ArrayList<String>) type.getPaymenttype());
//////        intent.putExtra(Constants.EXTRA_PAYMENT_BUNDLE, bundle);
////        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT_METHOD);
//    }

    /** Displays ride type selection {@link com.google.android.material.bottomsheet.BottomSheetDialog} with available types
     * @param isAcceptShare true/false based on selection availability **/
    @Override
    public void onClickRideType(boolean isAcceptShare) {
        rideTypeBottomSheet = new RideTypeBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRA_RIDE_TYPE, isAcceptShare);
        rideTypeBottomSheet.setArguments(bundle);
        rideTypeBottomSheet.setTargetFragment(this, Constants.BOTTOMSHEETRIDECALLBACK);
        rideTypeBottomSheet.show(getBaseActivity().getSupportFragmentManager(), "BottomSheet Ride Frag");
    }

    /** Displays no. of seats selection {@link com.google.android.material.bottomsheet.BottomSheetDialog}
     * @param shareRideDetails {@link ShareRideDetails} response model
     * @param cr String parameter **/
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

    /** Displays waiting for driver dialog screen after confirming ride
     * @param id Id of the request **/
    @Override
    public void ShowWaitingDialog(String id) {
        waitProgressDialog = new WaitProgressDialog();
        WaitProgressDialog.newInstance(id).show(getActivity().getSupportFragmentManager());
    }

    /** Returns current {@link Context} **/
    @Override
    public Context getAttachedcontext() {
        return getContext();
    }

    /** Returns true/false by determining attached or not **/
    @Override
    public boolean isAttached() {
        return isAdded();
    }

    /** Opens {@link ETAParent} dialog **/
    @Override
    public void openETADialog(BaseResponse baseResponse) {
        ETAParent.newInstance(baseResponse).show(getChildFragmentManager());
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomTotop = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top);
        topToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.top_bottom);
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

    /** Callback to get result from previous screen
     * @param requestCode Code of the requests
     * @param resultCode Code of the {@link Intent} result
     * @param data {@link Intent} with data returned from previous screen **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETEINRIDE) {
            //Intially Pickcard is foreground and guess user has been selected drop in ridefrgament tn we have to refect them &change dropcard foreground in Mapfragment too..
            if (data != null && getBaseActivity() != null) {
                getBaseActivity().setResultToDropAddress(data.getStringExtra(Constants.EXTRA_Data));
                rideFragViewModel.DropProceed.set(false);
                rideFragViewModel.DropAddress.set(data.getStringExtra(Constants.EXTRA_Data));
            }


        } else if (requestCode == Constants.BOTTOMSHEETCALLBACK && data.hasExtra(Constants.EXTRA_Data)) {
            switch (data.getStringExtra(Constants.EXTRA_Data)) {
                case "card":
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                    fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_card));
                    rideFragViewModel.paymentOption.set("0");
                    break;
                case "cash":

                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                    fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_cash));
                    rideFragViewModel.paymentOption.set("1");
                    break;

                case "wallet":
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_wallet);
                    fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_wallet));
                    rideFragViewModel.paymentOption.set("2");
                    break;
                case "Corporate":
                    fragmentRideBinding.FPPaymentArrow.setVisibility(View.GONE);
                    fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_grey);
                    fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.text_corporate));
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
        } else if (requestCode == Constants.BOTTOMSHEETRIDECALLBACK) {
            rideFragViewModel.isShare = data.getBooleanExtra(Constants.EXTRA_RIDE_TYPE, false) ? 1 : 0;
            rideFragViewModel.isShareRide.set(data.getBooleanExtra(Constants.EXTRA_RIDE_TYPE, false));
        } else if (requestCode == Constants.RIDE_PROMO_RESULT) {
            if (data != null) {
                bookedID = data.getStringExtra("BookedID");
                rideFragViewModel.isPromodone.set(true);
                if (isViaPrivateKey) {
                    rideFragViewModel.ETANetWorkcall(scanContent, ride_type, bookedID);
                } else {
                    rideFragViewModel.setDriverDetails(getType);
                    rideFragViewModel.ETANetWorkcall(getType, ride_type, bookedID);
                }
            }

            //   }

        }

    }

    /** Sets Payment option to corporate based on the boolean parameter
     * @param isCorporateUser true/false **/
    @Override
    public void setCorporateUser(boolean isCorporateUser) {
        fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_corporate);
        fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.text_corporate));
        rideFragViewModel.paymentOption.set("4");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rideFragViewModel.stopTypesTimer();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    /** Logs out the current user **/
    @Override
    public void logoutApp() {
        if (getActivity() != null)
            if (getActivity() instanceof DrawerAct)
                ((DrawerAct) getActivity()).logout();
    }

    /** Populates available cars to {@link List}
     * @param types {@link List} with cars data
     * @param defaultTypes default selected type **/
    @Override
    public void addcarList(List<TypeNew> types, int defaultTypes) {
//    boolean isVehicleSet=false;
      /*  if (types != null && types.size() > 0) {
            for (Type vehicle : types)
                if (vehicle != null && vehicle.drivers != null && vehicle.drivers.size() > 0 && adapter.getSelectedCar() == null) {
                    adapter.selectedCarId = vehicle.id;
                    carSlected(vehicle);
                    break;
                }
        }*/

        if (types != null && types.size() > 0 && adapter.getSelectedCar() == null) {
            for (TypeNew vehicle : types)
                if (vehicle != null /*&& vehicle.drivers != null && vehicle.drivers.size() > 0 */ && adapter.getSelectedCar() == null) {
                    if (vehicle.typeId != null && vehicle.typeId== defaultTypes) {
                        adapter.selectedCarId = vehicle.typeId;
                        carSlected(vehicle);
                        break;
                    }
                }
        }
        if (!isViaPrivateKey && adapter.selectedCarId == null && adapter.getSelectedCar() == null && types.size() > 0 && types.get(0) != null) {
            carSlected(types.get(0));
            adapter.selectedCarId = types.get(0).typeId;
        }
        adapter.addList(types);
    }

//    @Override
//    public void addCarListNew(List<TypeNew> types, int defaultTypes) {
////        if (types != null && types.size() > 0 && adapter.getSelectedCar() == null) {
////
////        }
//
//        if (!isViaPrivateKey && adapter.selectedCarId == null && adapter.getSelectedCar() == null && types.size() > 0 && types.get(0) != null) {
//            typeSelected(types.get(0));
//            adapter.selectedCarId = types.get(0).zoneId;
//        }
//        adapter.addList(types);
//        if (!isViaPrivateKey && types.size() > 0 && types.get(0) != null) {
//            typeSelected(types.get(0));
//        }
//    }

    @Override
    public void onClickTripSchedule() {
        BottomSheetDialog scheduleDialog = new BottomSheetDialog(getAttachedcontext(), R.style.AppBottomSheetDialogTheme);
        scheduleDialog.setContentView(R.layout.layout_schedule_bottom);
        scheduleDialog.show();

        TextView tLaterTitle = scheduleDialog.findViewById(R.id.txt_schedule_title);
        TextView tLaterText1 = scheduleDialog.findViewById(R.id.txt_schedule_text_1);
        TextView tLaterText2 = scheduleDialog.findViewById(R.id.txt_schedule_text_2);
        SingleDateAndTimePicker picker = scheduleDialog.findViewById(R.id.schedule_picker);
        Button bBook = scheduleDialog.findViewById(R.id.btn_schedule_book);

        tLaterTitle.setText(getBaseAct().getTranslatedString(R.string.txt_pickup_time));
        tLaterText1.setText(getBaseAct().getTranslatedString(R.string.txt_schedule_ride));
        tLaterText2.setText(getBaseAct().getTranslatedString(R.string.txt_schedule_ride_desc));
        bBook.setText(getBaseAct().getTranslatedString(R.string.txt_ConfirmBooking));

        SimpleDateFormat mFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
        SimpleDateFormat mtimeFormatter = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        SimpleDateFormat mdateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

        bBook.setOnClickListener(v -> {
            String current_date = mdateFormatter.format(new Date());
            String selected_date = mdateFormatter.format(picker.getDate());
            try {
                if (mdateFormatter.parse(current_date).compareTo(mdateFormatter.parse(selected_date)) == 0) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.MINUTE, 59);

                    String After30Time = mtimeFormatter.format(now.getTime());
                    String Selectedtime = mtimeFormatter.format(picker.getDate());


                    long different = mtimeFormatter.parse(After30Time).getTime() - mtimeFormatter.parse(Selectedtime).getTime();

                    if (different < 0) {
                        /* we can call ride later */
                        rideFragViewModel.OnClickConfirmBooking(mFormatter.format(picker.getDate()));
                        scheduleDialog.dismiss();
                    } else {
                        showMessage(getBaseAct().getTranslatedString(R.string.Txt_Schedule_Alert));
                    }
                } else {
                    /* we can call ride later */
                    rideFragViewModel.OnClickConfirmBooking(mFormatter.format(picker.getDate()));
                    scheduleDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClickNotesToDriver() {
        BottomSheetDialog notesDialog = new BottomSheetDialog(getAttachedcontext(), R.style.AppBottomSheetDialogTheme);
        notesDialog.setContentView(R.layout.layout_notes_bottom);
        notesDialog.show();

        TextView tNotesTitle = notesDialog.findViewById(R.id.txt_notes_title);
        TextView tNotesDesc = notesDialog.findViewById(R.id.txt_notes_desc);
        EditText eNotes = notesDialog.findViewById(R.id.et_driver_notes);
        Button bNotes = notesDialog.findViewById(R.id.btn_driver_notes);

        tNotesTitle.setText(getBaseAct().getTranslatedString(R.string.txt_note_driver));
        tNotesDesc.setText(getBaseAct().getTranslatedString(R.string.txt_note_driver_hint));
        eNotes.setHint(getBaseAct().getTranslatedString(R.string.txt_note_example));
        bNotes.setText(getBaseAct().getTranslatedString(R.string.txt_confirm));

        eNotes.setText(rideFragViewModel.driverNotes.get());

        bNotes.setOnClickListener(v -> {
            String sNote = eNotes.getText().toString().trim();
            rideFragViewModel.driverNotes.set(sNote);
            notesDialog.dismiss();
        });

//        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
//        View dialogView = LayoutInflater.from(getAttachedcontext()).inflate(R.layout.ride_later_success_alert, viewGroup, false);
//        Button okButt = dialogView.findViewById(R.id.submit);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getAttachedcontext());
//        builder.setView(dialogView);
//        builder.setCancelable(false);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        okButt.setOnClickListener(v -> alertDialog.dismiss());
//        alertDialog.show();
    }

    @Override
    public void setNoDrivers() {
//        fragmentRideBinding.time.setText("NA");
        rideFragViewModel.isDriversAvailable.set(false);
    }

    @Override
    public boolean isAddedInAct() {
        return isAdded();
    }

    @Override
    public void setRouteData(Route routeDest) {
        this.routeDest = routeDest;
    }

    @Override
    public void openTripFragment(Request request) {

    }

    @Override
    public void promoCodeSet(String booked_id) {
//        bookedID = booked_id;
//        rideFragViewModel.isPromodone.set(true);
    }

    @Override
    public void refreshTypesAdapter(String etaPrice, String etaTime) {
        ArrayList<TypeNew> newTypes = new ArrayList<>();
        ArrayList<TypeNew> types = adapter.getTypes();
        for (TypeNew type: types) {
            if (type.typeId == rideFragViewModel.type.id) {
                type.etaPrice = etaPrice;
                type.etaTime = etaTime;
                type.isselected = true;
            }
            newTypes.add(type);
        }
        adapter.addList(newTypes);
        adapter.notifyDataSetChanged();
    }

    /** Returns a reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getBaseAct() {
        return getBaseActivity();
    }

    /** Returns selected car object model **/
    @Override
    public Type GetSelectedCarObj() {
        return adapter.getSelectedCar();
    }

    @Override
    public TypeNew getNewSelectedCar() {
        return adapter.getSelectedNewCar();
    }

    /** Called when a car is selected
     * @param type Car data model **/
    public void carSlected(TypeNew type) {
//        if (type != null && type.typeId != null && rideFragViewModel != null) {
//            rideFragViewModel.setDriverMarkers(rideFragViewModel.driverPins);
//        }

//        if (type != null)
//            getType = type;

        JSONObject object = new JSONObject();
        try {
            object.put("id", sharedPrefence.Getvalue(SharedPrefence.ID));
            object.put("type", type.typeId + "");
            object.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            object.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            object.put("pick_lat", pickUpLatlng.latitude + "");
            object.put("pick_lng", pickUpLatlng.longitude + "");
            object.put("pickup_address", pickupAddress);
            SocketHelper.sendRiderByTypes(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isViaPrivateKey) {
            rideFragViewModel.ETANetWorkcall(scanContent, ride_type, bookedID);
        } else {
//            rideFragViewModel.setDriverDetails(type);
//            rideFragViewModel.ETANetWorkcall(type, ride_type, bookedID);
        }

        if (rideFragViewModel.sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER)) {
            rideFragViewModel.is_CorporateUser.set(true);
            setCorporateUser(true);
        } else {
            if (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT) != -1 && type.paymentType != null)
                switch (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT)) {
                    case 0:
                        if (type.paymentType.contains("card") || type.paymentType.contains("all")) {
                            fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                            fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_card));
                            rideFragViewModel.paymentOption.set("0");
                        }
                        break;
                    case 1:
                        if (type.paymentType.contains("cash") || type.paymentType.contains("all")) {
                            fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                            fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_cash));
                            rideFragViewModel.paymentOption.set("1");
                            break;
                        }
                }
        }

    }

    /** Setups available payment options returned by server via API
     * @param payment {@link List} of available payment options(card, cash, wallet, etc.,) **/
    @Override
    public void setUpPayment(List<String> payment) {
        if (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT) != -1 && payment != null)
            switch (rideFragViewModel.sharedPrefence.getInt(SharedPrefence.PREFFERED_PAYMENT)) {
                case 0:
                    if (payment.contains("card") || payment.contains("all")) {
                        fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_card_yellow);
                        fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_card));
                        rideFragViewModel.paymentOption.set("0");
                    }
                    break;
                case 1:
                    if (payment.contains("cash") || payment.contains("all")) {
                        fragmentRideBinding.FPPaymentsymbol.setImageResource(R.drawable.ic_cash);
                        fragmentRideBinding.FPPaymentTXt.setText(getBaseAct().getTranslatedString(R.string.txt_cash));
                        rideFragViewModel.paymentOption.set("1");
                        break;
                    }
            }
    }

    /** Called when ride later options is chosen. Calls {@link SlideDateTimePicker} dialog to select desired ride date & time **/
    @Override
    public void RideLaterClicked() {
        new SlideDateTimePicker.Builder(getChildFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                .setIs24HourTime(false)
                .build()
                .show();

    }

    /** Listens to changes in {@link SlideDateTimePicker} via {@link SlideDateTimeListener} **/
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            long different = 0;

            String currentdate = mdateFormatter.format(new Date());
            String selecteddate = mdateFormatter.format(date);
            if (rideFragViewModel.type == null)
                return;
            try {
                if (mdateFormatter.parse(currentdate).compareTo(mdateFormatter.parse(selecteddate)) == 0) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.MINUTE, 15);

                    String After30Time = mtimeFormatter.format(now.getTime());
                    String Selectedtime = mtimeFormatter.format(date);


                    different = mtimeFormatter.parse(After30Time).getTime() - mtimeFormatter.parse(Selectedtime).getTime();

                    if (different < 0) {
                        rideFragViewModel.type.setdate(mFormatter.format(date));
                        rideFragViewModel.type.setIsFrom(Constants.RideLater);
                        rideFragViewModel.type.setpicklatlng(pickUpLatlng);
                        rideFragViewModel.type.setDroplatlng(dropUPLatlng);
                        rideFragViewModel.type.setpickAddress(pickupAddress);
                        rideFragViewModel.type.setDropAddress(dropAddress);
                        rideFragViewModel.type.setScanContent(scanContent);
                        rideFragViewModel.OnClickConfirmBooking("");
                    } else {
                        showMessage(getBaseAct().getTranslatedString(R.string.Txt_Schedule_Alert));
                    }

                } else {
                    rideFragViewModel.type.setdate(mFormatter.format(date));
                    rideFragViewModel.type.setIsFrom(Constants.RideLater);
                    rideFragViewModel.type.setpicklatlng(pickUpLatlng);
                    rideFragViewModel.type.setDroplatlng(dropUPLatlng);
                    rideFragViewModel.type.setpickAddress(pickupAddress);
                    rideFragViewModel.type.setDropAddress(dropAddress);
                    rideFragViewModel.type.setScanContent(scanContent);
                    rideFragViewModel.OnClickConfirmBooking("");
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    /** Notifies to the user there are no drivers available **/
    @Override
    public void notifyNoDriverMessage() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).removeWaitProgressDialog();

            Message message = mHandler.obtainMessage(1, getBaseAct().getTranslatedString(R.string.Txt_NoDriverFound));
            message.sendToTarget();
        }
    }

    /** Asks the users they want top rated drivers should be chosen or not
     * @param currency Local currency of the country
     * @param driverAddCharges Driver additional charges **/
    @Override
    public void openAlert(String currency, double driverAddCharges) {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.top_driver_alert, viewGroup, false);
        TextView title = dialogView.findViewById(R.id.alert_title);
        TextView content = dialogView.findViewById(R.id.alert_content);
        TextView amount = dialogView.findViewById(R.id.alert_amount_shown);
        TextView yes = dialogView.findViewById(R.id.yes_txt);
        TextView no = dialogView.findViewById(R.id.no_txt);

        yes.setText(getBaseAct().getTranslatedString(R.string.text_yes));
        no.setText(getBaseAct().getTranslatedString(R.string.text_no));
        title.setText(getBaseAct().getTranslatedString(R.string.app_name));
        content.setText(getBaseAct().getTranslatedString(R.string.do_you_want_top_driver));
        if (driverAddCharges < 1)
            amount.setText(getBaseAct().getTranslatedString(R.string.add_charges) + " " + currency + " " + CommonUtils.doubleDecimalFromat(driverAddCharges) + " " + getBaseAct().getTranslatedString(R.string.applicable));
        else amount.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                rideFragViewModel.topdriverApi();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /** Opens promo code activity when apply promo option is choosen
     * @param id Id of the request **/
    @Override
    public void onclickpromoCode(Integer id) {
//        Intent intent = new Intent(getBaseActivity(), PromoAct.class);
//        intent.putExtra("isRide", "1");
//        intent.putExtra("typeId", id);
//        getBaseActivity().startActivityForResult(intent, Constants.RIDE_PROMO_RESULT);
//        getBaseActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        BottomSheetDialog promoDialog = new BottomSheetDialog(getAttachedcontext(), R.style.AppBottomSheetDialogTheme);
        promoDialog.setContentView(R.layout.promo_bottom_sheet);
        promoDialog.show();

        TextView tPromoTitle = promoDialog.findViewById(R.id.txt_promo_title);
        TextView tPromoDesc = promoDialog.findViewById(R.id.txt_promo_desc);
        EditText ePromoCode = promoDialog.findViewById(R.id.et_promo_code);
        Button bApplyPromo = promoDialog.findViewById(R.id.btn_apply_promo);

        tPromoTitle.setText(getBaseAct().getTranslatedString(R.string.txt_add_title_promo));
        tPromoDesc.setText(getBaseAct().getTranslatedString(R.string.txt_promo_note));
        ePromoCode.setHint(getBaseAct().getTranslatedString(R.string.txt_promo_code));
        bApplyPromo.setText(getBaseAct().getTranslatedString(R.string.txt_apply_promo_code));

        bApplyPromo.setOnClickListener(v -> {
            String sPromoCode = ePromoCode.getText().toString().trim();
            if (CommonUtils.IsEmpty(sPromoCode)) {
                showMessage(getBaseAct().getTranslatedString(R.string.Validate_Promocode));
            } else {
                rideFragViewModel.applyPromoAPICall(getTypeNew.zoneId, sPromoCode);
            }
        });
    }

    /** Strikes out original price and displays new discounted price after promo code applied successfully **/
    @Override
    public void promoAvail() {
//        fragmentRideBinding.fare.setPaintFlags(fragmentRideBinding.fare.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        fragmentRideBinding.fare.setTextSize(12f);
//        if (getActivity() != null)
//            fragmentRideBinding.fare.setTextColor(getActivity().getResources().getColor(R.color.clr_gray_light2));
//        else
//            fragmentRideBinding.fare.setTextColor(getResources().getColor(R.color.clr_gray_light2));
    }

    /** Alerts to user they are blocked from booking any ride **/
    @Override
    public void openBlockedAlert() {
        if (getActivity() != null) {
            ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.user_blocked_lay, viewGroup, false);
            TextView title = dialogView.findViewById(R.id.alert_title);
            TextView content = dialogView.findViewById(R.id.alert_content);
            TextView submit = dialogView.findViewById(R.id.okButton);

            title.setText(getBaseAct().getTranslatedString(R.string.app_name));
            content.setText(getBaseAct().getTranslatedString(R.string.user_blocked_text));
            submit.setText(getBaseAct().getTranslatedString(R.string.text_submit));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            dialogView.findViewById(R.id.okButton);
            dialogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    goback();
                }
            });
            alertDialog.show();
        }

    }

    /** Notifies user the trip has been registered successfully at registered time **/
    @Override
    public void openTripRegisteredAlert(TripRegisteredDetails tripRegisteredDetails) {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.user_blocked_lay, viewGroup, false);
        TextView title = dialogView.findViewById(R.id.alert_title);
        TextView content = dialogView.findViewById(R.id.alert_content);
        TextView submit = dialogView.findViewById(R.id.okButton);
        SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
        SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);
        String registeredTime = "", startTime = "", endTime = "";
        try {
            registeredTime = TargetFormatter.format(realformatter.parse(tripRegisteredDetails.getTripRegisteredAt()));
            startTime = TargetFormatter.format((realformatter.parse(tripRegisteredDetails.getTripPeriodStartAt())));
            endTime = TargetFormatter.format(realformatter.parse(tripRegisteredDetails.getTripPeriodEndAt()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        title.setText(getBaseAct().getTranslatedString(R.string.app_name));
        content.setText(getBaseAct().getTranslatedString(R.string.txt_trip_scheduleAt) + " " + registeredTime + "." + getBaseAct().getTranslatedString(R.string.txt_cannot_reg_trip) + " " + startTime + " To " + endTime);
        submit.setText(getBaseAct().getTranslatedString(R.string.text_ok));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogView.findViewById(R.id.okButton);
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                goback();
            }
        });
        alertDialog.show();
    }

    @Override
    public void addCarListNew(List<TypeNew> types, int i) {
        if (types != null && types.size() > 0 && adapter.getSelectedCar() == null) {

        }

        if (!isViaPrivateKey && adapter.selectedCarId == null && adapter.getSelectedCar() == null && types.size() > 0 && types.get(0) != null) {
            carSlected(types.get(0));
            adapter.selectedCarId = types.get(0).zoneId;
        }
        adapter.addList(types);
        if (!isViaPrivateKey && types.size() > 0 && types.get(0) != null) {
            carSlected(types.get(0));
        }
    }

    /** Called when trip scheduling was successful
     * @param type_id Id of the selected car
     * @param req_id Id of the request
     * @param user_id Id of the user
     * @param user_token Token string of the user
     * @param latitude Latitude of the user
     * @param longitude Longitude of the user **/
    @Override
    public void scheduleSucess(String type_id, String req_id, String user_id, String user_token, double latitude, double longitude) {
        this.type_id = type_id;
        this.req_id = req_id;
        this.user_id = user_id;
        this.user_token = user_token;
        this.latitude = latitude;
        this.longitude = longitude;

        Intent topDriver = new Intent(getActivity(), TopDriverAct.class);
        topDriver.putExtra("req_id", req_id);
        topDriver.putExtra("id", user_id);
        topDriver.putExtra("token", user_token);
        topDriver.putExtra("type_id", type_id);
        topDriver.putExtra("lati", "" + latitude);
        topDriver.putExtra("longi", "" + longitude);
        startActivity(topDriver);

    }

    public void fareDetailsClicked(TypeNew request) {
        if (rideFragViewModel.baseResponse != null) {
            openETADialog(rideFragViewModel.baseResponse);
        }
//        ETAParent.newInstance(request, routeDest).show(getChildFragmentManager());
    }

//    public void fareDetailsClicked(TypeNew request) {
//        ETAParent.newInstance(request, routeDest).show(getChildFragmentManager());
//    }

}
