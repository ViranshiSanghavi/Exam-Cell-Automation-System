package com.example.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoticeBoardActivity extends Activity {

    private ListView notificationListView;
    private LinearLayout emptyView;
    private List<String> notifications;
    private NoticeAdapter adapter;
    private ImageView addNoticeButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "NoticePrefs";
    private static final String KEY_NOTICES = "notices";
    private static final String CHANNEL_ID = "NoticeBoard_Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        createNotificationChannel();

        notificationListView = findViewById(R.id.notification_list);
        emptyView = findViewById(R.id.empty_view);

        ImageView backButton = findViewById(R.id.back_button);
        addNoticeButton = findViewById(R.id.add_notice_button);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        notifications = new ArrayList<>(getSavedNotices());

        adapter = new NoticeAdapter();
        notificationListView.setAdapter(adapter);

        updateUI();

        addNoticeButton.setOnClickListener(v -> showAddNoticeDialog());

        backButton.setOnClickListener(v -> showExitConfirmation());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notice Board Channel";
            String description = "Channel for Notice Board Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String noticeText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Notice")
                .setContentText(noticeText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showAddNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Notice");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_notice, null);
        EditText noticeInput = dialogView.findViewById(R.id.notice_input);
        RadioGroup categoryGroup = dialogView.findViewById(R.id.category_group);
        RadioButton studentOption = dialogView.findViewById(R.id.radio_student);
        RadioButton teacherOption = dialogView.findViewById(R.id.radio_teacher);
        RadioButton bothOption = dialogView.findViewById(R.id.radio_both);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String noticeText = noticeInput.getText().toString().trim();
            if (!noticeText.isEmpty()) {
                String category = "";
                if (studentOption.isChecked()) category = "[Student] ";
                else if (teacherOption.isChecked()) category = "[Teacher] ";
                else if (bothOption.isChecked()) category = "[Everyone] ";

                String finalNotice = category + noticeText;
                notifications.add(0, finalNotice);
                saveNotices();
                adapter.notifyDataSetChanged();
                updateUI();

                sendNotification(finalNotice);

                Toast toast = Toast.makeText(this, "Notice added successfully!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Please enter a notice!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUI() {
        if (notifications.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            notificationListView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            notificationListView.setVisibility(View.VISIBLE);
        }
    }

    private void saveNotices() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> noticeSet = new HashSet<>(notifications);
        editor.putStringSet(KEY_NOTICES, noticeSet);
        editor.apply();
    }

    private Set<String> getSavedNotices() {
        return sharedPreferences.getStringSet(KEY_NOTICES, new HashSet<>());
    }

    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to leave?");

        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private class NoticeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Object getItem(int position) {
            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NoticeBoardActivity.this)
                        .inflate(R.layout.list_item_notice, parent, false);
            }

            TextView noticeText = convertView.findViewById(R.id.notice_text);
            ImageView deleteButton = convertView.findViewById(R.id.delete_button); // Changed to ImageView

            noticeText.setText(notifications.get(position));

            deleteButton.setOnClickListener(v -> {
                showDeleteConfirmation(position);
            });

            return convertView;
        }
    }

    private void showDeleteConfirmation(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Notice");
        builder.setMessage("Are you sure you want to delete this notice?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String deletedNotice = notifications.get(position);
            notifications.remove(position);
            saveNotices();
            adapter.notifyDataSetChanged();
            updateUI();

            sendNotification("Notice Deleted: " + deletedNotice);

            Toast toast = Toast.makeText(this, "Notice deleted successfully!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_red_dark));
    }
}