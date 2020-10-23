package taxi.ratingen.ui.signup.country_code;


import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import taxi.ratingen.R;
import taxi.ratingen.retro.responsemodel.CountryListModel;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ViewHolder> implements TextWatcher {

    Context context;
    List<CountryListModel> data;
    public List<CountryListModel> filteredDataList;
    public List<CountryListModel> primaryDataList;
    CountryListNavigator countryListNavigator;

    public CountryListAdapter(Context context, List<CountryListModel> data, CountryListNavigator countryListNavigator) {
        this.countryListNavigator = countryListNavigator;
        this.context = context;
        this.data = data;
        primaryDataList = data;
        filteredDataList = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.country_list_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.countryName.setText(filteredDataList.get(i).name);
        viewHolder.countryCode.setText("(" + filteredDataList.get(i).callingCode + ")");
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (primaryDataList != null && primaryDataList.size() > 0 && editable != null) {
            String strFilterable = editable.toString();
            List<CountryListModel> newFilteredList = new ArrayList<>();
            if (!TextUtils.isEmpty(editable.toString())) {
                for (CountryListModel row : primaryDataList) {
                    if (row.name.toLowerCase().startsWith(strFilterable) || row.callingCode.contains(strFilterable)) {
                        newFilteredList.add(row);
                    }
                }
                filteredDataList = newFilteredList;
                notifyDataSetChanged();
            } else {
                filteredDataList = primaryDataList;
                notifyDataSetChanged();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView countryName, countryCode;
        ImageView countryFlag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.country_name);
            countryCode = itemView.findViewById(R.id.countryCode);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String flag = "";
                    String code = filteredDataList.get(getAdapterPosition()).callingCode;
                    String name = filteredDataList.get(getAdapterPosition()).name;
                    String countryId = filteredDataList.get(getAdapterPosition()).id;
                    String iso2 = filteredDataList.get(getAdapterPosition()).iso2;
                    countryListNavigator.clickedItem(flag, code, name, countryId, iso2);
                }
            });

        }
    }
}
