package com.weiconghong.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private final String DEFAULT = "No Data Provided";

    private Official official;
    private TextView location;

    private TextView office;
    private TextView name;
    private TextView party;

    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView url;

    private ImageView photo;
    private ImageView googlePlus;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youTube;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        location = findViewById(R.id.location);
        office = findViewById(R.id.office);
        name  = findViewById(R.id.name);
        party = findViewById(R.id.party);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        url = findViewById(R.id.url);

        photo = findViewById(R.id.photo);
        googlePlus = findViewById(R.id.googlePlus);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youTube = findViewById(R.id.youTube);


        Intent intent = getIntent();

        if (intent.hasExtra("location")) {
            location.setText(intent.getStringExtra("location"));
        }

        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");

            office.setText(official.getOffice());
            name.setText(official.getName());

            party.setText("(" + official.getParty() + ")");
            if (official.getParty().equals("Democratic"))
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            else if (official.getParty().equals("Republican"))
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            else {
                if (official.getParty().equals("Unknown"))
                    party.setVisibility(View.INVISIBLE);

                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

            address.setText(official.getAddress());
            Linkify.addLinks(address, Linkify.MAP_ADDRESSES);

            phone.setText(official.getPhone());
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);

            email.setText(official.getEmail());
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);

            url.setText(official.getUrl());
            Linkify.addLinks(url, Linkify.WEB_URLS);


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

            if (official.getGooglePlus().equals(DEFAULT))
                googlePlus.setVisibility(View.INVISIBLE);
            if (official.getFacebook().equals(DEFAULT))
                facebook.setVisibility(View.INVISIBLE);
            if (official.getTwitter().equals(DEFAULT))
                twitter.setVisibility(View.INVISIBLE);
            if (official.getYouTube().equals(DEFAULT))
                youTube.setVisibility(View.INVISIBLE);
        }
    }



    public void openPhotoActivity(View v) {
        Log.d(TAG, "openPhotoActivity: ");

        if (official.getPhotoUrl().equals(DEFAULT))
            return;

        Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
        intent.putExtra("address", location.getText().toString());
        intent.putExtra("official", official);
        startActivity(intent);
    }



    public void googlePlusClicked(View v) {
        String name = official.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }



    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }



    public void youTubeClicked(View v) {
        String name = official.getYouTube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
