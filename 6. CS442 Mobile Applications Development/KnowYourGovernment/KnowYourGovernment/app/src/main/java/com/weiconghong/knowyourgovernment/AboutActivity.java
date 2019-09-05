package com.weiconghong.knowyourgovernment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    private TextView project;
    private TextView copyright;
    private TextView version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        project = findViewById(R.id.project);
        copyright = findViewById(R.id.copyright);
        version = findViewById(R.id.version);
    }
}
