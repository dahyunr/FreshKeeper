package com.example.freshkeeper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import android.widget.Button;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private ArrayList<CalendarDay> calendarDayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // MaterialCalendarView 설정
        calendarView = findViewById(R.id.calendarView);

        if (calendarView != null) {
            // Decorator 설정할 날짜 추가
            calendarDayList.add(CalendarDay.from(2022, 5, 25));
            calendarDayList.add(CalendarDay.from(2022, 5, 24));
            calendarDayList.add(CalendarDay.from(2022, 5, 23));

            // 오늘 날짜 선택
            calendarView.setSelectedDate(CalendarDay.today());

            // Decorator 추가
            Decorator decorator = new Decorator(calendarDayList, this);
            calendarView.addDecorator(decorator);
        } else {
            // calendarView가 null인 경우 로그 출력
            System.out.println("calendarView가 null입니다.");
        }

        // 날짜 선택 버튼 설정
        Button dateButton = findViewById(R.id.date_button);
        if (dateButton != null) {
            dateButton.setOnClickListener(v -> {
                // 날짜 선택기 생성 및 표시
                MaterialDatePicker<Long> datePicker =
                        MaterialDatePicker.Builder.datePicker()
                                .setTitleText("날짜 선택")
                                .build();

                datePicker.show(getSupportFragmentManager(), "date_picker");

                // 선택된 날짜를 처리하는 리스너 추가
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    // 선택된 날짜를 처리하는 코드
                    long selectedDate = selection;
                    // 선택된 날짜 활용 가능 (필요한 작업 추가 가능)
                });
            });
        }
    }
}
