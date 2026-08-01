package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private LinearLayout attendanceButton;
    private LinearLayout examReportButton;
    private LinearLayout applyLeaveButton;
    private LinearLayout noticeButton;
    private LinearLayout ktFormFillingButton;
    private LinearLayout resultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_dashboard);

        // Initialize UI components
        welcomeText = findViewById(R.id.welcomeText);
        attendanceButton = findViewById(R.id.attendanceButton);
        examReportButton = findViewById(R.id.examReportButton);
        applyLeaveButton = findViewById(R.id.applyLeaveButton);
        noticeButton = findViewById(R.id.noticeButton);
        ktFormFillingButton = findViewById(R.id.ktFormFillingButton);
        resultButton = findViewById(R.id.resultButton);

        // Set title from strings.xml
        setTitle(R.string.app_name);

        // Get the student name from the previous activity
        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");

        if (studentName != null) {
            welcomeText.setText("Welcome, " + studentName + "!");
        } else {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
        }

        // Set click listeners for buttons
        attendanceButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, AttendanceActivity.class))
        );

        examReportButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, ExamReportActivity.class))
        );

        applyLeaveButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, LeaderboardActivity.class))
        );

        noticeButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, NoticeActivity.class))
        );

        ktFormFillingButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, KTFormFillingActivity.class))
        );

        resultButton.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, ResultActivity.class))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_notification) {
            // Handle notification icon click
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            // Start Settings Activity
            Intent intent = new Intent(StudentDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}