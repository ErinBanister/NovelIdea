package com.example.android.novelidea;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.novelidea.data.ProductContract.ProductsEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Create action button to add books from the main screen*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BookEditorActivity.class);
                startActivity(i);
            }
        });

        /**set View for when there's no data in the book list */
        ListView bookList = findViewById(R.id.bookListView);
        View emptyView = findViewById(R.id.emptyView);
        bookList.setEmptyView(emptyView);

        /**set the adapter for the book list*/
        mCursorAdapter = new ProductCursorAdapter(this, null);
        bookList.setAdapter(mCursorAdapter);

        /**set the onClickListeners for the list*/
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /**intent to open BookEditorActivity*/
                Intent i = new Intent(MainActivity.this, BookEditorActivity.class);

                /**send current book with the id appended to the end for easy lookup*/
                Uri currentBook = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);

                /**send only the current data to the intent to open the BookEditorActivity*/
                i.setData(currentBook);
                startActivity(i);

            }
        });

        /**setup the loader so the cursor can be managed through it*/
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    /**
     * add dummy data into the database
     */
    private void insertDummyData() {

        //starts making the value-pair connections
        ContentValues values = new ContentValues();
        values.put(ProductsEntry.COLUMN_TITLE, "How to Create a Database App");
        values.put(ProductsEntry.COLUMN_PRICE, 20);
        values.put(ProductsEntry.COLUMN_AUTHOR, "Erin Banister");
        values.put(ProductsEntry.COLUMN_QTY, 1);
        values.put(ProductsEntry.COLUMN_GENRE, "SELF-HELP");
        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, "Amazon");
        values.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, "1-888-280-4331");
        Uri dummyBookUri = getContentResolver().insert(ProductsEntry.CONTENT_URI, values);
        if (dummyBookUri != null) {
            getContentResolver().notifyChange(dummyBookUri, null);
            Toast.makeText(this, getString(R.string.dummyData), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, getString(R.string.dummyDataFailed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.main_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        // User clicked on a menu option in the app bar overflow menu
        switch (i.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.add_dummy_data:
                insertDummyData();

                return true;
        }
        return super.onOptionsItemSelected(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_TITLE,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_AUTHOR,
                ProductsEntry.COLUMN_QTY,
                ProductsEntry.COLUMN_GENRE,
                ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsEntry.COLUMN_SUPPLIER_PHONE};

        /**Loader to run the query in the background instead of the main thread*/
        return new CursorLoader(
                this,                       //context
                ProductsEntry.CONTENT_URI,         //Content URI to Query
                proj,                              //projection
                null,                      //selection
                null,                   //selection arguments
                null                       //sort order
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /**swaps the old cursor with the current data*/
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**cursor when data is deleted*/
        mCursorAdapter.swapCursor(null);
    }

}

