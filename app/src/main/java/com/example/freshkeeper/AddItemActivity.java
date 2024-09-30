package com.example.freshkeeper;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    private EditText itemName, itemRegDate, itemExpDate, itemMemo, itemQuantity;
    private Spinner storageMethodSpinner;
    private Button saveButton, cancelButton;
    private ImageView itemImage, itemQuantityMinus, itemQuantityPlus;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private int quantity = 1;
    private Uri imageUri;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemName = findViewById(R.id.item_name);
        itemRegDate = findViewById(R.id.item_reg_date);
        itemExpDate = findViewById(R.id.item_exp_date);
        itemMemo = findViewById(R.id.item_memo);
        itemQuantity = findViewById(R.id.item_quantity);
        itemQuantityMinus = findViewById(R.id.item_quantity_minus);
        itemQuantityPlus = findViewById(R.id.item_quantity_plus);
        itemImage = findViewById(R.id.item_image);
        storageMethodSpinner = findViewById(R.id.storage_method);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        calendar = Calendar.getInstance();
        dbHelper = new DatabaseHelper(this);

        // 기본 이미지 로드
        Glide.with(this).load(R.drawable.fk_gallery).into(itemImage);

        itemQuantity.setText(String.valueOf(quantity));

        itemQuantityMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                itemQuantity.setText(String.valueOf(quantity));
            }
        });

        itemQuantityPlus.setOnClickListener(v -> {
            quantity++;
            itemQuantity.setText(String.valueOf(quantity));
        });

        // 이미지 클릭 시 갤러리 열기
        itemImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        itemRegDate.setOnClickListener(v -> showDatePickerDialog(itemRegDate));
        itemExpDate.setOnClickListener(v -> showDatePickerDialog(itemExpDate));

        saveButton.setOnClickListener(v -> {
            String name = itemName.getText().toString().trim();
            String regDate = itemRegDate.getText().toString().trim();
            String expDate = itemExpDate.getText().toString().trim();
            String memo = itemMemo.getText().toString().trim();
            int quantity = Integer.parseInt(itemQuantity.getText().toString().trim());
            int storageMethod = storageMethodSpinner.getSelectedItemPosition();

            if (!name.isEmpty() && isValidDate(regDate) && isValidDate(expDate)) {
                long result = dbHelper.insertOrUpdateItem(name, regDate, expDate, memo, quantity, storageMethod, imagePath);
                if (result != -1) {
                    Toast.makeText(AddItemActivity.this, "상품이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddItemActivity.this, "상품 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddItemActivity.this, "모든 필드를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    // 권한 요청
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_CODE_STORAGE_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "파일 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Glide.with(this).load(imageUri).into(itemImage);
                imagePath = imageUri.toString();  // 이미지 경로 저장
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePickerDialog(final EditText dateField) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateField.setText(dateFormat.format(selectedDate.getTime()));
        }, year, month, day);
        datePickerDialog.show();
    }

    private boolean isValidDate(String date) {
        if (date.length() == 6) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(date);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (date.length() == 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(date);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
