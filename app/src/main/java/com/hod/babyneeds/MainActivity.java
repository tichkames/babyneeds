package com.hod.babyneeds;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hod.babyneeds.data.DatabaseHandler;
import com.hod.babyneeds.model.Item;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText etItem;
    private EditText etQuantity;
    private EditText etColor;
    private EditText etSize;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);

        byPassActivity();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });

        //Check for saved items
        List<Item> items = databaseHandler.getAllItems();
        for (Item item : items) {
            Log.d(TAG, "item: " + item);
        }
    }

    private void byPassActivity() {
        if(databaseHandler.getItemsCount() > 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            finish();
        }
    }

    private void saveItem(View view) {
        //Save item to DB
        Item item = new Item();
        item.setItemName(etItem.getText().toString().trim());
        item.setItemColor(etColor.getText().toString().trim());
        item.setItemQuantity(Integer.parseInt(etQuantity.getText().toString().trim()));
        item.setItemSize(Integer.parseInt(etSize.getText().toString().trim()));

        databaseHandler.addItem(item);

        Snackbar.make(view, "Item Saved: " + item, Snackbar.LENGTH_SHORT).show();

        //Move to next screen - details screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        }, 1200);
    }

    private void createPopupDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

        etItem = view.findViewById(R.id.et_item);
        etQuantity = view.findViewById(R.id.et_item_qty);
        etColor = view.findViewById(R.id.et_item_color);
        etSize = view.findViewById(R.id.et_item_size);

        saveButton = view.findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!etItem.getText().toString().isEmpty() && !etQuantity.getText().toString().isEmpty() &&
                        !etColor.getText().toString().isEmpty() && !etSize.getText().toString().isEmpty())
                    saveItem(v);
                else
                    Snackbar.make(v, "Empty fields not allowed!", Snackbar.LENGTH_LONG).show();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}