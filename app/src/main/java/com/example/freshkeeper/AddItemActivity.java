package com.example.freshkeeper;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemName, itemRegDate, itemExpDate;
    private Button saveButton, cancelButton;
    private ImageView itemImage;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.item_name);
        itemRegDate = findViewById(R.id.item_reg_date);
        itemExpDate = findViewById(R.id.item_exp_date);
        itemImage = findViewById(R.id.item_image);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        calendar = Calendar.getInstance();

        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");
        String productImage = intent.getStringExtra("productImage");

        itemName.setText(productName);
        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(itemImage);
        } else {
            itemImage.setImageResource(R.drawable.fk_placeholder);
        }

        saveButton.setOnClickListener(v -> {
            finish();
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog(final EditText dateField) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) ->
                dateField.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), year, month, day);
        datePickerDialog.show();
    }
}
