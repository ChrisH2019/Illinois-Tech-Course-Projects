package com.weiconghong.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    public TextView office;
    public TextView name;


    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);

        office = itemView.findViewById(R.id.officeEntry);
        name = itemView.findViewById(R.id.nameEntry);
    }
}
