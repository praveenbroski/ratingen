package taxi.ratingen.ui.drawerscreen.mapscrn.adapter;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.retro.responsemodel.TypeNew;

/**
 * Created by root on 11/16/17.
 */

public class ChildCarsTypesViewModel {
    TypeNew request;
//    TypeNew request;
    public ObservableField<String> name;
    public ObservableField<String> carurl;
    public ObservableBoolean Isselected;
    public ObservableField<String> rideFare = new ObservableField<>("NA");
    public ObservableField<String> etaTime = new ObservableField<>("NA");
    public ObservableBoolean isDriverAvailable=new ObservableBoolean(true);
    public CarsTypesViewModelListener mListener;
    public ChildCarsTypesViewModel(TypeNew request, CarsTypesViewModelListener listener) {
//    public ChildCarsTypesViewModel(TypeNew request,CarsTypesViewModelListener listener) {
        this.request=request;
        this.mListener=listener;
        name=new ObservableField<>(request.name);
//        carurl=new ObservableField<>(BuildConfig.BASE_VEHICLE_IMG_URL+request.icon);
        carurl = new ObservableField<>(request.icon);
        Isselected=new ObservableBoolean(request.isselected==null?false:request.isselected);
        rideFare.set(request.etaPrice);
        etaTime.set(request.etaTime);
        isDriverAvailable.set((request.getDrivers()!=null&&request.getDrivers().size()>0));
    }

    /** called when a car in list of cars clicked **/
    public void onItemCarClick() {
        request.isselected=true;
        mListener.onItemCarClick(request);
    }

    /** called when fare details clicked **/
    public void onClickFareDetails() {
        mListener.onClickFareDetails(request);
    }

    /** custom {@link BindingAdapter} function to set image of car **/
    @BindingAdapter("childcarimageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).apply(new RequestOptions().error(R.drawable.ic_car).placeholder(R.drawable.ic_car)).into(imageView);

    }

    public interface CarsTypesViewModelListener {
        void onItemCarClick(TypeNew id);

        void onClickFareDetails(TypeNew request);
    }
}
