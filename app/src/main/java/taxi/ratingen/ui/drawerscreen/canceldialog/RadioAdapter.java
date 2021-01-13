package taxi.ratingen.ui.drawerscreen.canceldialog;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;

import java.util.List;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {
    public int mSelectedItem = -1;
    public String selecteID;
    public List<BaseResponse.ReasonCancel> mItems;
    private Context mContext;
    cancelDialogNavigator listener;

    public RadioAdapter(Context context, List<BaseResponse.ReasonCancel> items, cancelDialogNavigator listener) {
        mContext = context;
        mItems = items;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.mRadio.setChecked(i == mSelectedItem);
        viewHolder.cancelText.setText(mItems.get(i).reason);
        viewHolder.viewParent.setTag(mItems.get(i).id);
    }

    public String getSelectPosition() {
        return selecteID;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.cancel_radio_item, viewGroup, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public RadioButton mRadio;
        public View viewParent;
        TextView cancelText;

        public ViewHolder(final View inflate) {
            super(inflate);
            cancelText = inflate.findViewById(R.id.cancel_txt);
            mRadio = inflate.findViewById(R.id.radio_cancel_desc);
            viewParent = inflate.findViewById(R.id.layout_cancel_desc);
            mRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewParent.performClick();
                }
            });
            viewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedItem = getAdapterPosition();
                    selecteID = String.valueOf(view.getTag());
                    notifyDataSetChanged();
                    if (listener != null)
                        if (!TextUtils.isEmpty(selecteID))
                            listener.selectedOthers(selecteID.equalsIgnoreCase("0"));
                }
            });
        }
    }
}
