package taxi.ratingen.ui.drawerscreen.sos.adapter;

import androidx.databinding.ObservableField;

import taxi.ratingen.retro.responsemodel.So;

/**
 * Created by root on 12/20/17.
 */

public class SosAdapterViewModel {
    ChidItemViewModelListener listener;
   public So so;
   public ObservableField<String> name=new ObservableField<>();
    public ObservableField<String> Number=new ObservableField<>();
    public SosAdapterViewModel(So so, SosAdapter.ChildViewHolder childViewHolder) {
        listener=childViewHolder;
        this.so=so;
        name.set(so.name);
        Number.set(so.number);
    }

    /** Interface for {@link SosAdapter} to bind phone icon click **/
    public interface ChidItemViewModelListener {
        void Onclickphone(So so);

    }

    /** called when phone icon is clicked **/
    public void onItemPhoneClick(){
        listener.Onclickphone(so);
    }

}
