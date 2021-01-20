package taxi.ratingen.ui.drawerscreen.history.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import taxi.ratingen.retro.responsemodel.TaxiRequestModel;
import taxi.ratingen.databinding.HistoryItemBinding;
import taxi.ratingen.databinding.PaginationLoadingBinding;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.ui.drawerscreen.historydetails.HistoryDetailsScrn;
import taxi.ratingen.utilz.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 1/4/18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<TaxiRequestModel.ResultData> histories;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    Activity activity;
    public boolean isItemClick = true;

    public HistoryAdapter(ArrayList<TaxiRequestModel.ResultData> histories, Activity activity) {
        this.histories = histories;
        this.activity = activity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                HistoryItemBinding fragmentHistoryItemBinding = HistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new ChildHistoryViewHolder(fragmentHistoryItemBinding);

            case LOADING:
            default:
                PaginationLoadingBinding paginationLoadingBinding = PaginationLoadingBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new PaginationViewHolder(paginationLoadingBinding);

        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == histories.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private class PaginationViewHolder extends BaseViewHolder {
        public PaginationViewHolder(PaginationLoadingBinding paginationLoadingBinding) {
            super(paginationLoadingBinding.getRoot());
        }

        @Override
        public void onBind(int position) {

        }
    }

    /** populates history list **/
    public void addItem(List<TaxiRequestModel.ResultData> histories, boolean isdeleted) {
        if (isdeleted) {
            this.histories.clear();
            notifyDataSetChanged();
        }

        for (TaxiRequestModel.ResultData ff : histories) {
            add(ff);
        }
    }

    /** adds single history item to history list **/
    public void add(TaxiRequestModel.ResultData r) {
        histories.add(r);
        notifyItemInserted(histories.size() - 1);
    }

    public void clearList() {
        histories.clear();
        notifyDataSetChanged();
    }

    /** adds a circular progress bar when loading next page **/
    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new TaxiRequestModel.ResultData());
    }

    /** removes bottom loading circular progress when loading completes **/
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = histories.size() - 1;
        TaxiRequestModel.ResultData result = getItem(position);

        if (result != null) {
            histories.remove(position);
            notifyItemRemoved(position);
        }
    }

    /** returns history item at given position **/
    public TaxiRequestModel.ResultData getItem(int position) {
        return histories.get(position);
    }

    public class ChildHistoryViewHolder extends BaseViewHolder implements ChildHistoryViewModel.ChidItemViewModelListener {

        private HistoryItemBinding mBinding;

        private ChildHistoryViewModel childHistoryViewModel;

        public ChildHistoryViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final TaxiRequestModel.ResultData request = histories.get(position);
            childHistoryViewModel = new ChildHistoryViewModel(request, this);
            mBinding.setViewModel(childHistoryViewModel);
            mBinding.executePendingBindings();

        }

        /** called when history item is clicked **/
        @Override
        public void onItemClick(TaxiRequestModel.ResultData request) {
            if (request != null) {
                if (isItemClick) {
                    Intent intent = new Intent(itemView.getContext(), HistoryDetailsScrn.class);
                    intent.putExtra(Constants.EXTRA_Datastrn, request.id);
                    activity.startActivityForResult(intent, Constants.HISTORYLISTSETRESULT);
                    isItemClick = false;
                }
            }
        }

        /** updates history list with new data **/
        @Override
        public void updatelist(TaxiRequestModel.ResultData request) {
            Iterator it = histories.iterator();
            int i = 0;
            while (it.hasNext()) {
                TaxiRequestModel.ResultData req = (TaxiRequestModel.ResultData) it.next();
                if (Integer.parseInt(req.id) == Integer.parseInt(req.id)) {
                    histories.set(i, request);
                    /*notifyDataSetChanged();*/
                    break;
                }
                i++;
            }
        }
    }
}
