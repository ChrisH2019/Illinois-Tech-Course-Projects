package com.weiconghong.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StockAppDB";

    private static final String TABLE_NAME = "StockWatchTable";

    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";


    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + SYMBOL + " TEXT not null unique, "
                    + COMPANY + " TEXT not null)";

    private SQLiteDatabase database;


    public  DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: DONE");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    public ArrayList<String[]> loadStocks() {

        Log.d(TAG, "loadStock: START");
        ArrayList<String[]> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANY},
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[] {symbol, company});
                cursor.moveToNext();
            }

            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");
        return stocks;
    }


    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: " + stock.getSymbol());

        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompanyName());

        database.insert(TABLE_NAME, null, values);

        Log.d(TAG, "addStock: Add Complete");
    }


    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock" + symbol);

        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[] { symbol });

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-6s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }


    public void shutDown() {
        database.close();
    }
}
