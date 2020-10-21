package taxi.ratingen.ui.topdriver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import taxi.ratingen.R;

import java.util.List;

public class TopDriverAdapter extends RecyclerView.Adapter<TopDriverAdapter.ViewHolder> {
    private List<TopDriverModel> driverList;
    private TopDriverAct topDriverAct;

    TopDriverAdapter(TopDriverAct topDriverAct, List<TopDriverModel> driverList) {
        this.driverList = driverList;
        this.topDriverAct = topDriverAct;
    }


    @NonNull
    @Override
    public TopDriverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.top_driver_list, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopDriverAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.rating.setText(driverList.get(i).getReview());
        viewHolder.name.setText(driverList.get(i).getFirstname() + driverList.get(i).getLastname());
        viewHolder.carType.setText(driverList.get(i).getDriverType());
        viewHolder.carModel.setText(driverList.get(i).getCarModel());
        Glide.with(topDriverAct)
                .load(driverList.get(i).getProfilePic())
                .apply(new RequestOptions().placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile))
                .into(viewHolder.driverProfile);

        /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topDriverAct.valuesFromAdapter(driverList.get(i).getId());
            }
        });*/
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, carType, carModel;
        ImageView driverProfile;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.drivername);
            rating = itemView.findViewById(R.id.rating);
            carType = itemView.findViewById(R.id.car_type);
            carModel = itemView.findViewById(R.id.car_model);
            driverProfile = itemView.findViewById(R.id.driver_img);

        }
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }
}
