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


public class AdminDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private LinearLayout deadlineTrackerButton;
    private LinearLayout questionPaperButton;
    private LinearLayout userAdministrationButton;
    private LinearLayout noticeBoardButton;
    private MyDBHelper dbHelper; // Declare DB helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize the DB helper
        dbHelper = new MyDBHelper(this);

        // Initialize the welcome text view
        welcomeText = findViewById(R.id.welcomeText);

        // Get the admin's email from the intent (for example)
        Intent intent = getIntent();
        String adminEmail = intent.getStringExtra("adminEmail");

        if (adminEmail != null) {
            // Fetch the admin name using the email
            String adminName = dbHelper.getAdminNameByEmail(adminEmail);
            if (adminName != null) {
                welcomeText.setText("Welcome Admin, " + adminName + "!");
            } else {
                // If no admin found with the email
                Toast.makeText(this, "Admin not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If no email found in the intent, display an error
            Toast.makeText(this, "Admin email not provided", Toast.LENGTH_SHORT).show();
        }

        // Initialize buttons
        deadlineTrackerButton = findViewById(R.id.deadlineTrackerButton);
        questionPaperButton = findViewById(R.id.questionPaperButton);
        userAdministrationButton = findViewById(R.id.userAdministrationButton);
        noticeBoardButton = findViewById(R.id.noticeBoardButton);

        // Set click listeners for each button
        deadlineTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Deadline Tracker Activity
                Intent intent = new Intent(AdminDashboardActivity.this, DeadlineTrackerActivity.class);
                startActivity(intent);
            }
        });

        questionPaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Question Paper Activity
                Intent intent = new Intent(AdminDashboardActivity.this, QuestionPaperActivity.class);
                startActivity(intent);
            }
        });

        userAdministrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start User Administration Activity
                Intent intent = new Intent(AdminDashboardActivity.this, UserAdministrationActivity.class);
                startActivity(intent);
            }
        });

        noticeBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Notice Board Activity
                Intent intent = new Intent(AdminDashboardActivity.this, NoticeBoardActivity.class);
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
            Intent intent = new Intent(AdminDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
