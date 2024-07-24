package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private ImageView plusButton;

    private List<FoodItem> allItems;
    private List<FoodItem> frozenItems;
    private List<FoodItem> refrigeratedItems;
    private List<FoodItem> roomTempItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fkmain);

        // 디버그 로그 추가
        Log.d("FkmainActivity", "onCreate 시작");

        // View 초기화
        searchBar = findViewById(R.id.search_bar);
        searchBar.setIconifiedByDefault(false); // 검색창 클릭으로 검색 가능하도록 설정
        searchBar.setQueryHint("식품을 검색하세요"); // 검색창에 힌트 추가

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
                return true; // true로 변경
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

        // 정렬 옵션 클릭 리스너 설정
        sortOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOptions.getVisibility() == View.GONE) {
                    sortOptions.setVisibility(View.VISIBLE);
                } else {
                    sortOptions.setVisibility(View.GONE);
                }
            }
        });

        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortOptions.setVisibility(View.GONE);
                sortOrder.setText("이름순");
                Collections.sort(allItems, new Comparator<FoodItem>() {
                    @Override
                    public int compare(FoodItem o1, FoodItem o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                adapter.updateList(allItems);
            }
        });

        sortRegDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortOptions.setVisibility(View.GONE);
                sortOrder.setText("등록순");
                Collections.sort(allItems, new Comparator<FoodItem>() {
                    @Override
                    public int compare(FoodItem o1, FoodItem o2) {
                        return o1.getRegDate().compareTo(o2.getRegDate());
                    }
                });
                adapter.updateList(allItems);
            }
        });

        sortExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortOptions.setVisibility(View.GONE);
                sortOrder.setText("유통기한순");
                Collections.sort(allItems, new Comparator<FoodItem>() {
                    @Override
                    public int compare(FoodItem o1, FoodItem o2) {
                        return o1.getExpDate().compareTo(o2.getExpDate());
                    }
                });
                adapter.updateList(allItems);
            }
        });

        // 기본 선택 탭 설정
        updateTabSelection(tabAll);
        adapter.updateList(allItems);

        // 플러스 버튼 클릭 리스너 설정
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FkmainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        // 디버그 로그 추가
        Log.d("FkmainActivity", "onCreate 종료");
    }

    private void populateData() {
        // 예제 데이터 추가
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
        tabAll.setBackgroundResource(R.drawable.tab_button_background);
        tabFrozen.setBackgroundResource(R.drawable.tab_button_background);
        tabRefrigerated.setBackgroundResource(R.drawable.tab_button_background);
        tabRoomTemp.setBackgroundResource(R.drawable.tab_button_background);

        selectedTab.setBackgroundResource(R.drawable.tab_button_background_selected);
    }
}
