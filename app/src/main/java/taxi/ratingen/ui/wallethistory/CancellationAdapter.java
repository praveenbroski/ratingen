package taxi.ratingen.ui.wallethistory;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import taxi.ratingen.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CancellationAdapter extends RecyclerView.Adapter<CancellationAdapter.ViewHolder> {

    public List<CancelledListModel> cancelledTripList = new ArrayList<>();
    Context ctx;
    String txt_unbilled;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);


//    public CancellationAdapter(WalletHistAct walletHistAct, List<CancelledListModel> cancelledTripList, String txt_unbilled) {
//
//        this.cancelledTripList = cancelledTripList;
//        ctx = walletHistAct;
//        this.txt_unbilled = txt_unbilled;
//    }

    CancellationAdapter() {

    }

    @NonNull
    @Override
    public CancellationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wallet_histrory_items, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CancellationAdapter.ViewHolder viewHolder, final int i) {

        if (cancelledTripList.get(i).getRequestId() != null) {
            viewHolder.reqId.setVisibility(View.VISIBLE);
            viewHolder.reqId.setText("" + cancelledTripList.get(i).getRequestId());
        }

        viewHolder.typeName.setText(txt_unbilled);
        try {
            viewHolder.date.setText(TargetFormatter.format(realformatter.parse(cancelledTripList.get(i).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //viewHolder.curr_symbol.setText(currencySymbol);
        viewHolder.symbol.setText(cancelledTripList.get(i).getCurrencySymbol());
        viewHolder.amount.setText(cancelledTripList.get(i).getCancellationFee());

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeName, date, symbol, curr_symbol, reqId, amount;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeName = itemView.findViewById(R.id.type);
            reqId = itemView.findViewById(R.id.req_id);
            date = itemView.findViewById(R.id.date);
            symbol = itemView.findViewById(R.id.symbol);
            curr_symbol = itemView.findViewById(R.id.currency_symbol);
            amount = itemView.findViewById(R.id.item1);

            curr_symbol.setVisibility(View.GONE);


        }
    }

    public void addAll(List<CancelledListModel> mcList, String txt_unbilled) {
        this.txt_unbilled = txt_unbilled;
        cancelledTripList.addAll(mcList);
        notifyDataSetChanged();
//        for (HistoryDetailsModel mc : mcList) {
//            add(mc);
//        }
    }

    @Override
    public int getItemCount() {
        return cancelledTripList.size();
    }
}
