package com.example.android.novelidea.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.novelidea.R;
import com.example.android.novelidea.data.ProductContract.ProductsEntry;

public class ProductProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * Content Uri for the Book table & single book
     */

    private static final UriMatcher mUri = new UriMatcher(UriMatcher.NO_MATCH);

    /**Static initializer runs every time this class is referenced throughout the app*/
    static {
        /**Acceptable Content URI patterns include ALL items from DB and SINGLE item from DB*/
        mUri.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, ProductsEntry.BOOKS);
        mUri.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", ProductsEntry.BOOK_ID);
    }

    private ProductDbHelper mDbHelper;
    private String returnValue;
    private int missingValue = 0;

    @Override
    public boolean onCreate() {
        /**Define new DB Helper instance*/
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sql = mDbHelper.getReadableDatabase();
        Cursor cursor;

        /** ensure the Uri matches those defined above*/
        int uriMatch = mUri.match(uri);
        switch (uriMatch) {
            case ProductsEntry.BOOKS:
                cursor = sql.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ProductsEntry.BOOK_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                cursor = sql.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.unknownUri) + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUri.match(uri);
        switch (match) {
            case ProductsEntry.BOOKS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case ProductsEntry.BOOK_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        /**Only insert if values are valid*/
        missingValue = 0;
        String returnValue = checkValues(uri, values);

        if (missingValue != 0) {
            /**Missing a value, do not proceed*/
            Toast.makeText(getContext(), returnValue, Toast.LENGTH_SHORT).show();
            return null;
        } else if (values.size() == 0) {
            /**do not insert, no values present*/
            return null;
        } else {
            /**Data is valid, proceed with insert function */
            SQLiteDatabase sql = mDbHelper.getWritableDatabase();
            long rowsInserted = sql.insert(ProductsEntry.TABLE_NAME, null, values);

            /**if ID = -1, insertion failed*/
            if (rowsInserted == -1) {
                Log.e(LOG_TAG, getContext().getString(R.string.insertionFailed) + uri);
                return null;
            }

            /**notify listeners that data has changed*/
            getContext().getContentResolver().notifyChange(uri, null);
            /**return full appended Uri*/
            return ContentUris.withAppendedId(uri, rowsInserted);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int matchRow = mUri.match(uri);
        if (matchRow == ProductsEntry.BOOKS) {
            Uri insertURI = insertBook(uri, values);
            return insertURI;
        } else {
            throw new IllegalArgumentException(getContext().getString(R.string.unsupported) + uri);
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        SQLiteDatabase sql = mDbHelper.getWritableDatabase();
        int deletedRows;
        final int matchUri = mUri.match(uri);
        switch (matchUri) {
            case ProductsEntry.BOOKS:
                deletedRows = sql.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ProductsEntry.BOOK_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = sql.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unsupportedDelete) + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return deletedRows;
        } else {
            return 0;
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        final int matchUri = mUri.match(uri);
        switch (matchUri) {
            case ProductsEntry.BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case ProductsEntry.BOOK_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported Update" + uri.toString());
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String check = checkValues(uri, values);
        if (missingValue == 0) {
            /**values are good, continue with update*/
            if (values.size() == 0) {
                return 0;
            } else {
                SQLiteDatabase sql = mDbHelper.getWritableDatabase();
                int rowsUpdated = sql.update(ProductsEntry.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return rowsUpdated;
                } else {
                    return 0;
                }

            }
        } else {
            return 0;
        }
    }

    private String checkValues(Uri uri, ContentValues values) {
        String title;
        String author;
        int price;
        int qty;
        String supplierName;
        String supplierPhone;
        returnValue = "";
        missingValue = 0;


        if (values.containsKey(ProductsEntry.COLUMN_TITLE)) {
            title = values.getAsString(ProductsEntry.COLUMN_TITLE);
            if (testEmptyString(title)) {
                /**if the title is empty*/
                missingValue = ProductsEntry.MISSING_TITLE;
                returnValue = getContext().getString(R.string.missingNothing);
            }
        }

        if (values.containsKey(ProductsEntry.COLUMN_AUTHOR)) {
            author = values.getAsString(ProductsEntry.COLUMN_AUTHOR);
            if (testEmptyString(author)) {
                /**if the author is empty*/
                missingValue = ProductsEntry.MISSING_AUTHOR;
                returnValue = getContext().getString(R.string.missingAuthor);
            }
        }
        if (values.containsKey(ProductsEntry.COLUMN_PRICE)) {
            String rawPrice = values.getAsString(ProductsEntry.COLUMN_PRICE);
            if (!testEmptyString(rawPrice)) {
                price = Integer.parseInt(rawPrice);
                if (!(price >= 0)) {
                    /**price is less than 0*/
                    missingValue = ProductsEntry.MISSING_PRICE;
                    returnValue = getContext().getString(R.string.missingPrice);
                }
            }
        }
        if (values.containsKey(ProductsEntry.COLUMN_QTY)) {
            String rawQty = values.getAsString(ProductsEntry.COLUMN_QTY);
            if (!testEmptyString(rawQty)) {
                qty = Integer.parseInt(rawQty); //'' values.getAsInteger(ProductsEntry.COLUMN_QTY);
                if (!(qty >= 0)) {
                    /**qty is less than 0*/
                    missingValue = ProductsEntry.MISSING_QTY;
                    returnValue = getContext().getString(R.string.missingQty);
                }
            }
        }

        if (values.containsKey(ProductsEntry.COLUMN_SUPPLIER_NAME)) {
            supplierName = values.getAsString(ProductsEntry.COLUMN_SUPPLIER_NAME);
            if (testEmptyString(supplierName)) {
                /**Supplier Name is empty*/
                missingValue = ProductsEntry.MISSING_SUPPLIER;
                returnValue = getContext().getString(R.string.missingSupplier);
            }
        }

        if (values.containsKey(ProductsEntry.COLUMN_SUPPLIER_PHONE)) {
            supplierPhone = values.getAsString(ProductsEntry.COLUMN_SUPPLIER_PHONE);
            if (testEmptyString(supplierPhone)) {
                /**Supplier Phone is empty */
                missingValue = ProductsEntry.MISSING_SUPPLIER_PHONE;
                returnValue = getContext().getString(R.string.missingSupplierPhone);
            }
        }
        return returnValue;
    }

    public boolean testEmptyString(String thisValue) {
        String nullStringValue = null;

        if (thisValue == null | "".equals(thisValue) | thisValue.equals(nullStringValue) | thisValue.equals(" ")) {
            return true;
        } else {
            return false;
        }
    }
}


