package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MarksUploadActivity extends AppCompatActivity {
    private Spinner spinnerYear, spinnerDivision, spinnerDepartment, spinnerExamGroup, spinnerSemester;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_upload);

        initializeViews();
        setupSpinners();
        setupNextButton();
    }

    private void initializeViews() {
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerDivision = findViewById(R.id.spinnerDivision);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerExamGroup = findViewById(R.id.spinnerExamGroup);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        btnNext = findViewById(R.id.btnNext);
    }

    private ArrayAdapter<String> createAdapter(String[] items) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

    private void setupSpinners() {
        spinnerYear.setAdapter(createAdapter(new String[]{"FY", "SY", "TY"}));
        spinnerDivision.setAdapter(createAdapter(new String[]{"A", "B"}));
        spinnerDepartment.setAdapter(createAdapter(new String[]{"IT", "DS"}));
        spinnerExamGroup.setAdapter(createAdapter(new String[]{"CE1", "CE2", "Sem End", "Practical"}));

        // Semester Spinner with disabled odd semesters
        String[] semesters = {"1", "2", "3", "4", "5", "6"};
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semesters) {
            @Override
            public boolean isEnabled(int position) {
                // Disable odd semesters (1, 3, 5)
                return (position + 1) % 2 == 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (!isEnabled(position)) {
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.LTGRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.WHITE);
                }

                return view;
            }
        };

        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);
    }

    private void setupNextButton() {
        btnNext.setOnClickListener(v -> {
            if (validateSelections()) {
                String selectedSemester = spinnerSemester.getSelectedItem().toString();
                int semester = Integer.parseInt(selectedSemester);

                if (semester % 2 != 0) {
                    Toast.makeText(MarksUploadActivity.this,
                            "Please select an even semester.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MarksUploadActivity.this, StudentListActivity.class);
                    intent.putExtra("year", spinnerYear.getSelectedItem().toString());
                    intent.putExtra("division", spinnerDivision.getSelectedItem().toString());
                    intent.putExtra("department", spinnerDepartment.getSelectedItem().toString());
                    intent.putExtra("examGroup", spinnerExamGroup.getSelectedItem().toString());
                    intent.putExtra("semester", spinnerSemester.getSelectedItem().toString());
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateSelections() {
        if (spinnerYear.getSelectedItem() == null ||
                spinnerDivision.getSelectedItem() == null ||
                spinnerDepartment.getSelectedItem() == null ||
                spinnerExamGroup.getSelectedItem() == null ||
                spinnerSemester.getSelectedItem() == null) {
            Toast.makeText(this, "Please select all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
