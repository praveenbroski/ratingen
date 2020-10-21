package taxi.ratingen.ui.drawerscreen.favorites;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import taxi.ratingen.databinding.ChildSearchPlaceBinding;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.ui.drawerscreen.changeplace.ChildSearchPlaceViewModel;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Favplace> favPlaceList;
    FavoriteNavigator favoriteNavigator;

    public FavoriteAdapter(List<Favplace> favPlaceList, FavoriteNavigator favoriteNavigator) {
        this.favPlaceList = favPlaceList;
        this.favoriteNavigator = favoriteNavigator;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChildSearchPlaceBinding childItemBinding = ChildSearchPlaceBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(childItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return favPlaceList.size();
    }

    public void addList(List<Favplace> favPlaceList) {
        this.favPlaceList = favPlaceList;
        notifyDataSetChanged();
    }

    public class ChildViewHolder extends BaseViewHolder implements ChildSearchPlaceViewModel.ChidSearchPlaceItemListener {

        private ChildSearchPlaceBinding mBinding;

        ChildViewHolder(ChildSearchPlaceBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            Favplace favplace = favPlaceList.get(position);
            ChildSearchPlaceViewModel childPlaceFavViewModel = new ChildSearchPlaceViewModel(favplace, this);
            childPlaceFavViewModel.showDelete.set(true);
            mBinding.setViewModel(childPlaceFavViewModel);
            mBinding.executePendingBindings();
        }

        /** called when favourite place ({@link Favplace}) is selected **/
        @Override
        public void favSelected(Favplace favplace) {

        }

        @Override
        public void deleteClicked(Favplace favplace) {
            favoriteNavigator.deleteFavClicked(favplace);
        }

    }

}
