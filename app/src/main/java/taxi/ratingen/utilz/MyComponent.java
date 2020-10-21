package taxi.ratingen.utilz;


import taxi.ratingen.ui.drawerscreen.complaint.ComplaintViewModel;
import taxi.ratingen.ui.drawerscreen.feedback.FeedbackViewModel;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrnViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationViewModel;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideFragViewModel;

public class MyComponent<T> implements androidx.databinding.DataBindingComponent {

    private T instance;

    public MyComponent(T t) {
        instance = t;
    }


    public RideFragViewModel getRideFragViewModel() {
        return (RideFragViewModel) instance;
    }


    public RideConfirmationViewModel getRideConfirmationViewModel() {
        return (RideConfirmationViewModel) instance;
    }


    public MapScrnViewModel getMapScrnViewModel() {
        return (MapScrnViewModel) instance;
    }

    @Override
    public FeedbackViewModel getFeedbackViewModel() {
        return (FeedbackViewModel) instance;
    }

    @Override
    public ComplaintViewModel getComplaintViewModel() {
        return (ComplaintViewModel) instance;
    }


}