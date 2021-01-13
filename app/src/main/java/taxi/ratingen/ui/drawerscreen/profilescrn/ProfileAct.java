package taxi.ratingen.ui.drawerscreen.profilescrn;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;

import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;

import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityProfileBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.ui.drawerscreen.profilescrn.edit.NameMailEdit;
import taxi.ratingen.ui.getstarted.LanguageAdapter;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class ProfileAct extends BaseActivity<ActivityProfileBinding, ProfileViewModel> implements ProfileNavigator, HasAndroidInjector {


    public static final String TAG = "ProfileAct";
    @Inject
    ProfileViewModel profileViewModel;

    ActivityProfileBinding activityProfileBinding;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = getViewDataBinding();
        profileViewModel.setNavigator(this);
        Setup();
    }

    /**
     * {@link android.widget.Toolbar} setup
     * Profile data fetching API
     * **/
    private void Setup() {
        setSupportActionBar(activityProfileBinding.layoutToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        activityProfileBinding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.back_nav);
        activityProfileBinding.layoutToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityProfileBinding.layoutToolbar.toolbar.setTitle(getTranslatedString(R.string.text_profile));
//        activityProfileBinding.editFnameProfile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                activityProfileBinding.scrollProfile.scrollTo(0, activityProfileBinding.scrollProfile.getBottom());
//            }
//        });

        profileViewModel.callProfileRetriveApi();

        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)))
            profileViewModel.txt_Language_update.set(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE));
    }

    @Override
    public void refreshFocusScroll() {
        activityProfileBinding.scrollProfile.scrollTo(0, activityProfileBinding.scrollProfile.getBottom());
    }

    /** Returns reference of {@link BaseActivity} **/
    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    @Override
    public ProfileViewModel getViewModel() {
        return profileViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    /** Open camera {@link Intent} to take photo for profile picture **/
    @Override
    public void OpenCameraIntent() {
        cameraIntent();
    }

    /** Open gallery {@link Intent} to select profile picture from saved pictures **/
    @Override
    public void OpenGalleryIntent() {
        galleryIntent();
    }

    /** Send profile broadcasts **/
    @Override
    public void SendBroadcast() {
        Intent intent = new Intent(Constants.ProfileBroastcast);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /** To finish the current activity **/
    @Override
    public void finishAct() {
        finish();
    }

    /** Profile pic selection options whether from Camera or Gallery **/
    @Override
    public void alertSelectCameraGalary() {
        String[] items;
        if (!CommonUtils.IsEmpty(profileViewModel.profileURL.get())) {
            items = new String[] {getBaseAct().getTranslatedString(R.string.text_camera),
                    getBaseAct().getTranslatedString(R.string.text_galary),
                    getBaseAct().getTranslatedString(R.string.txt_view_profile)};
        } else {
            items = new String[] {getBaseAct().getTranslatedString(R.string.text_camera),
                    getBaseAct().getTranslatedString(R.string.text_galary)};
        }
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder1.setTitle(getTranslatedString(R.string.txt_Choose));
        builder1.setCancelable(true);
        builder1.setItems(items, (dialog, which) -> {
            if (which == 0) {
                cameraIntent();
            } else if (which == 1) {
                galleryIntent();
            } else if (which == 2) {
                List<String> iList = new ArrayList<>();
                iList.add(profileViewModel.profileURL.get());
                new StfalconImageViewer.Builder<>(getBaseAct(), iList, ((imageView, image) ->
                        Glide.with(getBaseAct())
                                .load(profileViewModel.profileURL.get())
                                .transition(GenericTransitionOptions.with(R.anim.slide_left))
                                .into(imageView)))
                        .show(true);
            }
        }).show();
    }

    /** Callback to receive results from previous screen. In this case Camera intent result or Gallery image selection result **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE)
                profileViewModel.onSelectFromGalleryResult(data);
            else if (requestCode == Constants.REQUEST_CAMERA)
                profileViewModel.onCaptureImageResult(data);
            else if (requestCode == Constants.REQUEST_PROFILE_UPDATE)
                profileViewModel.callProfileRetriveApi();
        }
    }

    /** Open gallery {@link Intent} **/
    private void galleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGranted(Constants.storagePermission)) {
            requestPermissionsSafely(Constants.storagePermission, Constants.REQUEST_PERMISSION);
        } else {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.SELECT_FILE);

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, Constants.SELECT_FILE);
        }

    }

    /** Open camera {@link Intent} **/
    private void cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGranted(Constants.storagePermission)) {
            requestPermissionsSafely(Constants.storagePermission, Constants.REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        pictureUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
            startActivityForResult(intent, Constants.REQUEST_CAMERA);
        }

    }

    @Override
    public void openFirstNameUpdate() {
        Intent intent = new Intent(this, NameMailEdit.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_MODE, 0);
        bundle.putString(Constants.EXTRA_VALUE, profileViewModel.fullName.get());
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.REQUEST_PROFILE_UPDATE);
    }

    @Override
    public void openLastNameUpdate() {
//        Intent intent = new Intent(this, NameMailEdit.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.EXTRA_MODE, 1);
//        bundle.putString(Constants.EXTRA_VALUE, profileViewModel.LastName.get());
//        intent.putExtras(bundle);
//        startActivityForResult(intent, Constants.REQUEST_PROFILE_UPDATE);
    }

    @Override
    public void openMailUpdate() {
        Intent intent = new Intent(this, NameMailEdit.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_MODE, 2);
        bundle.putString(Constants.EXTRA_VALUE, profileViewModel.Email.get());
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.REQUEST_PROFILE_UPDATE);
    }

    @Override
    public void openPhoneUpdate() {
        Intent intent = new Intent(this, NameMailEdit.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_MODE, 3);
        bundle.putString(Constants.EXTRA_VALUE, profileViewModel.Phone_Number.get());
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.REQUEST_PROFILE_UPDATE);
    }

    @Override
    public void languageItems(List<String> items) {
        dialog = new Dialog(this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lang_dialog);

        RecyclerView recyclerView = dialog.findViewById(R.id.lang_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        LanguageAdapter languageAdapter = new LanguageAdapter(items,this);
        recyclerView.setAdapter(languageAdapter);
        dialog.show();
    }

    @Override
    public void setSelectedLanguage(String s) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        profileViewModel.txt_Language_update.set(s);
        String loc;
        if (s.contains("pt")) {
            loc = "pt";
        } else if (s.contains("ar")) {
            loc = "ar";
        } else if (s.equalsIgnoreCase("zh")) {
            loc = "zh";
        } else if (s.contains("fr")) {
            loc = "fr";
        } else if (s.contains("es")) {
            loc = "es";
        } else if (s.contains("ja")) {
            loc = "ja";
        } else if (s.contains("ko")) {
            loc = "ko";
        } else {
            loc = "en";
        }
        profileViewModel.sharedPrefence.savevalue(SharedPrefence.LANGUANGE, loc);
        refreshScreen();
    }

    @Override
    public void refreshScreen() {
        finish();
        Intent intent = new Intent(this, DrawerAct.class);
        startActivity(intent);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

}
