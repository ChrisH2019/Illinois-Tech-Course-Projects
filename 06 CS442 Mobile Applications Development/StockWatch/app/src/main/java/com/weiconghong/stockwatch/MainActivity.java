package com.weiconghong.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{


    private static final String MARKET_WATCH_URL = "http://www.marketwatch.com/investing/stock/";
    private static final String TAG = "MainActivity";

    private List<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private SwipeRefreshLayout swiper;

    private DatabaseHandler databaseHandler;

    public HashMap<String, String> stockHashMapData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        stockAdapter = new StockAdapter(stockList, this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStock();
            }
        });

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.dumpDbToLog();

        if (isOnline()) {
            ArrayList<String[]> sList = databaseHandler.loadStocks();
            stockList.clear();
            for (String[] symbol : sList) {
                new AsyncStockDownloader(this).execute(symbol[0]);
            }
            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();
        } else {
            networkAlert();
        }

        new AsyncNameDownloader(MainActivity.this).execute();

    }


    private void refreshStock() {
        if (!isOnline()) {
            networkAlert();
            swiper.setRefreshing(false);
        }

        else {
            ArrayList<String[]> sList = databaseHandler.loadStocks();
            stockList.clear();

            for (String[] symbol: sList) {
                new AsyncStockDownloader(this).execute(symbol[0]);
            }

            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();
            swiper.setRefreshing(false);
        }
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

    private void networkAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks Cannot Be Added Without A Network Connection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }


    public void addNewStock(Stock stock) {
        Log.d(TAG, "addNewStock: " + stock);

        if (duplicateStock(stock.getSymbol())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.duplicate);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock Symbol " + stock.getSymbol() + " is already displayed");

            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        } else {
            stockList.add(stock);
            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();
            databaseHandler.addStock(stock);
        }
    }


    private boolean duplicateStock(String symbol) {
        for (Stock stock : stockList) {
            if (stock.getSymbol().equals(symbol)) {
                return true;
            }
        }

        return false;
    }


    public void searchStock(String symbol) {
        new AsyncStockDownloader(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, symbol);
    }



    public void dataFromNameDownloader(HashMap hm) {

        stockHashMapData = hm;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:

                if (!isOnline()) {
                    networkAlert();
                    return true;
                }

                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText editText;
                    editText = new EditText(this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    editText.setGravity(Gravity.CENTER_HORIZONTAL);
                    editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                    builder.setView(editText);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = editText.getText().toString();

                            if (symbol.isEmpty() || stockHashMapData.isEmpty()) {
                                emptyStockHashMapDataDialog(symbol);
                            }

                            if (stockHashMapData.size() == 1 || stockHashMapData.containsKey(symbol)) {
                                autoSelection(symbol);
                            } else if (stockHashMapData.size() > 1) {
                                makeSelection(symbol);
                            }

                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    builder.setTitle("Stock Selection");
                    builder.setMessage("Please enter a Stock Symbol: ");

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    public void autoSelection(String s) {
        if (duplicateStock(s)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.duplicate);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock Symbol " + s + " is already displayed");

            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        }

        searchStock(s);

    }

    public void makeSelection(String s) {
        final String[] stockArray = new String[stockHashMapData.size()];
        int i = 0;
        for (String symbol : stockHashMapData.keySet()) {
            if (symbol.startsWith(s.substring(0, 1)))
                stockArray[i++] = String.format("%s - %s", symbol, stockHashMapData.get(symbol));
        }

        if (stockArray.length == 0) {
            emptyStockHashMapDataDialog(s);
        }


        Log.d(TAG, "makeSelection: " +stockArray[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        builder.setItems(stockArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String symbol = stockArray[which].substring(0, stockArray[which].indexOf(" - "));

                autoSelection(symbol);

            }
        });

        builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void emptyStockHashMapDataDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: " + s);
        builder.setMessage("Data for stock symbol");

        AlertDialog dialog = builder.create();
        dialog.show();

        return;
    }




    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Stock stock = stockList.get(position);

        String market_watch_URL = MARKET_WATCH_URL + stock.getSymbol();
        Uri market_watch_Uri = Uri.parse(market_watch_URL);
        String urlToUse = market_watch_Uri.toString();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlToUse));
        startActivity(intent);

    }


    @Override
    public boolean onLongClick(final View v) {
        final int position = recyclerView.getChildLayoutPosition(v);
        final Stock stock = stockList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + stock.getSymbol() + "?");
        builder.setIcon(R.drawable.delete);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(v.getContext(), "Stock Symbol " + stock.getSymbol() +
                        " deleted", Toast.LENGTH_SHORT).show();
                databaseHandler.deleteStock(stock.getSymbol());
                stockList.remove(position);
                stockAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }
}
