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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsArticleDownloader";
    private final String KEY = "";
    private final String ARTICLE_URL = "https://newsapi.org/v2/top-headlines?";

    private String source;
    private NewsService newsService;
    private ArrayList<Article> articlesArrayList = new ArrayList<>();


    public NewsArticleDownloader(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(s);

        newsService.setArticles(articlesArrayList);
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri.Builder buildUri = Uri.parse(ARTICLE_URL).buildUpon();
        buildUri.appendQueryParameter("sources", source);
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
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsArticleObject = (JSONObject) jsonArray.get(i);
                String author = newsArticleObject.getString("author");
                String title = newsArticleObject.getString("title");
                String description = newsArticleObject.getString("description");
                String url = newsArticleObject.getString("url");
                String urlToImage = newsArticleObject.getString("urlToImage");
                String publishedAt = newsArticleObject.getString("publishedAt");

                articlesArrayList.add(new Article(author, title, description, url, urlToImage, publishedAt));
            }
        }

        catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e);
        }
    }
}
