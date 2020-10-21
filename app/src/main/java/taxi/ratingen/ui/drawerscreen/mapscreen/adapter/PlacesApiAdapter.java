package taxi.ratingen.ui.drawerscreen.mapscreen.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import taxi.ratingen.ui.drawerscreen.mapscreen.MapFragment;
import taxi.ratingen.ui.drawerscreen.mapscreen.MapNavigator;

import java.util.ArrayList;

/**
 * Created by root on 10/11/17.
 */

public class PlacesApiAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList = new ArrayList<String>();
    MapNavigator mapNavigator;


    public PlacesApiAdapter(@NonNull Context context, MapFragment mapFragment, @LayoutRes int resource) {
        super(context, resource);
        mapNavigator= mapFragment;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        if (resultList.size() > index) {
            return resultList.get(index);
        }
        return "";
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    // Retrieve the autocomplete results.
                    /*resultList.clear();*/

                    resultList = mapNavigator.getPlaceList(charSequence.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
