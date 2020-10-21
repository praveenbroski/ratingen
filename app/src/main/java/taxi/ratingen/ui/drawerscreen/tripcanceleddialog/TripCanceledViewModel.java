package taxi.ratingen.ui.drawerscreen.tripcanceleddialog;

import androidx.databinding.ObservableBoolean;
import android.view.View;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.util.HashMap;

public class TripCanceledViewModel extends BaseNetwork<BaseResponse, TripCancelNavigtor> {
    public ObservableBoolean trip_canceld = new ObservableBoolean();
    public ObservableBoolean pickup_change = new ObservableBoolean();
    public ObservableBoolean drop_change = new ObservableBoolean();
    public ObservableBoolean button_txt = new ObservableBoolean();
    public SharedPrefence sharedPrefence;

    public TripCanceledViewModel(GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson) {
        super(gitHubService, sharedPrefence, gson);
        this.sharedPrefence=sharedPrefence;
    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }

    @Override
    public void onSuccessfulApi(long taskId, BaseResponse response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    /** Click listener for Go to Home button **/
    public void onClickHome(View V) {
        getmNavigator().homeClicked();
    }

}
