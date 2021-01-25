package taxi.ratingen.ui.nodriveralert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import dagger.android.HasAndroidInjector;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.databinding.NoDriverLayBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.drawerscreen.ridescreen.waitingdialog.WaitProgressDialog;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;

public class NoDriverAct extends BaseActivity<NoDriverLayBinding, NoDriverViewModel> implements NoDriverNavigator, HasAndroidInjector {

    public static final String TAG = "NoDriverAct";
    NoDriverLayBinding noDriverLayBinding;
    public static String ARG_PARAM1 = "param";
    String reqId;
    MediaPlayer mMediaPlayer;
    @Inject
    NoDriverViewModel noDriverViewModel;

    @Inject
    SharedPrefence sharedPrefence;

    @Inject
    DispatchingAndroidInjector<Object> fragmentDispatchingAndroidInjector;

    //    public static NoDriverFrag newInstance(int alertAction) {
//        NoDriverFrag fragment = new NoDriverFrag();
//        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM1, alertAction);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * Setup {@link androidx.appcompat.widget.Toolbar},
     * Register {@link BroadcastReceiver}s
     * **/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noDriverLayBinding = getViewDataBinding();
        noDriverViewModel.setNavigator(this);

        removeWaitProgressDialog();
        setSupportActionBar(noDriverLayBinding.layoutToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        noDriverLayBinding.layoutToolbar.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        noDriverLayBinding.layoutToolbar.toolbar.setTitle(getTranslatedString(R.string.app_name));

        if (getIntent() != null)
            reqId = getIntent().getStringExtra("reqId");

        Log.e("requestId==", "id--" + reqId);
        noDriverViewModel.requestId = reqId;

        //  audioPlay();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.PushWaitingorAcceptByDriver));

        IntentFilter intentFilter = new IntentFilter(Constants.BroadcastsActions.LATER_NO_DRIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(laterNoDriver, intentFilter);
    }

    /** {@link MediaPlayer} to play no driver found sound file **/
    public void audioPlay() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        mMediaPlayer = MediaPlayer.create(this, R.raw.no_captain_found);
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        noDriverLayBinding = getViewDataBinding();
//        noDriverViewModel.setNavigator(this);
//
//        if (getArguments() != null) {
//            reqId = getArguments().getInt(ARG_PARAM1);
//            Log.e("reqId11==", "reqId11==" + reqId);
//            noDriverViewModel.requestId = reqId;
//        }
//        ((DrawerAct) Objects.requireNonNull(getActivity())).lockDrawer();
//        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
//        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.no_captain_found);
//        if (!mMediaPlayer.isPlaying()) {
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.start();
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        /*dismiss();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();

        if (laterNoDriver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(laterNoDriver);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }

    @Override
    public NoDriverViewModel getViewModel() {
        return noDriverViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.no_driver_lay;
    }

    @Override
    public SharedPrefence getSharedPrefence() {
        return sharedPrefence;
    }

    @Override
    public void reScheduleSucess() {
        //stopProcess();
        mMediaPlayer.stop();
        WaitProgressDialog waitProgressDialog = new WaitProgressDialog();
        WaitProgressDialog.newInstance("" + reqId).show(getSupportFragmentManager());

        //  Toast.makeText(getActivity(), getActivity().getString(R.string.trip_rescheduled), Toast.LENGTH_SHORT).show();
    }

    /** {@link BroadcastReceiver} to receive no driver msg. **/
    public BroadcastReceiver laterNoDriver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeWaitProgressDialog();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        audioPlay();
        Constants.ACTIVITY_OPENEND_ALRDY = true;
    }

    @Override
    public void onClickNO() {
        stopProcess();
    }

    @Override
    public void closeWaitingDialog() {
        removeWaitProgressDialog();
    }

    @Override
    public BaseActivity getBaseAct() {
        return this;
    }

    /** Finishes {@link NoDriverAct} **/
    public void stopProcess() {
        mMediaPlayer.stop();
        //  mMediaPlayer.reset();
        Constants.ACTIVITY_OPENEND_ALRDY = false;
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.BroadcastsActions.RemoveWaitingDialog);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);

        finish();

        //   startActivity(new Intent(getActivity(), DrawerAct.class));

    }

    /** {@link BroadcastReceiver} to receiver broadcasts sent within the app **/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Mapscrn", "Mapscrn");
            removeWaitProgressDialog();
            finish();
            //  removeRideConfirmationFragment();
            if (intent.hasExtra(Constants.EXTRA_Data)) {

                String json = intent.getExtras().getString(Constants.EXTRA_Data);

                BaseResponse baseResponse = CommonUtils.getSingleObject(json, BaseResponse.class);
                if (baseResponse != null && baseResponse.getRequest() != null) {
                    if (baseResponse.getRequest().later != null && baseResponse.getRequest().later == 1) {
                        //   Toast.makeText(NoDriverAct.this, getString(R.string.text_accepted), Toast.LENGTH_SHORT).show();

                    } else
                        NeedTripFragment(baseResponse.getRequest(), baseResponse.request.driver);
                } else if (baseResponse.successMessage != null &&
                        (baseResponse.successMessage.contains("no driver") || baseResponse.successMessage.contains("no_driver") || baseResponse.successMessage.contains("no_driver_found"))) {
                    Toast.makeText(NoDriverAct.this, getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(NoDriverAct.this, getTranslatedString(R.string.Txt_NoDriverFound), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    public AndroidInjector<Object> androidInjector() {
        return fragmentDispatchingAndroidInjector;
    }

}
