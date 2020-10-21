package taxi.ratingen.ui.drawerscreen.changeplace;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import android.view.View;
import android.widget.ImageView;

import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseView;
import taxi.ratingen.utilz.exception.CustomException;

import java.util.HashMap;

/**
 * Created by root on 11/30/17.
 */

public class ChildSearchPlaceViewModel extends BaseNetwork<BaseResponse,ChildSearchPlaceViewModel.ChidSearchPlaceItemListener> {
   public ObservableField<String> place,NickName;
   public Favplace favplace;
   ChidSearchPlaceItemListener adapterlister;
   public ObservableField<Boolean> showDelete = new ObservableField<>(false);
   public ChildSearchPlaceViewModel(Favplace favplace, ChidSearchPlaceItemListener adapterlister) {
      super(null);
      this.favplace=favplace;
      this.adapterlister=adapterlister;
      place=new ObservableField<>(favplace.placeId);
      NickName=new ObservableField<>(favplace.nickName);
   }

   /** favourite icon tapped on places search result **/
   public void onFavSelected(View view) {
      adapterlister.favSelected(favplace);
   }

   public void deleteFavClicked(View v) {
      adapterlister.deleteClicked(favplace);
   }

   @Override
   public void onSuccessfulApi(long taskId, BaseResponse response) {

   }

   @Override
   public void onFailureApi(long taskId, CustomException e) {

   }

   @Override
   public HashMap<String, String> getMap() {
      return null;
   }

   /** interface for places search result **/
   public interface ChidSearchPlaceItemListener extends BaseView {
      void favSelected(Favplace favplace);

      void deleteClicked(Favplace favplace);
   }

   @BindingAdapter({"android:src"})
   public static void setImageViewResource(ImageView imageView, int resource) {
      imageView.setImageResource(resource);
   }


}
