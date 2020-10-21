package taxi.ratingen.ui.drawerscreen.tripscreen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.tripcanceleddialog.TripCanceledDialogFrag;

import androidx.databinding.library.baseAdapters.BR;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Driver;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.databinding.FragmentTripBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.ui.drawerscreen.canceldialog.cancelDialogFrag;
import taxi.ratingen.ui.drawerscreen.changeplace.SearchPlaceActivity;
import taxi.ratingen.ui.drawerscreen.promoscrn.PromoAct;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.ui.drawerscreen.sos.SosFragment;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import javax.inject.Inject;

import taxi.ratingen.ui.drawerscreen.historydetails.HistoryDetViewModel;


public class TripFragment extends BaseFragment<FragmentTripBinding, TripFragViewModel> implements TripNavigator, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "TripFragment";

    // TODO: Rename and change types of parameters
    private Request mParam1;
    private Driver mParam2;
    boolean ispickDrop;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    View layoutMarkerPickup, layoutMArkerDrop;
    Marker Pickup, Drop;
    @Inject
    SharedPrefence sharedPrefence;

    BaseActivity context;

//    ResizeAnimation resizeAnimation;

    @Inject
    TripFragViewModel tripFragViewModel;

    FragmentTripBinding fragmentTripBinding;
    GoogleMap mgoogleMap;

    public TripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripFragment newInstance(Request param1, Driver param2) {
        TripFragment fragment = new TripFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.PushTripCancelled));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiverTripStatus, new IntentFilter(Constants.PushTripCompleted));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mDriverChanged,
                new IntentFilter(Constants.driverChanged));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(acceptedReceiever,
                new IntentFilter(Constants.PushWaitingorAcceptByDriver));


        if (getArguments() != null) {
            mParam1 = (Request) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = (Driver) getArguments().getSerializable(ARG_PARAM2);
        }

        this.context = getbaseAct();

        getBaseActivity().HideNshowToolbar(false);
    }

    @Override
    public void onResume() {
        super.onResume();

//        Intent intent = new Intent(getActivity(), DrawerAct.class);
//        startActivity(intent);

    }

    /*  private void IntialView(View view) {


              nav_right = (ImageView) view.findViewById(R.id.img_nav_header);
              nav_left = (ImageView) view.findViewById(R.id.img_nav_header_2);
              blink_circle = (ImageView) view.findViewById(R.id.blink_circle);
              sps_left = (LinearLayout) view.findViewById(R.id.sps_left);
              sps_right = (LinearLayout) view.findViewById(R.id.sps_right);
              page1 = (CardView) view.findViewById(R.id.lyt_header_v1);
              page2 = (CardView) view.findViewById(R.id.lyt_header_v2);
              header = (LinearLayout) view.findViewById(R.id.header);
          }*/
//  *********************************

    /** {@link BroadcastReceiver} to receiver Trip cancelled msg **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {
                String json = intent.getExtras().getString(Constants.EXTRA_Data);
                if (json.equalsIgnoreCase("Trip cancelled")) {
                    openTripCancelMsg();
                }
                // getBaseActivity().NeedHomeFragment();
            }
        }
    };

    /** Opens {@link TripCanceledDialogFrag} when trip cancelled msg is received **/
    private void openTripCancelMsg() {
        if (getActivity() != null) {
            if (getActivity().getSupportFragmentManager().findFragmentByTag(TripCanceledDialogFrag.TAG) == null)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, TripCanceledDialogFrag.newInstance(1), TripCanceledDialogFrag.TAG)
                        .commitAllowingStateLoss();
            else {
                TripCanceledDialogFrag fragment = null;
                fragment = (TripCanceledDialogFrag) getActivity().getSupportFragmentManager().findFragmentByTag(TripCanceledDialogFrag.TAG);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        .replace(R.id.Container, TripCanceledDialogFrag.newInstance(1), TripCanceledDialogFrag.TAG)
                        .commitAllowingStateLoss();
            }
        }
    }

    /** {@link BroadcastReceiver} for receiving current status of the trip. They are
     * Bill generated, Trip Started, Driver arrived **/
    private BroadcastReceiver receiverTripStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {
                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                if (json.contains("bill_generated")) {
                    BaseResponse baseRespose = CommonUtils.getSingleObject(json, BaseResponse.class);
                    if (baseRespose != null)
                        if (baseRespose.successMessage.equalsIgnoreCase("bill_generated"))
                            if (baseRespose.getRequest() != null)
                                if (baseRespose.getRequest().isCompleted == 1)
                                    ShowFeedBackScreen(baseRespose.getRequest(), baseRespose.getRequest().isCorporate == 1);

                } else if (json.contains("Trip Started")) {
                    BaseResponse baseRespose = CommonUtils.getSingleObject(json, BaseResponse.class);
                    if (baseRespose != null)
                        if (baseRespose.successMessage.equalsIgnoreCase("Trip Started"))
                            if (baseRespose.getRequest() != null)
                                tripFragViewModel.tripstarted();

                } else if (json.contains("driver arrived")) {
                    BaseResponse baseRespose = CommonUtils.getSingleObject(json, BaseResponse.class);
                    if (baseRespose != null)
                        if (baseRespose.successMessage.equalsIgnoreCase("driver arrived"))
                            if (baseRespose.getRequest() != null)
                                tripFragViewModel.tripArrived();

                }
            }

        }
    };

    /** Initialize {@link FusedLocationProviderClient} to get location data **/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentTripBinding = getViewDataBinding();
        tripFragViewModel.setNavigator(this);
        init_pages();

        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.trip_map)).getMapAsync(this);
        tripFragViewModel.setValues(mParam1, mParam2);
        tripFragViewModel.SetSocketListener();

        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(getActivity());

        getActivity().setTitle(getbaseAct().getTranslatedString(R.string.app_name));

        //     ((DrawerAct) getActivity()).mToolbar.setNavigationIcon(null);

        //  ((DrawerAct) getActivity()).lockDrawer();

        blink_animation(fragmentTripBinding.imgTripDot);

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(fragmentTripBinding.tripBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        fragmentTripBinding.bottomSheetPersistent.setOnClickListener(v -> {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public TripFragViewModel getViewModel() {
        return tripFragViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trip;
    }

    /** Called to animate {@link ImageView} near trip status text
     * @param v View that needs to be animated **/
    private void blink_animation(View v) {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(400);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        v.startAnimation(animation);
    }

    public void init_pages() {
        load_Headerpage1();
    }

    public void load_Headerpage1() {

//        fragmentTripBinding.lytHeaderV1.animate().alpha(1.0f);
//        fragmentTripBinding.lytHeaderV2.animate().alpha(0.0f);
//        resizeAnimation = new ResizeAnimation(fragmentTripBinding.spsRight, 00);
//        resizeAnimation.setDuration(20);
//        fragmentTripBinding.spsRight.startAnimation(resizeAnimation);
//        resizeAnimation = new ResizeAnimation(fragmentTripBinding.spsLeft, 80);
//        resizeAnimation.setDuration(20);
//        fragmentTripBinding.spsLeft.startAnimation(resizeAnimation);
    }

    public void load_Headerpage2() {
//        fragmentTripBinding.lytHeaderV2.animate().alpha(1.0f);
//        fragmentTripBinding.lytHeaderV1.animate().alpha(0.0f);
//        resizeAnimation = new ResizeAnimation(fragmentTripBinding.spsLeft, 00);
//        resizeAnimation.setDuration(20);
//        fragmentTripBinding.spsLeft.startAnimation(resizeAnimation);
//        resizeAnimation = new ResizeAnimation(fragmentTripBinding.spsRight, 80);
//        resizeAnimation.setDuration(20);
//        fragmentTripBinding.spsRight.startAnimation(resizeAnimplation);

    }

    @Override
    public void LeftNavclicked() {
        load_Headerpage1();
    }

    @Override
    public void RightNavclicked() {
        load_Headerpage2();
    }

    @Override
    public boolean isAddedinAct() {
        return isAdded();
    }

    @Override
    public void HomeScreen() {
        if (getBaseActivity() != null)
            getBaseActivity().NeedHomeFragment();
    }

    @Override
    public void ShowFeedBackScreen(Request request, boolean isCorporate) {
        sharedPrefence.saveBoolean(SharedPrefence.MAPTYPE, false);
        getbaseAct().NeedFeedbackFragment(request, isCorporate);
    }

    /** Starts {@link PromoAct} when promo is selected **/
    @Override
    public void ShowPromoCodeScrn(String reqid) {
        if (!CommonUtils.IsEmpty(reqid)) {
//            Intent intent = new Intent(getBaseActivity(), PromoAct.class);
//            intent.putExtra(Constants.EXTRA_Datastrn, reqid);
//            intent.putExtra("isRide", "0");
//            getBaseActivity().startActivityForResult(intent, Constants.PROMOSETRESULT);
//            getBaseActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            BottomSheetDialog promoDialog = new BottomSheetDialog(getBaseActivity(), R.style.AppBottomSheetDialogTheme);
            promoDialog.setContentView(R.layout.promo_bottom_sheet);
            promoDialog.show();

            TextView tPromoTitle = promoDialog.findViewById(R.id.txt_promo_title);
            TextView tPromoDesc = promoDialog.findViewById(R.id.txt_promo_desc);
            EditText ePromoCode = promoDialog.findViewById(R.id.et_promo_code);
            Button bApplyPromo = promoDialog.findViewById(R.id.btn_apply_promo);

            tPromoTitle.setText(getBaseActivity().getTranslatedString(R.string.txt_add_title_promo));
            tPromoDesc.setText(getBaseActivity().getTranslatedString(R.string.txt_promo_note));
            ePromoCode.setHint(getBaseActivity().getTranslatedString(R.string.txt_promo_code));
            bApplyPromo.setText(getBaseActivity().getTranslatedString(R.string.txt_apply_promo_code));

            bApplyPromo.setOnClickListener(v -> {
                String sPromoCode = ePromoCode.getText().toString().trim();
                if (CommonUtils.IsEmpty(sPromoCode)) {
                    showMessage(getBaseActivity().getTranslatedString(R.string.Validate_Promocode));
                } else {
                    promoDialog.dismiss();
                    tripFragViewModel.applyPromoAPI(sPromoCode);
                }
            });
        }
    }

    /** Shows {@link cancelDialogFrag} when user clicks cancel trip button **/
    @Override
    public void ShowCancelDialog(String requestid) {
        cancelDialogFrag newInstance = cancelDialogFrag.newInstance(requestid, "");
//        newInstance.setTargetFragment(this, Constants.CANCELTRIPCALLBACK);
        newInstance.show(this.getFragmentManager());
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getbaseAct() {
        return getBaseActivity() != null ? getBaseActivity() : (BaseActivity) getActivity();
    }

    /** This callback is called when {@link GoogleMap} loading was complete **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setPadding(0, 250, 0, 490);
        this.mgoogleMap = googleMap;
        tripFragViewModel.mGoogleMap = googleMap;
        mgoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                changeMapStyle();
                tripFragViewModel.isMapRendered.set(true);
                tripFragViewModel.buildGoogleApiClient(mgoogleMap);

                if (sharedPrefence.GetBoolean(SharedPrefence.MAPTYPE)) {
                    mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    tripFragViewModel.mapType.set(true);
                } else {
                    mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    tripFragViewModel.mapType.set(false);
                }
            }
        });

    }

    /** Changes {@link GoogleMap} style **/
    private void changeMapStyle() {
        if (HistoryDetViewModel.googleMap == null)
            return;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json);
        HistoryDetViewModel.googleMap.setMapStyle(style);
    }

    public class ResizeAnimation extends Animation {
        final int startWidth;
        final int targetWidth;
        View view;

        public ResizeAnimation(View view, int targetWidth) {
            this.view = view;
            this.targetWidth = targetWidth;
            startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // tripFragViewModel.DisconnectSocket();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiverTripStatus);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mDriverChanged);

        tripFragViewModel.isMapRendered.set(false);
    }

    /** Callback to receive result from previous screen
     * @param requestCode Code of the request
     * @param resultCode Code of the result
     * @param data {@link Intent} with data from previous screen **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            if (ispickDrop) {
                if (data != null && data.hasExtra(Constants.EXTRA_Data)) {
                    tripFragViewModel.processSelectedAddress(data.getExtras().getString(Constants.EXTRA_Data), ispickDrop);
                }
            } else {
                if (data != null && data.hasExtra(Constants.EXTRA_Data)) {
                    tripFragViewModel.processSelectedAddress(data.getExtras().getString(Constants.EXTRA_Data), ispickDrop);
                }
            }


        } else if (resultCode == Constants.PROMOSETRESULT) {
            if (data.getExtras().getString(Constants.EXTRA_Data).equalsIgnoreCase("Applied")) {
                tripFragViewModel.isPromodone.set(false);
                tripFragViewModel.enablePromoOption.set(false);
            }

        } else if (requestCode == Constants.CANCELTRIPCALLBACK) {
            if (data.getExtras() != null) {
                if (data.getStringExtra(Constants.EXTRA_Data).equalsIgnoreCase("Cancelled")) {
                    getBaseActivity().NeedHomeFragment();
                }
            }

        }

    }

    /** Called to change pickup/drop location after trip confirmed by driver **/
    @Override
    public void chagePickup(boolean isPickup) {
        ispickDrop = isPickup;
        LatLng latLng;
        Intent intent = new Intent(getContext(), SearchPlaceActivity.class);
        if (ispickDrop) {
            if (tripFragViewModel != null && tripFragViewModel.request != null
                    && tripFragViewModel.request.pickLatitude != 0.0 && tripFragViewModel.request.pickLongitude != 0.0) {
                latLng = new LatLng(tripFragViewModel.request.pickLatitude, tripFragViewModel.request.pickLongitude);
                intent.putExtra(Constants.EXTRA_LAT_LNG, latLng);
                intent.putExtra(Constants.EXTRA_IS_PICKUP, "1");
                intent.putExtra(Constants.EXTRA_PICK_ADDRESS, tripFragViewModel.request.pickLocation);
                intent.putExtra(Constants.EXTRA_DROP_ADDRESS, tripFragViewModel.request.dropLocation);
            }
        } else {
            if (tripFragViewModel != null && tripFragViewModel.request != null
                    && tripFragViewModel.request.pickLatitude != 0.0 && tripFragViewModel.request.pickLongitude != 0.0
                    && tripFragViewModel.request.dropLatitude != null && tripFragViewModel.request.dropLongitude != null) {
                latLng = new LatLng(tripFragViewModel.request.dropLatitude, tripFragViewModel.request.dropLongitude);
                intent.putExtra(Constants.EXTRA_LAT_LNG, latLng);
                intent.putExtra(Constants.EXTRA_IS_PICKUP, "0");
                intent.putExtra(Constants.EXTRA_PICK_ADDRESS, tripFragViewModel.request.pickLocation);
                intent.putExtra(Constants.EXTRA_DROP_ADDRESS, tripFragViewModel.request.dropLocation);
            }
        }

        intent.putExtra(Constants.EXTRA_SEARCH_TYPE, context.getTranslatedString(isPickup ? R.string.txt_EnterPick : R.string.txt_EnterDrop));
        startActivityForResult(intent, Constants.REQUEST_CODE_AUTOCOMPLETE);
    }

    /** Notifies user there are no driver available **/
    @Override
    public void notifyNoDriverMessage() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).removeWaitProgressDialog();
        }
    }

    /** Called to get the current location **/
    @Override
    public void openRequestLocation() {
        if (mgoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();

                        if (tripFragViewModel.driverLatLng.get() != null) {
                            CameraPosition newCamPos = new CameraPosition(new LatLng(tripFragViewModel.driverLatLng.get().latitude, tripFragViewModel.driverLatLng.get().longitude),
                                    16.0f,
                                    mgoogleMap.getCameraPosition().tilt, //use old tilt
                                    mgoogleMap.getCameraPosition().bearing);
                            mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 15, null);
                        } else {
                            assert location != null;
                            LatLng currentLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            CameraPosition newCamPos = new CameraPosition(currentLatLng,
                                    16.0f,
                                    mgoogleMap.getCameraPosition().tilt, //use old tilt
                                    mgoogleMap.getCameraPosition().bearing);
                            mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 15, null);
                        }

                    }
                }
            });
        }
    }

    /** Opens {@link SosFragment} when sos button is clicked **/
    @Override
    public void sosClicked() {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .add(R.id.Container, SosFragment.newInstance("", ""), SosFragment.TAG)
                    .commit();
    }

    /** Called when user clicks cancle button. It opens a dialog with reasons to select for cancelling.
     * After selecting or typing the reason cancel API is called to cancel the trip **/
    @Override
    public void openCancelalert() {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.top_driver_alert, viewGroup, false);
        TextView title = dialogView.findViewById(R.id.alert_title);
        TextView content = dialogView.findViewById(R.id.alert_content);
        TextView amount = dialogView.findViewById(R.id.alert_amount_shown);
        TextView yes = dialogView.findViewById(R.id.yes_txt);
        TextView no = dialogView.findViewById(R.id.no_txt);
        LinearLayout yesLay, noLay;
        yesLay = dialogView.findViewById(R.id.yes_lay);
        noLay = dialogView.findViewById(R.id.no_lay);
        amount.setVisibility(View.GONE);
        yes.setText(context.getTranslatedString(R.string.text_yes));
        no.setText(context.getTranslatedString(R.string.text_no));
        title.setText(context.getTranslatedString(R.string.app_name));
        content.setText(context.getTranslatedString(R.string.do_you_want_toCancel));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        yesLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                tripFragViewModel.cancelApi();
            }
        });
        noLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /** Contains method to open trip cancelled screen when trip cancelled msg arrives on {@link BroadcastReceiver} **/
    @Override
    public void callTripCancel() {
        openTripCancelMsg();
    }

    /** Assigns values to the variables
     * @param from From location
     * @param pickLatitude Pickup location latitude
     * @param pickLongitude Pickup location longitude
     * @param dropLatitude Drop location latitude
     * @param dropLongitude Drop location longitude **/
    @Override
    public void setVAlue(final String from, Float pickLatitude, Float pickLongitude, final Float dropLatitude, final Float dropLongitude) {
        if (getActivity() != null) {
            final LatLng pickupLatLng = new LatLng(pickLatitude, pickLongitude);
            Handler mainHandler = new Handler(getActivity().getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Pickup = mgoogleMap.addMarker(new MarkerOptions()
                            .position(pickupLatLng)
                            .title("Pickup Location")
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(setPickupMArker(getActivity(), from))));

                  /*  if (dropBaloon[0] == null && dropLatlng != null && dropLatlng.latitude != 0.0 && dropLatlng.longitude != 0.0)
                        dropBaloon[0] = googleMap.addMarker(new MarkerOptions()
                                .position(dropLatlng)
                                .title("Drop Location")
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(createDropMArker(getActivity(), from))));*/
                }
            });
        }

    }

    /** Assigns values to the variables
     * @param from From location
     * @param pickLatitude Pickup location latitude
     * @param pickLongitude Pickup location longitude
     * @param dropLatitude Drop location latitude
     * @param dropLongitude Drop location longitude **/
    @Override
    public void setDropValue(final String from, Float pickLatitude, Float pickLongitude, final Float dropLatitude, final Float dropLongitude) {
        if (getActivity() != null) {
            Handler mainHandler = new Handler(getActivity().getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (dropLatitude != null && dropLongitude != null && dropLatitude != 0.0 && dropLongitude != 0.0)
                        Drop = mgoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(dropLatitude, dropLongitude))
                                .title("Drop Location")
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(createDropMArker(getActivity(), from))));

                   /* googleMap.addMarker(new MarkerOptions()
                            .position(pickupLatlng)
                            .title("Pickup Location")
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(setPickupMArker(getActivity(), from))));*/
                }
            });
        }

    }


  /*  @Override
    public void setDropValue(final String from, Float pickLatitude, Float pickLongitude, final Float dropLatitude, final Float dropLongitude) {
        if (getActivity() != null) {
            final LatLng pickupLatLng = new LatLng(pickLatitude, pickLongitude);
            LatLng dropLatLng = null;
            if (dropLatitude != null && dropLongitude != null && dropLatitude != 0.0 && dropLongitude != 0.0)
                dropLatLng = new LatLng(dropLatitude, dropLongitude);
            Handler mainHandler = new Handler(getActivity().getMainLooper());
            final LatLng finalDropLatLng = dropLatLng;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mgoogleMap.addMarker(new MarkerOptions()
                            .position(pickupLatLng)
                            .title("Pickup Location")
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(setPickupMArker(getActivity(), from))));

                    if (finalDropLatLng != null && dropLatitude != 0.0 && dropLongitude != 0.0)
                        mgoogleMap.addMarker(new MarkerOptions()
                                .position(finalDropLatLng)
                                .title("Pickup Location")
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(createDropMArker(getActivity(), from))));
                }
            });
        }

    }

    @Override
    public void setVAlue(final String from, Float pickLatitude, Float pickLongitude, final Float dropLatitude, final Float dropLongitude) {
        if (getActivity() != null) {
            final LatLng pickupLatLng = new LatLng(pickLatitude, pickLongitude);
            LatLng dropLatLng = null;
            if (dropLatitude != null && dropLongitude != null && dropLatitude != 0.0 && dropLongitude != 0.0)
                dropLatLng = new LatLng(dropLatitude, dropLongitude);

            Handler mainHandler = new Handler(getActivity().getMainLooper());
            final LatLng finalDropLatLng = dropLatLng;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mgoogleMap.addMarker(new MarkerOptions()
                            .position(pickupLatLng)
                            .title("Pickup Location")
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(setPickupMArker(getActivity(), from))));

                    if (finalDropLatLng != null && dropLatitude != 0.0 && dropLongitude != 0.0)
                        mgoogleMap.addMarker(new MarkerOptions()
                                .position(finalDropLatLng)
                                .title("Drop Location")
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(createDropMArker(getActivity(), from))))
                                ;
                }
            });
        }

    }
*/

  /** This method displays a custom {@link Marker} from xml layout on the pickup location
   * @param context current {@link Context}
   * @param from Pickup location string **/
    public Bitmap setPickupMArker(Context context, String from) {
        if (layoutMarkerPickup == null)
            layoutMarkerPickup = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_pickup, null);

        ImageView markerImage = layoutMarkerPickup.findViewById(R.id.pin_icon);
        TextView pickKm = layoutMarkerPickup.findViewById(R.id.kmText);
        TextView minsText = layoutMarkerPickup.findViewById(R.id.min_text);
        ImageView baloonImg = layoutMarkerPickup.findViewById(R.id.pick_balooon);

        if (tripFragViewModel.isTripArrived.get()) {
            pickKm.setVisibility(View.GONE);
            minsText.setVisibility(View.GONE);
            baloonImg.setVisibility(View.GONE);
        } else {
            pickKm.setVisibility(View.VISIBLE);
            pickKm.setText(from);
            minsText.setVisibility(View.GONE);
            markerImage.setVisibility(View.GONE);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutMarkerPickup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMarkerPickup.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMarkerPickup.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMarkerPickup.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layoutMarkerPickup.getMeasuredWidth(), layoutMarkerPickup.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutMarkerPickup.draw(canvas);

        return bitmap;
    }

    /** This method displays a custom {@link Marker} from xml layout on the drop location
     * @param context current {@link Context}
     * @param from Drop location string **/
    public Bitmap createDropMArker(Context context, String from) {
        if (layoutMArkerDrop == null && context != null)
            layoutMArkerDrop = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_drop, null);

        ImageView markerImage = layoutMArkerDrop.findViewById(R.id.pin_icon);
        TextView dropKm = layoutMArkerDrop.findViewById(R.id.dropkmText);
        TextView minsText = layoutMArkerDrop.findViewById(R.id.min_text);
        ImageView baloonImg = layoutMArkerDrop.findViewById(R.id.drop_baloon);

        dropKm.setVisibility(View.VISIBLE);
        baloonImg.setVisibility(View.VISIBLE);
        dropKm.setText(from);
        minsText.setVisibility(View.GONE);
        markerImage.setVisibility(View.GONE);

       /* if (tripViewModel.isArrived.get()) {

        } else {
            dropKm.setVisibility(View.GONE);
            minsText.setVisibility(View.GONE);
            baloonImg.setVisibility(View.GONE);
            // markerImage.setVisibility(View.VISIBLE);
        }*/

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutMArkerDrop.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMArkerDrop.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMArkerDrop.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMArkerDrop.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layoutMArkerDrop.getMeasuredWidth(), layoutMArkerDrop.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutMArkerDrop.draw(canvas);

        return bitmap;
    }


 /*   public Bitmap setPickupMArker(Context context, String from) {
        if (layoutMarkerPickup == null)
            layoutMarkerPickup = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_pickup, null);

        ImageView markerImage = layoutMarkerPickup.findViewById(R.id.pin_icon);
        TextView pickKm = layoutMarkerPickup.findViewById(R.id.kmText);
        TextView minsText = layoutMarkerPickup.findViewById(R.id.min_text);


        if (tripFragViewModel.isTripArrived.get()) {
            pickKm.setVisibility(View.GONE);
            markerImage.setVisibility(View.VISIBLE);
            minsText.setVisibility(View.GONE);
        } else {
            pickKm.setVisibility(View.VISIBLE);
            pickKm.setText(from);
            minsText.setVisibility(View.GONE);
            markerImage.setVisibility(View.GONE);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutMarkerPickup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMarkerPickup.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMarkerPickup.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMarkerPickup.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layoutMarkerPickup.getMeasuredWidth(), layoutMarkerPickup.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutMarkerPickup.draw(canvas);

        return bitmap;
    }

    public Bitmap createDropMArker(Context context, String from) {
        if (layoutMArkerDrop == null)
            layoutMArkerDrop = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_drop, null);

        ImageView markerImage = layoutMArkerDrop.findViewById(R.id.pin_icon);
        TextView dropKm = layoutMArkerDrop.findViewById(R.id.dropkmText);
        TextView minsText = layoutMArkerDrop.findViewById(R.id.min_text);


        if (tripFragViewModel.isTripArrived.get()) {
            dropKm.setVisibility(View.VISIBLE);
            dropKm.setText(from);
            minsText.setVisibility(View.GONE);
            markerImage.setVisibility(View.GONE);
        } else {
            dropKm.setVisibility(View.GONE);
            minsText.setVisibility(View.GONE);
            markerImage.setVisibility(View.VISIBLE);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutMArkerDrop.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMArkerDrop.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMArkerDrop.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        layoutMArkerDrop.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layoutMArkerDrop.getMeasuredWidth(), layoutMArkerDrop.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layoutMArkerDrop.draw(canvas);

        return bitmap;
    }*/

    /** {@link BroadcastReceiver} to receive msg. about the change in driver **/
    private BroadcastReceiver mDriverChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("req_id")) {
                String req_id = intent.getExtras().getString("req_id");
                int id = intent.getExtras().getInt("id");
                Log.e("request_id", "id==" + req_id + "id" + id);
                WaitProgressDialog.newInstance("" + id).show(getActivity().getSupportFragmentManager());
            }
        }
    };

    /** {@link BroadcastReceiver} to receive Trip accepted msg. This removes {@link WaitProgressDialog} and opens trip screen **/
    private BroadcastReceiver acceptedReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (getActivity() != null && getActivity().getSupportFragmentManager() != null && getActivity().getSupportFragmentManager().findFragmentByTag(WaitProgressDialog.TAG) != null)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .remove(Objects.requireNonNull(getActivity().getSupportFragmentManager().findFragmentByTag(WaitProgressDialog.TAG)))
                        .commitAllowingStateLoss();

            if (intent.hasExtra(Constants.EXTRA_Data)) {

                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = CommonUtils.getSingleObject(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null) {
                    if (getActivity() != null && baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                        // Toast.makeText(getActivity(), getActivity().getString(R.string.Txt_DriverAccepted), Toast.LENGTH_SHORT).show();
                    } else {
                        if (getbaseAct() != null && baseResponse.request != null)
                            getbaseAct().NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);
                    }
                } else if (baseResponse.successMessage != null &&
                        (baseResponse.successMessage.contains("no driver") || baseResponse.successMessage.contains("no_driver") || baseResponse.successMessage.contains("no_driver_found"))) {
                    if (getBaseActivity() != null)
                        Toast.makeText(getBaseActivity(), getbaseAct().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), getbaseAct().getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();

                if (getActivity() != null && getActivity().getSupportFragmentManager() != null && getActivity().getSupportFragmentManager().findFragmentByTag(TripFragment.TAG) != null) {
                    Intent i = new Intent(getActivity(),DrawerAct.class);
//                    i.setClassName(context.getPackageName(), "ui.drawerscreen.DrawerAct");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }

        }
    };
}
/*
 * P--------------lat/lng: (11.005295753479004,76.99166870117188)
 * D--------------lat/lng: (10.999857902526855,76.9764175415039)
 *
 * Change:
 * P--------------lat/lng: (11.0052962,76.99166749999999)
 *
 * */