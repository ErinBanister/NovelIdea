package com.example.android.novelidea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.novelidea.data.ProductContract.ProductsEntry;

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructor for ProductCursorAdapter
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * View inflater
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        /**textviews to update with each cursor*/
        TextView authorTextView = view.findViewById(R.id.author);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView qtyTextView = view.findViewById(R.id.qty);
        final Button saleButton = view.findViewById(R.id.saleButton);

        /**cursor columns*/
        int authorCol = cursor.getColumnIndex(ProductsEntry.COLUMN_AUTHOR);
        int titleCol = cursor.getColumnIndex(ProductsEntry.COLUMN_TITLE);
        int genreCol = cursor.getColumnIndex(ProductsEntry.COLUMN_GENRE);
        int priceCol = cursor.getColumnIndex(ProductsEntry.COLUMN_PRICE);
        int qtyCol = cursor.getColumnIndex(ProductsEntry.COLUMN_QTY);
        int supplierCol = cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhCol = cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_PHONE);

        /**get cursor values for each col*/
        final String author = cursor.getString(authorCol);
        final String title = cursor.getString(titleCol);
        final int genre = cursor.getInt(genreCol);
        final int price = cursor.getInt(priceCol);
        final int qty = cursor.getInt(qtyCol);
        final String supplier = cursor.getString(supplierCol);
        final String supplierPhone = cursor.getString(supplierPhCol);

        /**Update current view with cursor values*/
        authorTextView.setText(author);
        titleTextView.setText(title);
        priceTextView.setText(Integer.toString(price));
        qtyTextView.setText(Integer.toString(qty));

        /**set the onClickListener for the sale button**/
        int idCOL = cursor.getColumnIndex(ProductsEntry._ID);
        final int id = cursor.getInt(idCOL);
        saleButton.setTag(id);
        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri.Builder builder = ProductsEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(id));
                Uri updateUri = builder.build();

                if (updateUri != null) {
                    /**ensure qty doesn't dip below 0*/
                    int newQty = qty - 1;
                    if (newQty < 0) {
                        /**cannot have a qty value less than 0*/
                        Toast.makeText(context, context.getString(R.string.qtyError), Toast.LENGTH_SHORT).show();

                    } else {

                        /**Set ContentValues using row fields*/
                        ContentValues values = new ContentValues();
                        values.put(ProductsEntry.COLUMN_AUTHOR, author);
                        values.put(ProductsEntry.COLUMN_TITLE, title);
                        values.put(ProductsEntry.COLUMN_PRICE, price);
                        values.put(ProductsEntry.COLUMN_QTY, newQty);
                        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, supplier);
                        values.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
                        values.put(ProductsEntry.COLUMN_GENRE, genre);

                        /**Update fields based on new QTY*/
                        int bookRows = context.getContentResolver().update(updateUri, values, null, null);
                        String message = context.getString(R.string.updateQty) + title + " ";
                        if (!(bookRows > 0)) {
                            message = message + context.getString(R.string.unsuccessful);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}