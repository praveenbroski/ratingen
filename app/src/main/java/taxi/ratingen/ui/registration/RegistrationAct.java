package taxi.ratingen.ui.registration;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;

import androidx.databinding.library.baseAdapters.BR;

import java.io.File;

import javax.inject.Inject;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityRegistrationBinding;
import taxi.ratingen.ui.applyrefferal.ApplayRefferal;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.RealPathUtil;
import taxi.ratingen.utilz.SharedPrefence;

public class RegistrationAct extends BaseActivity<ActivityRegistrationBinding, RegistrationViewModel> implements RegistrationNavigator {

    @Inject
    SharedPrefence sharedPrefence;
    @Inject
    RegistrationViewModel registrationViewModel;
    ActivityRegistrationBinding activityRegistrationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistrationBinding = getViewDataBinding();
        activityRegistrationBinding.setViewModel(registrationViewModel);
        registrationViewModel.setNavigator(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.clr_FB4A46));
        }

        activityRegistrationBinding.termsCondt.setPaintFlags(activityRegistrationBinding.termsCondt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRegistrationBinding.privacyPolicy.setPaintFlags(activityRegistrationBinding.privacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (getIntent() != null) {
            registrationViewModel.uuid.set(getIntent().getStringExtra(Constants.uuidValue));
            registrationViewModel.userPhone.set(getIntent().getStringExtra(Constants.phoneNum));
            registrationViewModel.countryID.set(getIntent().getStringExtra(Constants.countryId));
            registrationViewModel.userCountryCode.set(getIntent().getStringExtra(Constants.phonePrefix));
        }

        activityRegistrationBinding.backImg.setOnClickListener(view -> finish());
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
        else {
            if (registrationViewModel != null)
                registrationViewModel.setIsLoading(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Opens {@link DrawerAct}
     **/
    @Override
    public void OpenDrawerAct() {
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

    @Override
    public void getPath(Intent data) {
        registrationViewModel.RealPath = RealPathUtil.getRealPath(this, data.getData());
        registrationViewModel.RealFile = new File(registrationViewModel.RealPath == null ? "" : registrationViewModel.RealPath);
        registrationViewModel.bitmap_profilePicture.set(registrationViewModel.RealPath);
    }

}
