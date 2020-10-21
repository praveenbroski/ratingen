package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import taxi.ratingen.R;
import taxi.ratingen.utilz.Constants;

public class ChooseSeatBottomSheet extends BottomSheetDialogFragment {

    String crcy,fOne,fTwo;
    LinearLayout CSB_One_layout, CSB_Two_layout;
    TextView tv_currency1,tv_currency2,cost_for_one,cost_for_two;
    RideNavigator mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            crcy = getArguments().getString("Crcy");
            fOne = getArguments().getString("Fare1");
            fTwo = getArguments().getString("Fare2");
        }
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.choose_seat_bottomsheet, null);
        IntializeView(contentView);

        dialog.setContentView(contentView);
    }

    /** Defines widgets from xml layout **/
    private void IntializeView(View contentView) {
        CSB_One_layout = contentView.findViewById(R.id.CSB_One_layout);
        CSB_Two_layout = contentView.findViewById(R.id.CSB_Two_layout);
        tv_currency1 = contentView.findViewById(R.id.tv_currency1);
        tv_currency2 = contentView.findViewById(R.id.tv_currency2);
        cost_for_one = contentView.findViewById(R.id.cost_for_one);
        cost_for_two = contentView.findViewById(R.id.cost_for_two);

        tv_currency1.setText(""+crcy);
        tv_currency2.setText(""+crcy);
        cost_for_one.setText(""+fOne);
        cost_for_two.setText(""+fTwo);

        CSB_One_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "one_user"));
                dismiss();
            }
        });

        CSB_Two_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "two_user"));
                dismiss();
            }
        });
    }

    public interface PaymentOnclick {

    }
}