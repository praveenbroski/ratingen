package taxi.ratingen.ui.drawerscreen.sos.adapter;

import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import taxi.ratingen.retro.responsemodel.So;
import taxi.ratingen.databinding.ChildSosBinding;
import taxi.ratingen.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/20/17.
 */

public class SosAdapter  extends  RecyclerView.Adapter<BaseViewHolder> {
    List<So> sosList;

    /** Constructor for adapter initialization with {@link ArrayList} of {@link So} daa models
     * @param sos {@link ArrayList} of {@link So} data models **/
    public SosAdapter(ArrayList<So> sos) {
        sosList = sos;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildSosBinding childItemBinding = ChildSosBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ChildViewHolder(childItemBinding);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
       holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return sosList.size();
    }

    /** Populate sosList of this class with {@link List} of {@link So} from the parameter
     * @param sos {@link So} {@link List} **/
    public void addList(List<So> sos) {
        sosList.clear();
        sosList.addAll(sos);
        notifyDataSetChanged();
    }

    public class ChildViewHolder extends BaseViewHolder implements SosAdapterViewModel.ChidItemViewModelListener {

        private ChildSosBinding mBinding;

        private SosAdapterViewModel childVisaViewModel;

        public ChildViewHolder(ChildSosBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        @Override
        public void onBind(int position) {
            So so=sosList.get(position);


            childVisaViewModel = new SosAdapterViewModel(so,this);

            mBinding.setViewModel(childVisaViewModel);


            mBinding.executePendingBindings();


        }

        /** Click listener for phone icon to initiate phone call **/
        @Override
        public void Onclickphone(So so) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + so.number));

            itemView.getContext().startActivity(callIntent);
        }
    }

}
