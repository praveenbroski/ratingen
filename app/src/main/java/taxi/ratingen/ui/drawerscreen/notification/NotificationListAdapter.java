package taxi.ratingen.ui.drawerscreen.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    Context ctx;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    List<BaseResponse.NotificationList> notificationList;

    public NotificationListAdapter(Context ctx, List<BaseResponse.NotificationList> notificationList) {
        this.ctx = ctx;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.noti_lis_item, viewGroup, false);

        return new NotificationListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.title.setText(notificationList.get(i).getTitle());
        viewHolder.desc.setText(notificationList.get(i).getMessage());
        try {
            viewHolder.date.setText(TargetFormatter.format(realformatter.parse(notificationList.get(i).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(ctx).load(notificationList.get(i).getImage()).
                apply(RequestOptions.circleCropTransform().error(R.drawable.ic_user).
                        placeholder(R.drawable.ic_user)).into(viewHolder.notiImg);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, date;
        ImageView notiImg;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noti_title);
            desc = itemView.findViewById(R.id.noti_desc);
            date = itemView.findViewById(R.id.noti_date);
            notiImg = itemView.findViewById(R.id.notification_img);
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
