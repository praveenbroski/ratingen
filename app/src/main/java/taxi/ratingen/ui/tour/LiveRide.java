package taxi.ratingen.ui.tour;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import taxi.ratingen.R;
import taxi.ratingen.app.MyApp;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.SharedPrefence;

import javax.inject.Inject;

public class LiveRide extends Fragment {

    //static int from;
    TextView title, titleDesc;
    TranslationModel translationModel;

    @Inject
    SharedPrefence sharedPrefence;

    public static LiveRide newInstance() {
        return new LiveRide();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefence = new SharedPrefence(PreferenceManager.getDefaultSharedPreferences(MyApp.getmContext()), PreferenceManager.getDefaultSharedPreferences(MyApp.getmContext()).edit());

        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = new Gson().fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);

            Log.e("data--", "fjhfj" + translationModel.txt_skip);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title.setText(translationModel.txt_live_tracking);
        titleDesc.setText(translationModel.txt_live_desc);

       /* if (from == 0) {
            title.setText(translationModel.txt_req_ride);
            titleDesc.setText(translationModel.txt_req_desc);
        } else if (from == 1) {
            title.setText(translationModel.txt_veh_selection);
            titleDesc.setText(translationModel.txt_veh_desc);
        } else if (from == 2) {
            title.setText(translationModel.txt_live_tracking);
            titleDesc.setText(translationModel.txt_live_desc);
        } else if (from == 3) {
            title.setText(translationModel.txt_trip_share);
            titleDesc.setText(translationModel.txt_trip_desc);
        } else {
            title.setText(translationModel.txt_req_ride);
            titleDesc.setText(translationModel.txt_req_desc);
        }

*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_slider, container, false);
        title = rootView.findViewById(R.id.title);
        titleDesc = rootView.findViewById(R.id.title_desc);

        return rootView;
    }
}
