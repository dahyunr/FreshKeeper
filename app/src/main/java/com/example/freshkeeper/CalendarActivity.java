package com.example.freshkeeper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import android.widget.Button;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 날짜 선택 버튼 설정
        Button dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(v -> {
            // 날짜 선택기 생성 및 표시
            MaterialDatePicker<Long> datePicker =
                    MaterialDatePicker.Builder.datePicker()
                            .setTitleText("날짜 선택")
                            .build();

            datePicker.show(getSupportFragmentManager(), "date_picker");

            // 선택된 날짜를 처리하는 리스너 추가
            datePicker.addOnPositiveButtonClickListener(selection -> {
                // 선택된 날짜를 처리하는 코드 (예: 선택된 날짜를 텍스트로 표시)
                long selectedDate = selection;
                // 여기서 selectedDate를 활용할 수 있습니다.
            });
        });
    }
}
