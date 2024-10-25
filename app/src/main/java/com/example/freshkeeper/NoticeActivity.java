package com.example.freshkeeper;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class NoticeActivity extends AppCompatActivity {

    private ListView noticeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        noticeListView = findViewById(R.id.notice_list);

        // 공지사항 목록 데이터
        String[] notices = {
                "앱 업데이트 공지: 최신 버전 1.1.0 출시 (2024.09.25)",
                "시스템 점검 안내: 2024년 10월 5일 01:00 ~ 03:00",
                "앱 사용 꿀팁: 유통기한 관리를 더 효율적으로 하는 방법!",
                "새로운 기능 추가: 유통기한 알림을 설정하여 알림을 받아보세요 (2024.10.10)",
                "사용자 피드백 요청: 앱 개선을 위한 설문 조사 참여 부탁드립니다 (2024.10.15)",
                "유통기한 등록 방법 안내: 바코드 스캔을 통해 쉽게 등록해보세요!",
                "정기 점검 안내: 2024년 11월 27일 서버 점검 예정 (2024.11.01)",
                "앱 사용 가이드 업데이트: 새로 추가된 기능들을 확인해보세요 (2024.10.20)"
        };

        // ArrayAdapter를 사용해 ListView에 데이터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                notices
        );

        noticeListView.setAdapter(adapter);
    }

    public void onBackButtonClicked(View view) {
        finish(); // 현재 액티비티를 종료하여 이전 화면(MypageActivity)으로 돌아감
    }
}
