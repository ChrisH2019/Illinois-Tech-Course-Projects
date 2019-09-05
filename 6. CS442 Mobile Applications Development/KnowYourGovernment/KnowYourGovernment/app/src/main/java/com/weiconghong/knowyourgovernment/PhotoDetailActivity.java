package com.weiconghong.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";

    private Official official;
    private TextView location;

    private TextView office;
    private TextView name;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        location = findViewById(R.id.location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        photo = findViewById(R.id.photo);

        Intent intent = getIntent();
        if (intent.hasExtra("location"))
            location.setText(intent.getStringExtra("location"));

        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            office.setText(official.getOffice());
            name.setText(official.getName());
            Log.d(TAG, "onCreate: " + office + " " + name);

            if (official.getParty().equals("Democratic"))
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            else if (official.getParty().equals("Republican"))
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            else
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);

            final String photoUrl = official.getPhotoUrl();
            if (photoUrl != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        final String changedUrl = photoUrl.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(photo);
                    }
                }).build();
                picasso.load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            } else {
                Picasso.get().load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(photo);
            }
        }
    }
}

