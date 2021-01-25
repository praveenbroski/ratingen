package taxi.ratingen.ui.drawerscreen.history.adapter;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.utilz.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by root on 1/4/18.
 */

public class ChildHistoryViewModel {
    ChidItemViewModelListener mListener;
    TaxiRequestModel.ResultData history;
    public ObservableField<String> driverurl, carurl, typename, total, requestid, pickadd, dropAdd, DateTime;
    public ObservableBoolean Iscancelled, islater, iscompleted, currency, isDropAvailable, isUnsucess, isINTrip, isTotalShown;
    ChidItemViewModelListener listener;
    public ObservableBoolean isShare;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);

    public ChildHistoryViewModel(TaxiRequestModel.ResultData request, ChidItemViewModelListener childHistoryViewHolder) {
        mListener = childHistoryViewHolder;
        /*DataBindingUtil.setDefaultComponent(new MyComponent(this));*/
        this.history = request;
        this.listener = childHistoryViewHolder;
        if (request.driverDetail != null) {
            driverurl = new ObservableField<>(request.driverDetail.driverData.profilePicture);
            carurl = new ObservableField<>(request.driverDetail.driverData.vehicleTypeImage);
            typename = new ObservableField<>(request.driverDetail.driverData.vehicleTypeName);
        } else {
            driverurl = new ObservableField<>("");
            carurl = new ObservableField<>("");
            typename = new ObservableField<>("");
        }
        DateTime = new ObservableField<>();
        isTotalShown = new ObservableBoolean(false);
        Iscancelled = new ObservableBoolean();
        iscompleted = new ObservableBoolean();
        islater = new ObservableBoolean();
        isUnsucess = new ObservableBoolean();
        isINTrip = new ObservableBoolean();
        total = new ObservableField<>("");
        DateTime.set(request.tripStartTime);
//        try {
//            if (request.tripStartTime != null)
//                DateTime.set(TargetFormatter.format(realformatter.parse(request.tripStartTime)));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        requestid = new ObservableField<>(" ( " + request.requestNumber + " ) ");
        pickadd = new ObservableField<>(request.pickAddress);
        dropAdd = new ObservableField<>(request.dropAddress);
        isDropAvailable = new ObservableBoolean(!CommonUtils.IsEmpty(request.dropAddress));
        if (request.isCancelled != null)
            Iscancelled.set(request.isCancelled != 0);
        else Iscancelled.set(false);
        if (request.isCompleted != null)
            iscompleted.set(request.isCompleted != 0);
        else iscompleted.set(false);
        if (request.isLater != null)
            islater.set(request.isLater == 1);
        else islater.set(false);
        isShare = new ObservableBoolean(request.isShare != null && request.isShare == 1);
        if (request.isCancelled != null && request.cancelMethod != null)
            isUnsucess.set(request.isCancelled == 1 && request.cancelMethod.equals("3"));
        else isUnsucess.set(false);
        if (request.isCancelled != null && request.isCompleted != null)
            isINTrip.set(request.isCancelled == 0 && request.isCompleted == 0);
        else isINTrip.set(false);
        if (!Iscancelled.get())
            if (request.billDetail != null && request.billDetail.billData != null)
                if (request.billDetail.billData.requestedCurrencySymbol != null && request.billDetail.billData.totalAmount != null)
                total.set("" + request.billDetail.billData.requestedCurrencySymbol
                        + CommonUtils.doubleDecimalFromat(Double.valueOf(String.valueOf(request.billDetail.billData.totalAmount))));
            else total.set("");
        if (islater.get() && iscompleted.get())
            isTotalShown.set(true);
    }

    /** sets click listener for history item **/
    public void onItemClick() {
        if (mListener != null)
            mListener.onItemClick(history);
    }

    public interface ChidItemViewModelListener {
        void onItemClick(TaxiRequestModel.ResultData request);

        void updatelist(TaxiRequestModel.ResultData request);
    }

    /** custom {@link BindingAdapter} function to set car image from API **/
    @BindingAdapter("imageUrlcaricon")
    public static void setImageUrl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.fitCenterTransform()
                .error(R.drawable.ic_car)
                .placeholder(R.drawable.ic_car))
                .into(imageView);
    }

    /** custom {@link BindingAdapter} function to set driver's picture **/
    @BindingAdapter("imageUrldrivericon")
    public static void setImageUrfl(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()
                .error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user))
                .into(imageView);
    }

}
