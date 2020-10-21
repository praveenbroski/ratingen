package taxi.ratingen.retro.responsemodel;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class FAQModel {
    public ObservableBoolean clickedArrow=new ObservableBoolean(true);
    @Expose
    public String id;
    @Expose
    public String question;
    @Expose
    public String answer;

    @Override
    public String toString() {
        return question;
    }

    List<FAQModel> faqModelList;

    public void onClickArrow(View v){
        for (FAQModel faqModel: faqModelList) {
            faqModel.clickedArrow.set(true);
        }
        clickedArrow.set(!clickedArrow.get());
    }
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    public void setList(List<FAQModel> faqModelList) {
        this.faqModelList = faqModelList;
    }

}
