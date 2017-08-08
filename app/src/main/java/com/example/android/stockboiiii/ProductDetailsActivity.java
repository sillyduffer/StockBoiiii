package com.example.android.stockboiiii;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stockboiiii.data.ProductContract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mNameView;
    private TextView mPriceView;
    private TextView mQuantityView;
    private TextView mSummaryView;
    private ImageView mImageView;
    private int mCurrentQuantity;
    private String mCurrentName;
    private String mCurrentSummary;
    private String mCurrentPrice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mNameView = (TextView) findViewById(R.id.details_name);
        mPriceView = (TextView) findViewById(R.id.details_price);
        mQuantityView = (TextView) findViewById(R.id.details_quantity);
        mSummaryView = (TextView) findViewById(R.id.details_summary);
        mImageView = (ImageView) findViewById(R.id.details_image_view);
        Button mDeleteButton = (Button) findViewById(R.id.details_delete_product);
        Button mOrderButton = (Button) findViewById(R.id.details_order_from_supplier);
        Button mIncreaseButton = (Button) findViewById(R.id.details_increase_quantity);
        Button mDecreaseButton = (Button) findViewById(R.id.details_decrease_quantity);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantityByOne(v.getId());
            }
        });

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantityByOne(v.getId());
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailSubject = "Request for more " + mCurrentName + "s";
                String emailBody = "Product Name: " + mCurrentName
                        + "\nDescription: " + mCurrentSummary
                        + "\nPrice: " + mCurrentPrice
                        + "\nCurrent Quantity: " + String.valueOf(mCurrentQuantity);
                orderMore(emailSubject, emailBody);
            }
        });
    }

    private void orderMore(String subject, String attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void changeQuantityByOne(int id) {
        int newQuantity = -100;
        switch (id) {
            case R.id.details_decrease_quantity:
                if (mCurrentQuantity > 0) {
                    newQuantity = mCurrentQuantity - 1;
                    break;
                } else {
                    Toast.makeText(this, "Negative quantity not allowed", Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.details_increase_quantity:
                newQuantity = mCurrentQuantity + 1;
                break;
        }

        if (newQuantity != -100) {
            String newQuantityString = String.valueOf(newQuantity);
            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY, newQuantity);

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating quantity",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quantity updated",
                        Toast.LENGTH_SHORT).show();
                mQuantityView.setText(newQuantityString);
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product?");
        builder.setPositiveButton("Delete Product", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error Deleting Product",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product Deleted",
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    /**
     * Used this repository: https://github.com/crlsndrsjmnz/MyShareImageExample as reference
     * Credit is due to Carlos Jimenez
     */
    public Bitmap loadBitmap(Uri imageUri) {

        if (imageUri == null || imageUri.toString().isEmpty()){
            return null;
        }

        int viewWidth = mImageView.getWidth();
        int viewHeight = mImageView.getHeight();

        InputStream inputStream = null;
        try {
            inputStream = this.getContentResolver().openInputStream(imageUri);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
            inputStream.close();

            int imageWidth = bitmapOptions.outWidth;
            int imageHeight = bitmapOptions.outHeight;

            int scaleFactor = Math.min(imageWidth / viewWidth, imageHeight / viewHeight);

            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = scaleFactor;
            bitmapOptions.inPurgeable = true;

            inputStream = this.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
            inputStream.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("Details", "Could not load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("Details", "Could not load image.", e);
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_ITEM_NAME,
                ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY,
                ProductContract.ProductEntry.COLUMN_ITEM_PRICE,
                ProductContract.ProductEntry.COLUMN_ITEM_SUMMARY,
                ProductContract.ProductEntry.COLUMN_ITEM_IMAGE
        };

        return new android.support.v4.content.CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY);
            int summaryColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_SUMMARY);
            int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_IMAGE);

            double priceInCents = cursor.getDouble(priceColumnIndex);
            double priceInDollars = priceInCents/100;

            String productName = cursor.getString(nameColumnIndex);
            String productPrice = getString(R.string.dollar_sign) + String.valueOf(priceInDollars);
            String productQuantity = cursor.getString(quantityColumnIndex) + getString(R.string.in_stock);
            String productSummary = cursor.getString(summaryColumnIndex);
            String productImage = cursor.getString(imageColumnIndex);

            Uri imageUri = Uri.parse(productImage);

            mImageView.setImageBitmap(loadBitmap(imageUri));

            mCurrentQuantity = cursor.getInt(quantityColumnIndex);
            mCurrentName = productName;
            mCurrentPrice = productPrice;
            mCurrentSummary = productSummary;

            mNameView.setText(productName);
            mPriceView.setText(productPrice);
            mQuantityView.setText(productQuantity);
            mSummaryView.setText(productSummary);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameView.setText("");
        mPriceView.setText("");
        mQuantityView.setText("");
        mSummaryView.setText("");
    }
}
