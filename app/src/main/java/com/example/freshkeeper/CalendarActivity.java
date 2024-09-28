package com.example.freshkeeper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
    private CompactCalendarView compactCalendarView;
    private List<Date> calendarDayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // CompactCalendarView 초기화
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true); // 세 자리 약어 설정

        // 날짜를 추가하고 이벤트를 추가하는 예시
        calendarDayList.add(new Date(2022 - 1900, 4, 25)); // May 25, 2022
        calendarDayList.add(new Date(2022 - 1900, 4, 24)); // May 24, 2022
        calendarDayList.add(new Date(2022 - 1900, 4, 23)); // May 23, 2022

        // 이벤트 추가
        for (Date date : calendarDayList) {
            Event event = new Event(0xFF0000, date.getTime(), "Test Event");
            compactCalendarView.addEvent(event);
        }

        // 오늘 날짜로 설정
        compactCalendarView.setCurrentDate(new Date());

        // 날짜 선택 버튼 설정
        Button dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(v -> {
            // 버튼 클릭 시 처리
            Log.d(TAG, "날짜 선택 버튼 클릭됨");
        });
    }
}
