package taxi.ratingen.ui.getstarted;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import taxi.ratingen.R;
import taxi.ratingen.ui.drawerscreen.profilescrn.ProfileNavigator;
import taxi.ratingen.ui.drawerscreen.setting.SettingNavigator;

import java.util.List;


public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
    private List<String> langList;
    private Context context;
    GetStartedNavigator navigator;
    SettingNavigator settingNavigator;
    ProfileNavigator profileNavigator;
    int id = -1;

    LanguageAdapter(Context context, List<String> lang_list, GetStartedNavigator navigator) {
        langList = lang_list;
        this.context = context;
        this.navigator = navigator;
    }

    public LanguageAdapter(List<String> lang_list, SettingNavigator navigator) {
        langList = lang_list;
        this.settingNavigator = navigator;
    }

    public LanguageAdapter(List<String> lang_list, ProfileNavigator navigator) {
        langList = lang_list;
        this.profileNavigator = navigator;
    }

    @NonNull
    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lang_items, viewGroup, false);

        return new LanguageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.lang_names.setText(langList.get(i));

        if (id == i) {
            viewHolder.tickChkBox.setVisibility(View.VISIBLE);
            viewHolder.checkbox.setVisibility(View.GONE);
        } else {
            viewHolder.tickChkBox.setVisibility(View.GONE);
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lang_names;
        ImageView checkbox, tickChkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            lang_names = itemView.findViewById(R.id.lang_item);
            checkbox = itemView.findViewById(R.id.chkbox);
            tickChkBox = itemView.findViewById(R.id.tick_chk_box);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context != null) {
                        navigator.setSelectedLanguage(langList.get(getAdapterPosition()));
                        id = getAdapterPosition();
                        notifyDataSetChanged();
                    } else if (settingNavigator != null) {
                        settingNavigator.setSelectedLanguage(langList.get(getAdapterPosition()));
                    } else if (profileNavigator != null) {
                        profileNavigator.setSelectedLanguage(langList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return langList.size();
    }
}