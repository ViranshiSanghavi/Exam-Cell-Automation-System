package com.example.login;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExamReportActivity extends AppCompatActivity {
    private CalendarView examCalendarView;
    private RecyclerView recyclerViewExamSchedule;
    private ExamScheduleAdapter adapter;
    private List<ExamScheduleItem> examList;
    private HashMap<String, Integer> examColors;
    private int currentMonth, currentYear;

    private TextView textViewNoExams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_report);

        initializeViews();
        setupRecyclerView();
        loadExamData();
        setupCalendarListener();
        addLegend();
    }

    private void initializeViews() {
        examCalendarView = findViewById(R.id.examCalendarView);
        recyclerViewExamSchedule = findViewById(R.id.recyclerViewExamSchedule);
        textViewNoExams = findViewById(R.id.textViewNoExams);
        examColors = new HashMap<>();

        Calendar today = Calendar.getInstance();
        currentMonth = today.get(Calendar.MONTH);
        currentYear = today.get(Calendar.YEAR);

        examCalendarView.setDate(today.getTimeInMillis(), true, true);
    }

    private void setupRecyclerView() {
        recyclerViewExamSchedule.setLayoutManager(new LinearLayoutManager(this));
        examList = new ArrayList<>();
        adapter = new ExamScheduleAdapter(examList);
        recyclerViewExamSchedule.setAdapter(adapter);
    }

    private void loadExamData() {
        examColors.put("25-03-2025", Color.parseColor("#FFE0E0"));
        examColors.put("28-03-2025", Color.parseColor("#E0FFE0"));

        loadExamScheduleForMonth(currentMonth, currentYear);
    }

    private void setupCalendarListener() {
        examCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (month == currentMonth && year == currentYear) {
                    updateExamDisplay(dayOfMonth);
                }
            }
        });
    }

    private void loadExamScheduleForMonth(int month, int year) {
        examList.clear();

        if (month == Calendar.MARCH && year == 2025) {
            examList.add(new ExamScheduleItem("Artificial Intelligence", "25-03-2025", "10:00 AM - 12:00 PM"));
            examList.add(new ExamScheduleItem("Linux System Adminstration", "25-03-2025", "2:00 PM - 4:00 PM"));
            examList.add(new ExamScheduleItem("Software Engineering", "28-03-2025", "9:00 AM - 11:00 AM"));
        }

        adapter.notifyDataSetChanged();
        showOrHideNoExams();
    }

    private void updateExamDisplay(int selectedDay) {
        List<ExamScheduleItem> selectedExams = new ArrayList<>();
        String selectedDate = String.format("%02d-03-2025", selectedDay);

        for (ExamScheduleItem item : examList) {
            if (item.getDate().equals(selectedDate)) {
                selectedExams.add(item);
            }
        }

        adapter.updateExamList(selectedExams);
        if (selectedExams.isEmpty()) {
            textViewNoExams.setVisibility(View.VISIBLE);
            recyclerViewExamSchedule.setVisibility(View.GONE);
        } else {
            textViewNoExams.setVisibility(View.GONE);
            recyclerViewExamSchedule.setVisibility(View.VISIBLE);
        }
    }
    private void showOrHideNoExams() {
        if (examList.isEmpty()) {
            textViewNoExams.setVisibility(View.VISIBLE);
            recyclerViewExamSchedule.setVisibility(View.GONE);
        } else {
            textViewNoExams.setVisibility(View.GONE);
            recyclerViewExamSchedule.setVisibility(View.VISIBLE);
        }
    }

    private void addLegend() {
        LinearLayout legendLayout = findViewById(R.id.legendLayout);
        legendLayout.removeAllViews(); // Clear any existing views

        addLegendItem(legendLayout, Color.parseColor("#FFE0E0"), "AI/LSA");
        addLegendItem(legendLayout, Color.parseColor("#E0FFE0"), "SE");
    }

    private void addLegendItem(LinearLayout parent, int color, String text) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        itemLayout.setPadding(8, 0, 16, 0);

        // Create a small color indicator circle
        View colorIndicator = new View(this);
        int size = 40; // Adjust size if needed
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(size, size);
        colorIndicator.setLayoutParams(circleParams);
        colorIndicator.setBackgroundColor(color);

        // Create a text label
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(Color.BLACK);
        label.setPadding(8, 0, 0, 0);

        itemLayout.addView(colorIndicator);
        itemLayout.addView(label);
        parent.addView(itemLayout);
    }
}

