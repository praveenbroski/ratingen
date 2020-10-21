package taxi.ratingen.ui.drawerscreen.changeplace;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.databinding.ChildSearchPlaceBinding;

import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.utilz.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/30/17.
 */

public class SearchPlaceAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    public List<Favplace> favList;
    public SearchPlaceActivity placeApiAct;
    public boolean isPickup;

    public SearchPlaceAdapter(ArrayList<Favplace> favplaces, SearchPlaceActivity placeApiAct, boolean isPickup) {
        favList = favplaces;
        this.placeApiAct = placeApiAct;
        this.isPickup = isPickup;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildSearchPlaceBinding childItemBinding = ChildSearchPlaceBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(childItemBinding);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    /** populating {@link Favplace} list from API **/
    public void addList(List<Favplace> favplace) {
        favList.clear();
        favList.addAll(favplace);
        notifyDataSetChanged();
    }

    public class ChildViewHolder extends BaseViewHolder implements ChildSearchPlaceViewModel.ChidSearchPlaceItemListener {

        private ChildSearchPlaceBinding mBinding;

        private ChildSearchPlaceViewModel childPlaceFavViewModel;

        public ChildViewHolder(ChildSearchPlaceBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        @Override
        public void onBind(int position) {
            Favplace favplace = favList.get(position);
            childPlaceFavViewModel = new ChildSearchPlaceViewModel(favplace, this);
            mBinding.setViewModel(childPlaceFavViewModel);
            mBinding.executePendingBindings();


        }

        /** called when favourite place ({@link Favplace}) is selected **/
        @Override
        public void favSelected(Favplace favplace) {
            Intent intent = new Intent();
            if (favplace.IsPlaceLayout) {
                intent.putExtra(Constants.EXTRA_Data, favplace.PlaceApiOGaddress);
            } else {
                intent.putExtra(Constants.EXTRA_Data, favplace.placeId);
            }
            intent.putExtra(Constants.EXTRA_IS_PICKUP, isPickup);
            placeApiAct.setResult(Constants.REQUEST_CODE_AUTOCOMPLETE, intent);
            placeApiAct.finish();
        }

        @Override
        public void deleteClicked(Favplace favplace) {

        }

    }
}
