package taxi.ratingen.ui.drawerscreen.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import taxi.ratingen.retro.responsemodel.Bill;
import taxi.ratingen.databinding.ItemAddionalChargesBillBinding;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.ui.drawerscreen.DrawerAct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 26/7/18.
 */

public class AddChargeBillAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<Bill.AdditionalCharge> additionalChargeList;
    Integer selected_id;
    DrawerAct baseActivity;
    String currency;
    public AddChargeBillAdapter(String currency,ArrayList<Bill.AdditionalCharge> manageDocModels_,
                                DrawerAct drawerAct) {
        additionalChargeList = manageDocModels_;
        this.currency= currency;
        this.baseActivity = drawerAct;

    }

    public void addServices(ArrayList<Bill.AdditionalCharge> manageDocModels_) {
        additionalChargeList = manageDocModels_;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAddionalChargesBillBinding itemManageDocBinding = ItemAddionalChargesBillBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(itemManageDocBinding);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return additionalChargeList.size();
    }

    /** add items to {@link Bill.AdditionalCharge} list **/
    public void addList(List<Bill.AdditionalCharge> manageDocModels) {
//        itemManageDocListener = manageDocFragment;
        additionalChargeList.clear();
        additionalChargeList.addAll(manageDocModels);
        notifyDataSetChanged();
    }


    public class ChildViewHolder extends BaseViewHolder {

        ItemAddionalChargesBillBinding mBinding;

        private AddChrargeAdapViewModel manageDocAdapViewModel;

        public ChildViewHolder(ItemAddionalChargesBillBinding itm_Bind) {
            super(itm_Bind.getRoot());
            this.mBinding = itm_Bind;
        }

        /** bind {@link Bill.AdditionalCharge} view model with {@link ItemAddionalChargesBillBinding} layout **/
        @Override
        public void onBind(int position) {
            Bill.AdditionalCharge manageDocModel = additionalChargeList.get(position);
            manageDocAdapViewModel = new AddChrargeAdapViewModel(currency, manageDocModel, baseActivity);
            mBinding.setViewModel(manageDocAdapViewModel);
            mBinding.executePendingBindings();
        }

    }
    public void selectedItem(Integer s_id) {
        selected_id = s_id;
        notifyDataSetChanged();
    }
}