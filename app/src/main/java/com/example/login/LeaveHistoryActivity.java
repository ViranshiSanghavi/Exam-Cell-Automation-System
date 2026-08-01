package com.example.login;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LeaveHistoryActivity extends AppCompatActivity {

    private TextView dateTextView, typeTextView, statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_history_item); // Ensure this points to your XML layout

        initializeViews();

        // Retrieve data passed through the Intent
        String date = getIntent().getStringExtra("DATE");
        String leaveType = getIntent().getStringExtra("LEAVE_TYPE");
        String status = getIntent().getStringExtra("STATUS"); // Get the leave status from the Intent

        // Set default status if none is provided
        if (status == null) {
            status = "Pending"; // Default status if not set
        }

        // Set the data to TextViews
        dateTextView.setText(date);
        typeTextView.setText(leaveType);
        statusTextView.setText(status);
    }

    private void initializeViews() {
        dateTextView = findViewById(R.id.dateTextView);
        typeTextView = findViewById(R.id.typeTextView);
        statusTextView = findViewById(R.id.statusTextView);
    }
}
