package com.example.android.stockboiiii;

import android.content.ContentUris;
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

import com.example.android.stockboiiii.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {

    private int mCurrentQuantity;
    private TextView mQuantityView;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);//flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        mQuantityView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.list_sold);

        final Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow((ProductContract.ProductEntry._ID))));

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY);

        double priceInCents = cursor.getDouble(priceColumnIndex);
        double priceInDollars = priceInCents / 100;

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = String.valueOf(priceInDollars);
        String productQuantity = cursor.getString(quantityColumnIndex);

        mCurrentQuantity = cursor.getInt(quantityColumnIndex);

        nameView.setText(productName);
        priceView.setText(productPrice);
        mQuantityView.setText(productQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = -100;
                if (mCurrentQuantity > 0) {
                    newQuantity = mCurrentQuantity - 1;
                } else {
                    Toast.makeText(v.getContext(), R.string.cannot_make_sale, Toast.LENGTH_SHORT).show();
                }
                if (newQuantity != -100) {
                    String newQuantityString = String.valueOf(newQuantity);
                    ContentValues values = new ContentValues();

                    values.put(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY, newQuantity);

                    int rowsAffected = context.getContentResolver().update(currentProductUri, values, null, null);

                    if (rowsAffected == 0) {
                        Toast.makeText(v.getContext(), R.string.quantity_error,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), R.string.quantity_updated,
                                Toast.LENGTH_SHORT).show();
                        mQuantityView.setText(newQuantityString);
                    }
                }
            }
        });

    }
}
