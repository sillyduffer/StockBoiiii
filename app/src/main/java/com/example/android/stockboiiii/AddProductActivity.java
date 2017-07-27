package com.example.android.stockboiiii;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.stockboiiii.Data.ProductContract;

public class AddProductActivity extends AppCompatActivity {

    private EditText mNameFieldView;
    private EditText mPriceFieldView;
    private EditText mQuantityFieldView;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        setTitle("Add A Product");

        invalidateOptionsMenu();

        mNameFieldView = (EditText) findViewById(R.id.edit_name);
        mPriceFieldView = (EditText) findViewById(R.id.edit_price);
        mQuantityFieldView = (EditText) findViewById(R.id.edit_quantity);

        mNameFieldView.setOnTouchListener(mTouchListener);
        mPriceFieldView.setOnTouchListener(mTouchListener);
        mQuantityFieldView.setOnTouchListener(mTouchListener);

    }

    private void saveProduct() {
        String nameString = mNameFieldView.getText().toString();
        String priceString = mPriceFieldView.getText().toString();
        String quantityString = mQuantityFieldView.getText().toString();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY, quantityString);

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, "Save Failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product Saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddProductActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddProductActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Changes and Lose Product?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
