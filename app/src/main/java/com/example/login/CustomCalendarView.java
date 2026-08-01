package com.example.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.CalendarView;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class CustomCalendarView extends CalendarView {
    private Map<String, Integer> examDates;
    private Paint circlePaint;
    private Calendar calendar;
    private int currentMonth;
    private int currentYear;

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        examDates = new HashMap<>();
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        calendar = Calendar.getInstance();

        // Set initial date to January 2025
        calendar.set(2025, Calendar.JANUARY, 1);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        // Listen for month changes
        setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentMonth = month;
            currentYear = year;
            invalidate();
        });
    }

    public void addExamDate(String date, int color) {
        examDates.put(date, color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float cellWidth = width / 7f;
        float cellHeight = (height - getPaddingTop() - getPaddingBottom()) / 6f;

        for (Map.Entry<String, Integer> entry : examDates.entrySet()) {
            String[] dateParts = entry.getKey().split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-based
            int year = Integer.parseInt(dateParts[2]);

            if (month == currentMonth && year == currentYear) {
                calendar.set(year, month, day);
                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH) - 1;
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                float centerX = dayOfWeek * cellWidth + cellWidth / 2;
                float centerY = weekOfMonth * cellHeight + cellHeight / 2 + getPaddingTop() + cellHeight;

                circlePaint.setColor(entry.getValue());
                circlePaint.setAlpha(100); // Semi-transparent

                float radius = Math.min(cellWidth, cellHeight) / 3;
                canvas.drawCircle(centerX, centerY, radius, circlePaint);
            }
        }
    }
}
