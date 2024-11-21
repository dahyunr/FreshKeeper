package com.example.freshkeeper;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class NotificationActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 추가
        getSupportActionBar().setTitle("알림");

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 알림 목록을 가져와 어댑터 설정
        notificationAdapter = new NotificationAdapter(this, getNotifications());
        recyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 뒤로가기 버튼 클릭 시
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 예시: 알림 목록 가져오기
    private List<NotificationItem> getNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();

        // 알림 항목 추가
        notifications.add(new NotificationItem("새 댓글", "사용자가 댓글을 남겼습니다.", "2024-11-20 10:00", "comment", "postId_12345"));
        notifications.add(new NotificationItem("게시물 좋아요", "게시물이 좋아요를 받았습니다.", "2024-11-20 11:00", "like", "postId_67890"));
        notifications.add(new NotificationItem("공지 테스트", "이것은 테스트 공지입니다.", "2024-11-20 12:00", "like", "postId_11223"));

        return notifications;
    }
}
