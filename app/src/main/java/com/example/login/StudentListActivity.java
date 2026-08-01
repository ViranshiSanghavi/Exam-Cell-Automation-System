package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentListActivity extends AppCompatActivity {
    private ListView listViewStudents;
    private String selectedYear, selectedDivision, selectedDepartment, selectedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        listViewStudents = findViewById(R.id.listViewStudents);

        // Get data from Intent
        selectedYear = getIntent().getStringExtra("year");
        selectedDivision = getIntent().getStringExtra("division");
        selectedDepartment = getIntent().getStringExtra("department");

        // Determine subject based on year
        switch (selectedYear) {
            case "FY":
                selectedSubject = "C Programming";
                break;
            case "SY":
                selectedSubject = "Data Structure";
                break;
            case "TY":
                selectedSubject = "Artificial Intelligence";
                break;
        }

        setupStudentList();
    }

    private void setupStudentList() {
        ArrayList<String> students = getStudentsForGroup(selectedYear, selectedDivision, selectedDepartment);

        // Custom adapter to change text color to black
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, students) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };

        listViewStudents.setAdapter(adapter);

        listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStudent = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(StudentListActivity.this, MarksEntryActivity.class);
            intent.putExtra("studentName", selectedStudent);
            intent.putExtra("subject", selectedSubject);
            startActivity(intent);
        });
    }

    private ArrayList<String> getStudentsForGroup(String year, String division, String department) {
        Map<String, ArrayList<String>> studentGroups = new HashMap<>();

        // First Year (FY)
        studentGroups.put("FY_A_IT", new ArrayList<String>() {{
            add("Viranshi Alpesh Sanghavi");
            add("Devanshi Nimesh Joshi");
            add("Pranav Milind Kawatkar");
            add("Khushi Ravi Gorakh");
            add("Ravi Shankar Patil");
        }});

        studentGroups.put("FY_B_IT", new ArrayList<String>() {{
            add("Neha Sandeep Deshmukh");
            add("Amit Vikram Rajput");
            add("Priya Shubham Agrawal");
            add("Sahil Kiran Soni");
            add("Pooja Rajendra Mehta");
        }});

        studentGroups.put("FY_A_DS", new ArrayList<String>() {{
            add("Arjun Sandeep Shinde");
            add("Simran Ramesh Joshi");
            add("Vishal Bhupendra Yadav");
        }});

        studentGroups.put("FY_B_DS", new ArrayList<String>() {{
            add("Ayesha Nikhil More");
            add("Nikhil Prashant Agarwal");
            add("Ravi Deshmukh");
        }});

        // Second Year (SY)
        studentGroups.put("SY_A_IT", new ArrayList<String>() {{
            add("Priya Jain");
            add("Tanmay Bhagat");
            add("Tejas Awasare");
        }});

        studentGroups.put("SY_B_IT", new ArrayList<String>() {{
            add("Aaradhya Rao");
            add("Ananya Patil");
            add("Jatin Soni");
        }});

        studentGroups.put("SY_A_DS", new ArrayList<String>() {{
            add("Madhuri Jadhav");
            add("Shruti Desai");
            add("Karan Rathi");
        }});

        studentGroups.put("SY_B_DS", new ArrayList<String>() {{
            add("Anjali Mehta");
            add("Siddhi Zala");
            add("Mohit Kadam");
        }});

        // Third Year (TY)
        studentGroups.put("TY_A_IT", new ArrayList<String>() {{
            add("Vaishnavi Chavan");
            add("Sarthak Patil");
            add("Kunal Yadav");
        }});

        studentGroups.put("TY_B_IT", new ArrayList<String>() {{
            add("Rohan Mehta");
            add("Simran Patel");
            add("Ayushi Shah");
        }});

        studentGroups.put("TY_A_DS", new ArrayList<String>() {{
            add("Karan Jain");
            add("Neha Joshi");
            add("Sohan Desai");
        }});

        studentGroups.put("TY_B_DS", new ArrayList<String>() {{
            add("Rahul Modi");
            add("Priya Sharma");
            add("Tanya Gupta");
        }});

        // Generate key
        String key = year + "_" + division + "_" + department;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return studentGroups.getOrDefault(key, new ArrayList<>());
        }
        return null;
    }
}
