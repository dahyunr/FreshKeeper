package com.example.freshkeeper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.freshkeeper.database.DatabaseHelper;
import com.example.freshkeeper.utils.FileUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddItemActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final String TAG = "AddItemActivity";

    private AutoCompleteTextView itemNameEditText;
    private EditText itemRegDate, itemExpDate, itemMemo, itemQuantity;
    private Spinner storageMethodSpinner;
    private Button saveButton, cancelButton;
    private ImageView itemImage, itemQuantityMinus, itemQuantityPlus, itemRegDateIcon, itemExpDateIcon;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private int quantity = 1;
    private Uri imageUri;
    private String imagePath = null;
    private int itemId = -1;  // 아이템 ID를 저장하기 위한 변수
    private String barcode = ""; // 바코드 값을 저장하기 위한 변수
    private final Map<String, Integer> recommendedExpiryDays = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // 초기화
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemRegDate = findViewById(R.id.item_reg_date);
        itemExpDate = findViewById(R.id.item_exp_date);
        itemMemo = findViewById(R.id.item_memo);
        itemQuantity = findViewById(R.id.item_quantity);
        itemQuantityMinus = findViewById(R.id.item_quantity_minus);
        itemQuantityPlus = findViewById(R.id.item_quantity_plus);
        itemImage = findViewById(R.id.item_image);
        itemRegDateIcon = findViewById(R.id.item_reg_date_icon);
        itemExpDateIcon = findViewById(R.id.item_exp_date_icon);
        storageMethodSpinner = findViewById(R.id.storage_method);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        calendar = Calendar.getInstance();
        dbHelper = new DatabaseHelper(this);

        // 추천 유통기한 데이터 설정
        recommendedExpiryDays.put("상추", 3);
        recommendedExpiryDays.put("양파", 14);
        recommendedExpiryDays.put("배추", 5);
        recommendedExpiryDays.put("깻잎", 14);

        // 자동완성 어댑터 설정
        String[] items = {"상추", "양파", "배추", "깻잎"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        itemNameEditText.setThreshold(1);
        itemNameEditText.setAdapter(adapter);

        // 자동완성 항목 선택 시 이벤트 처리
        itemNameEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            if (recommendedExpiryDays.containsKey(selectedItem)) {
                int expiryDays = recommendedExpiryDays.get(selectedItem);
                // 등록일로부터 추천 유통기한 설정
                String recommendedExpiryDate;
                if (isValidDate(itemRegDate.getText().toString().trim())) {
                    recommendedExpiryDate = getRecommendedExpiryDateFromRegDate(expiryDays);
                } else {
                    recommendedExpiryDate = getRecommendedExpiryDateFromToday(expiryDays);
                }
                itemExpDate.setText(recommendedExpiryDate);
                // 토스트 메시지 출력
                Toast.makeText(AddItemActivity.this, "추천 유통기한이 입력되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 등록일 입력 시 추천 유통기한 자동 계산
        itemRegDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 포커스를 잃을 때
                String selectedItem = itemNameEditText.getText().toString().trim();
                if (recommendedExpiryDays.containsKey(selectedItem)) {
                    int expiryDays = recommendedExpiryDays.get(selectedItem);
                    String recommendedExpiryDate = getRecommendedExpiryDateFromRegDate(expiryDays);
                    itemExpDate.setText(recommendedExpiryDate);
                }
            }
        });

        // 등록일 아이콘 클릭 시 추천 유통기한 자동 계산
        itemRegDateIcon.setOnClickListener(v -> {
            showCustomDatePickerDialog(itemRegDate);
            String selectedItem = itemNameEditText.getText().toString().trim();
            if (recommendedExpiryDays.containsKey(selectedItem)) {
                int expiryDays = recommendedExpiryDays.get(selectedItem);
                String recommendedExpiryDate = getRecommendedExpiryDateFromRegDate(expiryDays);
                itemExpDate.setText(recommendedExpiryDate);
            }
        });

        // 기본 이미지 로드
        Glide.with(this).load(R.drawable.fk_ppp).into(itemImage);
        itemQuantity.setText(String.valueOf(quantity));

        // Spinner 설정에서 "전체" 옵션을 제거하고 "냉장", "냉동", "상온"만 남김
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.storage_methods, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageMethodSpinner.setAdapter(spinnerAdapter);

        // 전달된 Intent 데이터로부터 상품명과 기타 정보 설정
        Intent intent = getIntent();
        if (intent != null) {
            itemId = intent.getIntExtra("itemId", -1);
            String productName = intent.getStringExtra("itemName");
            String regDate = intent.getStringExtra("regDate");
            String expDate = intent.getStringExtra("expDate");
            String memo = intent.getStringExtra("memo");
            int quantityValue = intent.getIntExtra("quantity", 1);
            int storageMethod = intent.getIntExtra("storageMethod", 0);
            String filePath = intent.getStringExtra("filePath");
            imagePath = intent.getStringExtra("imagePath");
            barcode = intent.getStringExtra("barcode");

            // 전달된 정보를 뷰에 설정
            if (productName != null && !productName.isEmpty()) {
                itemNameEditText.setText(productName);
            } else {
                itemNameEditText.setHint("상품명을 입력하세요");
            }
            if (regDate != null && !regDate.isEmpty()) {
                itemRegDate.setText(regDate);
            }
            if (expDate != null && !expDate.isEmpty()) {
                itemExpDate.setText(expDate);
            }
            if (memo != null && !memo.isEmpty()) {
                itemMemo.setText(memo);
            }
            itemQuantity.setText(String.valueOf(quantityValue));
            storageMethodSpinner.setSelection(storageMethod);

            // 이미지 로드
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imagePath);
                    Glide.with(this).load(imageUri).into(itemImage);
                } catch (Exception e) {
                    Log.e(TAG, "이미지 로드 실패: " + e.getMessage());
                }
            } else if (filePath != null && !filePath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(FileUtils.readFileToString(filePath));
                    Glide.with(this).load(imageUri).into(itemImage);
                } catch (Exception e) {
                    Log.e(TAG, "이미지 로드 실패: " + e.getMessage());
                }
            }
        }

        // 상품명 입력 필드 클릭 시 기본 안내 문구 제거
        itemNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && itemNameEditText.getText().toString().equals("상품명을 입력하세요")) {
                itemNameEditText.setText("");
            }
        });

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

        itemRegDate.setOnClickListener(v -> {
            showCustomDatePickerDialog(itemRegDate);
        });
        itemExpDate.setOnClickListener(v -> {
            showCustomDatePickerDialog(itemExpDate);
        });

        itemExpDateIcon.setOnClickListener(v -> {
            showCustomDatePickerDialog(itemExpDate);
        });

        // 이미지 클릭 시 갤러리 열기
        itemImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        saveButton.setOnClickListener(v -> {
            String name = itemNameEditText.getText().toString().trim();
            String regDate = itemRegDate.getText().toString().trim();
            String expDate = itemExpDate.getText().toString().trim();
            String memo = itemMemo.getText().toString().trim();
            int quantity = Integer.parseInt(itemQuantity.getText().toString().trim());
            int storageMethod = storageMethodSpinner.getSelectedItemPosition();

            if (!name.isEmpty() && isValidDate(regDate) && isValidDate(expDate)) {
                long result = dbHelper.insertOrUpdateItem(itemId, name, regDate, expDate, memo, quantity, storageMethod, imagePath, barcode);
                if (result != -1) {
                    Toast.makeText(AddItemActivity.this, "상품이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent fkmainIntent = new Intent(AddItemActivity.this, FkmainActivity.class);
                    fkmainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(fkmainIntent);
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
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_CODE_STORAGE_PERMISSION);
        } else {
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
                Glide.with(this)
                        .load(imageUri)
                        .into(itemImage);
                itemImage.setAlpha(1.0f); // 투명도를 초기화
                imagePath = imageUri.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showCustomDatePickerDialog(final EditText dateField) {
        Log.d(TAG, "showCustomDatePickerDialog called");
        final View dialogView = View.inflate(this, R.layout.custom_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).create();

        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(view -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            dateField.setText(dateFormat.format(selectedDate.getTime()));
            Log.d(TAG, "Date selected: " + dateField.getText().toString());
            String selectedItem = itemNameEditText.getText().toString().trim();
            if (recommendedExpiryDays.containsKey(selectedItem)) {
                int expiryDays = recommendedExpiryDays.get(selectedItem);
                String recommendedExpiryDate = getRecommendedExpiryDateFromRegDate(expiryDays);
                itemExpDate.setText(recommendedExpiryDate);
                Toast.makeText(AddItemActivity.this, "추천 유통기한이 등록일 기준으로 업데이트되었습니다", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        });
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            Log.d(TAG, "DatePicker canceled");
            alertDialog.dismiss();
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private boolean isValidDate(String date) {
        if (date.length() == 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(date);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "날짜를 8자리로 입력해야 합니다 (yyyymmdd 형식).");
        }
        return false;
    }

    private String getRecommendedExpiryDateFromRegDate(int daysToAdd) {
        String regDateText = itemRegDate.getText().toString().trim();
        if (isValidDate(regDateText)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(regDateText));
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
                return dateFormat.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String getRecommendedExpiryDateFromToday(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
