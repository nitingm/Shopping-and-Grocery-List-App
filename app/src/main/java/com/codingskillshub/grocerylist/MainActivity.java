package com.codingskillshub.grocerylist;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.codingskillshub.grocerylist.data.GroceryContract.GroceryEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.loader.app.LoaderManager;
import android.content.CursorLoader;
//import androidx.loader.content.CursorLoader;

import android.app.LoaderManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int GROCERY_LOADER = 0;
    ListView groceryListView;
    GroceryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        groceryListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        groceryListView.setEmptyView(emptyView);
        mCursorAdapter = new GroceryCursorAdapter(this, null);
        // Attach the adapter to the ListView.
        groceryListView.setAdapter(mCursorAdapter);

        //setup item click listener
        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //prepare an intent to access the editor activity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //extracting the uri of each pet(row) to send with the intent
                Uri uri = ContentUris.withAppendedId(GroceryEntry.CONTENT_URI, id);
                //this method is used to send a uri with the intent
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //kick off the loader
        getLoaderManager().initLoader(GROCERY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(GroceryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(GroceryEntry.COLUMN_ITEM_NAME, "Tomatoes");
        values.put(GroceryEntry.COLUMN_ITEM_QTY, 1);
        values.put(GroceryEntry.COLUMN_ITEM_PRICE, 50.00);
        values.put(GroceryEntry.COLUMN_ITEM_DESCRIPTION, "Fresh and natural red tomatoes form local farm.");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(GroceryEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            /*
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;

             */
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                GroceryEntry._ID,
                GroceryEntry.COLUMN_ITEM_NAME,
                GroceryEntry.COLUMN_ITEM_QTY,
                GroceryEntry.COLUMN_ITEM_PRICE};

        return new CursorLoader(this,
                GroceryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        TextView mTotalSum = (TextView) findViewById(R.id.total_sum_view);
        float totalSum = 0;
        int priceColumnIndex = data.getColumnIndex(GroceryEntry.COLUMN_ITEM_PRICE);
        int qtyColumnIndex = data.getColumnIndex(GroceryEntry.COLUMN_ITEM_QTY);
        if(data.moveToFirst())
            totalSum = data.getFloat(priceColumnIndex) * data.getInt(qtyColumnIndex);;
        while(data.moveToNext()) {
            totalSum += data.getFloat(priceColumnIndex) * data.getInt(qtyColumnIndex);
        }
        String totalSumString = String.format("%.2f",totalSum);
        mTotalSum.setText(totalSumString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}