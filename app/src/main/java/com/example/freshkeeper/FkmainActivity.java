package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FkmainActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button tabAll, tabFrozen, tabRefrigerated, tabRoomTemp;
    private ScrollView itemList;
    private ImageView iconRef, iconCalendar, iconBarcode, iconMypage;
    private ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fkmain);

        searchBar = findViewById(R.id.search_bar);
        tabAll = findViewById(R.id.tab_all);
        tabFrozen = findViewById(R.id.tab_frozen);
        tabRefrigerated = findViewById(R.id.tab_refrigerated);
        tabRoomTemp = findViewById(R.id.tab_room_temp);
        itemList = findViewById(R.id.itemList);
        iconRef = findViewById(R.id.icon_ref);
        iconCalendar = findViewById(R.id.icon_calendar);
        iconBarcode = findViewById(R.id.icon_barcode);
        iconMypage = findViewById(R.id.icon_mypage);
        fab = findViewById(R.id.fab);

        // 검색 바 클릭 이벤트
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString();
                Toast.makeText(FkmainActivity.this, "검색: " + query, Toast.LENGTH_SHORT).show();
                // 검색 로직을 여기에 추가
            }
        });

        // 탭 클릭 이벤트
        tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "전체 탭 선택됨", Toast.LENGTH_SHORT).show();
                // 전체 탭 로직을 여기에 추가
            }
        });

        tabFrozen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "냉동 탭 선택됨", Toast.LENGTH_SHORT).show();
                // 냉동 탭 로직을 여기에 추가
            }
        });

        tabRefrigerated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "냉장 탭 선택됨", Toast.LENGTH_SHORT).show();
                // 냉장 탭 로직을 여기에 추가
            }
        });

        tabRoomTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "상온 탭 선택됨", Toast.LENGTH_SHORT).show();
                // 상온 탭 로직을 여기에 추가
            }
        });

        // 아이콘 클릭 이벤트
        iconRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "냉장고 아이콘 클릭됨", Toast.LENGTH_SHORT).show();
                // 냉장고 아이콘 클릭 로직을 여기에 추가
            }
        });

        iconCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "캘린더 아이콘 클릭됨", Toast.LENGTH_SHORT).show();
                // 캘린더 아이콘 클릭 로직을 여기에 추가
            }
        });

        iconBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "바코드 아이콘 클릭됨", Toast.LENGTH_SHORT).show();
                // 바코드 아이콘 클릭 로직을 여기에 추가
            }
        });

        iconMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "마이페이지 아이콘 클릭됨", Toast.LENGTH_SHORT).show();
                // 마이페이지 아이콘 클릭 로직을 여기에 추가
            }
        });

        // 플로팅 액션 버튼 클릭 이벤트
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FkmainActivity.this, "플로팅 액션 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                // 플로팅 액션 버튼 클릭 로직을 여기에 추가
            }
        });
    }
}
