package taxi.ratingen.ui.registration;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import javax.inject.Inject;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityRegistrationBinding;
import taxi.ratingen.ui.applyrefferal.ApplayRefferal;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

//import com.nplus.countrylist.CountryCodeChangeListener;
//import com.nplus.countrylist.CountryUtil;

public class RegistrationAct extends BaseActivity<ActivityRegistrationBinding, RegistrationViewModel>
        implements RegistrationNavigator, GoogleApiClient.OnConnectionFailedListener {
    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    RegistrationViewModel registrationViewModel;
    //    private CountryUtil mCountryUtil;
    ActivityRegistrationBinding activityRegistrationBinding;
    HashMap<String, String> hashMap;
    ScrollView scroll;
    EditText et_fname, edt_text;
    String country_Code_str, countryShort;
    //GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

// ...
// Initialize Firebase Auth

    /**
     * Initializes {@link FirebaseAuth},
     * Retrieves FCM device token,
     * Initializes {@link GoogleSignInOptions},
     * Initializes {@link GoogleApiClient},/*
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrationViewModel.setNavigator(this);
        activityRegistrationBinding = getViewDataBinding();
        getGCMDeviceToken();

        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        scroll = this.findViewById(R.id.reg_scroll);
        et_fname = this.findViewById(R.id.et_fname);
        hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("Data_Social");
        if (hashMap != null) {
            registrationViewModel.setSocialRegData(hashMap);
//            mCountryUtil = new CountryUtil(RegistrationAct.this, Constants.COUNTRY_CODE);
//            mCountryUtil.initUI(activityRegistrationBinding.editCountryCodeSignup, this, activityRegistrationBinding.signupFlag);
//            mCountryUtil.initCodes(RegistrationAct.this);
        }

        registrationViewModel.SetBinding();
        et_fname.setOnTouchListener((v, event) -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scroll.scrollTo(0, scroll.getBottom());
                }
            }, 500);

            return false;
        });
        edt_text = activityRegistrationBinding.signupEmailorPhone;


        edt_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edt_text.getText().length() == 1 && edt_text.getText().toString().startsWith("0"))
                    edt_text.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        activityRegistrationBinding.termsCondt.setPaintFlags(activityRegistrationBinding.termsCondt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRegistrationBinding.privacyPolicy.setPaintFlags(activityRegistrationBinding.privacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    @Override
    public RegistrationViewModel getViewModel() {
        return registrationViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_registration;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /**
     * Calls gallery {@link Intent} to select image for profile picture
     **/
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.SELECT_FILE);
    }

    /**
     * Calls camera {@link Intent} to capture image for profile picture
     **/
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        pictureUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
        startActivityForResult(intent, Constants.REQUEST_CAMERA);
    }

    @Override
    public void OpenCameraIntent() {
        cameraIntent();
    }

    @Override
    public void OpenGalleryIntent() {
        galleryIntent();
    }

    /**
     * Opens {@link ApplayRefferal} activity
     **/
    @Override
    public void OpenApplayRefferalLayout() {
        startActivity(new Intent(this, ApplayRefferal.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    /**
     * Sets phoneNumber String to phone number field
     *
     * @param phoneNumber Phone number {@link String}
     **/
    @Override
    public void setPhoneNumber(String phoneNumber) {
        activityRegistrationBinding.signupEmailorPhone.setText(phoneNumber);
    }

    /**
     * Change visibility of views
     **/
    @Override
    public void setDisablePhoneNumber() {
        activityRegistrationBinding.signupEmailorPhone.setEnabled(false);
        activityRegistrationBinding.signupEmailorPhone.setFocusable(false);
        activityRegistrationBinding.signupFlag.setEnabled(false);
        activityRegistrationBinding.signupFlag.setFocusable(false);
    }

    /**
     * Asks for runtime permissions for SDK versions above or equal to 23
     **/
    @Override
    public boolean getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGranted(Constants.storagePermission)) {
            requestPermissionsSafely(Constants.storagePermission, Constants.REQUEST_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Dismisses soft keyboard
     **/
    @Override
    public void hideVisibleKeyboard() {
        hideKeyboard();
    }

    /**
     * Callback for result from previous screen
     *
     * @param requestCode ID of the request
     * @param resultCode  ID of the result
     * @param data        Intent with data from previous screen
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if (resultCode == Activity.RESULT_OK) {
        if (requestCode == Constants.SELECT_FILE)
            registrationViewModel.onSelectFromGalleryResult(data);
        else if (requestCode == Constants.REQUEST_CAMERA)
            registrationViewModel.onCaptureImageResult(data);

        else if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            registrationViewModel.handleSignInResult(task);
        } else {
            if (registrationViewModel != null)
                registrationViewModel.setIsLoading(false);
        }
    }
    // }


    /**
     * Return selected country code
     **/
    @Override
    public String getCountryCode() {
        return country_Code_str;
    }

    /**
     * Return selected country's short code
     **/
    @Override
    public String getCountryNameShort() {
        return countryShort;
    }

    @Override
    public void setCountryCode(String flag) {
//        mCountryUtil = new CountryUtil(RegistrationAct.this, flag);
//        mCountryUtil.initUI(activityRegistrationBinding.editCountryCodeSignup, this, activityRegistrationBinding.signupFlag);
//        mCountryUtil.initCodes(RegistrationAct.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Called when facebook login is clicked
     **/
    @Override
    public void Facebookclicked() {
        if (registrationViewModel != null)
            registrationViewModel.setIsLoading(true);
    }

    /**
     * Called when Google login is clicked
     **/
    @Override
    public void gplusclicked() {
        if (registrationViewModel != null)
            registrationViewModel.setIsLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Opens {@link DrawerAct}
     **/
    @Override
    public void OpenDrawerAct() {
        mGoogleSignInClient.signOut();
        startActivity(new Intent(this, DrawerAct.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    /**
     * Returns reference of {@link BaseActivity}
     **/
    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    /**
     * Called when back is clicked
     **/
    @Override
    public void goBack() {
        finish();
    }

}
