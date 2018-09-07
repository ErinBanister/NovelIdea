package com.example.android.novelidea.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {
    /**
     * Inner class which  defines the table contents of the products table
     */

    public static final String CONTENT_AUTHORITY = "com.example.android.novelidea";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";


    public static final class ProductsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * MIME type for all pets
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * MIME type for a single pet
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";

        public static final int BOOKS = 100;
        public static final int BOOK_ID = 101;

        //Strings to send to the Inventory app as the columns of the product query
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_GENRE = "Genre";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_QTY = "QTY";
        public static final String COLUMN_SUPPLIER_NAME = "SupplierName";
        public static final String COLUMN_SUPPLIER_PHONE = "SupplierPhone";

        /**
         * Values for the Genre of the Book
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_DRAMA = 1;
        public static final int GENRE_SCIFI = 2;
        public static final int GENRE_HUMOR = 3;
        public static final int GENRE_FANTASY = 4;
        public static final int GENRE_ADVENTURE = 5;
        public static final int GENRE_MYSTERY = 6;
        public static final int GENRE_ROMANCE = 7;
        public static final int GENRE_HORROR = 8;
        public static final int GENRE_NONFICTION = 9;

        /**
         * missing values in sanity check
         */
        public static final int MISSING_NOTHING = 0;
        public static final int MISSING_TITLE = 1;
        public static final int MISSING_AUTHOR = 2;
        public static final int MISSING_PRICE = 3;
        public static final int MISSING_QTY = 4;
        public static final int MISSING_SUPPLIER = 5;
        public static final int MISSING_SUPPLIER_PHONE = 6;


    }


}

