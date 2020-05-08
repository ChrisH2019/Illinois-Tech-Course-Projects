package com.example.administrator.multinotes;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "NoteAdapter";

    private List<Note> noteList;
    private MainActivity mainActivity;

    public NoteAdapter(List<Note> noteList, MainActivity ma) {
        this.noteList = noteList;
        this.mainActivity = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making New");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_note_element_layout, parent,false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteDate.setText(note.getDate());

        if (note.getDescription().length() > 80) {
            holder.noteDescription.setText(note.getDescription().substring(0, 80) + "...");
        } else {
            holder.noteDescription.setText(note.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
