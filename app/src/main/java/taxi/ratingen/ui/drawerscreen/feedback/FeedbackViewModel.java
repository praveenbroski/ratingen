package taxi.ratingen.ui.drawerscreen.feedback;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Request;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.MyComponent;
import taxi.ratingen.utilz.SharedPrefence;


import java.util.HashMap;

/**
 * Created by root on 12/28/17.
 */

public class FeedbackViewModel extends BaseNetwork<BaseResponse, FeedbackNavigator> {
    public ObservableField<String> userPic = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> carNumber = new ObservableField<>();
    public ObservableField<String> carModel = new ObservableField<>();
    public ObservableFloat savedRating = new ObservableFloat();
    public ObservableFloat userReview = new ObservableFloat();
    public ObservableField<String> txt_comments = new ObservableField<>("");
    GitHubService gitHubService;
    SharedPrefence sharedPrefence;
    HashMap<String, String> map;
    Request request;

    public FeedbackViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, HashMap<String, String> map, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        DataBindingUtil.setDefaultComponent(new MyComponent(this));
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.map = map;
    }

    /**
     * set user's name & profile picture
     **/
    public void setUserDetails(Request model) {
        this.request = model;
        if (model.driver != null) {
            userName.set(model.driver.firstname + " " + model.driver.lastname);
            if (!CommonUtils.IsEmpty(model.driver.profilePic))
                userPic.set(Constants.URL.BaseURL + Constants.URL.DRIVER_PROFILE_PIC + model.driver.profilePic);
            savedRating.set(model.driver.review);
            if (!CommonUtils.IsEmpty(model.driver.carnumber))
                carNumber.set(model.driver.carnumber);
            if (!CommonUtils.IsEmpty(model.driver.carmodel))
                carModel.set(model.driver.carmodel);
        }
    }

    /**
     * called when send button is clicked and calls review driver API
     **/
    public void updateReview(View view) {
        if (validataion()) {
            setIsLoading(true);
            map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            map.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
            map.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
            map.put(Constants.NetworkParameters.request_id, request.id + "");
            map.put(Constants.NetworkParameters.rating, Math.round(userReview.get()) + "");
            map.put(Constants.NetworkParameters.comment, CommonUtils.IsEmpty(txt_comments.get()) ? "" : txt_comments.get());
            reviewDriverNetwork();
        }
    }

    /**
     * validates feedback form and ensures everything is filled
     **/
    public boolean validataion() {
        String msg = null;
       /* if (userReview.get() <= 0) {
            msg = "Rating user left empty!";
        } else if (CommonUtils.IsEmpty(txt_comments.get())) {
            msg = "Comments cannot be left empty!";

        }*/

        if (userReview.get() > 0 && userReview.get() < 3 && txt_comments.get().isEmpty()) {
            msg = "Comments cannot be left empty!";
        }

        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        }
        return true;
    }

    /**
     * called when API call was successful
     ***/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        /*if (response == null) {
            getmNavigator().showMessage(R.string.text_error_contact_server);
            return;
        }*/
        if (response.success) {
            if (response.successMessage.equalsIgnoreCase("rated successfully"))
                getmNavigator().ShowHomeFragment();
        } else {
            if (response.errorCode == 904) {
                getmNavigator().ShowHomeFragment();
            }
            getmNavigator().showMessage(response.errorMessage);
        }
    }

    /**
     * called when the API call fails
     **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e.getMessage());
        if (e.getCode() == Constants.ErrorCode.REQUEST_ALREADY_CANCELLED
                || e.getCode() == Constants.ErrorCode.DRIVER_ALREADY_RATED
                || e.getCode() == Constants.ErrorCode.REQUEST_NOT_REGISTERED
                || e.getCode() == Constants.ErrorCode.INVALID_LOGIN) {
            getmNavigator().ShowHomeFragment();
        } else if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().refreshCompanyKey();
        }
    }

    /**
     * adds client_id, client_token to the {@link HashMap} used for API calling
     **/
    @Override
    public HashMap<String, String> getMap() {
        return map;
    }

    /**
     * custom {@link BindingAdapter} method to set driver's pic in feedback page
     **/
    @BindingAdapter("imageUrlFeedback")
    public void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()
                .error(R.drawable.ic_prof_avatar)
                .placeholder(R.drawable.ic_prof_avatar))
                .into(imageView);
    }

}
