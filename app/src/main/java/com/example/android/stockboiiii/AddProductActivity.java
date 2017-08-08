package com.example.android.stockboiiii;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.stockboiiii.data.ProductContract;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1000;

    private EditText mNameFieldView;
    private EditText mPriceFieldView;
    private EditText mQuantityFieldView;
    private EditText mSummaryFieldView;

    private Uri mImageUri;

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
        mSummaryFieldView = (EditText) findViewById(R.id.edit_summary);
        Button mImageFieldButton = (Button) findViewById(R.id.add_image_button);

        mNameFieldView.setOnTouchListener(mTouchListener);
        mPriceFieldView.setOnTouchListener(mTouchListener);
        mQuantityFieldView.setOnTouchListener(mTouchListener);
        mSummaryFieldView.setOnTouchListener(mTouchListener);
        mImageFieldButton.setOnTouchListener(mTouchListener);

        mImageFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent imageIntent;

        if (Build.VERSION.SDK_INT < 19) {
            imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            imageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        imageIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                mImageUri = resultData.getData();
            }
        }
    }

    private void saveProduct() {
        String nameString = mNameFieldView.getText().toString();
        String priceString = mPriceFieldView.getText().toString();
        String quantityString = mQuantityFieldView.getText().toString();
        String summaryString = mSummaryFieldView.getText().toString();

        double priceValue = Double.parseDouble(priceString);

        double priceInCents = priceValue * 100;

        String storedPriceString = String.valueOf(priceInCents);

        if (nameString.equals("") || priceString.equals("") || quantityString.equals("") || mImageUri == null){
            Toast.makeText(this, R.string.required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        String imageString = mImageUri.toString();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_PRICE, priceInCents);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_SUMMARY, summaryString);
        values.put(ProductContract.ProductEntry.COLUMN_ITEM_IMAGE, imageString);

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, R.string.failed_save,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.successful_save,
                    Toast.LENGTH_SHORT).show();
            mProductHasChanged = false;
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
        builder.setMessage(R.string.discard_changes_prompt);
        builder.setPositiveButton(R.string.discard_confirm, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_edit, new DialogInterface.OnClickListener() {
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
