package taxi.ratingen.ui.drawerscreen.profilescrn;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.GenericsType;
import taxi.ratingen.utilz.RealPathUtil;
import taxi.ratingen.utilz.SharedPrefence;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by root on 12/27/17.
 */

public class ProfileViewModel extends BaseNetwork<BaseResponse, ProfileNavigator> {

    @Inject
    Context context;
    Gson gson;
    SharedPrefence sharedPrefence;
    public ObservableField<String> Email = new ObservableField<>("");
    public ObservableField<String> LastName = new ObservableField<>("");
    public ObservableField<String> FirstName = new ObservableField<>("");
    public ObservableField<String> cnf_Password = new ObservableField<>("");
    public ObservableField<String> Password = new ObservableField<>("");
    public ObservableField<String> new_Password = new ObservableField<>("");
    public ObservableField<String> Phone_Number = new ObservableField<>("");
    public ObservableBoolean mIsEditable = new ObservableBoolean(true);
    public ObservableField<GenericsType> bitmap_profilePicture = new ObservableField<>();
    public ObservableFloat userReview = new ObservableFloat();
    public ObservableField<String> txt_Language_update = new ObservableField<>();

    String RealPath = null;
    File RealFile;
    @Inject
    HashMap<String, String> hashMap;
    GitHubService gitHubService;
    GenericsType genericsType;

    @Inject
    public ProfileViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                            SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence = sharedPrefence;
        this.gson = gson;
        this.gitHubService = gitHubService;
        genericsType = new GenericsType();
    }

    /** Returns {@link HashMap} with query parameters used for API calls **/
    @Override
    public HashMap<String, String> getMap() {
        hashMap.clear();
        return hashMap;
    }

    /** Callback for successful API calls
     * @param taskId Id of the API task
     * @param response Response model **/
    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {
        setIsLoading(false);
        if (response.successMessage.equalsIgnoreCase("Profile Updated Successfully")) {
            setIsLoading(false);
            if (response.success) {
                //  getmNavigator().showMessage(getmNavigator().getAttachedContext().getString(R.string.Txt_updateSuccess));
                String userstring = gson.toJson(response.getUser());
                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userstring);
                getmNavigator().SendBroadcast();
                Toast.makeText(context, response.successMessage, Toast.LENGTH_SHORT).show();
//                getmNavigator().finishAct();
                Intent intent = new Intent(Constants.ProfileUpdate);
                LocalBroadcastManager.getInstance(getmNavigator().getBaseAct()).sendBroadcast(intent);
            }
        } else if (response.successMessage.equalsIgnoreCase("Profile Listed succesfully")) {
            String userstring = gson.toJson(response.getUser());
            sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userstring);
            //  getmNavigator().refreshDrawerActivity();
            setUserDetails();
        }
    }

    /** Callback for failed API calls
     * @param taskId Id of the API task
     * @param e Exception msg. **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            getmNavigator().showMessage(e);
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }

    }

    /** Custom {@link BindingAdapter} function to set profile picture
     * @param imageView Profile picture imageView
     * @param url URL of the image **/
    @BindingAdapter("imageUrlProfile")
    public static void setImageUrl(ImageView imageView, GenericsType url) {
        if (url != null) {
            Context context = imageView.getContext();
            Glide.with(context).load(url.get()).
                    apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                            placeholder(R.drawable.ic_user)).into(imageView);
        }
    }

    /** Click listener for profile update **/
    public void onclick_clicktoUpdate(View view) {
        mIsEditable.set(false);
        getmNavigator().refreshFocusScroll();
    }

    /** Call update parameters validation **/
    public void onclick_Updatebtn(View view) {
        validateNetworkParametrsToAPI(view);
    }

    /** Set user details data to variable from API **/
    public void setUserDetails() {
        String userstr = sharedPrefence.Getvalue(SharedPrefence.USERDETAILS);

        User user = CommonUtils.IsEmpty(userstr) ? null : gson.fromJson(userstr, User.class);
        if (user != null) {
            if (!CommonUtils.IsEmpty(user.firstname.trim())) {
                FirstName.set(user.firstname);
            }
            if (!CommonUtils.IsEmpty(user.lastname.trim())) {
                LastName.set(user.lastname);
            }
            if (!CommonUtils.IsEmpty(user.email.trim()))
                Email.set(user.email);

            if (!CommonUtils.IsEmpty(user.profilepic)) {
                genericsType.set(user.profilepic);
                bitmap_profilePicture.set(genericsType);
            }
            if (!CommonUtils.IsEmpty(user.phone.trim()))
                Phone_Number.set(user.phone);
            if (!CommonUtils.IsEmpty("" + user.ratings))
                userReview.set(user.ratings);
        }
    }

    /** Validate profile form data to check if values are not empty and in required format **/
    private boolean validateNetworkParametrsToAPI(View view) {
        String msg = null;
        if (CommonUtils.IsEmpty(FirstName.get().trim()))
            msg = getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_FirstName);
        else if (CommonUtils.IsEmpty(LastName.get().trim()))
            msg = getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_LastName);
        else if (!CommonUtils.IsEmpty(Email.get().trim())) {
            if (!CommonUtils.isEmailValid(Email.get().trim()))
                msg = getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Email);
        } else if (CommonUtils.IsEmpty(Phone_Number.get().trim()))
            msg = getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_PhoneNumber);

        if (msg != null) {
            getmNavigator().showMessage(msg);
            return false;
        } else {
            if (getmNavigator().isNetworkConnected()) {
                setIsLoading(true);
                requestbody.clear();
                requestbody.put(Constants.NetworkParameters.client_id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyID()));
                requestbody.put(Constants.NetworkParameters.client_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyToken()));
                requestbody.put(Constants.NetworkParameters.id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.ID)));
                requestbody.put(Constants.NetworkParameters.token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.TOKEN)));
                requestbody.put(Constants.NetworkParameters.firstname, RequestBody.create(MediaType.parse("text/plain"), FirstName.get()));
                requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), LastName.get()));

                if (!CommonUtils.IsEmpty(Password.get())) {
                    requestbody.put(Constants.NetworkParameters.new_password, RequestBody.create(MediaType.parse("text/plain"), new_Password.get()));
                    requestbody.put(Constants.NetworkParameters.old_password, RequestBody.create(MediaType.parse("text/plain"), Password.get()));
                }

                if (!CommonUtils.IsEmpty(Email.get()))
                    requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), Email.get()));

                if (RealPath != null) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), RealFile);
                    body = MultipartBody.Part.createFormData("profile_pic", RealFile.getName(), reqFile);
                }
                ProfileNetworkcall();
            }
        }
        return false;
    }

    /** Open camera, gallery selection dialog **/
    public void openGalaryorCamera(View view) {
        getmNavigator().alertSelectCameraGalary();
    }

    /** Called when picture from gallery is selected
     * @param data {@link Intent} with selected image data **/
    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri s_uri = data.getData();
            RealPath = RealPathUtil.getRealPath(context, s_uri);
            Log.v("fatal", RealPath);
            RealFile = new File(RealPath == null ? "" : RealPath);
            genericsType = new GenericsType();
            genericsType.set(RealFile);
            bitmap_profilePicture.set(genericsType);

            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        if (getmNavigator().isNetworkConnected()) {
            requestbody.clear();
            requestbody.put(Constants.NetworkParameters.client_id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyID()));
            requestbody.put(Constants.NetworkParameters.client_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyToken()));
            requestbody.put(Constants.NetworkParameters.id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.ID)));
            requestbody.put(Constants.NetworkParameters.token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.TOKEN)));
            if (RealPath != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), RealFile);
                body = MultipartBody.Part.createFormData("profile_pic", RealFile.getName(), reqFile);

                setIsLoading(true);
                ProfileNetworkcall();
            }
        } else
            getmNavigator().showNetworkMessage();
    }

    /** Called when picture was taken on camera
     * @param data {@link Intent} with captured image data **/
    public void onCaptureImageResult(Intent data) {
        if (data.getData() == null) {
            RealPath = createFile((Bitmap) data.getExtras().get("data"));
            genericsType = new GenericsType();
            genericsType.set(RealFile);
            bitmap_profilePicture.set(genericsType);
            uploadProfilePicture();
        }
//        RealPath = getRealPathFromURI(data.getData());
//        RealFile = new File(RealPath == null ? "" : RealPath);
//        genericsType.set(RealFile);
//        bitmap_profilePicture.set(genericsType);
    }

    /** Create file on phone memory to save the bitmap returned by camera intent
     * @param bitmap Bitmap image returned by camera intent **/
    public String createFile(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File file = new File(myDir, imageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            RealFile = file;
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /** Get image path from image {@link Uri} returned by intent
     * @param uri {@link Uri} of the image **/
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /** API to get profile details **/
    public void callProfileRetriveApi() {
        setIsLoading(true);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashMap.put(Constants.NetworkParameters.id, sharedPrefence.Getvalue(SharedPrefence.ID));
        hashMap.put(Constants.NetworkParameters.token, sharedPrefence.Getvalue(SharedPrefence.TOKEN));
        getUserProfile(hashMap);
    }

    /** Open first name update **/
    public void openFirstNameUpdate(View view) {
        getmNavigator().openFirstNameUpdate();
    }

    /** Open last name update **/
    public void openLastNameUpdate(View view) {
        getmNavigator().openLastNameUpdate();
    }

    /** Open mail update **/
    public void openMailUpdate(View view) {
        getmNavigator().openMailUpdate();
    }

    /** Open phone number update **/
    public void openPhoneNumberUpdate(View view) {
        getmNavigator().openPhoneUpdate();
    }

    /** Open language select dialog **/
    public void openLangSelect(View view) {
        showAlertDialog(view.getContext());
    }

    /** Creates {@link List} with available languages and shows the language selection dialog **/
    private void showAlertDialog(final Context context) {
        final List<String> items = new ArrayList<>();
        if (sharedPrefence.Getvalue(SharedPrefence.LANGUAGES) != null) {
            for (String key : gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.LANGUAGES), String[].class))
                switch (key) {
                    case "en":
                        items.add("English" + " (en)");
                        break;
                    case "ar":
                        items.add("العربية" + " (ar)");
                        break;
                    case "fr":
                        items.add("française" + " (fr)");
                        break;
                    case "es":
                        items.add("Español" + " (es)");
                        break;
                    case "ja":
                        items.add("日本語" + " (ja)");
                        break;
                    case "ko":
                        items.add("한국어" + " (ko)");
                        break;
                    case "pt":
                        items.add("português" + " (pt)");
                        break;
                    case "zh":
                        items.add("中文" + " (zh)");
                        break;
                }
        }
        getmNavigator().languageItems(items);
    }

}
