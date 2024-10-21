package com.example.freshkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshkeeper.database.DatabaseHelper;
import com.example.freshkeeper.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
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
        loadItemsFromDatabase(); // 전체 항목을 불러옴

        tabAll.setOnClickListener(v -> {
            activateTab(tabAll);
            loadItemsFromDatabase(); // 전체 탭 클릭 시 전체 항목 불러오기
        });

        tabFrozen.setOnClickListener(v -> {
            activateTab(tabFrozen);
            loadItemsByCategory(0); // 0: 냉동
        });

        tabRefrigerated.setOnClickListener(v -> {
            activateTab(tabRefrigerated);
            loadItemsByCategory(1); // 1: 냉장
        });

        tabRoomTemp.setOnClickListener(v -> {
            activateTab(tabRoomTemp);
            loadItemsByCategory(2); // 2: 상온
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

            // 수정된 부분: ID 값을 인텐트로 전달
            intent.putExtra("itemId", clickedItem.getId());
            intent.putExtra("itemName", clickedItem.getName());
            intent.putExtra("regDate", clickedItem.getRegDate());
            intent.putExtra("expDate", clickedItem.getExpDate());
            intent.putExtra("memo", clickedItem.getMemo());
            intent.putExtra("quantity", clickedItem.getQuantity());
            intent.putExtra("storageMethod", clickedItem.getStorageMethod());
            intent.putExtra("imagePath", clickedItem.getImagePath());

            startActivity(intent);
        });

        // 검색 기능 구현
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString()); // 입력 시 필터링
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 엔터 키 입력 방지
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            return actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
        });

        // 정렬 버튼 클릭 시 정렬 옵션을 표시
        sortOrder.setOnClickListener(v -> {
            if (sortOptions.getVisibility() == View.GONE) {
                sortOptions.setVisibility(View.VISIBLE);
            } else {
                sortOptions.setVisibility(View.GONE);
            }
        });

        // 정렬 옵션 클릭 이벤트 설정
        findViewById(R.id.sort_name).setOnClickListener(v -> {
            sortItemsByName();
            sortOrder.setText("이름순");
            sortOptions.setVisibility(View.GONE); // 정렬 후 옵션 숨김
        });

        findViewById(R.id.sort_reg_date).setOnClickListener(v -> {
            sortItemsByRegDate();
            sortOrder.setText("등록순");
            sortOptions.setVisibility(View.GONE); // 정렬 후 옵션 숨김
        });

        findViewById(R.id.sort_exp_date).setOnClickListener(v -> {
            sortItemsByExpDate();
            sortOrder.setText("유통기한순");
            sortOptions.setVisibility(View.GONE); // 정렬 후 옵션 숨김
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // 삭제 기능 구현
        adapter.setOnItemDeleteListener(position -> {
            FoodItem itemToDelete = allItems.get(position);
            if (dbHelper.deleteItem(itemToDelete.getId())) { // 수정된 부분: ID를 사용하여 데이터베이스에서 삭제
                allItems.remove(position); // 목록에서 삭제
                adapter.notifyItemRemoved(position); // UI 업데이트
                Log.d("FkmainActivity", "항목이 성공적으로 삭제되었습니다.");
            } else {
                Log.e("FkmainActivity", "데이터베이스에서 항목 삭제에 실패했습니다.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면으로 돌아올 때 아이템 리스트 새로고침
        loadItemsFromDatabase();
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
                // 권한이 허용된 경우
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
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("item_id")); // 수정된 부분: ID 값 추가
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String regDate = cursor.getString(cursor.getColumnIndexOrThrow("reg_date"));
                String expDate = cursor.getString(cursor.getColumnIndexOrThrow("exp_date"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int storageMethodValue = cursor.getInt(cursor.getColumnIndexOrThrow("storage_method"));

                String countdown = calculateDDay(expDate);
                allItems.add(new FoodItem(id, R.drawable.fk_placeholder, name, regDate, expDate, countdown, memo, imagePath, quantity, storageMethodValue)); // 수정된 부분: ID 값 추가
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
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("item_id")); // 수정된 부분: ID 값 추가
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String regDate = cursor.getString(cursor.getColumnIndexOrThrow("reg_date"));
                String expDate = cursor.getString(cursor.getColumnIndexOrThrow("exp_date"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int storageMethodValue = cursor.getInt(cursor.getColumnIndexOrThrow("storage_method"));

                String countdown = calculateDDay(expDate);
                allItems.add(new FoodItem(id, R.drawable.fk_placeholder, name, regDate, expDate, countdown, memo, imagePath, quantity, storageMethodValue)); // 수정된 부분: ID 값 추가
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private String calculateDDay(String expDate) {
        SimpleDateFormat dateFormat8 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        try {
            Date expirationDate;
            if (expDate.length() == 8) {
                expirationDate = dateFormat8.parse(expDate);
            } else if (expDate.length() == 6) {
                expirationDate = dateFormat6.parse(expDate);
            } else {
                return "D-??";
            }

            // Resetting the time component to midnight for comparison
            Date today = new Date();
            today = dateFormat8.parse(dateFormat8.format(today));
            expirationDate = dateFormat8.parse(dateFormat8.format(expirationDate));

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

    // 이름순 정렬
    private void sortItemsByName() {
        Collections.sort(allItems, (item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
        adapter.notifyDataSetChanged();
    }

    // 등록순 정렬
    private void sortItemsByRegDate() {
        Collections.sort(allItems, (item1, item2) -> item1.getRegDate().compareTo(item2.getRegDate()));
        adapter.notifyDataSetChanged();
    }

    // 유통기한순 정렬
    private void sortItemsByExpDate() {
        Collections.sort(allItems, (item1, item2) -> item1.getExpDate().compareTo(item2.getExpDate()));
        adapter.notifyDataSetChanged();
    }
}
