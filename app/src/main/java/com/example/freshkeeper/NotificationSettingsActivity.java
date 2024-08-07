package com.example.freshkeeper;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // 뒤로 가기 버튼에 대한 클릭 리스너 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // 액티비티를 종료하여 이전 화면으로 돌아감

        // 초기화 및 필요한 로직 추가 (미완성!!
    }
}
