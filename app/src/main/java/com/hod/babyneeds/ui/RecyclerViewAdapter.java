package com.hod.babyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hod.babyneeds.ListActivity;
import com.hod.babyneeds.MainActivity;
import com.hod.babyneeds.R;
import com.hod.babyneeds.data.DatabaseHandler;
import com.hod.babyneeds.model.Item;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Item> items;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        Item item = items.get(position); //object item

        holder.itemName.setText(MessageFormat.format("Item: {0}", item.getItemName()));
        holder.itemQuantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getItemQuantity())));
        holder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        holder.itemSize.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        holder.itemDateAdded.setText(MessageFormat.format("Date Added: {0}", item.getDateItemAdded()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView itemName;
        public TextView itemQuantity;
        public TextView itemColor;
        public TextView itemSize;
        public TextView itemDateAdded;
        public Button btnEdit;
        public Button btnDelete;

        private DatabaseHandler databaseHandler;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            this.itemName = itemView.findViewById(R.id.tv_item_name);
            this.itemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            this.itemColor = itemView.findViewById(R.id.tv_item_color);
            this.itemSize = itemView.findViewById(R.id.tv_item_size);
            this.itemDateAdded = itemView.findViewById(R.id.tv_item_date);
            this.btnEdit = itemView.findViewById(R.id.btn_edit);
            this.btnDelete = itemView.findViewById(R.id.btn_delete);

            btnEdit.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

            this.databaseHandler = new DatabaseHandler(ctx);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_edit :
                    //edit item
                    editItem(items.get(getAdapterPosition()));
                    break;
                case R.id.btn_delete :
                    //delete item
                    deleteItem(items.get(getAdapterPosition()).getId());
                    break;
            }
        }

        private void editItem(final Item editItem) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup, null);

            final EditText etItem = view.findViewById(R.id.et_item);
            final EditText etQuantity = view.findViewById(R.id.et_item_qty);
            final EditText etColor = view.findViewById(R.id.et_item_color);
            final EditText etSize = view.findViewById(R.id.et_item_size);
            Button saveButton = view.findViewById(R.id.btn_save);
            TextView tvTitle = view.findViewById(R.id.tv_title);

            tvTitle.setText(R.string.edit_title);
            saveButton.setText(R.string.update_text);

            //Populate popup with current object data!
            etItem.setText(editItem.getItemName());
            etQuantity.setText(String.valueOf(editItem.getItemQuantity()));
            etColor.setText(editItem.getItemColor());
            etSize.setText(String.valueOf(editItem.getItemSize()));

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!etItem.getText().toString().isEmpty() && !String.valueOf(etQuantity.getText()).isEmpty() &&
                            !etColor.getText().toString().isEmpty() && !String.valueOf(etSize.getText()).isEmpty()) {

                        editItem.setItemName(etItem.getText().toString());
                        editItem.setItemQuantity(Integer.valueOf(etQuantity.getText().toString()));
                        editItem.setItemColor(etColor.getText().toString());
                        editItem.setItemSize(Integer.valueOf(etSize.getText().toString()));

                        new DatabaseHandler(context).updateItem(editItem);
                        notifyItemChanged(getAdapterPosition(), editItem);

                    } else {
                        Snackbar.make(v, "Empty fields not allowed!", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 1200);
                }
            });

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.btn_conf_no);
            Button yesButton = view.findViewById(R.id.btn_conf_yes);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseHandler.deleteItem(id);
                    items.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}
