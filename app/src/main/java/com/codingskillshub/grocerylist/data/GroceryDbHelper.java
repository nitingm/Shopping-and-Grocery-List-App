package com.codingskillshub.grocerylist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codingskillshub.grocerylist.data.GroceryContract.GroceryEntry;

public class GroceryDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = GroceryDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "basket.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    public GroceryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_GROCERIES_TABLE =  "CREATE TABLE " + GroceryEntry.TABLE_NAME + " ("
                + GroceryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroceryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + GroceryEntry.COLUMN_ITEM_QTY + " INTEGER NOT NULL DEFAULT 0, "
                + GroceryEntry.COLUMN_ITEM_PRICE + " FLOAT NOT NULL DEFAULT 0,"
                + GroceryEntry.COLUMN_ITEM_DESCRIPTION + " LONGTEXT);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_GROCERIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
