package com.example.freshkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // 로그 사용을 위한 import 추가
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarActivity extends BaseActivity {

    private CompactCalendarView compactCalendarView;
    private TextView yearMonthTextView;
    private Date selectedDate;
    private Date todayDate = new Date(); // 오늘 날짜 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        compactCalendarView = findViewById(R.id.compactcalendar_view);
        yearMonthTextView = findViewById(R.id.year_month_text_view);
        Button dateButton = findViewById(R.id.date_button);

        // CompactCalendarView 설정
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setCurrentDate(todayDate);

        // 오늘 날짜 회색으로 표시
        Event todayEvent = new Event(ContextCompat.getColor(this, R.color.gray), todayDate.getTime(), "Today");
        compactCalendarView.addEvent(todayEvent); // 오늘 날짜 이벤트 추가

        // 날짜 클릭 리스너
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;

                // 로그 추가: 선택된 날짜와 오늘 날짜 확인
                Log.d("CalendarActivity", "Selected Date: " + selectedDate);
                Log.d("CalendarActivity", "Today Date: " + todayDate);

                // 선택된 날짜 녹색 강조, 오늘 날짜는 회색 유지
                compactCalendarView.removeAllEvents(); // 기존 이벤트 초기화
                compactCalendarView.addEvent(new Event(ContextCompat.getColor(CalendarActivity.this, R.color.tab_selected), selectedDate.getTime(), "Selected Date"));
                compactCalendarView.addEvent(todayEvent); // 오늘 날짜 회색으로 유지

                compactCalendarView.invalidate(); // 화면 갱신
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateYearMonthText(firstDayOfNewMonth);
            }
        });

        // 년/월 텍스트 업데이트
        updateYearMonthText(todayDate);

        // 하단바 아이콘 클릭 리스너 설정
        setupFooterNavigation();
    }

    // 년도와 월 텍스트 업데이트 (변경된 형식 적용)
    private void updateYearMonthText(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월", Locale.getDefault());
        yearMonthTextView.setText(dateFormat.format(date));
    }

    // 하단 네비게이션 아이콘 설정
    private void setupFooterNavigation() {
        ImageView iconFridge = findViewById(R.id.icon_ref);
        iconFridge.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, FkmainActivity.class);
            startActivity(intent);
        });

        ImageView iconBarcode = findViewById(R.id.icon_barcode);
        iconBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, BarcodeScanActivity.class);
            startActivity(intent);
        });

        ImageView iconMypage = findViewById(R.id.icon_mypage);
        iconMypage.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MypageActivity.class);
            startActivity(intent);
        });
    }
}
