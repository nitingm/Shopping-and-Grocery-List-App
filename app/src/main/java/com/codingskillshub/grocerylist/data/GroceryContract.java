package com.codingskillshub.grocerylist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//declare the class final so that it cannot be extended
public final class GroceryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private GroceryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.codingskillshub.grocerylist";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_GROCERIES = "groceries";

    /**
     * Inner class that defines constant values for the grocery items database table.
     * Each entry in the table represents a single pet.
     */
    public static final class GroceryEntry implements BaseColumns {
        /** The content URI to access the items data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GROCERIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of groceries.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROCERIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROCERIES;


        /** Name of database table for pets */
        public final static String TABLE_NAME = "groceries";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME ="item_name";

        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_QTY = "quantity";
        /**
         * Gender of the pet.
         *
         * The only possible values are {@link \GENDER_UNKNOWN}, {@link \GENDER_MALE},
         * or {@link \GENDER_FEMALE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_PRICE = "price";
        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_DESCRIPTION = "description";

    }
}
