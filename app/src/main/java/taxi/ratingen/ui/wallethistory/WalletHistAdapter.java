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


public class WalletHistAdapter extends RecyclerView.Adapter<WalletHistAdapter.ViewHolder> {

    public List<HistoryDetailsModel> historyList = new ArrayList<>();
    Context ctx;
    String currencySymbol;
    SimpleDateFormat TargetFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH);
    SimpleDateFormat realformatter = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH);

//    WalletHistAdapter(WalletHistAct ctx, List<HistoryDetailsModel> driverList, String currencySymbol) {
//        this.historyList = driverList;
//        this.ctx = ctx;
//        this.currencySymbol = currencySymbol;
//    }

    WalletHistAdapter() {

    }


    @NonNull
    @Override
    public WalletHistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wallet_histrory_items, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletHistAdapter.ViewHolder viewHolder, final int i) {

        if (historyList.get(i).getRequestId() != null)
            viewHolder.reqId.setText("" + historyList.get(i).getRequestId());

        if (historyList.get(i).getType() != null)
            viewHolder.typeName.setText(historyList.get(i).getType());

        try {
            viewHolder.date.setText(TargetFormatter.format(realformatter.parse(historyList.get(i).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.curr_symbol.setText(currencySymbol);
        viewHolder.symbol.setText(historyList.get(i).getSymbol());
        viewHolder.amount.setText(historyList.get(i).getAmount());

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


        }
    }

    public void addAll(List<HistoryDetailsModel> mcList, String currencySymbol) {
        this.currencySymbol = currencySymbol;
        historyList.addAll(mcList);
        notifyDataSetChanged();
//        for (HistoryDetailsModel mc : mcList) {
//            add(mc);
//        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
