package com.weiconghong.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String SERVICE_DATA = "SERVICE_DATA";

    private boolean running = true;
    private ArrayList<Article> storyArrayList = new ArrayList<>();
    private ServiceReceiver serviceReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter(ACTION_MSG_TO_SERVICE);

        serviceReceiver = new ServiceReceiver();
        registerReceiver(serviceReceiver, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (storyArrayList.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_NEWS_STORY);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(SERVICE_DATA, storyArrayList);
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                        storyArrayList.clear();
                    }
                }
                Log.d(TAG, "run: Ending loop");
            }
        }).start();

        return Service.START_STICKY;
    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }



    public void setArticles(ArrayList<Article> articles) {
        storyArrayList.clear();
        storyArrayList.addAll(articles);
    }



    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case ACTION_MSG_TO_SERVICE:
                    String source = "";
                    if (intent.hasExtra("sourceID"))
                        source = intent.getStringExtra("sourceID");
                    Log.d(TAG, "onReceive: " + source);
                    new NewsArticleDownloader(NewsService.this, source).execute();
                    break;
                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }
        }
    }
}
