package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.freshkeeper.database.DatabaseHelper;
import java.util.List;

public class InquiryHistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private InquiryHistoryAdapter adapter;
    private DatabaseHelper databaseHelper;
    private TextView emptyTextView; // 문의 내역이 없을 때 표시할 텍스트뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_history);

        // DatabaseHelper 초기화
        databaseHelper = new DatabaseHelper(this);

        // 뒤로가기 버튼 설정
        ImageView backButton = findViewById(R.id.back_button_history);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 문의 내역이 없을 때 표시할 텍스트뷰 설정
        emptyTextView = findViewById(R.id.empty_text_view);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.inquiry_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터베이스에서 문의 내역 조회
        List<InquiryItem> inquiryList = databaseHelper.getAllInquiries(); // List<InquiryItem>로 변경

        // 문의 내역이 있는지 확인하여 UI 업데이트
        if (inquiryList == null || inquiryList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE); // 문의 내역이 없을 때 텍스트뷰 표시
            recyclerView.setVisibility(View.GONE); // RecyclerView 숨기기
            emptyTextView.setText("문의 내역이 없습니다"); // 추가: 텍스트뷰에 메시지 설정
        } else {
            emptyTextView.setVisibility(View.GONE); // 문의 내역이 있을 때 텍스트뷰 숨기기
            recyclerView.setVisibility(View.VISIBLE); // RecyclerView 표시
        }

        // Adapter 설정
        adapter = new InquiryHistoryAdapter(inquiryList); // List<InquiryItem>를 받는 adapter 설정
        recyclerView.setAdapter(adapter);
    }
}
