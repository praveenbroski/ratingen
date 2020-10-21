package taxi.ratingen.ui.drawerscreen.placeapiscreen.adapter;

import android.content.DialogInterface;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.Favplace;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.ui.base.BaseView;
import taxi.ratingen.ui.drawerscreen.placeapiscreen.PlaceApiNavigator;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

/**
 * Created by root on 11/30/17.
 */

public class ChildPlaceFavViewModel extends BaseNetwork<BaseResponse, ChildPlaceFavViewModel.ChidPlaceItemListener> {

   public ObservableBoolean IsFavtitle,IsPlaceLayout;
   public ObservableField<String> place,NickName;
   GitHubService gitHubService;
   public Favplace favplace;
   public PopupMenu popupMenu;
   ChidPlaceItemListener adapterlister;
   HashMap<String, String> hashMap;
   SharedPrefence sharedPrefence;

   public ChildPlaceFavViewModel(GitHubService gitHubService, Favplace favplace, ChidPlaceItemListener adapterlister,
                                 HashMap<String, String> hashMap, SharedPrefence sharedPrefence) {
      super(gitHubService);
      this.favplace=favplace;
      this.gitHubService=gitHubService;
      this.adapterlister=adapterlister;
      this.hashMap=hashMap;
      this.sharedPrefence =sharedPrefence;
      place=new ObservableField<>(favplace.placeId);
      NickName=new ObservableField<>(favplace.nickName);
      IsFavtitle=new ObservableBoolean(favplace.IsFavTit);
      IsPlaceLayout=new ObservableBoolean(favplace.IsPlaceLayout);
   }

   public interface ChidPlaceItemListener extends BaseView {
      PlaceApiNavigator getListener();
      void Deleteditems(int i);
      void Favselected(Favplace favplace);
      BaseActivity getBaseAct();
   }

   /** Click listener when fav item's delete clicked. It asks user for a confirmation before deleting saved fav. location **/
   public void onDeleteClick(final View view) {
      popupMenu = new PopupMenu(view.getContext(),view);
      popupMenu.inflate(R.menu.favmenu);
      popupMenu.show();
      popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
               case R.id.menu_delete:
                  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                           case DialogInterface.BUTTON_POSITIVE:
                             dialog.dismiss();
                              adapterlister.getListener().showProgress(true);
                              hashMap.clear();
                              hashMap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
                              hashMap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
                              hashMap.put(Constants.NetworkParameters.id,sharedPrefence.Getvalue(SharedPrefence.ID));
                              hashMap.put(Constants.NetworkParameters.token,sharedPrefence.Getvalue(SharedPrefence.TOKEN));
                              hashMap.put(Constants.NetworkParameters.favid,""+favplace.Favid);
                              DeleteFavNetworkcall();
                              break;
                           case DialogInterface.BUTTON_NEGATIVE:
                              //No button clicked
                              dialog.dismiss();
                              break;
                        }
                     }
                  };

                  AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                  builder.setMessage(adapterlister.getBaseAct().getTranslatedString(R.string.text_desc_delete)).setPositiveButton(android.R.string.ok, dialogClickListener)
                          .setNegativeButton(android.R.string.no, dialogClickListener).show();

                  break;
            }
            return true;
         }
      });
   }

   /** Called when any of the Favourite address was selected **/
   public void onFavselected(View view){
      adapterlister.Favselected(favplace);
   }

   /** Callback for successful API calls
    * @param taskId ID of the API task
    * @param response Response model **/
   @Override
   public void onSuccessfulApi(long taskId, BaseResponse response) {
      adapterlister.getListener().showProgress(false);
      if(response.successMessage.equalsIgnoreCase("Favorite_Deleted_Successfully")){
            adapterlister.Deleteditems(response.favid);
      }
   }

   /** Callback for failed API calls
    * @param taskId ID of the API task
    * @param e Exception msg **/
   @Override
   public void onFailureApi(long taskId, CustomException e) {
      adapterlister.getListener().showProgress(false);
      adapterlister.getListener().showMessage(e);
      if (e.getCode() == Constants.ErrorCode.COMPANY_CREDENTIALS_NOT_VALID ||
              e.getCode() == Constants.ErrorCode.COMPANY_KEY_DATE_EXPIRED ||
              e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_ACTIVE ||
              e.getCode() == Constants.ErrorCode.COMPANY_KEY_NOT_VALID) {
         if(getmNavigator()!=null);
         getmNavigator().refreshCompanyKey();
      }
   }

   /** Returns a {@link HashMap} with query parameters required for API calls **/
   @Override
   public HashMap<String, String> getMap() {
      return hashMap;
   }

}
