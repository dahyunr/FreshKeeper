package com.example.freshkeeper;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemName, itemRegDate, itemExpDate, itemQuantity, itemMemo;
    private Button saveButton, cancelButton;
    private ImageView itemQuantityMinus, itemQuantityPlus, itemRegDateIcon, itemExpDateIcon, itemImage;
    private Spinner storageMethod;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dialog 스타일 적용
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(true);

        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.item_name);
        itemRegDate = findViewById(R.id.item_reg_date);
        itemExpDate = findViewById(R.id.item_exp_date);
        itemQuantity = findViewById(R.id.item_quantity);
        itemMemo = findViewById(R.id.item_memo);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        itemQuantityMinus = findViewById(R.id.item_quantity_minus);
        itemQuantityPlus = findViewById(R.id.item_quantity_plus);
        itemRegDateIcon = findViewById(R.id.item_reg_date_icon);
        itemExpDateIcon = findViewById(R.id.item_exp_date_icon);
        storageMethod = findViewById(R.id.storage_method);
        itemImage = findViewById(R.id.item_image);
        calendar = Calendar.getInstance();

        // 기본값을 0으로 설정
        itemQuantity.setText("0");

        itemQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(itemQuantity.getText().toString());
                if (quantity > 0) {
                    itemQuantity.setText(String.valueOf(quantity - 1));
                }
            }
        });

        itemQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(itemQuantity.getText().toString());
                itemQuantity.setText(String.valueOf(quantity + 1));
            }
        });

        itemRegDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(itemRegDate);
            }
        });

        itemExpDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(itemExpDate);
            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getText().toString();
                String regDate = itemRegDate.getText().toString();
                String expDate = itemExpDate.getText().toString();
                String quantity = itemQuantity.getText().toString();
                String memo = itemMemo.getText().toString();

                if (name.isEmpty() || regDate.isEmpty() || expDate.isEmpty() || quantity.isEmpty()) {
                    Toast.makeText(AddItemActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 여기에 새로운 아이템을 저장하는 코드를 추가하세요.
                    Toast.makeText(AddItemActivity.this, "아이템이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddItemActivity.this, FkmainActivity.class);
                    startActivity(intent);
                    finish(); // 액티비티 종료하여 이전 화면으로 돌아가기
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddItemActivity.this, FkmainActivity.class);
                startActivity(intent);
                finish(); // 취소 버튼 클릭 시 액티비티 종료
            }
        });
    }

    private void showDatePickerDialog(final EditText dateField) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateField.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            itemImage.setImageURI(data.getData());
        }
    }
}
