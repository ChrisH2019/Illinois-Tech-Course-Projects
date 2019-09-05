package com.example.administrator.multinotes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncActivity extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncActivity";
    private MainActivity mainActivity;
    StringBuilder stringBuilder;

    public AsyncActivity(MainActivity ma) {
        this.mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(strings[0]);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, strings[1]));

            stringBuilder = new StringBuilder();
            String node;
            while ((node = bufferedReader.readLine()) != null) {
                stringBuilder.append(node);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "doInBackground: No Notes File Present");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        Log.d(TAG, "onPostExecute: ");
        super.onPostExecute(string);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(string);
        } catch (Exception e) {
            Log.d(TAG, "onPostExecute: Async Error");
        }

        mainActivity.whenAsyncIsDone(jsonArray);
        Log.d(TAG, "onPostExecute: AsyncTask terminating successfully");
    }
}
