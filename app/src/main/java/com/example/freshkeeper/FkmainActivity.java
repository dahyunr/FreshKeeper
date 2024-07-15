package com.example.freshkeeper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

public class FkmainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private SearchView searchBar;
    private Button tabAll, tabFrozen, tabRefrigerated, tabRoomTemp;

    private List<FoodItem> allItems;
    private List<FoodItem> frozenItems;
    private List<FoodItem> refrigeratedItems;
    private List<FoodItem> roomTempItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fkmain);

        // View 초기화
        searchBar = findViewById(R.id.search_bar);
        tabAll = findViewById(R.id.tab_all);
        tabFrozen = findViewById(R.id.tab_frozen);
        tabRefrigerated = findViewById(R.id.tab_refrigerated);
        tabRoomTemp = findViewById(R.id.tab_room_temp);
        recyclerView = findViewById(R.id.recycler_view);

        // 아이템 리스트 생성
        allItems = new ArrayList<>();
        frozenItems = new ArrayList<>();
        refrigeratedItems = new ArrayList<>();
        roomTempItems = new ArrayList<>();

        // 데이터 추가
        populateData();

        // RecyclerView 설정
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodItemAdapter(allItems);
        recyclerView.setAdapter(adapter);

        // 검색 기능 설정
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        // 탭 클릭 리스너 설정
        tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTabSelection(tabAll);
                adapter.updateList(allItems);
            }
        });

        tabFrozen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTabSelection(tabFrozen);
                adapter.updateList(frozenItems);
            }
        });

        tabRefrigerated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTabSelection(tabRefrigerated);
                adapter.updateList(refrigeratedItems);
            }
        });

        tabRoomTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTabSelection(tabRoomTemp);
                adapter.updateList(roomTempItems);
            }
        });

        // 기본 선택 탭 설정
        updateTabSelection(tabAll);
        adapter.updateList(allItems);
    }

    private void populateData() {
        // 예제 데이터 추가
        allItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "등록일: 2024.06.21", "유통기한: 2024.07.10", "D-10"));
        allItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "등록일: 2024.06.21", "유통기한: 2024.08.16", "D-37"));
        allItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "등록일: 2024.05.17", "유통기한: 2024.10.01", "D-83"));
        allItems.add(new FoodItem(R.drawable.fk_fish, "옛날 붕어빵", "등록일: 2024.04.11", "유통기한: 2024.08.22", "D-43"));
        allItems.add(new FoodItem(R.drawable.fk_frenchfries, "프렌치 프라이", "등록일: 2024.06.28", "유통기한: 2024.07.28", "D-18"));
        allItems.add(new FoodItem(R.drawable.fk_chicken, "닭가슴살", "등록일: 2024.06.21", "유통기한: 2024.08.21", "D-30"));

        frozenItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "등록일: 2024.06.21", "유통기한: 2024.07.10", "D-10"));
        frozenItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "등록일: 2024.06.21", "유통기한: 2024.08.16", "D-37"));
        frozenItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "등록일: 2024.05.17", "유통기한: 2024.10.01", "D-83"));

        refrigeratedItems.add(new FoodItem(R.drawable.fk_fish, "옛날 붕어빵", "등록일: 2024.04.11", "유통기한: 2024.08.22", "D-43"));
        refrigeratedItems.add(new FoodItem(R.drawable.fk_frenchfries, "프렌치 프라이", "등록일: 2024.06.28", "유통기한: 2024.07.28", "D-18"));
        refrigeratedItems.add(new FoodItem(R.drawable.fk_chicken, "닭가슴살", "등록일: 2024.06.21", "유통기한: 2024.08.21", "D-30"));

        roomTempItems.add(new FoodItem(R.drawable.fk_bibigo, "비비고 만두", "등록일: 2024.06.21", "유통기한: 2024.07.10", "D-10"));
        roomTempItems.add(new FoodItem(R.drawable.fk_hotdog, "크리스피 핫도그", "등록일: 2024.06.21", "유통기한: 2024.08.16", "D-37"));
        roomTempItems.add(new FoodItem(R.drawable.fk_blueberry, "냉동 블루베리", "등록일: 2024.05.17", "유통기한: 2024.10.01", "D-83"));
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

    private void updateTabSelection(Button selectedTab) {
        tabAll.setTextColor(getResources().getColor(R.color.tab_unselected));
        tabFrozen.setTextColor(getResources().getColor(R.color.tab_unselected));
        tabRefrigerated.setTextColor(getResources().getColor(R.color.tab_unselected));
        tabRoomTemp.setTextColor(getResources().getColor(R.color.tab_unselected));

        selectedTab.setTextColor(getResources().getColor(R.color.tab_selected));
    }
}
