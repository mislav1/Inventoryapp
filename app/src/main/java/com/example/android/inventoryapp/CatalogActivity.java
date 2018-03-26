package com.example.android.inventoryapp;

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
import com.example.android.inventoryapp.data.InventoryContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        ListView lvItems = (ListView) findViewById(R.id.list_view_products);

        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);
        mCursorAdapter=new ProductCursorAdapter(this,null);
        lvItems.setAdapter(mCursorAdapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentUri= ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                    Intent i = new Intent(CatalogActivity.this, DetailActivity.class);
                    i.setData(currentUri);
                    startActivity(i);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    public void sellButtonClicked(View view) {
        int id = (Integer) view.getTag();
        Uri currentUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        Cursor cursor=null;
        cursor = getContentResolver().query(currentUri,null,null,null,null);
        cursor.moveToPosition(0);
        Integer productQuantity=cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        if(productQuantity>0){
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity-1);
            getContentResolver().update(currentUri, values, null, null);}
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_all) {
            getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY};
        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
