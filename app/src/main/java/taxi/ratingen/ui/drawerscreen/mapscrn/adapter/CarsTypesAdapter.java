package taxi.ratingen.ui.drawerscreen.mapscrn.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import taxi.ratingen.retro.responsemodel.Type;
import taxi.ratingen.databinding.ChildCartypesBinding;
import taxi.ratingen.retro.responsemodel.TypeNew;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.ui.drawerscreen.ridescreen.RideConfirmationFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 11/16/17.
 */

public class CarsTypesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    ArrayList<Type> types;
    RideConfirmationFragment mapScrn;
    public String selectedCarId;

    public CarsTypesAdapter(RideConfirmationFragment mapScrn, ArrayList<Type> types) {
        this.types = types;
        this.mapScrn = mapScrn;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildCartypesBinding childItemBinding = ChildCartypesBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(childItemBinding);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public void addList(List<Type> type) {
        this.types.clear();
        if (this.selectedCarId == null && type != null && type.size() > 0)
            for (Type typeCar : type) {
                if (typeCar.drivers != null)
                    if (typeCar.drivers.size() > 0) {
                        selectedCarId = typeCar.type_id;
                        break;
                    }
            }
        this.types.addAll(type);
        notifyDataSetChanged();
    }

    public Type getSelectedCar() {
        for (Type obj : types) {
            if (obj.type_id.equalsIgnoreCase(selectedCarId))
                return obj;
        }
        return null;
    }

    public class ChildViewHolder extends BaseViewHolder implements ChildCarsTypesViewModel.CarsTypesViewModelListener {

        private ChildCartypesBinding mBinding;

        private ChildCarsTypesViewModel childCarsTypesViewModel;

        public ChildViewHolder(ChildCartypesBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            types.get(position).isselected = (types.get(position).type_id.equalsIgnoreCase(selectedCarId));
            if (types.get(position).type_id.equalsIgnoreCase(selectedCarId))
                types.set(position, types.get(position));
            final Type request = types.get(position);
            childCarsTypesViewModel = new ChildCarsTypesViewModel(request, this);
            mBinding.setViewModel(childCarsTypesViewModel);
            mBinding.executePendingBindings();
        }

        @Override
        public void onItemCarClick(Type typ) {
            for (Type obj : types) {
                obj.isselected = obj.id == typ.id;
            }
            selectedCarId = typ.type_id;
            if (mapScrn != null)
                mapScrn.carSlected(typ);
            notifyDataSetChanged();
        }

        @Override
        public void onClickFareDetails(Type id) {
            if (mapScrn != null)
                mapScrn.fareDetailsClicked(id);
        }

    }

}

