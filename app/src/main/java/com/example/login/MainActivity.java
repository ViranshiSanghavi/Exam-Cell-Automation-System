package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Manually initialize Firebase
        initializeFirebase();

        // Handler to delay the transition to SelectionActivity
        new Handler().postDelayed(() -> {
            // Start SelectionActivity after delay
            Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(intent);
            finish(); // Finish MainActivity to remove it from the back stack
        }, SPLASH_TIME_OUT);
    }

    // Method to manually initialize Firebase using FirebaseOptions
    private void initializeFirebase() {
        // Check if Firebase is already initialized to avoid re-initialization
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:405127512620:android:b77aff59e78b0bd09e0789") // mobilesdk_app_id
                    .setApiKey("AIzaSyDQy-e-e49v6LiEum2B1-jW0HTn7hJDvUw") // current_key
                    .setDatabaseUrl("https://exam-cell-12432.firebaseio.com") // Replace with your database URL
                    .setStorageBucket("exam-cell-12432.appspot.com") // storage_bucket
                    .setProjectId("exam-cell-12432") // project_id
                    .build();

            // Initialize Firebase with the configured options
            FirebaseApp.initializeApp(this, options);
        }
    }
}
