package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AnswerPaperRecheckingActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "TeacherNotifications";
    private static final String NOTIFICATIONS_KEY = "notifications";

    private ListView lvNotifications;
    private TextView tvEmptyNotifications;
    private ArrayList<String> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_paper_rechecking);

        lvNotifications = findViewById(R.id.lvNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);

        // Get all notifications
        Set<String> notifications = getNotifications();
        notificationList = new ArrayList<>(notifications);

        // Create and set adapter
        NotificationAdapter adapter = new NotificationAdapter(this, notificationList);
        lvNotifications.setAdapter(adapter);

        // Show empty view if list is empty
        if (notificationList.isEmpty()) {
            tvEmptyNotifications.setVisibility(TextView.VISIBLE);
        } else {
            tvEmptyNotifications.setVisibility(TextView.GONE);
        }
    }

    private Set<String> getNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());
    }

    // Save notifications to SharedPreferences
    private void saveNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(NOTIFICATIONS_KEY, new HashSet<>(notificationList));
        editor.apply();
    }

    // Custom Adapter for notifications with CheckBox and Delete Button
    private class NotificationAdapter extends ArrayAdapter<String> {

        NotificationAdapter(Context context, ArrayList<String> notifications) {
            super(context, 0, notifications);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // Create a new LinearLayout as the root view for each item
                LinearLayout rootLayout = new LinearLayout(getContext());
                rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rootLayout.setOrientation(LinearLayout.HORIZONTAL);
                rootLayout.setPadding(16, 16, 16, 16);
                rootLayout.setBackgroundColor(Color.parseColor("#80000000")); // Transparent black background
                rootLayout.setElevation(4); // Add shadow

                // TextView for the notification text
                TextView tvNotification = new TextView(getContext());
                tvNotification.setId(View.generateViewId());
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1); // Weight = 1 to take remaining space
                tvNotification.setLayoutParams(textParams);
                tvNotification.setTextSize(16);
                tvNotification.setTextColor(Color.WHITE); // White text for better contrast
                rootLayout.addView(tvNotification);

                // CheckBox for marking as done
                CheckBox cbDone = new CheckBox(getContext());
                cbDone.setId(View.generateViewId());
                LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cbDone.setLayoutParams(checkBoxParams);
                cbDone.setButtonTintList(ContextCompat.getColorStateList(getContext(), android.R.color.holo_blue_light));
                rootLayout.addView(cbDone);

                // Delete Button (Bin Icon)
                ImageButton btnDelete = new ImageButton(getContext());
                btnDelete.setId(View.generateViewId());
                LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                btnDelete.setLayoutParams(deleteParams);
                btnDelete.setBackground(null); // Remove button background
                btnDelete.setImageResource(android.R.drawable.ic_delete); // Bin icon
                btnDelete.setColorFilter(Color.WHITE); // White color for bin icon
                btnDelete.setPadding(8, 8, 8, 8);
                rootLayout.addView(btnDelete);

                convertView = rootLayout;
            }

            // Get the notification text
            final String notification = getItem(position);

            // Bind data to views
            LinearLayout rootLayout = (LinearLayout) convertView;
            TextView tvNotification = (TextView) rootLayout.getChildAt(0);
            CheckBox cbDone = (CheckBox) rootLayout.getChildAt(1);
            ImageButton btnDelete = (ImageButton) rootLayout.getChildAt(2);

            tvNotification.setText(notification);

            // Handle checkbox state
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Mark the notification as done (you can add logic here if needed)
                    tvNotification.setAlpha(0.5f); // Dim the text when checked
                } else {
                    tvNotification.setAlpha(1.0f); // Restore the text opacity
                }
            });

            // Handle delete button click
            btnDelete.setOnClickListener(v -> {
                // Remove the notification from the list
                notificationList.remove(position);
                saveNotifications(); // Save updated list to SharedPreferences
                notifyDataSetChanged(); // Refresh the list

                // Show a toast message
                Toast.makeText(getContext(), "Notification deleted", Toast.LENGTH_SHORT).show();

                // Show empty view if list is empty
                if (notificationList.isEmpty()) {
                    tvEmptyNotifications.setVisibility(TextView.VISIBLE);
                }
            });

            return convertView;
        }
    }
}