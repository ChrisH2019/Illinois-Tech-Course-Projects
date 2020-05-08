package com.weiconghong.stockwatch;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {


    public TextView symbol;
    public TextView companyName;
    public TextView price;
    public TextView priceChange;


    public MyViewHolder(@NonNull View v) {
        super(v);
        symbol = v.findViewById(R.id.symbol);
        companyName = v.findViewById(R.id.companyName);
        price = v.findViewById(R.id.price);
        priceChange = v.findViewById(R.id.priceChange);
    }
}
