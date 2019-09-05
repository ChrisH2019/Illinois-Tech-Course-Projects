package com.example.administrator.multinotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";

    private EditText title;
    private EditText description;

    private Note note;
    private String previousTitle;
    private String previousDescription;

    public static SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
    private static int position;
    private Boolean save = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        title =  findViewById(R.id.noteTitle);
        description = findViewById(R.id.noteDescription);

        description.setMovementMethod(new ScrollingMovementMethod());
        description.setTextIsSelectable(true);

        Intent intent = getIntent();

        if (intent.hasExtra("Add New Note")) {
            Log.d(TAG, "onCreate: Add New Note");
            position = intent.getIntExtra("Add New Note", -1);
            note = new Note();
            previousTitle = note.getTitle();
            previousDescription = note.getDescription();
        }

        if (intent.hasExtra("Edit Previous Note")) {
            Log.d(TAG, "onCreate: Edit Previous Note");
            note = (Note) intent.getSerializableExtra("Edit Previous Note");
            position = intent.getIntExtra("Position", -1);
            previousTitle = note.getTitle();
            previousDescription = note.getDescription();
            title.setText(previousTitle);
            description.setText(previousDescription);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        if (save) {
            note = loadFile();
            if (note != null) {
                Log.d(TAG, "onResume: File Loaded");
                title.setText(note.getTitle());
                description.setText(note.getDescription());
            } else {
                Log.d(TAG, "onResume: File Not Loaded");
            }
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        note.setTitle(title.getText().toString());
        note.setDate(NoteActivity.formatter.format(new Date()));
        note.setDescription(description.getText().toString());
        saveFile();
        save = true;
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    private void saveFile() {
        Log.d(TAG, "saveFile: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.single_file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("title").value(note.getTitle());
            writer.name("date").value(note.getDate());
            writer.name("description").value(note.getDescription());
            writer.endObject();
            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private Note loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");
        note = new Note();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.single_file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("title")) {
                    note.setTitle(reader.nextString());
                } else if (name.equals("description")) {
                    note.setDescription(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.save:
                if (title.getText().toString().isEmpty()) {
                    Log.d(TAG, "onOptionsItemSelected: No Title No Saved");
                    Toast.makeText(this,"Untitled Note Not Saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                } else if (title.getText().toString().equals(previousTitle) &&
                        description.getText().toString().equals(previousDescription)) {
                    Log.d(TAG, "onOptionsItemSelected: No Change No Saved");
                    Toast.makeText(this, "No Change No Saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                } else {
                    note.setTitle(title.getText().toString());
                    Log.d(TAG, "onOptionsItemSelected: " + title.getText().toString());
                    note.setDate(NoteActivity.formatter.format(new Date()));
                    Log.d(TAG, "onOptionsItemSelected: " + NoteActivity.formatter.format(new Date()));
                    note.setDescription(description.getText().toString());
                    Log.d(TAG, "onOptionsItemSelected: " + description.getText().toString());
                    Intent data = new Intent();
                    data.putExtra("Note Saved", note);
                    data.putExtra("Position", position);
                    setResult(RESULT_OK, data);
                    Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(this, "Untitled Note Not Saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else if (title.getText().toString().equals(previousTitle)
                && description.getText().toString().equals(previousDescription)) {
            Toast.makeText(this, "No Change No Saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    note.setTitle(title.getText().toString());
                    Log.d(TAG, "onOptionsItemSelected: " + title.getText().toString());
                    note.setDate(NoteActivity.formatter.format(new Date()));
                    Log.d(TAG, "onOptionsItemSelected: " + NoteActivity.formatter.format(new Date()));
                    note.setDescription(description.getText().toString());
                    Log.d(TAG, "onOptionsItemSelected: " + description.getText().toString());
                    Intent data = new Intent();
                    data.putExtra("Note Saved", note);
                    data.putExtra("Position", position);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            builder.setMessage("Your note is not saved!" + "\nSave note '" + title.getText() + "'?");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

