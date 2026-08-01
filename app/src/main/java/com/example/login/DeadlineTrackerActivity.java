package com.example.login;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeadlineTrackerActivity extends AppCompatActivity {
    private EditText eventTitle;
    private TextView deadlineDisplay;
    private Button setDeadlineButton, saveDeadlineButton;
    private Spinner recipientSpinner;
    private Calendar selectedDate;
    private RecyclerView deadlinesRecyclerView;
    private DeadlineAdapter deadlineAdapter;
    private List<Deadline> deadlines;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "DeadlinePrefs";
    private static final String KEY_DEADLINES = "deadlines";

    // Notification channel IDs for different recipients
    private static final String CHANNEL_ID_STUDENT = "deadline_notifications_student";
    private static final String CHANNEL_ID_TEACHER = "deadline_notifications_teacher";
    private static final String CHANNEL_ID_BOTH = "deadline_notifications_both";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_tracker);

        initializeViews();
        setupRecyclerView();
        loadDeadlines();
        setupListeners();
        setupRecipientSpinner();
        createNotificationChannels();
    }

    private void initializeViews() {
        eventTitle = findViewById(R.id.eventTitle);
        deadlineDisplay = findViewById(R.id.deadlineDisplay);
        setDeadlineButton = findViewById(R.id.setDeadlineButton);
        saveDeadlineButton = findViewById(R.id.saveDeadlineButton);
        recipientSpinner = findViewById(R.id.recipientSpinner);
        deadlinesRecyclerView = findViewById(R.id.deadlinesRecyclerView);
        selectedDate = Calendar.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void setupRecipientSpinner() {
        // Create the adapter with built-in layouts that specify text colors
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipient_options, android.R.layout.simple_spinner_item);

        // Set the dropdown view resource (what appears when spinner is clicked)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        recipientSpinner.setAdapter(adapter);

        // Set the text color directly on the spinner view
        recipientSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                // Change the text color of the selected item
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        deadlines = new ArrayList<>();
        deadlineAdapter = new DeadlineAdapter(deadlines, this::confirmDeleteDeadline);
        deadlinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deadlinesRecyclerView.setAdapter(deadlineAdapter);
    }

    private void loadDeadlines() {
        String jsonDeadlines = sharedPreferences.getString(KEY_DEADLINES, null);
        if (jsonDeadlines != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Deadline>>() {}.getType();
            deadlines.clear();
            deadlines.addAll(gson.fromJson(jsonDeadlines, type));
            deadlineAdapter.notifyDataSetChanged();
        }
    }

    private void saveDeadlines() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonDeadlines = gson.toJson(deadlines);
        editor.putString(KEY_DEADLINES, jsonDeadlines);
        editor.apply();
    }

    private void setupListeners() {
        setDeadlineButton.setOnClickListener(v -> pickDateTime());
        saveDeadlineButton.setOnClickListener(v -> saveDeadline());
    }

    private void pickDateTime() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);
                updateDeadlineDisplay();
            }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), false).show();
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDeadlineDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        deadlineDisplay.setText("Deadline: " + sdf.format(selectedDate.getTime()));
    }

    private void saveDeadline() {
        String title = eventTitle.getText().toString().trim();
        String recipientType = recipientSpinner.getSelectedItem().toString();

        if (title.isEmpty() || deadlineDisplay.getText().toString().equals("Deadline: Not Set")) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        Deadline deadline = new Deadline(title, selectedDate.getTime(), recipientType);
        deadlines.add(deadline);
        deadlineAdapter.notifyItemInserted(deadlines.size() - 1);
        saveDeadlines();

        sendNotification(recipientType, title);

        eventTitle.setText("");
        deadlineDisplay.setText("Deadline: Not Set");
        Toast.makeText(this, "Deadline saved", Toast.LENGTH_SHORT).show();
    }

    // Create notification channels for different recipients
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Student channel
            NotificationChannel channelStudent = new NotificationChannel(
                    CHANNEL_ID_STUDENT,
                    "Student Deadline Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelStudent);

            // Teacher channel
            NotificationChannel channelTeacher = new NotificationChannel(
                    CHANNEL_ID_TEACHER,
                    "Teacher Deadline Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelTeacher);

            // Both channel
            NotificationChannel channelBoth = new NotificationChannel(
                    CHANNEL_ID_BOTH,
                    "All Users Deadline Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channelBoth);
        }
    }

    private void sendNotification(String recipientType, String deadlineTitle) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String deadlineTime = sdf.format(selectedDate.getTime());

        Intent intent = new Intent(this, NoticeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Determine notification channel and notification ID based on recipient type
        String channelId;
        int notificationId;
        String notificationTitle = "New Deadline: " + deadlineTitle;
        String notificationContent = "Due by " + deadlineTime;

        switch (recipientType) {
            case "Student":
                channelId = CHANNEL_ID_STUDENT;
                notificationId = 1;
                break;
            case "Teacher":
                channelId = CHANNEL_ID_TEACHER;
                notificationId = 2;
                break;
            case "Both":
                channelId = CHANNEL_ID_BOTH;
                notificationId = 3;
                break;
            default:
                channelId = CHANNEL_ID_BOTH;
                notificationId = 4;
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());

        // If recipient type is "Both", send separate notifications to Student and Teacher channels
        if (recipientType.equals("Both")) {
            // Student notification
            NotificationCompat.Builder builderStudent = new NotificationCompat.Builder(this, CHANNEL_ID_STUDENT)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Student: " + notificationTitle)
                    .setContentText(notificationContent)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            // Teacher notification
            NotificationCompat.Builder builderTeacher = new NotificationCompat.Builder(this, CHANNEL_ID_TEACHER)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Teacher: " + notificationTitle)
                    .setContentText(notificationContent)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(5, builderStudent.build());
            notificationManager.notify(6, builderTeacher.build());
        }
    }

    // Show confirmation dialog before deleting deadline
    private void confirmDeleteDeadline(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this deadline?");

        // Yes button
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteDeadline(position);
        });

        // No button
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteDeadline(int position) {
        deadlines.remove(position);
        deadlineAdapter.notifyItemRemoved(position);
        saveDeadlines();
        Toast.makeText(this, "Deadline deleted", Toast.LENGTH_SHORT).show();
    }
}