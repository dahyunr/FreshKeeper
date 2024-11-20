package com.example.freshkeeper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.example.freshkeeper.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends BaseActivity {

    private CompactCalendarView compactCalendarView;
    private TextView yearMonthTextView;
    private ImageView prevMonthArrow;
    private ImageView nextMonthArrow;
    private RecyclerView itemListRecyclerView;
    private Date selectedDate;
    private Date todayDate = new Date();
    private DatabaseHelper dbHelper;
    private Event todayEvent;
    private FoodItemAdapter adapter;
    private List<FoodItem> foodItems;

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
        prevMonthArrow = findViewById(R.id.previous_month_arrow);
        nextMonthArrow = findViewById(R.id.next_month_arrow);
        itemListRecyclerView = findViewById(R.id.item_list_recycler_view);

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

        // Set click listeners for arrows
        prevMonthArrow.setOnClickListener(v -> {
            compactCalendarView.scrollLeft();
            updateYearMonthText(compactCalendarView.getFirstDayOfCurrentMonth());
            Log.d(TAG, "onClick: Previous month displayed");
        });

        nextMonthArrow.setOnClickListener(v -> {
            compactCalendarView.scrollRight();
            updateYearMonthText(compactCalendarView.getFirstDayOfCurrentMonth());
            Log.d(TAG, "onClick: Next month displayed");
        });

        // Set the initial year and month text for the calendar
        updateYearMonthText(todayDate);
        Log.d(TAG, "onCreate: Year and month text updated");

        // 공통 하단 네비게이션 설정
        setupFooterNavigation();

        // Setup RecyclerView
        setupRecyclerView();

        // Load items for today's date initially
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String todayDateFormatted = dateFormat.format(todayDate);
        loadItemsForSelectedDate(todayDateFormatted);
    }

    // Method to update the year and month displayed above the calendar
    private void updateYearMonthText(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월", Locale.getDefault());
        yearMonthTextView.setText(dateFormat.format(date));
        Log.d(TAG, "updateYearMonthText: Updated year and month text to " + yearMonthTextView.getText());
    }

    // Method to load items for the selected date from the database
    private void loadItemsForSelectedDate(String selectedDate) {
        foodItems.clear(); // Clear any previous items

        Cursor cursor = dbHelper.getItemsByExpDate(selectedDate); // Fetch items for the selected date
        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String itemMemo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
                String itemExpDate = cursor.getString(cursor.getColumnIndexOrThrow("exp_date"));
                String itemRegDate = cursor.getString(cursor.getColumnIndexOrThrow("reg_date"));
                String itemImagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int storageMethod = cursor.getInt(cursor.getColumnIndexOrThrow("storage_method"));

                String countdown = calculateDDay(itemExpDate);
                foodItems.add(new FoodItem(R.drawable.fk_placeholder, itemName, itemRegDate, itemExpDate, countdown, itemMemo, itemImagePath, itemQuantity, storageMethod));

            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No items found for this date: " + selectedDate);
            Toast.makeText(this, "해당 날짜에 등록된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // Setup RecyclerView
    private void setupRecyclerView() {
        itemListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemListRecyclerView.setHasFixedSize(true);
        foodItems = new ArrayList<>();
        adapter = new FoodItemAdapter(this, foodItems);
        itemListRecyclerView.setAdapter(adapter);
    }

    // Method to calculate D-Day
    private String calculateDDay(String expDate) {
        SimpleDateFormat dateFormat8 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        try {
            Date expirationDate;
            if (expDate.length() == 8) {
                expirationDate = dateFormat8.parse(expDate);
            } else if (expDate.length() == 6) {
                expirationDate = dateFormat6.parse(expDate);
            } else {
                return "D-??";
            }

            Date today = new Date();
            long diffInMillis = expirationDate.getTime() - today.getTime();
            long daysLeft = diffInMillis / (1000 * 60 * 60 * 24);

            if (daysLeft == 0) {
                return "D-day";
            } else if (daysLeft > 0) {
                return "D-" + daysLeft;
            } else {
                return "D+" + Math.abs(daysLeft);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "D-??";
    }
}
