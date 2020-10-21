package taxi.ratingen.ui.drawerscreen.tripcanceleddialog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.view.View;

import taxi.ratingen.app.MyApp;
import taxi.ratingen.BR;
import taxi.ratingen.R;
import taxi.ratingen.databinding.ActivityTripCancelBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseFragment;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import javax.inject.Inject;

import taxi.ratingen.ui.nodriveralert.NoDriverAct;

public class TripCanceledDialogFrag extends BaseFragment<ActivityTripCancelBinding, TripCanceledViewModel> implements TripCancelNavigtor {

    public static final String TAG = "TripCanceledDialogFrag";
    @Inject
    TripCanceledViewModel tripCanceledViewModel;

    MediaPlayer mMediaPlayer;

    @Inject
    SharedPrefence sharedPrefence;

    ActivityTripCancelBinding tripCancelBinding;
    public int alertAction = 0;

    public static TripCanceledDialogFrag newInstance(int alertAction) {
        TripCanceledDialogFrag fragment = new TripCanceledDialogFrag();
        Bundle args = new Bundle();
        args.putInt(NoDriverAct.ARG_PARAM1, alertAction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripCancelBinding = getViewDataBinding();
        tripCanceledViewModel = getViewModel();
        tripCanceledViewModel.setNavigator(this);
        sharedPrefence = new SharedPrefence(PreferenceManager.getDefaultSharedPreferences(MyApp.getmContext()), PreferenceManager.getDefaultSharedPreferences(MyApp.getmContext()).edit());

        assert getArguments() != null;
        alertAction = getArguments().getInt(NoDriverAct.ARG_PARAM1);

    }

    /** Informs user about the drive has cancelled the trip in a voice msg. **/
    private void voiceMsg() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);

        if (alertAction != 0 && alertAction == 1) {
            tripCanceledViewModel.button_txt.set(true);
            tripCanceledViewModel.trip_canceld.set(true);
            mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.speech);
        }

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }

    }

    @Override
    public TripCanceledViewModel getViewModel() {
        if (tripCanceledViewModel == null)
            tripCanceledViewModel = new TripCanceledViewModel(null, getBaseActivity().getSharedPrefence(), new Gson());
        return tripCanceledViewModel;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        /*dismiss();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        voiceMsg();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_trip_cancel;
    }

    /** Opens Home fragment and stop {@link MediaPlayer} **/
    @Override
    public void homeClicked() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setLooping(false);
        }
        /*dismiss();*/
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).NeedHomeFragment();
            //   ((DrawerAct) getActivity()).resumeDriverState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }
}
