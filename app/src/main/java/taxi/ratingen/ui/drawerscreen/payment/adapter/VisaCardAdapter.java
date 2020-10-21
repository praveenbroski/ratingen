package taxi.ratingen.ui.drawerscreen.payment.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Payment;
import taxi.ratingen.databinding.ChildVisacardBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 11/3/17.
 */

public class VisaCardAdapter extends  RecyclerView.Adapter<BaseViewHolder> {
    ArrayList<Payment> data;
    GitHubService gitHubService;
    SharedPrefence sharedPrefence;
    HashMap<String, String> hashMap;
    public VisaCardAdapter(ArrayList<Payment> payments, GitHubService gitHubService, SharedPrefence sharedPrefence, HashMap<String, String> hashMap) {
        data = payments;
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildVisacardBinding childItemBinding = ChildVisacardBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(childItemBinding);

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /** Populate payments(cards) list
     * @param payments **/
    public void addItems(List<Payment> payments) {
        data.clear();
        data.addAll(payments);
        notifyDataSetChanged();
    }

    /** Returns the ID of selected card **/
    public Integer getSelectedCardId(){
        Iterator it = data.iterator();

        while(it.hasNext()) {
            Payment obj = (Payment)it.next();
            if(obj.isDefault)
                return obj.cardId;
        }
        return null;
    }

    public class ChildViewHolder extends BaseViewHolder implements ChildVisaViewModel.ChidItemViewModelListener {

        private ChildVisacardBinding mBinding;

        private ChildVisaViewModel childVisaViewModel;

        public ChildViewHolder(ChildVisacardBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            Payment payment = data.get(position);
            childVisaViewModel = new ChildVisaViewModel(payment,this,gitHubService,sharedPrefence,hashMap);
            mBinding.setViewModel(childVisaViewModel);
            mBinding.executePendingBindings();
        }

        @Override
        public void PopulateData(List<Payment> payment) {
            addItems(payment);
        }

        @Override
        public void ClearList() {
            data.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getAttachedView() {
            return itemView;
        }

        @Override
        public BaseActivity getBaseActivity() {
            return getBaseActivity();
        }

    }

}
