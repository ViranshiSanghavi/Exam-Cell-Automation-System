package com.example.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MarksEditActivity extends AppCompatActivity {

    private EditText editTextMarks;
    private TextView textViewMarks, textViewPercentage, textViewCGPA;
    private Button btnSave;
    private String studentName, subject, className;
    private MarksDataManager marksDataManager;
    private static final String DEFAULT_CLASS_NAME = "Unclassified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_edit);

        // Initialize MarksDataManager
        marksDataManager = MarksDataManager.getInstance(this);

        initializeViews();
        setupData();
        setupSaveButton();
    }

    private void initializeViews() {
        editTextMarks = findViewById(R.id.editTextMarks);
        textViewMarks = findViewById(R.id.textViewMarks);
        textViewPercentage = findViewById(R.id.textViewPercentage);
        textViewCGPA = findViewById(R.id.textViewCGPA);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupData() {
        // Get intent extras with null checks
        studentName = getIntent().getStringExtra("studentName");
        subject = getIntent().getStringExtra("subject");
        className = getIntent().getStringExtra("className");

        // Use default values if any input is null
        studentName = TextUtils.isEmpty(studentName) ? "Unknown Student" : studentName;
        subject = TextUtils.isEmpty(subject) ? "Unknown Subject" : subject;
        className = TextUtils.isEmpty(className) ? DEFAULT_CLASS_NAME : className;

        textViewMarks.setText(String.format("Edit Marks for %s - %s", studentName, subject));

        // Load existing marks with robust error handling
        try {
            List<MarksDataManager.StudentMarks> classMarksList = marksDataManager.getMarksForClass(className);
            for (MarksDataManager.StudentMarks studentMarks : classMarksList) {
                if (studentMarks.studentName.equals(studentName) &&
                        studentMarks.subject.equals(subject)) {

                    editTextMarks.setText(String.format("%.2f", studentMarks.marks));
                    textViewPercentage.setText(String.format("Percentage: %.2f%%", studentMarks.percentage));
                    textViewCGPA.setText(String.format("CGPA: %.2f", studentMarks.cgpa));
                    break;
                }
            }
        } catch (Exception e) {
            // Log error or show a toast if needed
            Toast.makeText(this, "Error loading existing marks", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateMarks()) {
                try {
                    double marks = Double.parseDouble(editTextMarks.getText().toString());

                    // Calculate percentage and CGPA
                    double percentage = MarksDataManager.StudentMarks.calculatePercentage(marks);
                    double cgpa = MarksDataManager.StudentMarks.calculateCGPA(percentage);

                    // Create StudentMarks object with null-safe constructor
                    MarksDataManager.StudentMarks studentMarks = new MarksDataManager.StudentMarks(
                            studentName, subject, marks, percentage, cgpa, System.currentTimeMillis()
                    );

                    // Save updated marks with fallback class name
                    marksDataManager.saveStudentMarks(className, studentMarks);

                    // Update UI
                    textViewPercentage.setText(String.format("Percentage: %.2f%%", percentage));
                    textViewCGPA.setText(String.format("CGPA: %.2f", cgpa));

                    Toast.makeText(MarksEditActivity.this,
                            "Marks updated successfully",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MarksEditActivity.this,
                            "Failed to save marks. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateMarks() {
        String marksString = editTextMarks.getText().toString().trim();

        if (TextUtils.isEmpty(marksString)) {
            Toast.makeText(this, "Please enter marks", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double marks = Double.parseDouble(marksString);
            if (marks < 0 || marks > 100) {
                Toast.makeText(this, "Marks should be between 0 and 100", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid marks format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}