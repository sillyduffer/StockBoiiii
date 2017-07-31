package com.example.android.stockboiiii;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.stockboiiii.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter{


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);//flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ITEM_QUANTITY);

        String productName =  cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        nameView.setText(productName);
        priceView.setText(productPrice);
        quantityView.setText(productQuantity);
    }
}
