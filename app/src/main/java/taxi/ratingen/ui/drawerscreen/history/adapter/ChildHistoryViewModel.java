package taxi.ratingen.ui.drawerscreen.history.adapter;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.history;
import taxi.ratingen.utilz.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by root on 1/4/18.
 */

public class ChildHistoryViewModel {
    ChidItemViewModelListener mListener;
    taxi.ratingen.retro.responsemodel.history history;
    public ObservableField<String> driverurl, carurl, typename, total, requestid, pickadd, dropAdd, DateTime;
    public ObservableBoolean Iscancelled, islater, iscompleted, currency, isDropAvailable, isUnsucess, isINTrip, isTotalShown;
    ChidItemViewModelListener listener;
    public ObservableBoolean isShare;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);

    public ChildHistoryViewModel(history request, ChidItemViewModelListener childHistoryViewHolder) {
        mListener = childHistoryViewHolder;
        /*DataBindingUtil.setDefaultComponent(new MyComponent(this));*/
        this.history = request;
        this.listener = childHistoryViewHolder;
        driverurl = new ObservableField<>(request.driverProfilePic);
        carurl = new ObservableField<>(request.typeIcon);
        typename = new ObservableField<>(request.typename);
        DateTime = new ObservableField<>();
        isTotalShown = new ObservableBoolean(false);
        Iscancelled = new ObservableBoolean();
        iscompleted = new ObservableBoolean();
        islater = new ObservableBoolean();
        isUnsucess = new ObservableBoolean();
        isINTrip = new ObservableBoolean();
        total = new ObservableField<>("");
        try {
            if (request.tripStartTime != null)
                DateTime.set(TargetFormatter.format(realformatter.parse(request.tripStartTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        requestid = new ObservableField<>(" ( "+request.requestId+" ) ");
        pickadd = new ObservableField<>(request.pickLocation);
        dropAdd = new ObservableField<>(request.dropLocation);
        isDropAvailable = new ObservableBoolean(!CommonUtils.IsEmpty(request.dropLocation));
        if (request.isCancelled != null)
            Iscancelled.set(request.isCancelled != 0);
        else Iscancelled.set(false);
        if (request.isCompleted != null)
            iscompleted.set(request.isCompleted != 0);
        else iscompleted.set(false);
        if (request.later != null)
            islater.set(request.later != 0);
        else islater.set(false);
        isShare = new ObservableBoolean(request.is_share == 1);
        if (request.isCancelled != null && request.cancel_method != null)
            isUnsucess.set(request.isCancelled == 1 && request.cancel_method == 3);
        else isUnsucess.set(false);
        if (request.isCancelled != null && request.isCompleted != null)
            isINTrip.set(request.isCancelled == 0 && request.isCompleted == 0);
        else isINTrip.set(false);
        if (!Iscancelled.get())
            if (request.currency != null && request.total != null)
                total.set("" + request.currency + CommonUtils.doubleDecimalFromat(Double.valueOf(String.valueOf(request.total))));
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
        void onItemClick(history request);

        void updatelist(history request);
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
