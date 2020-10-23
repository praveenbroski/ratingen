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

    //    ArrayList<Type> types;
    ArrayList<TypeNew> types;
    RideConfirmationFragment mapScrn;
    public String selectedCarId;

    public CarsTypesAdapter(RideConfirmationFragment mapScrn, ArrayList<TypeNew> types) {
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

//    /** populate {@link Type} list **/
//    public void addList(List<Type> type) {
//        this.types.clear();
//        if (this.selectedCarId == null && type != null && type.size() > 0)
//            for (Type typeCar : type) {
//                assert typeCar.drivers != null;
//                if (typeCar.drivers.size() > 0) {
//                    selectedCarId = typeCar.id;
//                    break;
//                }
//            /*if (typeCar != null && !TextUtils.isEmpty(typeCar.duration)&&!typeCar.duration.equalsIgnoreCase("--")) {
//                    selectedCarId = typeCar.id;
//                    break;
//                }*/
//            }
////        if (selectedCarId == null && type.get(0) != null)
////            selectedCarId = type.get(0).id;
//        this.types.addAll(type);
//        notifyDataSetChanged();
//    }

    /** populate {@link TypeNew} list **/
    public void  addList(List<TypeNew> types) {
        this.types.clear();
        this.types.addAll(types);
        notifyDataSetChanged();
    }

    /** get selected car for ride **/
    public Type getSelectedCar() {
//        Iterator it = types.iterator();
//
//        while (it.hasNext()) {
//            Type obj = (Type) it.next();
//            if (obj.id == selectedCarId)
//                return obj;
//        }
        return null;
    }

    /** get new selected car **/
    public TypeNew getSelectedNewCar() {
        Iterator it = types.iterator();

        while (it.hasNext()) {
            TypeNew obj = (TypeNew) it.next();
            if (obj.getZoneId().equalsIgnoreCase(selectedCarId))
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
            types.get(position).isselected = (types.get(position).getZoneId().equals(selectedCarId));
            if (types.get(position).getZoneId().equals(selectedCarId))
                types.set(position, types.get(position));
//            final Type request = types.get(position);
            final TypeNew request = types.get(position);
            childCarsTypesViewModel = new ChildCarsTypesViewModel(request, this);
            mBinding.setViewModel(childCarsTypesViewModel);
            mBinding.executePendingBindings();
        }

        /** called when a car is selected **/
        @Override
        public void onItemCarClick(TypeNew typ) {
//            Log.d("Adapter","keys--"+typ.preferred_payment);
//            if(typ.drivers!=null&&typ.drivers.size()>0) {
            Iterator it = types.iterator();

            while (it.hasNext()) {
                TypeNew obj = (TypeNew) it.next();
                obj.isselected = obj.getZoneId().equals(typ.getZoneId());
            }
            selectedCarId = typ.getZoneId();
            if (mapScrn != null)
                mapScrn.typeSelected(typ);
//                mapScrn.carSlected(typ);
            notifyDataSetChanged();
//            }
        }

        @Override
        public void onClickFareDetails(TypeNew request) {
            if (mapScrn != null)
                mapScrn.fareDetailsClicked(request);
        }

    }

}

