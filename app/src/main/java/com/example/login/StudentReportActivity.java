package com.example.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class StudentReportActivity extends AppCompatActivity {

    TableLayout tableLayout;
    Button downloadButton;
    WebView excelWebView;
    String selectedClass, selectedDivision, selectedExamGroup;
    String[][] students;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Report...");
        progressDialog.setCancelable(false);

        tableLayout = findViewById(R.id.studentReportTable);
        downloadButton = findViewById(R.id.downloadExcelButton);
        excelWebView = findViewById(R.id.excelWebView);

        // Get class, division, and exam group from Intent
        selectedClass = getIntent().getStringExtra("selectedClass");
        selectedDivision = getIntent().getStringExtra("selectedDivision");
        selectedExamGroup = getIntent().getStringExtra("selectedExamGroup");

        // Show loading
        progressDialog.show();

        // Delay to simulate loading (optional)
        new Handler().postDelayed(() -> {
            // Generate Student Data
            generateStudentData();

            // Generate the report
            generateStudentReport();

            // Generate Excel File
            createExcelFile();

            // Generate HTML Preview
            generateHTMLPreview();

            // Dismiss loading
            progressDialog.dismiss();
        }, 1000); // 1 second delay

        // Set up Download Button
        downloadButton.setOnClickListener(v -> openExcelFile());
    }

    private void generateStudentData() {
        // Hardcoded Student Data Based on Class, Division & Exam Group
        if ("FY".equals(selectedClass) && "A".equals(selectedDivision)) {
            students = new String[][]{
                    {"101", "Amit Sharma", "CSE", "FY", "A", "20", "18", "35", "40", "No"},
                    {"102", "Priya Desai", "CSE", "FY", "A", "22", "19", "30", "38", "No"}
            };
        } else if ("FY".equals(selectedClass) && "B".equals(selectedDivision)) {
            students = new String[][]{
                    {"103", "Rohan Patil", "IT", "FY", "B", "15", "20", "25", "35", "Yes"},
                    {"104", "Neha Verma", "EXTC", "FY", "B", "19", "22", "40", "42", "No"}
            };
        } else if ("SY".equals(selectedClass) && "A".equals(selectedDivision)) {
            students = new String[][]{
                    {"105", "Suresh Iyer", "CSE", "SY", "A", "18", "17", "33", "36", "Yes"},
                    {"106", "Meera Patel", "IT", "SY", "A", "21", "20", "38", "42", "No"}
            };
        } else if ("SY".equals(selectedClass) && "B".equals(selectedDivision)) {
            students = new String[][]{
                    {"107", "Rahul Kumar", "EXTC", "SY", "B", "16", "18", "29", "34", "Yes"},
                    {"108", "Anjali Singh", "CSE", "SY", "B", "20", "22", "36", "40", "No"}
            };
        } else if ("TY".equals(selectedClass) && "A".equals(selectedDivision)) {
            students = new String[][]{
                    {"109", "Vikram Mehta", "CSE", "TY", "A", "22", "21", "40", "45", "No"},
                    {"110", "Pooja Gupta", "IT", "TY", "A", "19", "20", "35", "38", "No"}
            };
        } else if ("TY".equals(selectedClass) && "B".equals(selectedDivision)) {
            students = new String[][]{
                    {"111", "Arjun Reddy", "EXTC", "TY", "B", "17", "19", "32", "37", "Yes"},
                    {"112", "Shreya Joshi", "CSE", "TY", "B", "21", "23", "38", "42", "No"}
            };
        } else {
            students = new String[][]{};
        }
    }

    private void generateStudentReport() {
        tableLayout.removeAllViews();

        // Table Header
        TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.parseColor("#EEEEEE"));

        String[] headers = {"Roll No", "Name", "Dept", "Class", "Div", "CE1", "CE2", "Sem End", "Practical", "Live KT"};
        for (String title : headers) {
            addTableCell(header, title, true);
        }
        tableLayout.addView(header);

        // Table Rows
        int rowCount = 0;
        for (String[] student : students) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(rowCount % 2 == 0 ? Color.WHITE : Color.parseColor("#F8F8F8"));

            for (String value : student) {
                addTableCell(row, value, false);
            }
            tableLayout.addView(row);
            rowCount++;
        }
    }

    private void addTableCell(TableRow row, String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(16, 12, 16, 12);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        row.addView(textView);
    }

    private void createExcelFile() {
        try {
            // Generate unique filename based on class, division, and timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(new Date());
            String filename = String.format("StudentReport_%s_%s_%s.xls",
                    selectedClass, selectedDivision, timestamp);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Report", 0);

            // Add Headers
            String[] headers = {"Roll No", "Name", "Dept", "Class", "Div", "CE1", "CE2", "Sem End", "Practical", "Live KT"};
            for (int i = 0; i < headers.length; i++) {
                sheet.addCell(new Label(i, 0, headers[i]));
            }

            // Add Data
            for (int i = 0; i < students.length; i++) {
                for (int j = 0; j < students[i].length; j++) {
                    sheet.addCell(new Label(j, i + 1, students[i][j]));
                }
            }

            workbook.write();
            workbook.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error creating Excel file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void generateHTMLPreview() {
        try {
            // Create a simple HTML preview of the Excel data
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><head><style>")
                    .append("body { font-family: Arial, sans-serif; background-color: white; color: black; }")
                    .append("table { border-collapse: collapse; width: 100%; }")
                    .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; color: black; }")
                    .append("th { background-color: #f2f2f2; }")
                    .append("</style></head><body>")
                    .append("<h2>Student Report Preview - ").append(selectedClass).append(" ").append(selectedDivision).append("</h2>")
                    .append("<table>");

            // Add headers
            htmlContent.append("<tr>");
            String[] headers = {"Roll No", "Name", "Dept", "Class", "Div", "CE1", "CE2", "Sem End", "Practical", "Live KT"};
            for (String header : headers) {
                htmlContent.append("<th>").append(header).append("</th>");
            }
            htmlContent.append("</tr>");

            // Add data
            for (String[] student : students) {
                htmlContent.append("<tr>");
                for (String value : student) {
                    htmlContent.append("<td>").append(value).append("</td>");
                }
                htmlContent.append("</tr>");
            }

            htmlContent.append("</table></body></html>");

            // Load HTML preview in WebView
            runOnUiThread(() -> {
                excelWebView.loadDataWithBaseURL(null, htmlContent.toString(), "text/html", "UTF-8", null);
                excelWebView.setBackgroundColor(Color.WHITE);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error generating preview: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openExcelFile() {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File[] files = downloadsDir.listFiles((dir, name) -> name.startsWith("StudentReport_" + selectedClass + "_" + selectedDivision));

            if (files != null && files.length > 0) {
                File file = files[0];
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No Excel file found. Please generate the report first.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening Excel file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}