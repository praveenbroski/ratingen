package taxi.ratingen.ui.sociallogin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

/**
 * Created by root on 10/10/17.
 */

public class SiginSocialViewModel extends BaseNetwork<User, SiginSocialNavigator> {

    @Inject
    HashMap<String, String> Map;

    @Inject
    Context context;


    SharedPrefence sharedPrefence;


    Gson gson;

    @Inject
    public SiginSocialViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
    }


    public void onclickFacebook(View view) {
        getmNavigator().Facebookclicked();
    }

    public void onclickGoogle(View view) {
        getmNavigator().gplusclicked();
    }

    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);


        if (response.success) {

            if (response.getUser().Ispresented != null) {

                if (response.getUser().Ispresented) {


                    LoginNetworkcall();

                } else {

                    getmNavigator().OpenRegistration(Map);
                }

            } else {

                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, gson.toJson(response.user));
                sharedPrefence.savevalue(SharedPrefence.ID, response.user.id + "");
                sharedPrefence.savevalue(SharedPrefence.TOKEN, response.user.token);
                getmNavigator().OpenDrawerAct();

            }
        }
    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {
        e.printStackTrace();
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public HashMap<String, String> getMap() {
        Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        return Map;
    }

    private BaseTarget target = new BaseTarget<BitmapDrawable>() {
        @Override
        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.getBitmap().compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            String path = CommonUtils.WriteImage(bytes);
            Map.put(Constants.NetworkParameters.profile_pic, path);
            // We are using Singleton Instance for HashMap..we don't want to pass
            CheckIsRegister();
        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
        }

        @Override
        public void removeCallback(SizeReadyCallback cb) {
        }
    };


    public void CheckIsRegister() {
        // We are using Singleton Instance for HashMap..we don't want to pass

        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            checkIsRegistered();

        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            sharedPrefence.savevalue(SharedPrefence.FCMTOKEN, "" + token);
                            Log.e("RefreshTokenOLD", sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
                        }
                    });
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.i("Response", acct.toString());
            Map.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
            Map.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
            Map.put(Constants.NetworkParameters.email, acct.getEmail());
            Map.put(Constants.NetworkParameters.firstname, acct.getGivenName());
            Map.put(Constants.NetworkParameters.lastname, acct.getFamilyName());
            Map.put(Constants.NetworkParameters.social_unique_id, acct.getId());
            Map.put(Constants.NetworkParameters.device_token, sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN));
            Map.put(Constants.NetworkParameters.login_by, Constants.NetworkParameters.android);
            Map.put(Constants.NetworkParameters.login_method, Constants.NetworkParameters.google);
            if (acct.getPhotoUrl() != null) {
                Glide.with(context)
                        .load(acct.getPhotoUrl())
                        .into(target);
            } else {
                CheckIsRegister();

            }


        } else {
            // Signed out, show unauthenticated UI.

        }
    }

}
