package com.example.login;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "StudentPrefs";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Retrieve the email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(KEY_EMAIL, null); // Get the stored email

        if (email == null) {
            // If no email is found, show an error message and return
            setTextViewText(R.id.result, "Error: No user email found.");
            return;
        }

        // Retrieve student information from the database
        MyDBHelper dbHelper = new MyDBHelper(this);
        String studentName = dbHelper.getStudentNameByEmail(email);
        String studentDept = dbHelper.getStudentDepartmentByEmail(email);
        String studentDivision = dbHelper.getStudentDivisionByEmail(email);

        // Set student info in the UI
        setTextViewText(R.id.studentName, studentName);
        setTextViewText(R.id.studentDept, studentDept);
        setTextViewText(R.id.studentDiv, studentDivision);

        // Retrieve subjects dynamically based on the logged-in email
        List<SubjectMarks> subjects = getSubjectsForStudent(email);

        if (subjects.isEmpty()) {
            // If no subjects found for the student, show an error message
            setTextViewText(R.id.result, "No subjects found for the student.");
            return;
        }

        // Populate the marks table
        populateMarksTable(subjects);
    }

    private List<SubjectMarks> getSubjectsForStudent(String email) {
        // Fetch subjects based on email
        if (email.equals("devanshi@gmail.com")) {
            return getDevanshiSubjects();
        } else if (email.equals("sanghaviviranshi@gmail.com")) {
            return getViranshiSubjects();
        } else {
            // Handle unknown email case
            return new ArrayList<>(); // Return an empty list if no subjects found
        }
    }

    private void populateMarksTable(List<SubjectMarks> subjects) {
        TableLayout marksTable = findViewById(R.id.marksTable);
        int totalMarks = 0;
        final int TOTAL_POSSIBLE_MARKS = 750; // Assuming 150 per subject * 5 subjects

        for (SubjectMarks subject : subjects) {
            TableRow row = createTableRow();

            // Add cells for subject data
            addCell(row, subject.getName(), 200);
            addCell(row, String.valueOf(subject.getCE1()), 100);
            addCell(row, String.valueOf(subject.getCE2()), 100);
            addCell(row, String.valueOf(subject.getSER()), 100);
            addCell(row, String.valueOf(subject.getPR()), 100);

            int subjectTotal = subject.getCE1() + subject.getCE2() + subject.getSER() + subject.getPR();
            addCell(row, String.valueOf(subjectTotal), 100);

            totalMarks += subjectTotal;
            marksTable.addView(row);
        }

        // Calculate percentage
        double percentage = ((double) totalMarks / TOTAL_POSSIBLE_MARKS) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPercentage = df.format(percentage);

        // Display the total marks, percentage, and result
        setTextViewText(R.id.totalMarks, "Total Marks: " + totalMarks + "/" + TOTAL_POSSIBLE_MARKS);
        setTextViewText(R.id.percentage, "Percentage: " + formattedPercentage + "%");
        setTextViewText(R.id.result, "Result: " + (totalMarks >= 300 ? "PASS" : "FAIL"));
    }

    private void setTextViewText(int viewId, String text) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(text != null ? text : "N/A");
        }
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        return row;
    }

    private void addCell(TableRow row, String text, int widthInDp) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setGravity(Gravity.CENTER);
        cell.setPadding(8, 8, 8, 8);
        cell.setBackgroundColor(Color.WHITE);
        cell.setTextColor(Color.BLACK);

        // Convert dp to pixels
        float scale = getResources().getDisplayMetrics().density;
        int widthInPixels = (int) (widthInDp * scale + 0.5f);

        TableRow.LayoutParams params = new TableRow.LayoutParams(widthInPixels, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 1, 1, 1);
        cell.setLayoutParams(params);
        row.addView(cell);
    }

    private List<SubjectMarks> getDevanshiSubjects() {
        List<SubjectMarks> subjects = new ArrayList<>();
        subjects.add(new SubjectMarks("Enterprise Java", 18, 14, 35, 30)); // Example marks
        subjects.add(new SubjectMarks("Software Project Management", 15, 12, 40, 25));
        subjects.add(new SubjectMarks("Aritificial Intelligence", 20, 16, 45, 30));
        subjects.add(new SubjectMarks("Advanced Web Programming", 19, 14, 35, 20));
        subjects.add(new SubjectMarks("Internet of Things", 18, 15, 20, 15));
        return subjects;
    }

    private List<SubjectMarks> getViranshiSubjects() {
        List<SubjectMarks> subjects = new ArrayList<>();
        subjects.add(new SubjectMarks("Software Project Management", 20, 15, 30, 40)); // Example marks
        subjects.add(new SubjectMarks("Artificial Intelligence", 19, 18, 50, 40));
        subjects.add(new SubjectMarks("Business Tableau/Power BI", 15, 16, 25, 40));
        subjects.add(new SubjectMarks("Big Data Analytics using Hadoop", 13, 12, 20, 30));
        subjects.add(new SubjectMarks("Business Ethics and Environmental Science", 25, 30, 35, 25));
        return subjects;
    }

    private static class SubjectMarks {
        private final String name;
        private final int CE1, CE2, SER, PR;

        SubjectMarks(String name, int CE1, int CE2, int SER, int PR) {
            this.name = name;
            this.CE1 = CE1;
            this.CE2 = CE2;
            this.SER = SER;
            this.PR = PR;
        }

        String getName() { return name; }
        int getCE1() { return CE1; }
        int getCE2() { return CE2; }
        int getSER() { return SER; }
        int getPR() { return PR; }
    }
}
