package taxi.ratingen.utilz;

import androidx.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by root on 9/25/17.
 */


public class EmptyViewModel   {

    //EmptyViewModel created if we are not gng to access viewmodel..
    @Inject

    public EmptyViewModel(){

    }


    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Editfont"})
    public static void setEditFont(EditText textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:Buttonfont"})
    public static void setButtonFont(Button textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }
}
