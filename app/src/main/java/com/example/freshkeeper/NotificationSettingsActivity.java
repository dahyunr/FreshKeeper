package com.example.freshkeeper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

public class NotificationSettingsActivity extends BaseActivity {

    private Spinner dateSpinner, timeSpinner;
    private Switch expirationAlertSwitch;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        expirationAlertSwitch = findViewById(R.id.expiration_alert_switch);
        dateSpinner = findViewById(R.id.date_spinner);
        timeSpinner = findViewById(R.id.time_spinner);
        saveButton = findViewById(R.id.save_button);

        String[] dateOptions = {"당일", "1일 전", "2일 전", "3일 전", "4일 전", "5일 전"};
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dateOptions);
        dateSpinner.setAdapter(dateAdapter);

        String[] timeOptions = new String[24];
        for (int i = 0; i < 24; i++) {
            timeOptions[i] = String.format("%02d:00", i);
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeOptions);
        timeSpinner.setAdapter(timeAdapter);

        loadPreferences();

        expirationAlertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "알림이 설정되었습니다. 저장 버튼을 눌러 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (expirationAlertSwitch.isChecked()) {
                scheduleNotification();
                savePreferences();
                Toast.makeText(this, "알림이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                cancelNotification();
                Toast.makeText(this, "알림이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        int daysBefore = dateSpinner.getSelectedItemPosition();
        String message;

        switch (daysBefore) {
            case 0:
                message = "등록된 상품의 유통기한이 오늘입니다.";
                break;
            case 1:
                message = "등록된 상품의 유통기한이 1일 남았습니다.";
                break;
            case 2:
                message = "등록된 상품의 유통기한이 2일 남았습니다.";
                break;
            default:
                message = "등록된 상품의 유통기한이 다가오고 있습니다.";
                break;
        }

        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // 오늘 기준 설정된 일 수 전으로 시간을 설정
        calendar.add(Calendar.DATE, -daysBefore);

        // 사용자 설정 시간 적용
        int hourOfDay = timeSpinner.getSelectedItemPosition();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 알림 시간이 현재보다 이전인지 확인하여 다음 날로 조정
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        // 설정된 시간에 알림이 오도록 정확히 예약
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void savePreferences() {
        SharedPreferences preferences = getSharedPreferences("notificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isNotificationEnabled", expirationAlertSwitch.isChecked());
        editor.putInt("dateSpinnerPosition", dateSpinner.getSelectedItemPosition());
        editor.putInt("timeSpinnerPosition", timeSpinner.getSelectedItemPosition());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences("notificationPrefs", MODE_PRIVATE);
        boolean isNotificationEnabled = preferences.getBoolean("isNotificationEnabled", false);
        int datePosition = preferences.getInt("dateSpinnerPosition", 0);
        int timePosition = preferences.getInt("timeSpinnerPosition", 0);

        expirationAlertSwitch.setChecked(isNotificationEnabled);
        dateSpinner.setSelection(datePosition);
        timeSpinner.setSelection(timePosition);
    }
}