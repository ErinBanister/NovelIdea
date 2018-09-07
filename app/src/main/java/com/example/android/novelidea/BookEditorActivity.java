package com.example.android.novelidea;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.novelidea.data.ProductContract.ProductsEntry;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static int CURRENT_PET_LOADER = 0;
    private Uri mCurrentBookUri;
    private EditText mAuthorEditText;
    private EditText mTitleEditText;
    private EditText mPriceEditText;
    private TextView mQty;
    private Button mPlusOneQty;
    private Button mMinusOneQty;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private Spinner mGenreSpinner;
    private Button mSupplierButton;

    /**
     * Set Default Genre Spinner Value to Unknown
     */
    private int mGenre = ProductsEntry.GENRE_UNKNOWN;

    /**
     * Keep track if the book editor values have changed
     */
    private boolean mBookChanged;

    /**
     * OnTouchListener updates the boolean if a value has been changed(i.e. touched)
     */
    private View.OnTouchListener mItemTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookeditor);

        Intent i = getIntent();
        mCurrentBookUri = i.getData();

        /**if mCurrentBookUri is null, then it's a new book. Otherwise, the form will edit the book
         * passed in through the intent
         */
        if (mCurrentBookUri != null) {
            /**passed in Uri, fill fields with exiting data*/
            setTitle(getResources().getString(R.string.editBook));
            getLoaderManager().initLoader(CURRENT_PET_LOADER, null, this);
        } else {
            setTitle(getResources().getString(R.string.addBook));
            invalidateOptionsMenu();
        }

        /**set field variables to the appropriate views*/
        mAuthorEditText = findViewById(R.id.author);
        mTitleEditText = findViewById(R.id.title);
        mPriceEditText = findViewById(R.id.price);
        mQty = findViewById(R.id.qty);
        mPlusOneQty = findViewById(R.id.plusOneQty);
        mMinusOneQty = findViewById(R.id.minusOneQty);
        mSupplierEditText = findViewById(R.id.supplier);
        mSupplierPhoneEditText = findViewById(R.id.supplierPhone);
        mGenreSpinner = (Spinner) findViewById(R.id.genreSpinner);
        mSupplierButton = findViewById(R.id.callSupplierButton);

        /**set onTouchListener to each applicable BookEditorActivity view,
         * so the main view can be updated after a change
         */

        mAuthorEditText.setOnTouchListener(mItemTouchListener);
        mTitleEditText.setOnTouchListener(mItemTouchListener);
        mPriceEditText.setOnTouchListener(mItemTouchListener);
        mPlusOneQty.setOnTouchListener(mItemTouchListener);
        mMinusOneQty.setOnTouchListener(mItemTouchListener);
        mSupplierEditText.setOnTouchListener(mItemTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mItemTouchListener);
        mGenreSpinner.setOnTouchListener(mItemTouchListener);

        mSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
                String phoneURI = "tel:" + supplierPhone;
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse(phoneURI));
                startActivity(i);
            }
        });

        mPlusOneQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty;
                if (mQty.equals("") | mQty == null) {
                    qty = 0;
                } else {
                    qty = Integer.parseInt(mQty.getText().toString());
                }
                qty = qty + 1;
                mQty.setText(Integer.toString(qty));
            }
        });

        mMinusOneQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty;
                String qtyVal = mQty.getText().toString();
                if (mQty.equals("") | mQty == null | qtyVal.equals("0")) {
                    Toast.makeText(BookEditorActivity.this, getResources().getString(R.string.qtyError), Toast.LENGTH_SHORT).show();
                } else {
                    qty = Integer.parseInt(qtyVal) - 1;
                    mQty.setText(Integer.toString(qty));
                }
            }
        });

        setGenreSpinner();
    }

    private void setGenreSpinner() {
        /**Set Array for Genre Spinner. Data comes from the string array in arrays.xml*/
        ArrayAdapter genreSpinAdapter = ArrayAdapter.createFromResource(this, R.array.genre_array, android.R.layout.simple_spinner_item);

        /**set spinner adapter to ensure there's one item per line*/
        genreSpinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenreSpinner.setAdapter(genreSpinAdapter);

        mGenreSpinner.setSelection(mGenre);


        /**set genre value to spinner value after item is selected*/
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String genreSelected = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(genreSelected)) {
                    if (genreSelected.equals(getString(R.string.drama))) {
                        mGenre = ProductsEntry.GENRE_DRAMA;
                    } else if (genreSelected.equals(getString(R.string.scifi))) {
                        mGenre = ProductsEntry.GENRE_SCIFI;
                    } else if (genreSelected.equals(getString(R.string.humor))) {
                        mGenre = ProductsEntry.GENRE_HUMOR;
                    } else if (genreSelected.equals(getString(R.string.fantasy))) {
                        mGenre = ProductsEntry.GENRE_FANTASY;
                    } else if (genreSelected.equals(getString(R.string.adventure))) {
                        mGenre = ProductsEntry.GENRE_ADVENTURE;
                    } else if (genreSelected.equals(getString(R.string.mystery))) {
                        mGenre = ProductsEntry.GENRE_MYSTERY;
                    } else if (genreSelected.equals(getString(R.string.romance))) {
                        mGenre = ProductsEntry.GENRE_ROMANCE;
                    } else if (genreSelected.equals(getString(R.string.horror))) {
                        mGenre = ProductsEntry.GENRE_HORROR;
                    } else if (genreSelected.equals(getString(R.string.nonfiction))) {
                        mGenre = ProductsEntry.GENRE_NONFICTION;
                    } else {
                        mGenre = ProductsEntry.GENRE_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = ProductsEntry.GENRE_UNKNOWN;
            }
        });
    }

    private void saveBook() {
        String author = mAuthorEditText.getText().toString().trim();
        String title = mTitleEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String qty = mQty.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        /**check if uri and and fields are null. If so, don't do anything
         * (user hasn't inputted any new data into the new book fields)
         */
        int addUri;
        if (mCurrentBookUri == null) {
            addUri = 0;
        } else {
            addUri = 1;
        }

        /*if ((addUri == 0) && TextUtils.isEmpty(title) && TextUtils.isEmpty(author)
            && TextUtils.isEmpty(price) && TextUtils.isEmpty(qty)){
            return;
        }*/


        /**Ensure price is a valid int value*/
        int bookPrice = 0;
        if (!TextUtils.isEmpty(price)) {
            bookPrice = Integer.parseInt(price);
        }
        /**Set ContentValues using BookEditorActivity fields*/
        ContentValues values = new ContentValues();
        values.put(ProductsEntry.COLUMN_AUTHOR, author);
        values.put(ProductsEntry.COLUMN_TITLE, title);
        values.put(ProductsEntry.COLUMN_PRICE, bookPrice);
        values.put(ProductsEntry.COLUMN_QTY, qty);
        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
        values.put(ProductsEntry.COLUMN_GENRE, mGenre);
        String message = getString(R.string.addBook) + " " + title;

        /**Check if Uri is null. If null, then add book. If !null, then update current record in DB*/
        if (addUri == 0) {

            /**No Uri, create new record*/
            Uri newBook = getContentResolver().insert(ProductsEntry.CONTENT_URI, values);

            /**The Content Resolver returns a Uri.
             * If Uri is null, insert wasn't successful.
             */
            if (newBook != null) {
                message = message + getString(R.string.successful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                message = message + getString(R.string.unsuccessful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } else {
            /**Book Exists. Update data in DB
             * Content Resolver returns a row number, indicating how many rows were affected.
             * if 0, nothing was updated.
             */
            int bookRows = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (bookRows > 0) {
                /**if the returned value isn > 0, then update was successful.*/
                message = message + getString(R.string.successful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                message = message + getString(R.string.unsuccessful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * delete book from database(after confirmation)
     */
    private void deleteBook() {
        /**Ensure there is a URI to delete*/
        if (mCurrentBookUri != null) {
            String bookTitle = mTitleEditText.getText().toString().trim();
            if (TextUtils.isEmpty(bookTitle)) {
                bookTitle = "Unknown Book Title";
            }
            String message = R.string.deleteBookMsg + bookTitle;
            int deletedBook = getContentResolver().delete(mCurrentBookUri, null, null);

            /**Toast user to notify on successful deletion*/
            if (deletedBook > 0) {
                message = message + getString(R.string.successful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                message = message + getString(R.string.unsuccessful);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Create menu and options for app bar in editor activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**add menu to app bar for saving/deleting*/
        getMenuInflater().inflate(R.menu.bookeditor_menu, menu);
        return true;
    }

    /**
     * Remove delete option from menu in action bar if adding a new book
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem mi = menu.findItem(R.id.delete);
            mi.setVisible(false);
        }
        return true;
    }

    /**
     * actions when a user clicks a menu option from the app bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        switch (i.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save:
                /**save book and leave activity*/
                /**Ensure the fields have values to save.
                 * Requires Title, Author, Price, Supplier, & SupplierPhone*/
                int checkFieldValues = checkValues();

                if (checkFieldValues != 0) {
                    String[] messageArray = getResources().getStringArray(R.array.missing_value);
                    String message = getResources().getString(R.string.addFailed) + " " + messageArray[checkFieldValues];
                    Toast.makeText(BookEditorActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }

                saveBook();
                finish();
                return true;
            case R.id.delete:
                /**Confirm user wants to delete, then delete in separate function*/
                confirmDelete();
                return true;
            case android.R.id.home:
                /**only update if the data has changed, otherwise warn the user before returning
                 * to main activity*/
                if (!mBookChanged) {
                    /**Nothing changed, go back home*/
                    NavUtils.navigateUpFromSameTask(BookEditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /**User indicated to discard changes. Continue to main activity*/
                                NavUtils.navigateUpFromSameTask(BookEditorActivity.this);
                            }
                        };
                /**prompt user to discard changes to editor*/
                discardChanges(discardClickListener);
                return true;
        }
        return super.onOptionsItemSelected(i);
    }

    private void discardChanges(
            DialogInterface.OnClickListener discardButtonClickListener) {
        /**Alert Dialog to prompt user to discard changes when
         * pressing the home button from the editor
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discardChanges);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * prompt user to confirm deleting book
     */
    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteBook);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
     * user navigates to home
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        discardChanges(discardListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String proj[] = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_TITLE,
                ProductsEntry.COLUMN_AUTHOR,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_QTY,
                ProductsEntry.COLUMN_GENRE,
                ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsEntry.COLUMN_SUPPLIER_PHONE};
        return new CursorLoader(this, mCurrentBookUri, proj, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /**if cursor is null, leave function*/
        if (data == null || data.getCount() < 1) {
            return;
        }

        /**get data from first row of cursor*/
        if (data.moveToFirst()) {

            /**set field variables to the appropriate views*/
            mAuthorEditText = findViewById(R.id.author);
            mTitleEditText = findViewById(R.id.title);
            mPriceEditText = findViewById(R.id.price);
            mQty = findViewById(R.id.qty);
            mSupplierEditText = findViewById(R.id.supplier);
            mSupplierPhoneEditText = findViewById(R.id.supplierPhone);
            mGenreSpinner = (Spinner) findViewById(R.id.genreSpinner);

            /**get the columns for these values*/
            int authorCol = data.getColumnIndex(ProductsEntry.COLUMN_AUTHOR);
            int titleCol = data.getColumnIndex(ProductsEntry.COLUMN_TITLE);
            int genreCol = data.getColumnIndex(ProductsEntry.COLUMN_GENRE);
            int priceCol = data.getColumnIndex(ProductsEntry.COLUMN_PRICE);
            int qtyCol = data.getColumnIndex(ProductsEntry.COLUMN_QTY);
            int supplierCol = data.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhCol = data.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_PHONE);

            int genre = data.getInt(genreCol);

            // Update the views on the screen with the values from the database
            mAuthorEditText.setText(data.getString(authorCol));
            mTitleEditText.setText(data.getString(titleCol));
            mPriceEditText.setText(Integer.toString(data.getInt(priceCol)));
            mQty.setText(Integer.toString(data.getInt(qtyCol)));
            mSupplierEditText.setText(data.getString(supplierCol));
            mSupplierPhoneEditText.setText(data.getString(supplierPhCol));

            /**set spinner value**/
            mGenreSpinner.setSelection(genre);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAuthorEditText.setText("");
        mTitleEditText.setText("");
        mPriceEditText.setText("");
        mQty.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    public int checkValues() {
        String author = mAuthorEditText.getText().toString().trim();
        String title = mTitleEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        String phone = mSupplierPhoneEditText.getText().toString().trim();
        if (author.isEmpty()) {
            return ProductsEntry.MISSING_AUTHOR;
        } else if (title.isEmpty()) {
            return ProductsEntry.MISSING_TITLE;
        } else if (price.isEmpty()) {
            return ProductsEntry.MISSING_PRICE;
        } else if (supplier.isEmpty()) {
            return ProductsEntry.MISSING_SUPPLIER;
        } else if (phone.isEmpty()) {
            return ProductsEntry.MISSING_SUPPLIER_PHONE;
        } else {
            return ProductsEntry.MISSING_NOTHING;
        }
    }


}
