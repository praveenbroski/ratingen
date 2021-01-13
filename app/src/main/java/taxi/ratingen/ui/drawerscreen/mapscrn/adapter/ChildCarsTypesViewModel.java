package taxi.ratingen.ui.drawerscreen.mapscrn.adapter;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.utilz.CommonUtils;

/**
 * Created by root on 11/16/17.
 */

public class ChildCarsTypesViewModel {

    Type request;
    public ObservableField<String> name;
    public ObservableField<String> carurl;
    public ObservableField<String> etaTime = new ObservableField<>("TBD");
    public ObservableField<String> rideFare = new ObservableField<>("TBD");
    public ObservableBoolean isSelected;
    public ObservableBoolean isDriverAvailable = new ObservableBoolean(true);
    public CarsTypesViewModelListener mListener;

    public ChildCarsTypesViewModel(Type request, CarsTypesViewModelListener listener) {
        this.request = request;
        this.mListener = listener;
        name = new ObservableField<>(request.name);
//        duration = new ObservableField<>(request.duration);
        if (request.etaModel != null && request.etaModel.driver_arival_estimation != null)
            etaTime.set(request.etaModel.driver_arival_estimation);
        if (request.etaModel != null && request.etaModel.currency_symbol != null)
            rideFare.set(request.etaModel.currency_symbol + CommonUtils.doubleDecimalFromat(request.etaModel.min_amount)
                    + " - " + request.etaModel.currency_symbol + CommonUtils.doubleDecimalFromat(request.etaModel.max_amount));
        carurl = new ObservableField<>(request.icon);
        isSelected = new ObservableBoolean(request.isselected == null ? false : request.isselected);
        isDriverAvailable.set((request.drivers != null && request.drivers.size() > 0));
    }

    public void onItemCarClick() {
        request.isselected = true;
        mListener.onItemCarClick(request);
    }

    public void onClickFareDetails() {
        mListener.onClickFareDetails(request);
    }

    @BindingAdapter("childcarimageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).apply(new RequestOptions().error(R.drawable.ic_car).placeholder(R.drawable.ic_car)).into(imageView);
    }

    public interface CarsTypesViewModelListener {

        void onItemCarClick(Type id);

        void onClickFareDetails(Type id);

    }

}

