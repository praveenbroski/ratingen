package taxi.ratingen.ui.drawerscreen;

import android.app.Dialog;
import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.ProfileModel;
import taxi.ratingen.retro.responsemodel.ReqInProgress;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.net.Socket;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/11/17.
 */

public class DrawerViewModel extends BaseNetwork<BaseResponse, DrawerNavigator> {

    Gson gson;

    SharedPrefence sharedPrefence;

    @Inject
    HashMap<String, String> hashMap;

    Socket socket;
    public ObservableField<String> Imageurl = new ObservableField<>("");
    public ObservableField<String> Email = new ObservableField<>("");
    public ObservableField<String> Phone = new ObservableField<>("");
    public ObservableField<String> fullName = new ObservableField<>("");
    public ObservableField<String> rating = new ObservableField<>("");
    public ObservableBoolean isRatingCalled = new ObservableBoolean();
    public ObservableBoolean ratingUnread = new ObservableBoolean(false);


    @Inject
    public DrawerViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                           SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
    }

    /**
     * Set profile data's from API to variables
     **/
    void setupProfile() {
        String userStr = sharedPrefence.Getvalue(SharedPrefence.USERDETAILS);

        ProfileModel user = CommonUtils.IsEmpty(userStr) ? null : gson.fromJson(userStr, ProfileModel.class);

        if (user != null) {
            fullName.set(CommonUtils.IsEmpty(user.getName()) ? "" : user.getName());
            Email.set(CommonUtils.IsEmpty(user.getEmail()) ? "" : user.getEmail());
            Phone.set(CommonUtils.IsEmpty(user.getMobile()) ? "" : user.getMobile());
            Imageurl.set(CommonUtils.IsEmpty(user.getProfilePicture()) ? "" : user.getProfilePicture());
            rating.set(user.getRating() + "");
        }
    }

    /**
     * Calls API to get user ratings
     **/
    public void GetProfileRatings() {
        isRatingCalled.set(true);
        hashMap.clear();
        getUserProfile();

    }

    /**
     * Calls API to get if there is any request was currently in progress
     **/
    public void RequestInProNetwork() {
        if (getmNavigator().isNetworkConnected()) {
            getRequestInProgressNetwork();
        }
    }

    /**
     * API call to get favourite locations
     **/
    public void GetFavList() {
        if (getmNavigator().isNetworkConnected()) {
            hashMap.clear();
            hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
//            GetFavListNetworkcall();
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /**
     * Custom {@link BindingAdapter} function to set image to {@link ImageView} from {@link java.net.URL}
     *
     * @param imageView {@link ImageView}
     * @param url       image {@link java.net.URL}
     **/
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url)
                .apply(RequestOptions.circleCropTransform()
                        .override(57, 57)
                        .error(R.drawable.ic_menu_ham)
                        .placeholder(R.drawable.ic_menu_ham))
                .into(imageView);
    }

    /**
     * Callback method for successful API calls
     *
     * @param response {@link BaseResponse} model
     * @param taskId   Id of the API task
     **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        System.out.println("++" + response.toString());
        setIsLoading(false);
        if (response.message.equalsIgnoreCase("request_in_progress")) {
            getmNavigator().enableCorporateUser(sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER));
            ReqInProgress model = CommonUtils.getSingleObject(new Gson().toJson(response.data), ReqInProgress.class);
            if (model != null) {
                sharedPrefence.savevalue(SharedPrefence.ID, model.getId() + "");
                if (model.getOnTripRequest() != null) {
                    String requestString = gson.toJson(model.getOnTripRequest());
                    TaxiRequestModel.Result metaRequest = CommonUtils.getSingleObject(requestString, TaxiRequestModel.Result.class);
                    if (metaRequest != null) {
                        if (metaRequest.resultData != null) {
                            if (metaRequest.resultData.isCompleted == 1) {
//                            getmNavigator().ShowFeedbackFragment(metaRequest.resultData, false);
                            } else {
                                if (metaRequest.resultData.isLater != null && metaRequest.resultData.isLater == 1) {
//                                    getmNavigator().openRideLaterAlert(metaRequest.requestData, metaRequest.requestData.driverDetail.driverData);
                                } else {
                                    getmNavigator().showTripFragment(metaRequest.resultData, metaRequest.resultData.driverDetail.driverData);
                                }
                            }
                        } else
                            showMapScreen(model);
                    } else
                        showMapScreen(model);
                } else
                    showMapScreen(model);
            }

//            getmNavigator().enableCorporateUser(sharedPrefence.GetBoolean(SharedPrefence.IS_CORPORATE_USER));
//            if (response.request != null) {
//                if (response.request != null && response.request.driver != null) {
//
//                    if (response.request.isCompleted == 1) {
//                        getmNavigator().ShowFeedbackFragment(response.request, response.isCorporate == 1);
//                    } else {
//                        if (response.request.later != null && response.request.later == 1) {
//                            getmNavigator().openRideLaterAlert(response.request, response.request.driver);
//                        } else
//                            getmNavigator().ShowTripFragment(response.request, response.request.driver);
//                    }
//                } else
//                    getmNavigator().ShowMapFragment();
//            } else {
//                getmNavigator().ShowMapFragment();
//            }
//
//            if (response.getSos() != null && response.getSos().size() != 0) {
//                if (response.getUserSos() != null && response.getUserSos().size() != 0) {
//                    So so = new So();
//                    so.number = response.getUserSos().get(0).number;
//                    so.name = response.getUserSos().get(0).name;
//                    response.getSos().add(so);
//                    sharedPrefence.savevalue(SharedPrefence.SOSLIST, gson.toJson(response));
//                } else {
//                    sharedPrefence.savevalue(SharedPrefence.SOSLIST, gson.toJson(response));
//                }
//            }
        } else if (response.message.equalsIgnoreCase("Favorite_Added_Successfully")) {
            if (response.getFavplace().size() > 0) {
                String Favlist = gson.toJson(response);
                sharedPrefence.savevalue(SharedPrefence.FAVLIST, Favlist);
            }
        } else if (response.message.equalsIgnoreCase("user_profile")) {
            setIsLoading(false);
            isRatingCalled.set(false);
            String userString = gson.toJson(response.data);
            sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userString);
            //  getmNavigator().refreshDrawerActivity();
            setupProfile();
        } else if (response.message.equalsIgnoreCase("success")) {
            if (taskId == Constants.TaskId.LOGOUT) {
                getmNavigator().logout();
            }
        }
    }

    /**
     * Callback method for failed API tasks
     *
     * @param taskId Id of the API task
     * @param e      {@link CustomException} msg
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        if (e.getCode() == Constants.ErrorCode.TOKEN_EXPIRED || e.getCode() == Constants.ErrorCode.TOKEN_MISMATCHED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            getmNavigator().logout();
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().showMessage(e);
            getmNavigator().refreshCompanyKey();
        } else if (e.getCode() != Constants.ErrorCode.EMPTY_FAV_LIST)
            getmNavigator().showMessage(e);
    }

    /**
     * {@link HashMap} with query parameters used for API calls
     **/
    @Override
    public HashMap<String, String> getMap() {
        return hashMap;
    }

    private void showMapScreen(ReqInProgress model) {
        if (model.getProfilePicture() != null) {
            Imageurl.set(model.getProfilePicture());
        }
        getmNavigator().ShowMapFragment();
    }

    Dialog logoutDialog;

    /**
     * Called when logout was clicked. It shows an alert to ask confirmation
     **/
    public void ShowLogoutAlertDialog(final Context context) {

        logoutDialog = new Dialog(context);
        logoutDialog.setContentView(R.layout.log_out_alert);
        if (logoutDialog.getWindow() != null) {
            logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView title = logoutDialog.findViewById(R.id.app_name);
        TextView logout_desc = logoutDialog.findViewById(R.id.sure_logout);
        TextView okyTxt = logoutDialog.findViewById(R.id.okay_txt);
        TextView cancel_txt = logoutDialog.findViewById(R.id.cancel_txt);
        CardView cancel = logoutDialog.findViewById(R.id.cancel);
        CardView okay = logoutDialog.findViewById(R.id.ok);

        title.setText(getmNavigator().getBaseAct().getTranslatedString(R.string.app_name));
        logout_desc.setText(getmNavigator().getBaseAct().getTranslatedString(R.string.text_desc_logout));
        okyTxt.setText(getmNavigator().getBaseAct().getTranslatedString(R.string.text_ok));
        cancel_txt.setText(getmNavigator().getBaseAct().getTranslatedString(R.string.text_cancel));

        okay.setOnClickListener(v -> Logout());

        cancel.setOnClickListener(v -> logoutDialog.dismiss());

        logoutDialog.show();
        logoutDialog.setCanceledOnTouchOutside(false);
    }


    public void onMenuClick(View view) {
        getmNavigator().openCloseDrawer();
        ratingUnread.set(!ratingUnread.get());
    }

    public void onClickNotification(View v) {
        getmNavigator().onClickNotification();
    }
}
