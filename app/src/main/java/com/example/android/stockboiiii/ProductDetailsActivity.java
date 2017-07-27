package com.example.android.stockboiiii;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.stockboiiii.Data.ProductContract;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mNameView;
    private TextView mPriceView;
    private TextView mQuantityView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mNameView = (TextView) findViewById(R.id.details_name);
        mPriceView = (TextView) findViewById(R.id.details_price);
        mQuantityView = (TextView) findViewById(R.id.details_quantity);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_ITEM_NAME,
                ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY,
                ProductContract.ProductEntry.COLUMN_ITEM_PRICE
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
        cursor.moveToFirst();

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);

        String productName =  cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        mNameView.setText(productName);
        mPriceView.setText(productPrice);
        mQuantityView.setText(productQuantity);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameView.setText("");
        mPriceView.setText("");
        mQuantityView.setText("");
    }
}
