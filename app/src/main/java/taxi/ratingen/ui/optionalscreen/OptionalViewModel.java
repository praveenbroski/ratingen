package taxi.ratingen.ui.optionalscreen;

import androidx.databinding.BindingAdapter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import taxi.ratingen.retro.base.BaseNetwork;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.User;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.exception.CustomException;
import taxi.ratingen.utilz.SharedPrefence;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by root on 10/10/17.
 */

public class OptionalViewModel extends BaseNetwork<User,OptionalNavigator> {

    @Inject
    public OptionalViewModel(@Named(Constants.ourApp) GitHubService gitHubService, SharedPrefence sharedPrefence, Gson gson){
        super(gitHubService,sharedPrefence,gson);

    }

    /** Custom {@link BindingAdapter} method to set font of the {@link TextView}
     * @param textView {@link TextView} whose font needs to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    /** Custom {@link BindingAdapter} method to set font of the {@link EditText}
     * @param textView {@link EditText} whose font needs to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));

    }

    /** Custom {@link BindingAdapter} method to set font of the {@link Button}
     * @param textView {@link Button} whose font needs to be changed
     * @param fontName Name of the font **/
    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));

    }

    @Override
    public void onSuccessfulApi(long taskId, User response) {

    }

    @Override
    public void onFailureApi(long taskId, CustomException e) {

    }

    @Override
    public HashMap<String, String> getMap() {
        return null;
    }


   public void LoginButtonClicked(View view){
     getmNavigator().openLoginScreen();

   }

    public void SignupButtonClicked(View view){
        getmNavigator().openSigupScreen();
    }

}
