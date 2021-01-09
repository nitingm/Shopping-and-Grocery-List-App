package com.codingskillshub.grocerylist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.codingskillshub.grocerylist.data.GroceryContract.GroceryEntry;

import java.text.DecimalFormat;
/*
    https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter
    check out the above tutorial to understand Populating a ListView with a CursorAdapter
 */


public class GroceryCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link com.codingskillshub.grocerylist.GroceryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public GroceryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView qtyTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_QTY);
        int priceColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_ITEM_PRICE);
        // Read the pet attributes from the Cursor for the current pet
        String itemName = cursor.getString(nameColumnIndex);
        String itemQty = cursor.getString(qtyColumnIndex);
        String itemPrice = String.format("%.2f",cursor.getFloat(priceColumnIndex));

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        qtyTextView.setText(itemQty);
    }
}
