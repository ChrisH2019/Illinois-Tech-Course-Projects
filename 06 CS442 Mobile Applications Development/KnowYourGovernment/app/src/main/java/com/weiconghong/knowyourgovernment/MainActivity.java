package com.weiconghong.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;
    private List<Official> officialList = new ArrayList<>();

    private TextView location;
    private Locator locator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        recyclerView = findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        location = findViewById(R.id.location);

        if (!isOnline()) {
            location.setText("No Data For Location");
            noNetworkConnection();
        } else {
            locator = new Locator(MainActivity.this);
        }
    }


    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }



    private void noNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot Be accessed/loaded \nwithout an internet connection.");

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 9) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } else {
                        Toast.makeText(this, "Address cannot be acquired from provided latitude/longitude", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }



    public void doLocationWork(double latitude, double longitude) {

        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);

        List<Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");

                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String zip = addresses.get(0).getPostalCode();
                doCivicInfoDownloader(zip);
                //new CivicInfoDownloader(MainActivity.this).execute(zip);

            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());
                Toast.makeText(this, "Address cannot be acquired from provided latitude/longitude", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void doCivicInfoDownloader(String zip) {
        CivicInfoDownloader civicInfoDownloader = new CivicInfoDownloader(this);
        civicInfoDownloader.execute(zip);
    }



    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }


    public void setOfficialList(Object[] results) {

        if (results == null) {
            location.setText("No Data For Location");
            officialList.clear();

        } else {
            location.setText(results[0].toString());

            officialList.clear();
            ArrayList<Official> officialArrayList = (ArrayList<Official>) results[1];
            for (Official official : officialArrayList)
                officialList.add(official);
        }

        officialAdapter.notifyDataSetChanged();
        return;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:

                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;

            case R.id.location:

                if (!isOnline()) {
                    noNetworkConnection();
                    return true;
                }

                final EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setGravity(Gravity.CENTER_HORIZONTAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(editText);
                builder.setTitle("Enter a City, State or a Zip Code");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCivicInfoDownloader(editText.getText().toString());
                        //new CivicInfoDownloader(MainActivity.this).execute(editText.getText().toString());
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Official official = officialList.get(position);

        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("location", location.getText().toString());
        intent.putExtra("official", official);
        startActivity(intent);
    }



    @Override
    public boolean onLongClick(View v) {
        onClick(v);
        return false;
    }
}
