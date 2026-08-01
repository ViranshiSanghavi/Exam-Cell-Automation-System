package com.example.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarksEntryActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private TextView txtStudentName, txtSubject, txtUploadedMarks, txtPercentage, txtCGPA;
    private EditText editMarks;
    private Button btnUpload, btnEditMarks, btnExport;

    private String studentName, subject, className;
    private double uploadedMarks, percentage, cgpa;
    private MarksDataManager marksDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_entry);

        // Initialize MarksDataManager
        marksDataManager = MarksDataManager.getInstance(this);

        initializeViews();
        setupData();
        setupButtons();
        loadSavedMarks();
    }

    private void initializeViews() {
        txtStudentName = findViewById(R.id.txtStudentName);
        txtSubject = findViewById(R.id.txtSubject);
        txtUploadedMarks = findViewById(R.id.txtUploadedMarks);
        txtPercentage = findViewById(R.id.txtPercentage);
        txtCGPA = findViewById(R.id.txtCGPA);
        editMarks = findViewById(R.id.editMarks);
        btnUpload = findViewById(R.id.btnUpload);
        btnEditMarks = findViewById(R.id.btnEditMarks);
        btnExport = findViewById(R.id.btnExport);
    }

    private void setupData() {
        studentName = getIntent().getStringExtra("studentName");
        subject = getIntent().getStringExtra("subject");
        className = getIntent().getStringExtra("className");

        txtStudentName.setText("Student: " + studentName);
        txtSubject.setText("Subject: " + subject);
        txtUploadedMarks.setText("Marks: Not uploaded yet");
    }

    private void setupButtons() {
        btnUpload.setOnClickListener(v -> {
            if (validateMarks()) {
                uploadedMarks = Double.parseDouble(editMarks.getText().toString());
                long uploadTimestamp = System.currentTimeMillis();

                // Calculate percentage and CGPA
                percentage = MarksDataManager.StudentMarks.calculatePercentage(uploadedMarks);
                cgpa = MarksDataManager.StudentMarks.calculateCGPA(percentage);

                // Create StudentMarks object
                MarksDataManager.StudentMarks studentMarks = new MarksDataManager.StudentMarks(
                        studentName, subject, uploadedMarks, percentage, cgpa, uploadTimestamp
                );

                // Save marks
                marksDataManager.saveStudentMarks(className, studentMarks);

                updateMarksDisplay();
                Toast.makeText(MarksEntryActivity.this, "Marks uploaded successfully!", Toast.LENGTH_LONG).show();
            }
        });

        btnEditMarks.setOnClickListener(v -> {
            // Redirect to MarksEditActivity
            Intent intent = new Intent(MarksEntryActivity.this, MarksEditActivity.class);
            intent.putExtra("studentName", studentName);
            intent.putExtra("subject", subject);
            intent.putExtra("className", className);
            startActivity(intent);
        });

        btnExport.setOnClickListener(v -> checkStoragePermissionAndExport());
    }

    private void checkStoragePermissionAndExport() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            exportToCSV();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToCSV();
            } else {
                Toast.makeText(this, "Permission denied. Cannot export.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMarksDisplay() {
        txtUploadedMarks.setText(String.format("Marks: %.2f", uploadedMarks));
        txtPercentage.setText(String.format("Percentage: %.2f%%", percentage));
        txtCGPA.setText(String.format("CGPA: %.2f", cgpa));
    }

    private void exportToCSV() {
        List<MarksDataManager.StudentMarks> classMarks = marksDataManager.getMarksForClass(className);

        if (classMarks == null || classMarks.isEmpty()) {
            Toast.makeText(this, "No marks to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure Downloads directory exists
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            if (!downloadsDir.mkdirs()) {
                Toast.makeText(this, "Unable to create Downloads directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create file name with timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String fileName = className + "_StudentMarks_" + timestamp + ".csv";

        File file = new File(downloadsDir, fileName);

        try {
            FileWriter writer = new FileWriter(file);

            // Write CSV headers
            writer.append("Roll No,Student Name,Subject,Marks,Percentage,CGPA\n");

            // Write data rows
            for (int i = 0; i < classMarks.size(); i++) {
                MarksDataManager.StudentMarks marks = classMarks.get(i);
                writer.append(String.format("%d,%s,%s,%.2f,%.2f%%,%.2f\n",
                        i + 1,
                        marks.studentName,
                        marks.subject,
                        marks.marks,
                        marks.percentage,
                        marks.cgpa
                ));
            }

            writer.flush();
            writer.close();

            // Open the exported file
            openExportedFile(file);

            Toast.makeText(this, "Exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openExportedFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(intent, "Open CSV File"));
        } catch (Exception e) {
            Toast.makeText(this, "No app found to open CSV file", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateMarks() {
        String marksStr = editMarks.getText().toString().trim();
        if (marksStr.isEmpty()) {
            Toast.makeText(this, "Please enter marks", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double marks = Double.parseDouble(marksStr);
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

    private void loadSavedMarks() {
        List<MarksDataManager.StudentMarks> allMarks = marksDataManager.getMarksForClass(className);
        for (MarksDataManager.StudentMarks marks : allMarks) {
            if (marks.studentName.equals(studentName) && marks.subject.equals(subject)) {
                uploadedMarks = marks.marks;
                percentage = marks.percentage;
                cgpa = marks.cgpa;
                updateMarksDisplay();
                break;
            }
        }
    }
}