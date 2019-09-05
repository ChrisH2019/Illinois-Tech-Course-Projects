package com.weiconghong.newsgateway;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsSourceDownloader";
    private final String KEY = "";
    private final String SOURCE_URL = "https://newsapi.org/v2/sources?country=us";

    private String category;
    private ArrayList<String> categoriesArrayList = new ArrayList<>();
    private ArrayList<Source> sourcesArrayList = new ArrayList<>();

    private MainActivity mainActivity;



    public NewsSourceDownloader(MainActivity mainActivity, String category) {
        this.mainActivity = mainActivity;

        if (category.equals("all") || category.equals(""))
            this.category = "";
        else
            this.category = category;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(s);
        Collections.sort(categoriesArrayList);

        try {
            mainActivity.setSources(sourcesArrayList, categoriesArrayList);
        }

        catch (Exception e) {
            Log.d(TAG, "onPostExecute: " + e);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri.Builder buildUri = Uri.parse(SOURCE_URL).buildUpon();
        buildUri.appendQueryParameter("category", category);
        buildUri.appendQueryParameter("apiKey", KEY);
        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }

        catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        Log.d(TAG, "doInBackground: " + sb.toString());
        return sb.toString();
    }


    private void parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsSourceObject = (JSONObject) jsonArray.get(i);
                String id = newsSourceObject.getString("id");
                String name = newsSourceObject.getString("name");
                String url = newsSourceObject.getString("url");
                String category = newsSourceObject.getString("category");

                if (!categoriesArrayList.contains(category))
                    categoriesArrayList.add(category);

                sourcesArrayList.add(new Source(id, name, url, category));
            }
        }

        catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e);
        }
    }
}
