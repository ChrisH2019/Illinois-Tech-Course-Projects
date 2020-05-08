package com.example.administrator.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView noteTitle;
    public TextView noteDate;
    public TextView noteDescription;

    public MyViewHolder(View v) {
        super(v);
        noteTitle =  v.findViewById(R.id.title);
        noteDate =  v.findViewById(R.id.date);
        noteDescription =  v.findViewById(R.id.description);
    }
}
