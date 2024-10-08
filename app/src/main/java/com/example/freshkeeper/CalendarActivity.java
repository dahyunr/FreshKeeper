package com.example.freshkeeper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.example.freshkeeper.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends BaseActivity {

    private CompactCalendarView compactCalendarView;
    private TextView yearMonthTextView;
    private LinearLayout itemListLayout;
    private Date selectedDate;
    private Date todayDate = new Date();
    private DatabaseHelper dbHelper;
    private Event todayEvent;

    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Log.d(TAG, "onCreate: Activity created");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Find views by ID
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        yearMonthTextView = findViewById(R.id.year_month_text_view);
        itemListLayout = findViewById(R.id.item_list_layout);
        Button dateButton = findViewById(R.id.date_button);

        // Set current date on CompactCalendarView
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setCurrentDate(todayDate);

        Log.d(TAG, "onCreate: CompactCalendarView set with today's date");

        // Set today's date with gray color
        todayEvent = new Event(Color.GRAY, todayDate.getTime(), "Today");
        compactCalendarView.addEvent(todayEvent);
        Log.d(TAG, "onCreate: Today event added with gray color");

        // Set listener for date clicks on the CompactCalendarView
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String clickedDate = dateFormat.format(selectedDate);

                Log.d(TAG, "onDayClick: Selected date: " + selectedDate);
                Log.d(TAG, "onDayClick: Formatted clicked date: " + clickedDate);

                // Load items for the selected date
                loadItemsForSelectedDate(clickedDate);

                // Clear all previous events, add today and selected date events again
                compactCalendarView.removeAllEvents();
                Log.d(TAG, "onDayClick: All previous events removed");

                // Re-add today event with gray color
                compactCalendarView.addEvent(todayEvent);

                // Add selected date event with green color for testing
                compactCalendarView.addEvent(new Event(Color.GREEN, selectedDate.getTime(), "Selected Date"));
                Log.d(TAG, "onDayClick: Selected date event added with green color");

                // Refresh the calendar view on the UI thread
                runOnUiThread(() -> {
                    compactCalendarView.invalidate();
                    compactCalendarView.postInvalidateOnAnimation();  // 추가적인 화면 갱신
                    Log.d(TAG, "onDayClick: Calendar view invalidated for refresh");
                });
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "onMonthScroll: Scrolled to new month: " + firstDayOfNewMonth);
                updateYearMonthText(firstDayOfNewMonth);
            }
        });

        // Set the initial year and month text for the calendar
        updateYearMonthText(todayDate);
        Log.d(TAG, "onCreate: Year and month text updated");

        // Setup the footer navigation
        setupFooterNavigation();
    }

    // Method to update the year and month displayed above the calendar
    private void updateYearMonthText(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월", Locale.getDefault());
        yearMonthTextView.setText(dateFormat.format(date));
        Log.d(TAG, "updateYearMonthText: Updated year and month text to " + yearMonthTextView.getText());
    }

    // Method to load items for the selected date from the database
    private void loadItemsForSelectedDate(String selectedDate) {
        itemListLayout.removeAllViews(); // Clear any previous items

        Cursor cursor = dbHelper.getItemsByExpDate(selectedDate); // Fetch items for the selected date
        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String itemMemo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));

                // Create a new TextView for each item
                TextView itemTextView = new TextView(this);
                itemTextView.setText(itemName + ": " + itemMemo);
                itemListLayout.addView(itemTextView); // Add the TextView to the layout

            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No items found for this date: " + selectedDate);
            Toast.makeText(this, "해당 날짜에 등록된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    // Setup footer navigation buttons
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
