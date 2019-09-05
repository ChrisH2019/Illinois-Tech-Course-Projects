package com.weiconghong.stockwatch;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {


    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;


    public StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainActivity = ma;
    }

    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_entry, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.price.setText(String.format(Locale.US, "%.2f", stock.getPrice()));

        if (stock.getPriceChange() < 0) {
            holder.symbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
            holder.priceChange.setText(String.format(Locale.US, "\u25bc %.2f (%.2f%%)",
                    stock.getPriceChange(), stock.getChangePercent()));
        } else {
            holder.symbol.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.priceChange.setTextColor(Color.GREEN);
            holder.priceChange.setText(String.format(Locale.US, "\u25b2 %.2f (%.2f%%)",
                    stock.getPriceChange(), stock.getChangePercent()));
        }
    }


    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
