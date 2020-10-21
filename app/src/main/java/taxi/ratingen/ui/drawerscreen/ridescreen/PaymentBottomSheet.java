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

import java.util.ArrayList;

public class PaymentBottomSheet extends BottomSheetDialogFragment {

    ArrayList<String> mParam1;
    LinearLayout PB_AddMoneylayout, PB_Cashlayout, PB_Cardlayout, PB_walletlayout;
    TextView PB_OptionTitle;
    RideNavigator mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getStringArrayList(Constants.EXTRA_Data);
        }
    }

    /** Set layout to {@link BottomSheetDialogFragment} **/
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.payment_bottomsheet, null);
        IntializeView(contentView);

        dialog.setContentView(contentView);
    }

    /** Define & set visibility to widgets from xml layout **/
    private void IntializeView(View contentView) {
        PB_AddMoneylayout = contentView.findViewById(R.id.PB_AddMoneylayout);
        PB_Cashlayout = contentView.findViewById(R.id.PB_Cashlayout);
        PB_Cardlayout = contentView.findViewById(R.id.PB_Cardlayout);
        PB_walletlayout = contentView.findViewById(R.id.PB_walletlayout);
        PB_OptionTitle = contentView.findViewById(R.id.PB_OptionTitle);

        if (mParam1 != null && mParam1.size() > 0) {
            if (mParam1.contains("cash")) {
                PB_Cashlayout.setVisibility(View.VISIBLE);
            } else {
                PB_Cashlayout.setVisibility(View.GONE);
//                PB_Cardlayout.setVisibility(View.GONE);
//                PB_walletlayout.setVisibility(View.GONE);
            }
            if (mParam1.contains("card")) {
                PB_Cardlayout.setVisibility(View.VISIBLE);
            } else {
                PB_Cardlayout.setVisibility(View.GONE);
                /*PB_Cashlayout.setVisibility(View.GONE);
                PB_walletlayout.setVisibility(View.GONE);*/
            }

            if (mParam1.contains("wallet")) {
                PB_walletlayout.setVisibility(View.VISIBLE);
            } else {
                PB_walletlayout.setVisibility(View.GONE);
//                PB_Cashlayout.setVisibility(View.GONE);
//                PB_Cardlayout.setVisibility(View.GONE);
            }if (mParam1.contains("all")) {
                PB_Cashlayout.setVisibility(View.VISIBLE);
                PB_Cardlayout.setVisibility(View.VISIBLE);
                PB_walletlayout.setVisibility(View.VISIBLE);
            }

        } else {
            PB_Cashlayout.setVisibility(View.VISIBLE);
            PB_Cardlayout.setVisibility(View.VISIBLE);
            PB_walletlayout.setVisibility(View.VISIBLE);
        }
        PB_walletlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "wallet"));
                dismiss();
            }
        });

        PB_Cashlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "cash"));

                dismiss();
            }
        });

        PB_Cardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_Data, "card"));

                dismiss();
            }
        });


    }

    public interface PaymentOnclick {

    }

}