package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.android.inventoryapp.data.InventoryContract;

/**
 * Created by Mislav on 24.6.2017..
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView product_name = (TextView) view.findViewById(R.id.product_name);
        TextView price  = (TextView) view.findViewById(R.id.price);
        final TextView quantity = (TextView) view.findViewById(R.id.current_quantity);

        String nameProduct = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME));
        Integer priceProduct = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE));
        final Integer quantityProduct = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        Integer iid=cursor.getInt(cursor.getColumnIndex((InventoryContract.InventoryEntry._ID)));

        product_name.setText(nameProduct);
        price.setText(String.valueOf(priceProduct));
        quantity.setText(String.valueOf(quantityProduct));

        Button button=(Button) view.findViewById(R.id.sell_button);
        button.setTag(iid);
    }
}
