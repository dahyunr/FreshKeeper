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

        // 전달된 바코드 데이터를 이용해 제품명 자동 설정
        String barcodeValue = getIntent().getStringExtra("barcodeValue");
        if (barcodeValue != null) {
            // 실제로는 바코드 값으로 제품 정보를 조회해야 하지만, 예시로 간단히 제품명을 설정합니다.
            itemName.setText("바코드 제품명: " + barcodeValue);
            // 이미지도 설정할 수 있습니다 (예: 기본 이미지 설정)
            itemImage.setImageResource(R.drawable.fk_placeholder);
        }

        itemQuantityMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(itemQuantity.getText().toString());
            if (quantity > 0) {
                itemQuantity.setText(String.valueOf(quantity - 1));
            }
        });

        itemQuantityPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(itemQuantity.getText().toString());
            itemQuantity.setText(String.valueOf(quantity + 1));
        });

        itemRegDateIcon.setOnClickListener(v -> showDatePickerDialog(itemRegDate));

        itemExpDateIcon.setOnClickListener(v -> showDatePickerDialog(itemExpDate));

        itemImage.setOnClickListener(v -> openGallery());

        saveButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            String regDate = itemRegDate.getText().toString();
            String expDate = itemExpDate.getText().toString();
            String quantity = itemQuantity.getText().toString();
            String memo = itemMemo.getText().toString();

            if (name.isEmpty() || regDate.isEmpty() || expDate.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(AddItemActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 새로운 아이템 생성 및 결과로 반환
                Intent resultIntent = new Intent();
                FoodItem newItem = new FoodItem(
                        R.drawable.fk_placeholder, // 아이템 이미지 리소스를 적절히 설정해주세요
                        name,
                        regDate,
                        expDate,
                        calculateCountdown(expDate),
                        memo
                );
                resultIntent.putExtra("newItem", newItem);
                setResult(RESULT_OK, resultIntent); // 결과 설정
                finish(); // 액티비티 종료하여 이전 화면으로 돌아가기
            }
        });

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);  // 결과를 취소로 설정
            finish(); // 취소 버튼 클릭 시 현재 액티비티 종료하여 이전 화면으로 돌아가기
        });
    }

    private void showDatePickerDialog(final EditText dateField) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) ->
                dateField.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), year, month, day);
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

    private String calculateCountdown(String expDate) {
        // 유통기한 D-Day 계산 로직을 추가합니다.
        return "D-Day"; // 계산된 D-Day를 반환합니다.
    }
}
