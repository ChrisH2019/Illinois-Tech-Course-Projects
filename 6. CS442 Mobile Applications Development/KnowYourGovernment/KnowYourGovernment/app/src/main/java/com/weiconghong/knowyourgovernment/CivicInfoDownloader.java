package com.weiconghong.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "CivicInfoDownloader";

    private static final String CIVIC_INFO_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyANVInv0g6RMGgUSfBKjI7OvdTi6lSr6rk&address=";

    private final String DEFAULT = "No Data Provided";

    private ArrayList<Official> officialResults = new ArrayList<Official>();
    private Object[] results = new Object[2];
    private String location;

    private MainActivity mainActivity;

    public CivicInfoDownloader(MainActivity ma) {
        this.mainActivity = ma;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        if (s == null) {
            Toast.makeText(mainActivity, "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
            return;

        } else if (s.isEmpty()) {
            Toast.makeText(mainActivity, "No data is available for the specified location", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
            return;

        } else {
            parseJSON(s);
            results[0] = location;
            results[1] = officialResults;
            mainActivity.setOfficialList(results);
            return;
        }
    }
    @Override
    protected String doInBackground(String... strings) {
        String dataUrl = CIVIC_INFO_URL + strings[0];
        Uri dataUri = Uri.parse(dataUrl);
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
        } catch (Exception e) {
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

            JSONObject normalizedInputObject = jsonObject.getJSONObject("normalizedInput");
            String city = normalizedInputObject.getString("city");
            String state = normalizedInputObject.getString("state");
            String zip = normalizedInputObject.getString("zip");
            location = city + ", " + state + " " + zip;
            Log.d(TAG, "parseJSON: " + location);


            JSONArray officesArray = jsonObject.getJSONArray("offices");
            HashMap<String, String> officeIndexHashMap = new HashMap<>();
            for (int i = 0; i < officesArray.length(); i++) {
                JSONObject officeObject = (JSONObject) officesArray.get(i);
                String officialTitle = officeObject.getString("name");
                JSONArray officialIndices = officeObject.getJSONArray("officialIndices");

                for (int j = 0; j < officialIndices.length(); j++)
                    officeIndexHashMap.put(officialIndices.getString(j), officialTitle);
            }


            JSONArray officialsArray= jsonObject.getJSONArray("officials");
            for (int i = 0; i < officialsArray.length(); i++) {
                JSONObject officialObject = (JSONObject) officialsArray.get(i);

                String office = officeIndexHashMap.get(String.format("%d", i));
                String name = officialObject.getString("name");

                String address = "";
                if (officialObject.has("address")) {
                    JSONObject addressObject = (JSONObject) officialObject.getJSONArray("address").get(0);

                    if (addressObject.has("line1"))
                        address += addressObject.getString("line1") + "\n";
                    else if (addressObject.has("line2"))
                        address += ", " + addressObject.getString("line2") + "\n";
                    address += addressObject.get("city") + ", " + addressObject.get("state") + " " + addressObject.get("zip");

                } else {
                    address = DEFAULT;
                }

                String party;
                if (officialObject.has("party"))
                    party = officialObject.getString("party");
                else
                    party = "Unkown";

                String phone;
                if (officialObject.has("phones"))
                    phone = officialObject.getJSONArray("phones").get(0).toString();
                else
                    phone = DEFAULT;

                String url;
                if (officialObject.has("urls"))
                    url = officialObject.getJSONArray("urls").get(0).toString();
                else
                    url = DEFAULT;

                String email;
                if (officialObject.has("emails"))
                    email = officialObject.getJSONArray("emails").get(0).toString();
                else
                    email = DEFAULT;

                String photoUrl;
                if (officialObject.has("photoUrl"))
                    photoUrl = officialObject.getString("photoUrl");
                else
                    photoUrl = DEFAULT;

                String googlePlus = DEFAULT;
                String facebook = DEFAULT;
                String twitter = DEFAULT;
                String youTube = DEFAULT;
                if (officialObject.has("channels")) {
                    JSONArray channelsArray = officialObject.getJSONArray("channels");
                    for (int j = 0; j < channelsArray.length(); j++) {
                        JSONObject channelObject = (JSONObject) channelsArray.get(j);
                        if (channelObject.getString("type").equals("GooglePlus"))
                            googlePlus = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("Facebook"))
                            facebook = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("Twitter"))
                            twitter = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("YouTube"))
                            youTube = channelObject.getString("id");
                    }
                }

                Log.d(TAG, "parseJSON: " + office + name + party + address + phone + email + url + photoUrl);
                Log.d(TAG, "parseJSON: " + googlePlus + facebook + twitter + youTube);

                officialResults.add(new Official(office, name, party, address, phone, email, url, photoUrl, googlePlus, facebook, twitter, youTube));
            }
        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
