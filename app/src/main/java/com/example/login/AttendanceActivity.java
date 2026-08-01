package com.example.login;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.components.Legend;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "StudentPrefs";
    private static final String KEY_EMAIL = "email";

    private PieChart pieChart;
    private TextView tvEligibility;
    private TextView totalAttendance;
    private TextView attendancePercentage;
    private TextView attendanceResult;
    private RecyclerView rvSubjects;
    private SubjectAttendanceAdapter adapter; // Declare the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Initialize views
        pieChart = findViewById(R.id.pieChart);
        tvEligibility = findViewById(R.id.tvEligibility);
        totalAttendance = findViewById(R.id.totalAttendance);
        attendancePercentage = findViewById(R.id.attendancePercentage);
        attendanceResult = findViewById(R.id.attendanceResult);
        rvSubjects = findViewById(R.id.rvSubjects);

        // Setup RecyclerView
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(KEY_EMAIL, null);

        if (email == null) {
            setTextViewText(attendanceResult, "Error: No user email found.");
            return;
        }

        // Retrieve student attendance data
        List<SubjectAttendance> subjects = getAttendanceForStudent(email);

        if (subjects.isEmpty()) {
            setTextViewText(attendanceResult, "No attendance records found for the student.");
            return;
        }

        // Setup Pie Chart and Eligibility message
        int totalAttendanceCount = populateAttendanceInfo(subjects);
        setupPieChart(subjects, totalAttendanceCount);
        setupEligibilityMessage(subjects);

        // Set up the adapter for the RecyclerView
        adapter = new SubjectAttendanceAdapter(subjects);
        rvSubjects.setAdapter(adapter);
    }

    private List<SubjectAttendance> getAttendanceForStudent(String email) {
        // Dummy data fetching logic
        if (email.equals("devanshi@gmail.com")) {
            return getDevanshiAttendance();
        } else if (email.equals("sanghaviviranshi@gmail.com")) {
            return getViranshiAttendance();
        } else {
            return new ArrayList<>(); // Return empty if no attendance found
        }
    }

    private int populateAttendanceInfo(List<SubjectAttendance> subjects) {
        int totalAttendanceCount = 0;
        final int TOTAL_CLASSES = 100; // Example total classes for calculations

        for (SubjectAttendance subject : subjects) {
            totalAttendanceCount += subject.getAttendance();
        }

        // Display total attendance
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedAttendance = df.format((double) totalAttendanceCount / (TOTAL_CLASSES * subjects.size()) * 100);
        setTextViewText(totalAttendance, "Total Attendance: " + totalAttendanceCount + "/" + (TOTAL_CLASSES * subjects.size()));
        setTextViewText(attendancePercentage, "Attendance Percentage: " + formattedAttendance + "%");

        // Return the total attendance count for use in the pie chart center text
        return totalAttendanceCount; // Ensure to update method signature if necessary
    }

    private void setupPieChart(List<SubjectAttendance> subjects, int totalAttendanceCount) {
        List<PieEntry> entries = new ArrayList<>();
        for (SubjectAttendance subject : subjects) {
            entries.add(new PieEntry(subject.getAttendance(), subject.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Attendance");
        dataSet.setColors(new int[]{
                Color.rgb(76, 175, 80),   // Green
                Color.rgb(244, 67, 54),   // Red
                Color.rgb(255, 192, 203), // Pink
                Color.rgb(179, 143, 185), // Purple
                Color.rgb(75, 0, 130)     // Indigo
        });

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);

        // Calculate total attendance percentage
        final int TOTAL_CLASSES = 100; // Example total classes for percentage calculation
        double totalAttendancePercentage = ((double) totalAttendanceCount / (TOTAL_CLASSES * subjects.size())) * 100;

        // Set the total attendance percentage in the center text
        pieChart.setCenterText(String.format("Attendance\n%.2f%%", totalAttendancePercentage));
        pieChart.setCenterTextSize(16f); // Increased text size for better visibility
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.animateY(1000);
        pieChart.invalidate();

        // Increase the size of the pie chart
        ViewGroup.LayoutParams params = pieChart.getLayoutParams();
        params.height = (int) (300 * getResources().getDisplayMetrics().density); // Adjust height as needed
        params.width = (int) (300 * getResources().getDisplayMetrics().density);  // Adjust width as needed
        pieChart.setLayoutParams(params);

        // Setup legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);  // Allow word wrap in case subjects don't fit in one line
        legend.setDrawInside(false);
        legend.setTextSize(8f);
        legend.setTextColor(Color.WHITE);
    }

    private void setupEligibilityMessage(List<SubjectAttendance> subjects) {
        boolean isEligible = true;
        StringBuilder message = new StringBuilder("Eligibility Status:\n");

        for (SubjectAttendance subject : subjects) {
            if (subject.getAttendance() < 75) {
                isEligible = false; // Mark as not eligible if any subject has attendance < 75%
                message.append("- ").append(subject.getName()).append(" (").append(subject.getAttendance()).append("%)\n");
            }
        }

        message.append(isEligible ? "Eligible for examination." : "Not eligible for examination.");
        tvEligibility.setText(message.toString());
        tvEligibility.setTextColor(isEligible ? Color.rgb(76, 175, 80) : Color.rgb(244, 67, 54));
    }

    private void setTextViewText(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text != null ? text : "N/A");
        }
    }

    private List<SubjectAttendance> getDevanshiAttendance() {
        List<SubjectAttendance> subjects = new ArrayList<>();
        subjects.add(new SubjectAttendance("Enterprise Java", 80));
        subjects.add(new SubjectAttendance("Software Project Management", 75));
        subjects.add(new SubjectAttendance("Artificial Intelligence", 90));
        subjects.add(new SubjectAttendance("Advanced Web Programming", 82));
        subjects.add(new SubjectAttendance("Internet of Things", 85));
        return subjects;
    }

    private List<SubjectAttendance> getViranshiAttendance() {
        List<SubjectAttendance> subjects = new ArrayList<>();
        subjects.add(new SubjectAttendance("Software Project Management", 70));
        subjects.add(new SubjectAttendance("Artificial Intelligence", 85));
        subjects.add(new SubjectAttendance("Business Tableau/Power BI", 75));
        subjects.add(new SubjectAttendance("Big Data Analytics using Hadoop", 60));
        subjects.add(new SubjectAttendance("Business Ethics and Environmental Science", 80));
        return subjects;
    }

    private static class SubjectAttendance {
        private final String name;
        private final int attendance;

        SubjectAttendance(String name, int attendance) {
            this.name = name;
            this.attendance = attendance;
        }

        String getName() {
            return name;
        }

        int getAttendance() {
            return attendance;
        }
    }

    // Adapter for RecyclerView
    private static class SubjectAttendanceAdapter extends RecyclerView.Adapter<SubjectAttendanceAdapter.ViewHolder> {
        private final List<SubjectAttendance> subjects;

        SubjectAttendanceAdapter(List<SubjectAttendance> subjects) {
            this.subjects = subjects;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_attendance, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SubjectAttendance subject = subjects.get(position);
            holder.subjectName.setText(subject.getName());
            holder.attendance.setText(subject.getAttendance() + "%");
        }

        @Override
        public int getItemCount() {
            return subjects.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subjectName;
            TextView attendance;

            ViewHolder(View itemView) {
                super(itemView);
                subjectName = itemView.findViewById(R.id.tvSubject); // ID for subject name in item_subject_attendance.xml
                attendance = itemView.findViewById(R.id.tvAttendance); // ID for attendance in item_subject_attendance.xml
            }
        }
    }
}
