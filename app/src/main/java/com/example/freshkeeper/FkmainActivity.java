package com.example.freshkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;
import com.example.freshkeeper.utils.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import android.util.Log;


public class FkmainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private EditText searchEditText;
    private Button tabAll, tabFrozen, tabRefrigerated, tabRoomTemp;
    private TextView sortOrder;
    private LinearLayout sortOptions;
    private ImageView plusButton, barcodeButton, myPageButton, calendarButton;

    private List<FoodItem> allItems;
    private DatabaseHelper dbHelper;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fkmain);

        requestPermissions();
        dbHelper = new DatabaseHelper(this);

        searchEditText = findViewById(R.id.search_edit_text);
        tabAll = findViewById(R.id.tab_all);
        tabFrozen = findViewById(R.id.tab_frozen);
        tabRefrigerated = findViewById(R.id.tab_refrigerated);
        tabRoomTemp = findViewById(R.id.tab_room_temp);
        recyclerView = findViewById(R.id.recycler_view);
        sortOrder = findViewById(R.id.sort_order);
        sortOptions = findViewById(R.id.sort_options);
        plusButton = findViewById(R.id.plus_button);
        barcodeButton = findViewById(R.id.icon_barcode);
        myPageButton = findViewById(R.id.icon_mypage);
        calendarButton = findViewById(R.id.icon_calendar);

        allItems = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodItemAdapter(this, allItems);
        recyclerView.setAdapter(adapter);

        activateTab(tabAll);
        loadItemsFromDatabase();

        tabAll.setOnClickListener(v -> {
            activateTab(tabAll);
            loadItemsFromDatabase();
        });

        tabFrozen.setOnClickListener(v -> {
            activateTab(tabFrozen);
            loadItemsByCategory(0);
        });

        tabRefrigerated.setOnClickListener(v -> {
            activateTab(tabRefrigerated);
            loadItemsByCategory(1);
        });

        tabRoomTemp.setOnClickListener(v -> {
            activateTab(tabRoomTemp);
            loadItemsByCategory(2);
        });

        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        barcodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
        });

        myPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, MypageActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(FkmainActivity.this, AddItemActivity.class);
            FoodItem clickedItem = allItems.get(position);

            // 이미지 경로를 파일로 저장하고 경로 전달
            String imagePath = clickedItem.getImagePath();
            if (imagePath != null) {
                String filePath = FileUtils.saveDataToFile(this, "imagePath.txt", imagePath);
                intent.putExtra("filePath", filePath);
            }

            intent.putExtra("itemName", clickedItem.getName());
            startActivity(intent);
        });

        // 엔터 키 입력을 제한하는 리스너 추가
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                return true;  // 엔터 키 입력을 무시하고 아무 동작도 하지 않음
            }
            return false;
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sortOrder.setOnClickListener(v -> {
            if (sortOptions.getVisibility() == View.GONE) {
                sortOptions.setVisibility(View.VISIBLE);
            } else {
                sortOptions.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.sort_name).setOnClickListener(v -> sortItemsByName());
        findViewById(R.id.sort_reg_date).setOnClickListener(v -> sortItemsByRegDate());
        findViewById(R.id.sort_exp_date).setOnClickListener(v -> sortItemsByExpDate());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 삭제 시 데이터베이스에서도 영구적으로 삭제되도록 추가
        adapter.setOnItemDeleteListener(position -> {
            FoodItem itemToDelete = allItems.get(position);
            if (dbHelper.deleteItem(itemToDelete.getName())) { // 데이터베이스에서 삭제
                allItems.remove(position); // 목록에서 삭제
                adapter.notifyItemRemoved(position); // UI 업데이트
                Log.d("FkmainActivity", "항목이 성공적으로 삭제되었습니다.");
            } else {
                Log.e("FkmainActivity", "데이터베이스에서 항목 삭제에 실패했습니다.");
            }
        });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE || requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용
            } else {
                Toast.makeText(this, "권한을 허용해야 기능을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void activateTab(Button activeTab) {
        tabAll.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabFrozen.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabRefrigerated.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabRoomTemp.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        activeTab.setBackgroundColor(getResources().getColor(R.color.tab_active));
    }

    private void loadItemsFromDatabase() {
        allItems.clear();
        Cursor cursor = dbHelper.getAllItems();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String regDate = cursor.getString(cursor.getColumnIndexOrThrow("reg_date"));
                String expDate = cursor.getString(cursor.getColumnIndexOrThrow("exp_date"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                String countdown = calculateDDay(expDate);
                allItems.add(new FoodItem(R.drawable.fk_placeholder, name, regDate, expDate, countdown, memo, imagePath, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void loadItemsByCategory(int storageMethod) {
        allItems.clear();
        Cursor cursor = dbHelper.getItemsByStorageMethod(storageMethod);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String regDate = cursor.getString(cursor.getColumnIndexOrThrow("reg_date"));
                String expDate = cursor.getString(cursor.getColumnIndexOrThrow("exp_date"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                String countdown = calculateDDay(expDate);
                allItems.add(new FoodItem(R.drawable.fk_placeholder, name, regDate, expDate, countdown, memo, imagePath, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private String calculateDDay(String expDate) {
        SimpleDateFormat dateFormat8 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyMMdd");
        try {
            Date expirationDate;
            if (expDate.length() == 8) {
                expirationDate = dateFormat8.parse(expDate);
            } else if (expDate.length() == 6) {
                expirationDate = dateFormat6.parse(expDate);
            } else {
                return "D-??";
            }

            Date today = new Date();
            long diffInMillis = expirationDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (daysLeft == 0) {
                return "D-day";
            } else if (daysLeft > 0) {
                return "D-" + daysLeft;
            } else {
                return "D+" + Math.abs(daysLeft);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "D-??";
    }

    private void filter(String text) {
        List<FoodItem> filteredList = new ArrayList<>();
        for (FoodItem item : allItems) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void sortItemsByName() {
        Toast.makeText(this, "이름순 정렬", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void sortItemsByRegDate() {
        Toast.makeText(this, "등록순 정렬", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void sortItemsByExpDate() {
        Toast.makeText(this, "유통기한순 정렬", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }
}
