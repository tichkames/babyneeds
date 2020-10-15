package com.hod.babyneeds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hod.babyneeds.data.DatabaseHandler;
import com.hod.babyneeds.model.Item;
import com.hod.babyneeds.ui.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Item> items;
    private DatabaseHandler databaseHandler;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText etItem;
    private EditText etQuantity;
    private EditText etColor;
    private EditText etSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get items from database
        databaseHandler = new DatabaseHandler(this);
        items = databaseHandler.getAllItems();

        for (Item item : items) {
            Log.d(TAG, "Item: " + item);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, items);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        FloatingActionButton fab = findViewById(R.id.fab_default);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });
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
            }
        }, 1200);
    }
}