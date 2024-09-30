package com.example.freshkeeper;

import android.graphics.Color;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.util.Date;

public class SelectedDateDecorator implements CompactCalendarView.CompactCalendarViewListener {

    private Date selectedDate;
    private Date todayDate;
    private CompactCalendarView calendarView;

    public SelectedDateDecorator(CompactCalendarView calendarView, Date todayDate) {
        this.todayDate = todayDate; // 오늘 날짜
        this.calendarView = calendarView; // CompactCalendarView 인스턴스
    }

    @Override
    public void onDayClick(Date dateClicked) {
        // 선택한 날짜가 오늘일 경우 회색으로 표시
        if (dateClicked.equals(todayDate)) {
            Event todayEvent = new Event(Color.parseColor("#B0B0B0"), dateClicked.getTime(), "Today");
            calendarView.removeAllEvents(); // 기존 이벤트 모두 제거
            calendarView.addEvent(todayEvent); // 오늘 날짜 이벤트 추가
        } else {
            // 선택한 날짜가 오늘이 아닐 경우 녹색으로 표시
            Event selectedEvent = new Event(Color.parseColor("#4CAF50"), dateClicked.getTime(), "Selected Date");
            calendarView.removeAllEvents(); // 기존 이벤트 모두 제거
            calendarView.addEvent(selectedEvent); // 선택된 날짜 이벤트 추가
            // 오늘 날짜도 다시 추가하여 회색으로 유지
            Event todayEvent = new Event(Color.parseColor("#B0B0B0"), todayDate.getTime(), "Today");
            calendarView.addEvent(todayEvent);
        }
        calendarView.invalidate(); // 화면 갱신
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        // 월 스크롤 시 처리할 작업
    }
}
