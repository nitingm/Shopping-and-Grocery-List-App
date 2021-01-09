package com.codingskillshub.grocerylist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.CursorLoader;
import android.database.Cursor;
import android.app.LoaderManager;

import com.codingskillshub.grocerylist.data.GroceryContract.GroceryEntry;
import com.codingskillshub.grocerylist.myimplementation.DecimalDigitsInputFilter;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Intent intent;
    /** Identifier for the pet data loader */
    private static final int EXISTING_GROCERY_LOADER = 0;
    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentGroceryUri;
    //this boolean variable is to check if the pet is changed (if any field is touched)
    private boolean mGroceryHasChanged = false;

    //we set up this onTouchListener and hook it up with the fields to report(set the @mPetHasChanged to true)
    //when they are touched
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGroceryHasChanged = true;
            return false;
        }
    };
    /** EditText field to enter the grocery item name */
    private EditText mNameEditText;

    /** EditText field to enter the grocery quantity*/
    private EditText mQuantityEditText;

    /** EditText field to enter the grocery price */
    private EditText mPriceEditText;

    /** EditText field to enter the grocery description */
    private EditText mGroceryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mGroceryDescription = (EditText) findViewById(R.id.edit_item_description);

        //here we hook up the fields with the onTouchListener
        mNameEditText.setOnTouchListener(onTouchListener);
        mQuantityEditText.setOnTouchListener(onTouchListener);
        mGroceryDescription.setOnTouchListener(onTouchListener);
        mPriceEditText.setOnTouchListener(onTouchListener);

        mPriceEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(8,2)});
        //create an intent to receive data sent from the ListView items
        intent = getIntent();
        //get the uri sent from the CatalogActivity
        mCurrentGroceryUri = intent.getData();
        //here we check if the EditorActivity is for inserting a new pet or updating one
        //if the intent is null then no intent or data are sent and we are inserting a new pet\
        //if not then we are getting a uri to a specific pet to update its info
        if (mCurrentGroceryUri == null){

            setTitle(R.string.editor_activity_title_new_grocery);

            /*
             * This function tell android that it should redraw the menu.
             * By default, once the menu is created, it won't be redrawn every frame
             * (since that would be useless to redraw the same menu over and over again).
             * You should call this function when you changed something in the option menu
             *(added an element, deleted an element or changed a text).
             *  This way android will know that it's time te redraw the menu and your change will appear.
             */
            invalidateOptionsMenu();
        }else {

            setTitle(R.string.editor_activity_title_edit_grocery);
            getLoaderManager().initLoader(EXISTING_GROCERY_LOADER, null, this);
        }
    }

    private void saveGrocery() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String descriptionString = mGroceryDescription.getText().toString().trim();
        int quantity=0;
        float price = 0;
        if (!TextUtils.isEmpty(quantityString)){
            quantity = Integer.parseInt(quantityString);
        }
        if(!TextUtils.isEmpty(priceString)) {
            price = Float.parseFloat(priceString);
        }
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(GroceryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(GroceryEntry.COLUMN_ITEM_QTY, quantity);
        values.put(GroceryEntry.COLUMN_ITEM_PRICE, price);
        values.put(GroceryEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);

        if (mCurrentGroceryUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(descriptionString)) {
            return;
        }

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentGroceryUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(GroceryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentGroceryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentGroceryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveGrocery();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return  true;
            // Respond to a click on the "Up" arrow button in the app bar
            //https://developer.android.com/guide/navigation?utm_source=udacity&utm_medium=course&utm_campaign=android_basics#NavigateUp
            //
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mGroceryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mGroceryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentGroceryUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentGroceryUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {GroceryEntry._ID, GroceryEntry.COLUMN_ITEM_NAME, GroceryEntry.COLUMN_ITEM_QTY
                , GroceryEntry.COLUMN_ITEM_PRICE, GroceryEntry.COLUMN_ITEM_DESCRIPTION};

        //here we must pass the current uri (the one we pass from the intent)
        return new CursorLoader(this, mCurrentGroceryUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_QTY);
            int priceColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_PRICE);
            int descriptionColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_DESCRIPTION);
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            mNameEditText.setText(name);
            mQuantityEditText.setText(String.valueOf(quantity));
            mPriceEditText.setText(String.format("%.2f",price));
            mGroceryDescription.setText(description);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mGroceryDescription.setText("");
        mPriceEditText.setText("");
    }
}