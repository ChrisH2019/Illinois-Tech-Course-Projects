package com.example.administrator.multinotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";

    private static final int NOTE_REQUEST_CODE = 1;

    private List<Note> noteList = new ArrayList<>();

    private RecyclerView recyclerView;

    private NoteAdapter nAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        nAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AsyncActivity asyncActivity = new AsyncActivity(this);
        asyncActivity.execute(getString(R.string.total_file_name), getString(R.string.encoding));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        saveTotalFile();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    private void saveTotalFile() {
        Log.d(TAG, "savedTotalFile: ");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.total_file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note note : noteList) {
                writer.beginObject();
                writer.name("title").value(note.getTitle());
                writer.name("date").value(note.getDate());
                writer.name("description").value(note.getDescription());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
            Log.d(TAG, "saveTotalFile: Total JSONFILES SAVED");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(this, "About Selected", Toast.LENGTH_SHORT).show();
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.add:
                Toast.makeText(this, "Add Selected", Toast.LENGTH_SHORT).show();
                Intent addIntent = new Intent(MainActivity.this, NoteActivity.class);
                addIntent.putExtra("Add New Note", -2);
                startActivityForResult(addIntent, NOTE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == NOTE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("Note Saved");
                int position = data.getIntExtra("Position", -1);
                noteList.add(0, note);
                nAdapter.notifyDataSetChanged();
                Log.d(TAG, "onActivityResult: RESULT_OK");
                Log.d(TAG, "onActivityResult: " + note.toString());
                Log.d(TAG, "onActivityResult: " + position);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: RESULT_CANCELED");
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code" + requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int position = recyclerView.getChildLayoutPosition(v);
        Note note = noteList.get(position);

        Intent editIntent = new Intent(MainActivity.this, NoteActivity.class);
        editIntent.putExtra("Edit Previous Note", note);
        editIntent.putExtra("Position", position);
        startActivityForResult(editIntent, NOTE_REQUEST_CODE);
    }

    @Override
    public boolean onLongClick(final View v) {
        final int position = recyclerView.getChildLayoutPosition(v);
        final Note note = noteList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onLongClick: Note Deleted");
                Toast.makeText(v.getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                noteList.remove(position);
                nAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setMessage("Delete Note '" + note.getTitle() + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void whenAsyncIsDone (JSONArray jsonArray) {
        Log.d(TAG, "whenAsyncIsDone: ");
        try {
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                noteList.add(new Note(jsonObject.getString("title"),
                                       jsonObject.getString("date"),
                                       jsonObject.getString("description")));
            }
            nAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "whenAsyncIsDone: Retrieving Error");
        }
    }
}
