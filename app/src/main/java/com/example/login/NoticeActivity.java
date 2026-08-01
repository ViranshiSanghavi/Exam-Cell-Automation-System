package com.example.login;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoticeActivity extends AppCompatActivity {
    private TextView noticeContent;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "DeadlinePrefs";
    private static final String KEY_DEADLINES = "deadlines";

    private String loggedInUserType = "Student"; // Change this dynamically based on actual login

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);


    }

    private void displayNotices() {
        String json = sharedPreferences.getString(KEY_DEADLINES, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Deadline>>() {}.getType();
            List<Deadline> allNotices = gson.fromJson(json, type);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            List<String> filteredNotices = new ArrayList<>();

            for (Deadline notice : allNotices) {
                if (notice.getRecipientType().equals(loggedInUserType) || notice.getRecipientType().equals("Both")) {
                    String formattedDate = sdf.format(new Date(String.valueOf(notice.getDeadlineDate()))); // Convert timestamp to readable date
                    filteredNotices.add("📌 " + notice.getTitle() + "\nDue: " + formattedDate + "\n\n");
                }
            }

            if (filteredNotices.isEmpty()) {
                noticeContent.setText("No notices available.");
            } else {
                noticeContent.setText(String.join("", filteredNotices));
            }
        } else {
            noticeContent.setText("No notices available.");
        }
    }
}
