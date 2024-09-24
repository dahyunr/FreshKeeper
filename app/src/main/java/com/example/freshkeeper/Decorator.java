package com.example.freshkeeper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class Decorator implements DayViewDecorator {

    private final Drawable drawable;
    private HashSet<CalendarDay> dates;

    public Decorator(Collection<CalendarDay> dates, Activity context) {
        this.drawable = context.getDrawable(R.drawable.calendar_background); // drawable 리소스 사용
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day); // 지정한 날짜에만 데코 적용
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable); // 선택된 날짜에 원 그리기
    }
}
