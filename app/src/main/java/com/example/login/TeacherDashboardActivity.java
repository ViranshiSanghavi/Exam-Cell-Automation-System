package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private LinearLayout attendanceUploadButton;
    private LinearLayout marksUploadButton;
    private LinearLayout questionPaperUploadButton;
    private LinearLayout noticeButton;
    private LinearLayout answerPaperRecheckingButton;
    private LinearLayout profileSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Initialize the welcome text view
        welcomeText = findViewById(R.id.welcomeText);

        // Get the teacher's name from the intent
        Intent intent = getIntent();
        String teacherName = intent.getStringExtra("teacherName");

        if (teacherName != null) {
            // Display the teacher's name at the top of the dashboard
            welcomeText.setText("Welcome, " + teacherName + "!");
        } else {
            // If no teacher found, show an error message
            Toast.makeText(this, "Teacher not found", Toast.LENGTH_SHORT).show();
        }

        // Initialize buttons
        attendanceUploadButton = findViewById(R.id.attendanceUploadButton);
        marksUploadButton = findViewById(R.id.marksUploadButton);
        questionPaperUploadButton = findViewById(R.id.questionPaperUploadButton);
        noticeButton = findViewById(R.id.noticeButton);
        answerPaperRecheckingButton = findViewById(R.id.answerPaperRecheckingButton);
        profileSettingsButton = findViewById(R.id.profileSettingsButton);

        // Set click listeners for each button
        attendanceUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Attendance Upload Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, AttendanceUploadActivity.class);
                startActivity(intent);
            }
        });

        marksUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Marks Upload Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, MarksUploadActivity.class);
                startActivity(intent);
            }
        });

        questionPaperUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Question Paper Upload Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, QuestionPaperUploadActivity.class);
                startActivity(intent);
            }
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Notice Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });

        answerPaperRecheckingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Answer Paper Rechecking Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, AnswerPaperRecheckingActivity.class);
                startActivity(intent);
            }
        });

        profileSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Profile Settings Activity
                Intent intent = new Intent(TeacherDashboardActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu to add notification and settings icons
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
            Intent intent = new Intent(TeacherDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

