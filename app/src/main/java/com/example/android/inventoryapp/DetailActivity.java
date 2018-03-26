package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.android.inventoryapp.data.InventoryContract;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static com.example.android.inventoryapp.data.InventoryProvider.LOG_TAG;

/**
 * Created by Mislav on 22.6.2017..
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private int PICK_IMAGE_REQUEST=1;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private static final String STATE_URI = "STATE_URI";
    private EditText mSupplierEditText;
    private ImageView mPhotoImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(R.string.new_product_label);
            invalidateOptionsMenu();
            Button button=(Button)findViewById(R.id.delete_entry);
            button.setVisibility(View.GONE);
        } else {
            setTitle(R.string.edit_product_label);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.editText_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.supplier_info);
        Button plus =(Button)findViewById(R.id.plus);
        Button minus = (Button)findViewById(R.id.minus);

        mQuantityEditText.setText(String.valueOf(0));

        minus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Integer quantity=Integer.parseInt(mQuantityEditText.getText().toString().trim());
                if(quantity>0){
                mQuantityEditText.setText(String.valueOf(quantity-1));}
            }
        });

        plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Integer quantity=Integer.parseInt(mQuantityEditText.getText().toString().trim());
                if(quantity>=0){
                    mQuantityEditText.setText(String.valueOf(quantity+1));}
            }
        });

        mPhotoImageView=(ImageView) findViewById(R.id.take_photo);
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, "Select your picture"
                ), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mCurrentProductUri!=null){
                    getContentResolver().delete(mCurrentProductUri, null, null);
                    Toast.makeText(getApplicationContext(), R.string.product_deleted11, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) && !savedInstanceState.getString(STATE_URI)
                .equals("")) {
            mCurrentProductUri = Uri.parse(savedInstanceState.getString(STATE_URI));
            ViewTreeObserver viewTreeObserver = mPhotoImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    mPhotoImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mPhotoImageView.setImageBitmap(getBitmapFromUri(mCurrentProductUri));
                }
            });
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null || uri.toString().isEmpty())
            return null;
        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, null);
            input.close();
            return bitmap;
        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "failed to load image", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "failed to load image", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (mCurrentProductUri != null)
            outState.putString(STATE_URI, mCurrentProductUri.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    public void orderFromSupplier(View view){
        String[] email={mSupplierEditText.getText().toString().trim().toLowerCase()};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_TEXT, "Product name: "+mNameEditText.getText().toString().trim()+"\n"+"Quantity:\n");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Product order");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.fill_out, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPhotoImageView.getDrawable() == null) {
            Toast.makeText(this, R.string.add_image, Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imageBitmap = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.info_needed, Toast.LENGTH_SHORT).show();
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_INFO, supplierString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE_DATA, imageUri.toString());

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, (R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, (R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, (R.string.edit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, (R.string.edit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteProduct(View view){
        showDeleteConfirmationDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveProduct();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_IMAGE_DATA,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_INFO};

        return new CursorLoader(this,
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_INFO);
            int imageColumnIndex=cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE_DATA);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            imageUri = Uri.parse(cursor.getString(imageColumnIndex));
            mPhotoImageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data != null) try {

                imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mPhotoImageView = (ImageView) findViewById(R.id.take_photo);
                mPhotoImageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
