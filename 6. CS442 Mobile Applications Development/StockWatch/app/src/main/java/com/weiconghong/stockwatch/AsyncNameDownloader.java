package com.weiconghong.stockwatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncNameDownloader extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;

    private final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String TAG = "AsyncStockSearcher";

    private HashMap<String, String> stockHashMapData = new HashMap<>();
   // private String input;


    public AsyncNameDownloader(MainActivity ma) {
        this.mainActivity = ma;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(s);

        mainActivity.dataFromNameDownloader(stockHashMapData);

    }



    @Override
    protected String doInBackground(String... strings) {
        //input = strings[0];


        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }


    private void parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");

        try {
            JSONArray results = new JSONArray(s);

            for (int i = 0; i < results.length(); i++) {
                JSONObject stockJsonObject = (JSONObject) results.get(i);
                stockHashMapData.put(stockJsonObject.getString("symbol"), stockJsonObject.getString("name"));
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: ", e);
        }
    }
}
