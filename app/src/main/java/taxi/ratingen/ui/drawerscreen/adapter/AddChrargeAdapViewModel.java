package taxi.ratingen.ui.drawerscreen.adapter;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import taxi.ratingen.retro.responsemodel.Bill;
import taxi.ratingen.ui.base.BaseActivity;
import taxi.ratingen.utilz.CommonUtils;


/**
 * Created by root on 26/7/18.
 */

public class AddChrargeAdapViewModel {
    public Bill.AdditionalCharge manageDocModel;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> amount = new ObservableField<>();
    public BaseActivity baseActivity;
    private int DOCUPLOAD_CODE = 500;

    public AddChrargeAdapViewModel(String currency, Bill.AdditionalCharge manageDocModel_,
                                   BaseActivity baseActivity) {
        this.manageDocModel = manageDocModel_;
        name.set(manageDocModel.name);
        amount.set(currency + (CommonUtils.IsEmpty(manageDocModel.amount) ? "0.00" : CommonUtils.doubleDecimalFromat(Double.valueOf(manageDocModel.amount))));
        this.baseActivity = baseActivity;
    }


    public void onItemClick(View v) {

    }

    /** custom {@link BindingAdapter} method to set font for the textView **/
    @BindingAdapter({"bind:textfont"})
    public static void settextFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

}