package com.weiconghong.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String SERVICE_DATA = "SERVICE_DATA";

    private HashMap<String, Source> sourcesHashMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> categoriesHashMap = new HashMap<>();
    private ArrayList<String> namesArrayList = new ArrayList<>();
    private ArrayList<Article> articlesArrayList = new ArrayList<>();

    private Menu menu_main;
    private NewsReceiver newsReceiver;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private List<Fragment> fragments;
    private PageAdapter pageAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);

        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, intentFilter);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, namesArrayList));

        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        new NewsSourceDownloader(this, "").execute();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onResume() {
        newsReceiver = new NewsReceiver();

        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, intentFilter);

        super.onResume();
    }



    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);

        super.onDestroy();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: " + item);
            return true;
        }

        String category = (String) item.getTitle();

        ArrayList<String> tempList = new ArrayList<>((categoriesHashMap.get(category)));

        namesArrayList.clear();
        namesArrayList.addAll(tempList);

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu_main = menu;
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void selectItem(int position) {
        viewPager.setBackground(null);
        getSupportActionBar().setTitle(namesArrayList.get(position));

        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SERVICE);
        Source source = sourcesHashMap.get(namesArrayList.get(position));
        Log.d(TAG, "selectItem: " + source.getID());
        intent.putExtra("sourceID", source.getID());
        if (source != null)
            sendBroadcast(intent);
        drawerLayout.closeDrawer(drawerList);
    }


    public void setSources(ArrayList<Source> sources, ArrayList<String> categories) {
        Source source;
        String category;
        String name;

        sourcesHashMap.clear();
        categoriesHashMap.clear();
        namesArrayList.clear();

        for (int i = 0; i < sources.size(); i++) {
            source = sources.get(i);
            category = source.getCategory();
            name = source.getName();
            namesArrayList.add(name);
            sourcesHashMap.put(name, source);

            if (!categoriesHashMap.containsKey(category))
                categoriesHashMap.put(category, new ArrayList<String>());
            categoriesHashMap.get(category).add(name);
        }

        for (String c : categories)
            menu_main.add(c);

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }



    private void reDoFragments() {
        Log.d(TAG, "reDoFragments: ");

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        int i = 0;
        for (int j = 0; j < articlesArrayList.size(); j++) {
            String count = String.format("%d of %d", ++i, articlesArrayList.size());
            Article article = articlesArrayList.get(j);
            fragments.add(NewsFragment.newInstance(article.getAuthor(), article.getTitle(), article.getDescription(),
                                                      article.getUrl(), article.getUrlToImage(), article.getPublishedAt(), count));
        }

        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }



    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case MainActivity.ACTION_NEWS_STORY:
                    Bundle bundle = intent.getExtras();
                    articlesArrayList = (ArrayList<Article>) bundle.getSerializable(SERVICE_DATA);
                    reDoFragments();
                    break;
                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }

        }
    }



    private class PageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
}
