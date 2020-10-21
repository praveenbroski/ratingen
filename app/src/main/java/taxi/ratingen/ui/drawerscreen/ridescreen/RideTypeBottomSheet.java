package taxi.ratingen.ui.drawerscreen.ridescreen;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import taxi.ratingen.R;
import taxi.ratingen.utilz.Constants;

public class RideTypeBottomSheet extends BottomSheetDialogFragment {

    boolean isShareRideAvailable=false;
    TextView txt_NormalRide,txt_ShareRide;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isShareRideAvailable = getArguments().getBoolean(Constants.EXTRA_RIDE_TYPE);
        }
    }

    /** Setup layout to {@link BottomSheetDialogFragment}
     * @param dialog Dialog
     * @param style Style for the dialog **/
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.ride_bottomsheet, null);
        IntializeView(contentView);

        dialog.setContentView(contentView);
    }

    /** Define widgets in the xml
     * @param contentView Root view of the dialog **/
    private void IntializeView(View contentView) {
        txt_NormalRide = contentView.findViewById(R.id.normal_ride);
        txt_ShareRide= contentView.findViewById(R.id.share_ride);
        txt_ShareRide.setVisibility(isShareRideAvailable?View.VISIBLE:View.GONE);
        txt_NormalRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_RIDE_TYPE, false));
                dismiss();
            }
        });

        txt_ShareRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(Constants.EXTRA_RIDE_TYPE, true));

                dismiss();
            }
        });
    }

}