package com.weiconghong.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "OfficialAdapter";

    private List<Official> officialList;
    private MainActivity mainActivity;



    public OfficialAdapter(List<Official> officialList, MainActivity ma) {
        this.officialList = officialList;
        this.mainActivity = ma;
    }



    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making New");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_entry, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new OfficialViewHolder((itemView));
    }



    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.office.setText(official.getOffice());
        holder.name.setText(String.format("%s (%s)", official.getName(), official.getParty()));
    }



    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
