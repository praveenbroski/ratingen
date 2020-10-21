package taxi.ratingen.ui.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import taxi.ratingen.R;
import taxi.ratingen.retro.GitHubCountryCode;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.CountryCodeModel;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.RealPathUtil;
import taxi.ratingen.utilz.SharedPrefence;
import taxi.ratingen.utilz.exception.CustomException;

/**
 * Created by root on 10/9/17.
 */

public class RegistrationViewModel extends BaseNetwork<User, RegistrationNavigator> {
    @Inject
    HashMap<String, String> Map;

    @Inject
    Context context;


    Gson gson;


    SharedPrefence sharedPrefence;

    public ObservableField<String> Email = new ObservableField<>("");

    public ObservableField<String> LastName = new ObservableField<>("");
    public ObservableField<String> FirstName = new ObservableField<>("");

    public ObservableField<String> Password = new ObservableField<>("");
    public ObservableField<String> Countrycode = new ObservableField<>("");
    public ObservableField<String> PhoneNumber = new ObservableField<>("");
    public ObservableField<Boolean> isSocialLogin = new ObservableField<>();
    public ObservableField<String> EmailorPhone = new ObservableField<>("");
    public ObservableField<String> phoneForDisplay = new ObservableField<>("");
    public ObservableField<String> countryForDisplay = new ObservableField<>("");
    public ObservableField<File> bitmap = new ObservableField<>();
    String RealPath = null;
    File RealFile;
    GitHubCountryCode gitHubCountryCode;

    Boolean isAccTnC = false;

    @Inject
    public RegistrationViewModel(@Named(Constants.ourApp) GitHubService gitHubService,
                                 @Named(Constants.countryMap) GitHubCountryCode gitHubCountryCode,
                                 @Named("HashMapData") HashMap<String, String> stringHashMap,
                                 SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.gitHubCountryCode = gitHubCountryCode;
        this.gson = gson;
        this.sharedPrefence = sharedPrefence;
        isSocialLogin.set(false);
        if (stringHashMap != null && !stringHashMap.isEmpty()) {
            Map = stringHashMap;
            //SetBinding();

        }

    }

    public void SetBinding() {
        if (!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.phonenumber)) && Map.get(Constants.NetworkParameters.login_method).equalsIgnoreCase(Constants.NetworkParameters.manual)) {
            getmNavigator().setPhoneNumber(Map.get(Constants.NetworkParameters.phonenumber));

            if (!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.IsEnabled))) {

                if (Map.get(Constants.NetworkParameters.IsEnabled).equalsIgnoreCase("Yes")) { //Need to clear key..later
                    getmNavigator().setDisablePhoneNumber();
                } else {
                    getCurrentCountry();
                }
            }
        }

        if(!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.disp_phonenumber))&&!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.disp_country_code))){
            countryForDisplay.set(Map.get(Constants.NetworkParameters.disp_country_code));
            phoneForDisplay.set(Map.get(Constants.NetworkParameters.disp_phonenumber));
        }
    }

    /** Get currently selected country **/
    public void getCurrentCountry() {
        if (!CommonUtils.IsEmpty(sharedPrefence.getCURRENT_COUNTRY())) {
            Constants.COUNTRY_CODE = sharedPrefence.getCURRENT_COUNTRY();
            getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
            return;
        }
        if (getmNavigator().isNetworkConnected()) {
            setIsLoading(true);
            gitHubCountryCode.getCurrentCountry().enqueue(new Callback<CountryCodeModel>() {
                @Override
                public void onResponse(Call<CountryCodeModel> call, Response<CountryCodeModel> response) {
                    setIsLoading(false);
                    if (response != null)
                        if (response.toString() != null && response.body() != null) {
                            Constants.COUNTRY_CODE = response.body().toString();
                           // Constants.COUNTRY_CODE = "IN";
                            if (!CommonUtils.IsEmpty(Constants.COUNTRY_CODE)) {
                                sharedPrefence.saveCURRENT_COUNTRY(Constants.COUNTRY_CODE);
                                getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                            }
                        }
                }

                @Override
                public void onFailure(Call<CountryCodeModel> call, Throwable t) {
                    setIsLoading(false);
                    if (t != null)
                        t.printStackTrace();
                    getmNavigator().setCountryCode(Constants.COUNTRY_CODE);
                }
            });
        }
    }

    /** Custom {@link BindingAdapter} method to set image to {@link ImageView} from {@link File}
     * @param imageView ImageView
     * @param url Image {@link File} **/
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, File url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(imageView);
    }

    /** Click listener for imageView **/
    public void onclickImage(View view) {
        if (getmNavigator().getPermissions()) {
            String[] plantArray = view.getContext().getResources().getStringArray(R.array.PickImageRes);
            ShowAlertdialog(plantArray, getmNavigator().getBaseAct().getTranslatedString(R.string.Alert_Tit_Add_Photo), view.getContext());
        }
    }

    public void onUsernameChanged(Editable e) {
        EmailorPhone.set("e.toString()");
        phoneForDisplay.set(e.toString());
    }

    /** Click listener for signup button. Validates form and calls sign up API **/
    public void onclicksignup(View view) {


//        sharedPrefence.savevalue(SharedPrefence.FCMTOKEN, FirebaseInstanceId.getInstance().getToken());
        if (getmNavigator().isNetworkConnected()) {
            if (CommonUtils.IsEmpty(FirstName.get().trim()))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_FirstName));
            else if (CommonUtils.IsEmpty(LastName.get().trim()))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_LastName));
           /* else if (CommonUtils.IsEmpty(Email.get().trim()))
                getmNavigator().showSnackBar(view, view.getContext().getString(R.string.Validate_Email));*/
            else if (!CommonUtils.IsEmpty(Email.get()) && !CommonUtils.isEmailValid(Email.get().trim()))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_InvalidEmail));
            else if (CommonUtils.IsEmpty(Countrycode.get().trim().replace("-", "").replace(" ", "") + "" + EmailorPhone.get().trim().replace("-", "").replace(" ", "")))
                getmNavigator().showSnackBar(view, getmNavigator().getBaseAct().getTranslatedString(R.string.Validate_Mob_Number));
            else {
                setIsLoading(true);
                if (!CommonUtils.IsEmpty(RealPath)) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), RealFile);
                    body = MultipartBody.Part.createFormData("profile_pic", RealFile.getName(), reqFile);
                }
                requestbody.put(Constants.NetworkParameters.client_id, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyID()));
                requestbody.put(Constants.NetworkParameters.client_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.getCompanyToken()));
                requestbody.put(Constants.NetworkParameters.email, RequestBody.create(MediaType.parse("text/plain"), Email.get()));
                requestbody.put(Constants.NetworkParameters.password, RequestBody.create(MediaType.parse("text/plain"), Password.get()));
                requestbody.put(Constants.NetworkParameters.firstname, RequestBody.create(MediaType.parse("text/plain"), FirstName.get()));
                requestbody.put(Constants.NetworkParameters.lastname, RequestBody.create(MediaType.parse("text/plain"), LastName.get()));
                requestbody.put(Constants.NetworkParameters.phonenumber, RequestBody.create(MediaType.parse("text/plain"),
                        Countrycode.get().trim().replace("-", "").replace(" ", "") + "" +
                                CommonUtils.removeFirstZeros(EmailorPhone.get().trim().replace(" ", "").replace("-", ""))));
                requestbody.put(Constants.NetworkParameters.device_token, RequestBody.create(MediaType.parse("text/plain"), sharedPrefence.Getvalue(SharedPrefence.FCMTOKEN)));
                requestbody.put(Constants.NetworkParameters.login_by, RequestBody.create(MediaType.parse("text/plain"), Constants.NetworkParameters.android));
                requestbody.put(Constants.NetworkParameters.login_method, RequestBody.create(MediaType.parse("text/plain"), Map.get(Constants.NetworkParameters.login_method)));
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                        Locale.getDefault());
                Date currentLocalTime = calendar.getTime();
                DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
                String localTime = date.format(currentLocalTime);
                requestbody.put(Constants.NetworkParameters.timeZone, RequestBody.create(MediaType.parse("text/plain"), localTime));
                if (!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.country_code))) {
                    requestbody.put(Constants.NetworkParameters.country_code, RequestBody.create(MediaType.parse("text/plain"),
                            Map.get(Constants.NetworkParameters.country_code)));
                } else if (!CommonUtils.IsEmpty(getmNavigator().getCountryCode())) {
                    requestbody.put(Constants.NetworkParameters.country_code, RequestBody.create(MediaType.parse("text/plain"),
                            getmNavigator().getCountryCode()));
                } else {
                    requestbody.put(Constants.NetworkParameters.country_code, RequestBody.create(MediaType.parse("text/plain"),
                            Constants.text_default_country_codes));
                }

                if (!CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.country))) {
                    requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"),
                            (Map.get(Constants.NetworkParameters.country).equalsIgnoreCase("JO") ? "JOD" : Map.get(Constants.NetworkParameters.country))));
                } else if (!CommonUtils.IsEmpty(getmNavigator().getCountryNameShort())) {
                    requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"),
                            getmNavigator().getCountryNameShort().equalsIgnoreCase("JO") ? "JOD" : getmNavigator().getCountryNameShort()));
                } else {
//                    requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"),
//                            (Constants.COUNTRY_CODE.equalsIgnoreCase("JO") ? "JOD" : Constants.COUNTRY_CODE)));
                    requestbody.put(Constants.NetworkParameters.country, RequestBody.create(MediaType.parse("text/plain"),
                            (Constants.CHOOSED_COUNTRYCODE.equalsIgnoreCase("JO") ? "JOD" : Constants.CHOOSED_COUNTRYCODE)));
                }

                if (Map.containsKey(Constants.NetworkParameters.social_unique_id) && !CommonUtils.IsEmpty(Map.get(Constants.NetworkParameters.social_unique_id)))
                    requestbody.put(Constants.NetworkParameters.social_unique_id, RequestBody.create(MediaType.parse("text/plain"), Map.get(Constants.NetworkParameters.social_unique_id)));


                Log.d("Signup_Map", requestbody.toString());
                SignupNetworkcall();
            }
        } else {
            getmNavigator().showNetworkMessage();
        }
    }

    /**Callback for successful API calls
     * @param taskId ID of the API task
     * @param response {@link BaseResponse} model **/
    @Override
    public void onSuccessfulApi(long taskId, User response) {
        setIsLoading(false);
        if (response.success)
            if (response.getUser() != null) {
                String userstring = gson.toJson(response.getUser());
                sharedPrefence.savevalue(SharedPrefence.USERDETAILS, userstring);
                sharedPrefence.savevalue(SharedPrefence.TOKEN, response.getUser().token);
                sharedPrefence.savevalue(SharedPrefence.ID, "" + response.getUser().id);
                // getmNavigator().OpenApplayRefferalLayout();

                getmNavigator().OpenDrawerAct();
            }

    }

    /**Callback for failed API calls
     * @param taskId ID of the API task
     * @param e {@link CustomException} **/
    @Override
    public void onFailureApi(long taskId, CustomException e) {
        setIsLoading(false);
        getmNavigator().showMessage(e);
        if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
                e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
            if (getmNavigator() != null)
                getmNavigator().refreshCompanyKey();
        }
    }

    @Override
    public HashMap<String, String> getMap() {
        return Map;
    }


    /** Called when profile {@link ImageView} is clicked. Opens {@link AlertDialog} to present options to select gallery and camera **/
    private void ShowAlertdialog(final String[] plantArray, String Title, final Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setItems(plantArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (plantArray[item].equals(getmNavigator().getBaseAct().getTranslatedString(R.string.Takeaphoto))) {
                    getmNavigator().OpenCameraIntent();

                } else if (plantArray[item].equals(getmNavigator().getBaseAct().getTranslatedString(R.string.ChooseFromGall))) {
                    getmNavigator().OpenGalleryIntent();


                } else if (plantArray[item].equals(getmNavigator().getBaseAct().getTranslatedString(R.string.text_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /** Called in onActivityResult for gallery select intent. Converts intent data in to file and bitmap **/
    public void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            RealPath = RealPathUtil.getRealPath(context, data.getData());
            RealFile = new File(RealPath == null ? "" : RealPath);
            bitmap.set(RealFile);
        }

    }

    /** Called in onActivityResult for camera capture intent. Converts intent data in to file and bitmap **/
    public void onCaptureImageResult(Intent data) {
        if (data.getData() == null) {
            RealPath = createFile((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"));
            bitmap.set(RealFile);
            return;
        }
        RealPath = getRealPathFromURI(data.getData());
        RealFile = new File(RealPath == null ? "" : RealPath);
        bitmap.set(RealFile);
    }

    /** Returns full path of the File
     * @param  uri {@link Uri} of the file **/
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /** Create {@link File} by given {@link Bitmap}
     * @param bitmap Bitmap image **/
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

    /** Set data from social media to variables
     * @param hashMap {@link HashMap} with key and value **/
    public void setSocialRegData(HashMap<String, String> hashMap) {
        Email.set(hashMap.get(Constants.NetworkParameters.email));
        FirstName.set(hashMap.get(Constants.NetworkParameters.firstname));
        LastName.set(hashMap.get(Constants.NetworkParameters.lastname));
        if (hashMap.get(Constants.NetworkParameters.profile_pic) != null) {
            RealPath = (hashMap.get(Constants.NetworkParameters.profile_pic));
            RealFile = new File(RealPath == null ? "" : RealPath);
            bitmap.set(RealFile);
        }
        isSocialLogin.set(true);
    }

    /** Click listener for facebook login **/
    public void onClickFacebook(View view) {
        getmNavigator().Facebookclicked();
    }

    /** Click listener for Google **/
    public void onClickGoogle(View view) {
        getmNavigator().gplusclicked();
    }

    /** Click listener for clicking on display anywhere but widgets. This closes keyboard. **/
    public void onClickOutSide(View view) {
        getmNavigator().hideVisibleKeyboard();
    }


    private BaseTarget target = new BaseTarget<BitmapDrawable>() {
        @Override
        public void onResourceReady(BitmapDrawable bitmapResource, Transition<? super BitmapDrawable> transition) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapResource.getBitmap().compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_COMPRESSION_QUALITY, bytes);
            String path = CommonUtils.WriteImage(bytes);
            Map.put(Constants.NetworkParameters.profile_pic, path);
            if (path != null) {
                RealPath = (path);
                RealFile = new File(RealPath == null ? "" : RealPath);
                bitmap.set(RealFile);
            }
        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
        }

        @Override
        public void removeCallback(SizeReadyCallback cb) {
        }
    };

    /** Handle result from {@link GoogleSignInAccount} request **/
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setIsLoading(false);
            if (!CommonUtils.IsEmpty(account.getDisplayName()))
                FirstName.set(account.getDisplayName());
                           /* if (!CommonUtils.IsEmpty(user.()))
                                LastName.set(user.getFamilyName());*/
            if (!CommonUtils.IsEmpty(account.getEmail()))
                Email.set(account.getEmail());
            if (account.getPhotoUrl() != null) {
                Glide.with(context)
                        .load(account.getPhotoUrl())
                        .into(target);
            }
//                                FirstName.set(user.getDisplayName());
            // Signed in successfully, show authenticated UI.
            //  updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //   updateUI(null);
        }
    }

    /** Click listener for back button **/
    public void goBack(View view) {
        getmNavigator().goBack();
    }

}
