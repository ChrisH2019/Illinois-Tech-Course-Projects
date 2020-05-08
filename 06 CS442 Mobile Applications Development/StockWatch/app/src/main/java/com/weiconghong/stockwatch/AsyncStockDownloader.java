package com.weiconghong.stockwatch;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncStockDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;

    private final String DATA_URL = "https://api.iextrading.com";
    private static final String TAG = "AsyncStockLoader";


    public AsyncStockDownloader(MainActivity ma) {
        this.mainActivity = ma;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        Stock stock = parseJSON(s);

        Log.d(TAG, "onPostExecute: New Stock Added" + stock);

        mainActivity.addNewStock(stock);
    }


    @Override
    protected String doInBackground(String... strings) {
    String symbol = strings[0];

        // String data_URL = DATA_URL + strings[0] + "/quote?displayPercent=true";
        Uri.Builder buildUri = Uri.parse(DATA_URL).buildUpon();
        buildUri.appendPath("1.0");
        buildUri.appendPath("stock");
        buildUri.appendPath(symbol);
        buildUri.appendPath("quote");
        String urlToUse = buildUri.build().toString();
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

        Log.d(TAG, "doInBackground: " + sb.toString());

        return sb.toString();
    }


    private Stock parseJSON(String s) {
        Log.d(TAG, "parseJSON: " + s);

        try {
            JSONObject jsonObject = new JSONObject(s);

            String symbol = jsonObject.getString("symbol");
            String companyName = jsonObject.getString("companyName");
            double price = jsonObject.getDouble("latestPrice");
            double priceChange = jsonObject.getDouble("change");
            double changePercent = jsonObject.getDouble("changePercent");

            Stock stock = new Stock(symbol, companyName, price, priceChange, changePercent);
            return stock;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
