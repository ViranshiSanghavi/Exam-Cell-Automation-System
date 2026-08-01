package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {
    Spinner classSpinner, divisionSpinner, examGroupSpinner;
    Button generateReportButton;
    TextView titleTextView;

    // Define class and division arrays
    private final String[] classes = {"FY", "SY", "TY"};
    private final String[][] divisions = {
            {"A", "B", "C"},  // FY divisions
            {"A", "B"},        // SY divisions
            {"A", "B"}         // TY divisions
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views
        classSpinner = findViewById(R.id.classSpinner);
        divisionSpinner = findViewById(R.id.divisionSpinner);
        examGroupSpinner = findViewById(R.id.examGroupSpinner);
        generateReportButton = findViewById(R.id.generateReportButton);
        titleTextView = findViewById(R.id.consolidatedReportTitle);

        // Set up class spinner with black text and white background
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, classes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK); // Set selected text color to black
                view.setBackgroundColor(Color.WHITE); // Ensure background is white
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.BLACK); // Set dropdown text color to black
                view.setBackgroundColor(Color.WHITE); // Ensure dropdown background is white
                return view;
            }
        };
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        // Set up division spinner dynamically
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update division spinner based on selected class
                ArrayAdapter<String> divisionAdapter = new ArrayAdapter<String>(
                        ReportActivity.this, android.R.layout.simple_spinner_item, divisions[position]) {
                    @Override
                    public View getView(int pos, View convertView, ViewGroup parent) {
                        TextView view = (TextView) super.getView(pos, convertView, parent);
                        view.setTextColor(Color.BLACK);
                        view.setBackgroundColor(Color.WHITE);
                        return view;
                    }

                    @Override
                    public View getDropDownView(int pos, View convertView, ViewGroup parent) {
                        TextView view = (TextView) super.getDropDownView(pos, convertView, parent);
                        view.setTextColor(Color.BLACK);
                        view.setBackgroundColor(Color.WHITE);
                        return view;
                    }
                };
                divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                divisionSpinner.setAdapter(divisionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up exam group spinner with black text and white background
        String[] examGroups = {"CE1", "CE2", "Sem End"};
        ArrayAdapter<String> examGroupAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, examGroups) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.WHITE);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.WHITE);
                return view;
            }
        };
        examGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examGroupSpinner.setAdapter(examGroupAdapter);

        // Generate Report Button Click Listener
        generateReportButton.setOnClickListener(v -> {
            String selectedClass = classSpinner.getSelectedItem().toString();
            String selectedDivision = divisionSpinner.getSelectedItem().toString();
            String selectedExamGroup = examGroupSpinner.getSelectedItem().toString();

            // Update title
          //  titleTextView.setText("Consolidated Report: " );

            // Create intent and pass selections
            Intent intent = new Intent(ReportActivity.this, StudentReportActivity.class);
            intent.putExtra("selectedClass", selectedClass);
            intent.putExtra("selectedDivision", selectedDivision);
            intent.putExtra("selectedExamGroup", selectedExamGroup);
            startActivity(intent);
        });
    }
}
