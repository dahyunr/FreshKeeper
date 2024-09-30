package com.example.freshkeeper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationSettingsActivity extends BaseActivity {

    private Spinner dateSpinner, timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // 뒤로 가기 버튼에 대한 클릭 리스너 설정
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // 액티비티를 종료하여 이전 화면으로 돌아감

        // 날짜 및 시간 스피너 초기화
        dateSpinner = findViewById(R.id.date_spinner);
        timeSpinner = findViewById(R.id.time_spinner);

        // 날짜 스피너 설정
        String[] dateOptions = {"당일", "1일 전", "2일 전", "3일 전", "4일 전", "5일 전"};
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dateOptions);
        dateSpinner.setAdapter(dateAdapter);

        // 시간 스피너 설정 (00:00부터 23:00까지 1시간 단위)
        String[] timeOptions = new String[24];
        for (int i = 0; i < 24; i++) {
            timeOptions[i] = String.format("%02d:00", i);
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeOptions);
        timeSpinner.setAdapter(timeAdapter);
    }
}
