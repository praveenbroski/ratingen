package taxi.ratingen.ui.drawerscreen.placeapiscreen.adapter;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.databinding.ChildFavheaderBinding;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseViewHolder;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiAct;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiNavigator;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 11/30/17.
 */

public class PlaceandFavAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    public GitHubService gitHubService;
    public SharedPrefence sharedPrefence;
    public HashMap<String, String> hashMap;
    public List<Favplace> favList;
    public Gson gson;
    public PlaceApiNavigator listener;
    public PlaceApiAct placeApiAct;

    public PlaceandFavAdapter(ArrayList<Favplace> favplaces, GitHubService gitHubService,
                              SharedPrefence sharedPrefence, HashMap<String, String> hashMap, Gson gson,
                              PlaceApiAct placeApiAct) {
        favList = favplaces;
        this.gitHubService = gitHubService;
        this.sharedPrefence = sharedPrefence;
        this.hashMap = hashMap;
        this.gson = gson;
        this.placeApiAct=placeApiAct;
        this.listener = placeApiAct;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildFavheaderBinding childItemBinding = ChildFavheaderBinding.inflate(LayoutInflater.from(parent.getContext()),
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

    /** Populate favourite places list
     * @param favplace {@link Favplace} **/
    public void addList(List<Favplace> favplace) {
        favList.clear();
        favList.addAll(favplace);
        notifyDataSetChanged();
    }

    public class ChildViewHolder extends BaseViewHolder implements ChildPlaceFavViewModel.ChidPlaceItemListener {

        private ChildFavheaderBinding mBinding;

        private ChildPlaceFavViewModel childPlaceFavViewModel;

        public ChildViewHolder(ChildFavheaderBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        @Override
        public void onBind(int position) {
            Favplace favplace = favList.get(position);

            childPlaceFavViewModel = new ChildPlaceFavViewModel(gitHubService, favplace, this, hashMap, sharedPrefence);
            mBinding.setViewModel(childPlaceFavViewModel);
            mBinding.executePendingBindings();


        }


        @Override
        public PlaceApiNavigator getListener() {
            return listener;
        }

        /** Delete favourite at specific position
         * @param i position of clicked item **/
        @Override
        public void Deleteditems(int i) {
            int z = 0, y = 0;
            Iterator it = favList.iterator();

            while (it.hasNext()) {
                Favplace obj = (Favplace) it.next();
                if (getAdapterPosition() == 0 && favList.size() > 1) {

                    if (y == 1) {
                        obj.IsFavTit = true;
                        favList.set(y, obj);
                        break;
                    }


                }

                y++;
            }
            Iterator itz = favList.iterator();

            while (itz.hasNext()) {
                Favplace obj = (Favplace) itz.next();
                if (obj.Favid == i) {
                    favList.remove(obj);
                 /*   notifyItemRemoved(z);
                    notifyItemChanged(z);*/
                 notifyDataSetChanged();
                 /*   if(i==0&&favList.size() > 1){
                        notifyItemChanged(z+1);
                    }*/
                    break;
                }
                z++;
            }


            BaseResponse baseResponse = gson.fromJson(sharedPrefence.Getvalue(SharedPrefence.FAVLIST), BaseResponse.class);
            baseResponse.getFavplace().clear();
            baseResponse.setFavplace(favList);
            sharedPrefence.savevalue(SharedPrefence.FAVLIST, gson.toJson(baseResponse));

        }

        /** Called when {@link Favplace} was clicked from the list
         * @param favplace - {@link Favplace} **/
        @Override
        public void Favselected(Favplace favplace) {
            Intent intent=new Intent();

            if(favplace.IsPlaceLayout){
                intent.putExtra(Constants.EXTRA_Data,favplace.PlaceApiOGaddress);
            }else {
                intent.putExtra(Constants.EXTRA_Data,favplace.placeId);
            }

            placeApiAct.setResult(Constants.REQUEST_CODE_AUTOCOMPLETE,intent);
            placeApiAct.finish();

        }

        /** Returns reference of the {@link BaseActivity} **/
        @Override
        public BaseActivity getBaseAct() {
            return getListener().getAttachedContext();
        }

    }

}
