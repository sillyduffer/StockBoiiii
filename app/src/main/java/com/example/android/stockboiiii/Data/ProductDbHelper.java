package com.example.android.stockboiiii.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "products.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_HABITS_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_ITEM_SUMMARY + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
