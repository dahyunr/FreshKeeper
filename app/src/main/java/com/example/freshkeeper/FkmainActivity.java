package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FkmainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private SearchView searchBar;
    private Button tabAll, tabFrozen, tabRefrigerated, tabRoomTemp;
    private TextView sortOrder;
    private LinearLayout sortOptions;
    private TextView sortName, sortRegDate, sortExpDate;
    private ImageView plusButton, barcodeButton;

    private List<FoodItem> allItems;
    private List<FoodItem> frozenItems;
    private List<FoodItem> refrigeratedItems;
    private List<FoodItem> roomTempItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fkmain);

        Log.d("FkmainActivity", "onCreate 시작");

        // UI 요소 초기화
        //searchBar = findViewById(R.id.search_bar);
        tabAll = findViewById(R.id.tab_all);
        tabFrozen = findViewById(R.id.tab_frozen);
        tabRefrigerated = findViewById(R.id.tab_refrigerated);
        tabRoomTemp = findViewById(R.id.tab_room_temp);
        recyclerView = findViewById(R.id.recycler_view);
        sortOrder = findViewById(R.id.sort_order);
        sortOptions = findViewById(R.id.sort_options);
        sortName = findViewById(R.id.sort_name);
        sortRegDate = findViewById(R.id.sort_reg_date);
        sortExpDate = findViewById(R.id.sort_exp_date);
        plusButton = findViewById(R.id.plus_button);
        barcodeButton = findViewById(R.id.icon_barcode);

        allItems = new ArrayList<>();
        frozenItems = new ArrayList<>();
        refrigeratedItems = new ArrayList<>();
        roomTempItems = new ArrayList<>();

        populateData();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodItemAdapter(allItems);
        recyclerView.setAdapter(adapter);

        // SearchView 설정
        if (searchBar != null) {
            searchBar.setIconifiedByDefault(false);
            searchBar.setQueryHint("식품을 검색하세요");
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        } else {
            Log.e("FkmainActivity", "SearchView is null!");
        }

        // 탭 클릭 리스너 설정
        tabAll.setOnClickListener(v -> {
            updateTabSelection(tabAll);
            adapter.updateList(allItems);
        });

        tabFrozen.setOnClickListener(v -> {
            updateTabSelection(tabFrozen);
            adapter.updateList(frozenItems);
        });

        tabRefrigerated.setOnClickListener(v -> {
            updateTabSelection(tabRefrigerated);
            adapter.updateList(refrigeratedItems);
        });

        tabRoomTemp.setOnClickListener(v -> {
            updateTabSelection(tabRoomTemp);
            adapter.updateList(roomTempItems);
        });

        // 정렬 옵션 클릭 리스너 설정
        sortOrder.setOnClickListener(v -> {
            if (sortOptions.getVisibility() == View.GONE) {
                sortOptions.setVisibility(View.VISIBLE);
            } else {
                sortOptions.setVisibility(View.GONE);
            }
        });

        sortName.setOnClickListener(v -> {
            sortOptions.setVisibility(View.GONE);
            sortOrder.setText("이름순");
            Collections.sort(allItems, Comparator.comparing(FoodItem::getName));
            adapter.updateList(allItems);
        });

        sortRegDate.setOnClickListener(v -> {
            sortOptions.setVisibility(View.GONE);
            sortOrder.setText("등록순");
            Collections.sort(allItems, Comparator.comparing(FoodItem::getRegDate));
            adapter.updateList(allItems);
        });

        sortExpDate.setOnClickListener(v -> {
            sortOptions.setVisibility(View.GONE);
            sortOrder.setText("유통기한순");
            Collections.sort(allItems, Comparator.comparing(FoodItem::getExpDate));
            adapter.updateList(allItems);
        });

        // 기본 탭 선택 및 목록 업데이트
        updateTabSelection(tabAll);
        adapter.updateList(allItems);

        // 추가 버튼 및 바코드 버튼 클릭 리스너 설정
        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        barcodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
        });

        // MyPage 버튼 클릭 리스너 설정
        ImageView myPageButton = findViewById(R.id.icon_mypage);
        myPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, MypageActivity.class);
            startActivity(intent);
        });

        // Calendar 버튼 클릭 리스너 설정
        ImageView calendarButton = findViewById(R.id.icon_calendar);
        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(FkmainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        Log.d("FkmainActivity", "onCreate 종료");
    }

    // 데이터 초기화 메서드
    private void populateData() {
        allItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "2024.06.21", "2024.07.10", "D-10"));
        allItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "2024.06.21", "2024.08.16", "D-37"));
        allItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "2024.05.17", "2024.10.01", "D-83"));
        allItems.add(new FoodItem(R.drawable.fk_fish, "옛날 붕어빵", "2024.04.11", "2024.08.22", "D-43"));
        allItems.add(new FoodItem(R.drawable.fk_frenchfries, "프렌치 프라이", "2024.06.28", "2024.07.28", "D-18"));
        allItems.add(new FoodItem(R.drawable.fk_chicken, "닭가슴살", "2024.06.21", "2024.08.21", "D-30"));

        frozenItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "2024.06.21", "2024.07.10", "D-10"));
        frozenItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "2024.06.21", "2024.08.16", "D-37"));
        frozenItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "2024.05.17", "2024.10.01", "D-83"));

        refrigeratedItems.add(new FoodItem(R.drawable.fk_fish, "옛날 붕어빵", "2024.04.11", "2024.08.22", "D-43"));
        refrigeratedItems.add(new FoodItem(R.drawable.fk_frenchfries, "프렌치 프라이", "2024.06.28", "2024.07.28", "D-18"));
        refrigeratedItems.add(new FoodItem(R.drawable.fk_chicken, "닭가슴살", "2024.06.21", "2024.08.21", "D-30"));

        roomTempItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "2024.06.21", "2024.07.10", "D-10"));
        roomTempItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "2024.06.21", "2024.08.16", "D-37"));
        roomTempItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "2024.05.17", "2024.10.01", "D-83"));
    }

    // 검색 필터 메서드
    private void filter(String text) {
        List<FoodItem> filteredList = new ArrayList<>();
        for (FoodItem item : allItems) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    // 탭 선택 업데이트 메서드
    private void updateTabSelection(Button selectedTab) {
        tabAll.setBackgroundResource(R.drawable.tab_button_background);
        tabFrozen.setBackgroundResource(R.drawable.tab_button_background);
        tabRefrigerated.setBackgroundResource(R.drawable.tab_button_background);
        tabRoomTemp.setBackgroundResource(R.drawable.tab_button_background);

        selectedTab.setBackgroundResource(R.drawable.tab_button_background_selected);
    }
}
