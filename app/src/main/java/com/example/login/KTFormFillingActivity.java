package com.example.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class KTFormFillingActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "KTFormFillingActivity";
    private static final String SHARED_PREFS = "TeacherNotifications";
    private static final String NOTIFICATIONS_KEY = "notifications";
    private static final String RECEIPTS_PREFS = "UserReceipts";
    private static final String RECEIPTS_KEY = "receiptsData";

    private String selectedDepartment;
    private String selectedSemester;
    private String rollNumber;
    private String division;

    private List<String> selectedSubjects = new ArrayList<>();
    private int revaluationSubjectCount = 0;
    private int photocopySubjectCount = 0;

    private String[] itSem1Subjects = {
            "Programming Principles with C", "Fundamentals of Database Management Systems",
            "Digital Electronics", "Numerical Methods",
            "Principles of Accounting & Tally / Intro to German Lang",
            "Professional Communication Skills", "Human Values & Life Skills"
    };

    private String[] dsSem1Subjects = {
            "Introduction to Data Science", "Programming Logic Development",
            "Fundamentals of Operating System", "Linear Algebra and Basics of Calculus",
            "MIS/Org. Behavior", "Communication Skills in English", "Human Values & Life Skills"
    };

    private String[] itSem3Subjects = {
            "Python Programming", "Computer Networks",
            "Data Structures", "Applied Mathematics",
            "Personal Finance Management/Indian Economics and Business Model",
            "Conversational English", "Leadership and Management"
    };

    private String[] dsSem3Subjects = {
            "Advance R Programming", "Data Structures & Algorithm",
            "Advance SQL", "Personal Finance Management/Indian Economics and Business Model",
            "Conversational English", "Leadership and Management"
    };

    private String[] itSem5Subjects = {
            "Software Project Management", "Artificial Intelligence",
            "Advanced Web Programming", "Enterprise Java", "Internet of Things"
    };

    private String[] dsSem5Subjects = {
            "Software Project Management", "Artificial Intelligence",
            "Business Tableau/Power BI", "Big Data Analytics using Hadoop",
            "Business Ethics and Environmental Science"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kt_form_filling);

        Checkout.preload(getApplicationContext());

        Spinner spDepartment = findViewById(R.id.sp_department);
        EditText etRollNo = findViewById(R.id.et_roll_no);
        EditText etDivision = findViewById(R.id.et_division);
        Spinner spSemester = findViewById(R.id.sp_semester);
        Button btnOneCourse = findViewById(R.id.btn_one_course);
        Button btnTwoCourses = findViewById(R.id.btn_two_courses);
        Button btnMoreCourses = findViewById(R.id.btn_more_courses);
        Button btnPhotocopy = findViewById(R.id.btn_photocopy);
        Button btnRevaluation = findViewById(R.id.btn_revaluation);
        Button btnViewReceiptHistory = findViewById(R.id.btn_view_receipt_history);

        // Set up department options
        List<String> departments = new ArrayList<>();
        departments.add("IT");
        departments.add("DS");

        // Setting up the adapter for the department spinner
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(departmentAdapter);

        // Define semester options before using them
        List<String> semesters = new ArrayList<>();
        semesters.add("Semester 1");
        semesters.add("Semester 3");
        semesters.add("Semester 5");

        // Set up the adapter for the semester spinner
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemester.setAdapter(semesterAdapter);

        // Set listener for department selection
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set listener for semester selection
        spSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSemester = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnOneCourse.setOnClickListener(view -> handleCourseSelection(1));
        btnTwoCourses.setOnClickListener(view -> handleCourseSelection(2));
        btnMoreCourses.setOnClickListener(view -> handleCourseSelection(3));
        btnPhotocopy.setOnClickListener(view -> showPhotocopyDialog());
        btnRevaluation.setOnClickListener(view -> showRevaluationDialog());

        // Add receipt history button if it exists
        if (btnViewReceiptHistory != null) {
            btnViewReceiptHistory.setOnClickListener(view -> showReceiptHistory());
        }
    }

    private void handleCourseSelection(int courseCount) {
        if (validateFields()) {
            showSubjectSelectionDialog(courseCount);
        } else {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSubjectSelectionDialog(int courseCount) {
        List<String> subjects = getSubjectsForSelection();
        boolean[] checkedSubjects = new boolean[subjects.size()];
        ArrayList<String> selectedSubjectsList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Subjects");

        builder.setMultiChoiceItems(subjects.toArray(new String[0]), checkedSubjects, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedSubjectsList.add(subjects.get(which));
            } else {
                selectedSubjectsList.remove(subjects.get(which));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedSubjectsList.size() == courseCount) {
                selectedSubjects.clear();
                selectedSubjects.addAll(selectedSubjectsList);
                int amount = calculateCourseFee(courseCount);
                startPayment(amount, "KT Payment for " + selectedSubjects.size() + " Subject(s)");
            } else {
                Toast.makeText(KTFormFillingActivity.this, "Please select exactly " + courseCount + " subject(s)", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private int calculateCourseFee(int courseCount) {
        if (courseCount == 1) {
            return 295; // ₹295 for 1 course
        } else if (courseCount == 2) {
            return 495; // ₹495 for 2 courses
        } else {
            return 995; // ₹995 for more than 2 courses
        }
    }

    private List<String> getSubjectsForSelection() {
        if (selectedDepartment.equals("IT")) {
            switch (selectedSemester) {
                case "Semester 1":
                    return List.of(itSem1Subjects);
                case "Semester 3":
                    return List.of(itSem3Subjects);
                case "Semester 5":
                    return List.of(itSem5Subjects);
                default:
                    return new ArrayList<>();
            }
        } else if (selectedDepartment.equals("DS")) {
            switch (selectedSemester) {
                case "Semester 1":
                    return List.of(dsSem1Subjects);
                case "Semester 3":
                    return List.of(dsSem3Subjects);
                case "Semester 5":
                    return List.of(dsSem5Subjects);
                default:
                    return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    private void showPhotocopyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photocopy Selection")
                .setMessage("Select number of subjects for photocopy")
                .setPositiveButton("1 Subject - ₹50", (dialog, which) -> {
                    photocopySubjectCount = 1;
                    startPayment(50, "Photocopy for 1 Subject");
                })
                .setNegativeButton("2 Subjects - ₹100", (dialog, which) -> {
                    photocopySubjectCount = 2;
                    startPayment(100, "Photocopy for 2 Subjects");
                })
                .setNeutralButton("More than 2 Subjects - ₹150", (dialog, which) -> {
                    photocopySubjectCount = 3;
                    startPayment(150, "Photocopy for 3 Subjects");
                })
                .show();
    }

    private void showRevaluationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Revaluation Selection")
                .setMessage("Select number of subjects for revaluation")
                .setPositiveButton("1 Subject - ₹250", (dialog, which) -> {
                    revaluationSubjectCount = 1;
                    startPayment(250, "Revaluation for 1 Subject");
                })
                .setNegativeButton("2 Subjects - ₹500", (dialog, which) -> {
                    revaluationSubjectCount = 2;
                    startPayment(500, "Revaluation for 2 Subjects");
                })
                .setNeutralButton("More than 2 Subjects - ₹750", (dialog, which) -> {
                    revaluationSubjectCount = 3;
                    startPayment(750, "Revaluation for 3 Subjects");
                })
                .show();
    }

    private boolean validateFields() {
        rollNumber = ((EditText) findViewById(R.id.et_roll_no)).getText().toString().trim();
        division = ((EditText) findViewById(R.id.et_division)).getText().toString().trim();
        return !rollNumber.isEmpty() && !division.isEmpty() && selectedDepartment != null && selectedSemester != null;
    }

    private void startPayment(int amount, String description) {
        try {
            Checkout co = new Checkout();
            co.setKeyID("rzp_test_uPTZtiYp6Xcu0n");
            JSONObject options = new JSONObject();
            options.put("name", "KT Payment");
            options.put("description", description);
            options.put("currency", "INR");
            options.put("amount", amount * 100); // Amount in paise

            co.open(this, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting payment: " + e.getMessage());
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

        // Prepare detailed notification
        String notificationMessage = prepareNotificationMessage();

        // Save notification
        saveNotification(notificationMessage);

        // Prepare receipt data
        JSONObject receiptData = prepareReceiptJSON(razorpayPaymentID);

        // Save receipt to app history
        saveReceiptToHistory(receiptData);

        // Show receipt
        showReceipt(razorpayPaymentID);
    }

    private void showReceipt(String razorpayPaymentID) {
        // Inflate the receipt layout
        View receiptView = LayoutInflater.from(this).inflate(R.layout.receipt_layout, null);

        // Get references to views
        TextView tvReceiptDetails = receiptView.findViewById(R.id.tvReceiptDetails);
        Button btnCloseReceipt = receiptView.findViewById(R.id.btnCloseReceipt);
        Button btnDownloadReceipt = receiptView.findViewById(R.id.btnDownloadReceipt);

        // Prepare receipt details
        String receiptDetails = prepareReceiptDetails(razorpayPaymentID);

        // Set receipt details
        tvReceiptDetails.setText(receiptDetails);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(receiptView);
        AlertDialog receiptDialog = builder.create();
        receiptDialog.setCanceledOnTouchOutside(false); // Prevent dismissal by tapping outside

        // Handle close button click
        btnCloseReceipt.setOnClickListener(v -> receiptDialog.dismiss());

        // Handle download button click
        btnDownloadReceipt.setOnClickListener(v -> {
            downloadReceipt(receiptDetails, razorpayPaymentID);
            Toast.makeText(KTFormFillingActivity.this, "Receipt saved to Documents", Toast.LENGTH_SHORT).show();
        });

        receiptDialog.show();
    }

    private void showReceiptHistory() {
        // Get all saved receipts
        List<JSONObject> receipts = getAllSavedReceipts();

        if (receipts.isEmpty()) {
            Toast.makeText(this, "No receipt history found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create receipt list items for display
        String[] receiptItems = new String[receipts.size()];
        for (int i = 0; i < receipts.size(); i++) {
            try {
                JSONObject receipt = receipts.get(i);
                String timestamp = receipt.getString("timestamp");
                String paymentId = receipt.getString("paymentId");
                receiptItems[i] = "Receipt #" + (i+1) + " - " + timestamp + " (ID: " + paymentId + ")";
            } catch (JSONException e) {
                receiptItems[i] = "Receipt #" + (i+1);
            }
        }

        // Show receipt history dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Receipt History");
        builder.setItems(receiptItems, (dialog, which) -> {
            // Show selected receipt
            try {
                JSONObject selectedReceipt = receipts.get(which);
                showSavedReceipt(selectedReceipt);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading receipt", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void showSavedReceipt(JSONObject receiptData) {
        try {
            // Inflate the receipt layout
            View receiptView = LayoutInflater.from(this).inflate(R.layout.receipt_layout, null);

            // Get references to views
            TextView tvReceiptDetails = receiptView.findViewById(R.id.tvReceiptDetails);
            Button btnCloseReceipt = receiptView.findViewById(R.id.btnCloseReceipt);
            Button btnDownloadReceipt = receiptView.findViewById(R.id.btnDownloadReceipt);

            // Format receipt details from JSON
            String paymentId = receiptData.getString("paymentId");
            String rollNo = receiptData.getString("rollNo");
            String department = receiptData.getString("department");
            String semester = receiptData.getString("semester");
            String div = receiptData.getString("division");
            String timestamp = receiptData.getString("timestamp");

            StringBuilder details = new StringBuilder();
            details.append("Payment ID: ").append(paymentId).append("\n\n")
                    .append("Roll No: ").append(rollNo).append("\n")
                    .append("Department: ").append(department).append("\n")
                    .append("Semester: ").append(semester).append("\n")
                    .append("Division: ").append(div).append("\n\n");

            // Add subjects if available
            if (receiptData.has("subjects")) {
                JSONArray subjects = receiptData.getJSONArray("subjects");
                if (subjects.length() > 0) {
                    details.append("Subjects: ");
                    for (int i = 0; i < subjects.length(); i++) {
                        details.append(subjects.getString(i));
                        if (i < subjects.length() - 1) {
                            details.append(", ");
                        }
                    }
                    details.append("\n\n");
                }
            }

            // Add photocopy or revaluation details if available
            if (receiptData.has("photocopyCount") && receiptData.getInt("photocopyCount") > 0) {
                details.append("Photocopy Subjects: ").append(receiptData.getInt("photocopyCount")).append("\n\n");
            }

            if (receiptData.has("revaluationCount") && receiptData.getInt("revaluationCount") > 0) {
                details.append("Revaluation Subjects: ").append(receiptData.getInt("revaluationCount")).append("\n\n");
            }

            details.append("Timestamp: ").append(timestamp);

            // Set receipt details
            tvReceiptDetails.setText(details.toString());

            // Create and show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(receiptView);
            AlertDialog receiptDialog = builder.create();
            receiptDialog.setCanceledOnTouchOutside(false); // Prevent dismissal by tapping outside

            // Handle close button click
            btnCloseReceipt.setOnClickListener(v -> receiptDialog.dismiss());

            // Handle download button click
            btnDownloadReceipt.setOnClickListener(v -> {
                downloadReceipt(details.toString(), paymentId);
                Toast.makeText(KTFormFillingActivity.this, "Receipt saved to Documents", Toast.LENGTH_SHORT).show();
            });

            receiptDialog.show();

        } catch (JSONException e) {
            Toast.makeText(this, "Error displaying receipt", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error parsing receipt JSON: " + e.getMessage());
        }
    }

    private void downloadReceipt(String receiptDetails, String paymentId) {
        // Create PDF document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        // Draw receipt details
        String[] lines = receiptDetails.split("\n");
        int y = 40;
        for (String line : lines) {
            canvas.drawText(line, 10, y, paint);
            y += 20;
        }

        document.finishPage(page);

        // Save the document
        String fileName = "KT_Receipt_" + paymentId + ".pdf";
        File documentsPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            document.writeTo(new FileOutputStream(documentsPath));
            document.close();

            // Make the PDF viewable by other apps
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", documentsPath);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No PDF viewer found. Receipt saved.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing PDF: " + e.getMessage());
        }
    }

    private String prepareReceiptDetails(String razorpayPaymentID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        StringBuilder receiptDetails = new StringBuilder();
        receiptDetails.append("Payment ID: ").append(razorpayPaymentID).append("\n\n")
                .append("Roll No: ").append(rollNumber).append("\n")
                .append("Department: ").append(selectedDepartment).append("\n")
                .append("Semester: ").append(selectedSemester).append("\n")
                .append("Division: ").append(division).append("\n\n");

        // Add selected subjects if applicable
        if (!selectedSubjects.isEmpty()) {
            receiptDetails.append("Subjects: ").append(String.join(", ", selectedSubjects)).append("\n\n");
        }

        // Add photocopy or revaluation details if applicable
        if (photocopySubjectCount > 0) {
            receiptDetails.append("Photocopy Subjects: ").append(photocopySubjectCount).append("\n\n");
        }

        if (revaluationSubjectCount > 0) {
            receiptDetails.append("Revaluation Subjects: ").append(revaluationSubjectCount).append("\n\n");
        }

        receiptDetails.append("Timestamp: ").append(timestamp);

        return receiptDetails.toString();
    }

    private JSONObject prepareReceiptJSON(String razorpayPaymentID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        JSONObject receiptData = new JSONObject();
        try {
            receiptData.put("paymentId", razorpayPaymentID);
            receiptData.put("rollNo", rollNumber);
            receiptData.put("department", selectedDepartment);
            receiptData.put("semester", selectedSemester);
            receiptData.put("division", division);
            receiptData.put("timestamp", timestamp);

            // Add subjects if applicable
            if (!selectedSubjects.isEmpty()) {
                JSONArray subjectsArray = new JSONArray();
                for (String subject : selectedSubjects) {
                    subjectsArray.put(subject);
                }
                receiptData.put("subjects", subjectsArray);
            }

            // Add photocopy or revaluation details if applicable
            if (photocopySubjectCount > 0) {
                receiptData.put("photocopyCount", photocopySubjectCount);
            }

            if (revaluationSubjectCount > 0) {
                receiptData.put("revaluationCount", revaluationSubjectCount);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error creating receipt JSON: " + e.getMessage());
        }

        return receiptData;
    }

    private String prepareNotificationMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        StringBuilder message = new StringBuilder();
        message.append("KT Payment Details:\n")
                .append("Roll No: ").append(rollNumber).append("\n")
                .append("Department: ").append(selectedDepartment).append("\n")
                .append("Semester: ").append(selectedSemester).append("\n")
                .append("Division: ").append(division).append("\n");

        // Add selected subjects if applicable
        if (!selectedSubjects.isEmpty()) {
            message.append("Subjects: ").append(String.join(", ", selectedSubjects)).append("\n");
        }

        // Add photocopy or revaluation details if applicable
        if (photocopySubjectCount > 0) {
            message.append("Photocopy Subjects: ").append(photocopySubjectCount).append("\n");
        }

        if (revaluationSubjectCount > 0) {
            message.append("Revaluation Subjects: ").append(revaluationSubjectCount).append("\n");
        }

        message.append("Timestamp: ").append(timestamp);

        return message.toString();
    }

    private void saveNotification(String notificationMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // Retrieve existing notifications
        Set<String> notifications = sharedPreferences.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());

        // Create a mutable set and add new notification
        Set<String> updatedNotifications = new HashSet<>(notifications);
        updatedNotifications.add(notificationMessage);

        // Save updated notifications
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(NOTIFICATIONS_KEY, updatedNotifications);
        editor.apply();

        Log.d(TAG, "Notification saved: " + notificationMessage);
    }

    private void saveReceiptToHistory(JSONObject receiptData) {
        SharedPreferences sharedPreferences = getSharedPreferences(RECEIPTS_PREFS, Context.MODE_PRIVATE);

        // Create a JSON Array to store all receipts
        JSONArray receiptArray;
        String existingReceipts = sharedPreferences.getString(RECEIPTS_KEY, "");

        if (existingReceipts.isEmpty()) {
            receiptArray = new JSONArray();
        } else {
            try {
                receiptArray = new JSONArray(existingReceipts);
            } catch (JSONException e) {
                receiptArray = new JSONArray();
                Log.e(TAG, "Error parsing existing receipts: " + e.getMessage());
            }
        }

        // Add new receipt
        receiptArray.put(receiptData);

        // Save updated receipts
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RECEIPTS_KEY, receiptArray.toString());
        editor.apply();

        Log.d(TAG, "Receipt saved to history");
    }

    private List<JSONObject> getAllSavedReceipts() {
        List<JSONObject> receiptList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences(RECEIPTS_PREFS, Context.MODE_PRIVATE);
        String receiptsJson = sharedPreferences.getString(RECEIPTS_KEY, "");

        if (!receiptsJson.isEmpty()) {
            try {
                JSONArray receiptsArray = new JSONArray(receiptsJson);
                for (int i = 0; i < receiptsArray.length(); i++) {
                    receiptList.add(receiptsArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error retrieving receipts: " + e.getMessage());
            }
        }

        return receiptList;
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}