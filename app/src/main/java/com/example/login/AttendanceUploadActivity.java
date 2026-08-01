package com.example.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AttendanceUploadActivity extends AppCompatActivity {

    private AutoCompleteTextView departmentSpinner;
    private AutoCompleteTextView yearSpinner;
    private Button checkEligibilityButton;
    private TextView resultText;

    private MyDBHelper dbHelper;

    // Hardcoded student data
    private static final String[] STUDENT_NAMES = {
            "Viranshi Alpesh Sanghavi", "Devanshi Nimesh Joshi", "Pranav Milind Kawatkar", "Khushi Ravi Gorakh",
            "Ravi Shankar Patil", "Neha Sandeep Deshmukh", "Amit Vikram Rajput", "Priya Shubham Agrawal",
            "Sahil Kiran Soni", "Pooja Rajendra Mehta", "Arjun Sandeep Shinde", "Simran Ramesh Joshi",
            "Vishal Bhupendra Yadav", "Ayesha Nikhil More", "Nikhil Prashant Agarwal", "Ravi Deshmukh",
            "Priya Jain", "Tanmay Bhagat", "Tejas Awasare", "Aaradhya Rao", "Ananya Patil", "Jatin Soni",
            "Madhuri Jadhav", "Shruti Desai", "Karan Rathi", "Anjali Mehta", "Siddhi Zala", "Mohit Kadam",
            "Vaishnavi Chavan", "Sarthak Patil", "Kunal Yadav"
    };

    // Each student is assigned one of three departments
    private static final String[] DEPARTMENTS = {
            "IT", "IT", "DS", "DS", "AI/ML", "AI/ML", "IT", "IT", "DS", "DS", "AI/ML", "AI/ML",
            "IT", "IT", "DS", "DS", "AI/ML", "AI/ML", "IT", "IT", "DS", "DS", "AI/ML", "AI/ML",
            "IT", "IT", "DS", "DS", "AI/ML", "AI/ML", "IT", "IT"
    };

    private static final int[] ATTENDANCE_PERCENTAGES = {
            80, 70, 65, 85, 90, 76, 67, 88, 72, 80, 77, 65, 82, 90, 75,
            80, 78, 72, 88, 84, 70, 65, 92, 85, 77, 82, 90, 76, 89, 80,
            85, 77
    };

    private static final String[] ACADEMIC_YEARS = {
            "FY", "SY", "TY"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_upload);

        // Initialize views
        departmentSpinner = findViewById(R.id.departmentSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        checkEligibilityButton = findViewById(R.id.checkEligibilityButton);
        resultText = findViewById(R.id.resultText);

        // Initialize the database helper
        dbHelper = new MyDBHelper(this);

        // Setup Adapters for AutoCompleteTextViews
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ACADEMIC_YEARS);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[] {"IT", "DS", "AI/ML"});
        departmentSpinner.setAdapter(departmentAdapter);

        // Button click listener to check eligibility
        checkEligibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDepartment = departmentSpinner.getText().toString();
                String selectedYear = yearSpinner.getText().toString();
                checkEligibility(selectedYear, selectedDepartment);
            }
        });
    }

    private void checkEligibility(String year, String department) {
        StringBuilder resultBuilder = new StringBuilder();
        boolean studentFound = false;

        // Loop through hardcoded student data
        for (int i = 0; i < STUDENT_NAMES.length; i++) {
            String studentName = STUDENT_NAMES[i];
            String studentDepartment = DEPARTMENTS[i];
            int attendancePercentage = ATTENDANCE_PERCENTAGES[i];

            // If the student belongs to the selected department
            if (studentDepartment.equals(department)) {
                studentFound = true;

                // Check eligibility
                String eligibilityStatus = (attendancePercentage >= 75) ? "Eligible" : "Not Eligible";

                // Build result string
                resultBuilder.append(studentName)
                        .append("\nAttendance: ").append(attendancePercentage).append("%\n")
                        .append("Eligibility: ").append(eligibilityStatus)
                        .append("\n\n");
            }
        }

        if (studentFound) {
            resultText.setText(resultBuilder.toString());
        } else {
            resultText.setText("No students found for the selected department.");
        }
    }
}
