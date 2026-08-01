package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

public class SettingsActivity extends AppCompatActivity {
    private Switch darkModeSwitch;
    private EditText nameEditText;
    private EditText passwordEditText;
    private TextView currentNameText;
    private Button saveProfileButton;
    private CardView helpSupportCard;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;
    private MyDBHelper dbHelper;
    private String userEmail;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences and database helper
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        dbHelper = new MyDBHelper(this);

        // Get user email and type from intent
        userEmail = getIntent().getStringExtra("EMAIL");
        userType = getIntent().getStringExtra("USER_TYPE");

        // Initialize views
        initializeViews();

        // Load current settings
        loadCurrentSettings();

        // Set up click listeners
        setupListeners();
    }

    private void initializeViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        currentNameText = findViewById(R.id.currentNameText);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        helpSupportCard = findViewById(R.id.helpSupportCard);
        logoutButton = findViewById(R.id.logoutButton);

        // Set initial dark mode state
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);
    }

    private void loadCurrentSettings() {
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);

        String currentName = "";

        // Ensure userType and userEmail are not null before proceeding
        if (userType != null && userEmail != null) {
            if (userType.equals("student")) {
                currentName = dbHelper.getStudentNameByEmail(userEmail);
            } else if (userType.equals("teacher")) {
                currentName = dbHelper.getTeacherNameByEmail(userEmail);
            } else if (userType.equals("admin")) {
                currentName = dbHelper.getAdminNameByEmail(userEmail);
            }
        }

        // If name is null or empty, assign default name based on user type
        if (currentName == null || currentName.trim().isEmpty()) {
            if ("student".equals(userType)) {
                currentName = "Student User";
            } else if ("teacher".equals(userType)) {
                currentName = "Teacher User";
            } else if ("admin".equals(userType)) {
                currentName = "Admin User";
            } else {
                currentName = "User";
            }
        }

        // Set the fetched or default name in UI
        currentNameText.setText("Current Name: " + currentName);
        nameEditText.setHint(currentName);
    }

    private void setupListeners() {
        // Dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        });

        // Save profile button listener
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());

        // Help and Support card listener
        helpSupportCard.setOnClickListener(v -> showHelpAndSupport());

        // Logout button listener
        logoutButton.setOnClickListener(v -> logout());
    }

    private void saveProfileChanges() {
        String newName = nameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        // Check if both fields are filled
        if (!newName.isEmpty() && !newPassword.isEmpty()) {
            // Always show success message if both fields are filled
            Toast.makeText(this, "Successfully saved!", Toast.LENGTH_SHORT).show();

            // Update the database with the new name and password if provided
            if (userType != null && userEmail != null) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String tableName = "student".equals(userType) ?
                        MyDBHelper.TABLE_NAME : ("teacher".equals(userType) ? MyDBHelper.TEACHER_TABLE_NAME : MyDBHelper.ADMIN_TABLE_NAME);

                try {
                    // Update both name and password in the database
                    if (!newName.isEmpty()) {
                        db.execSQL("UPDATE " + tableName + " SET FULL_NAME = ? WHERE EMAIL = ?",
                                new String[]{newName, userEmail});
                    }
                    if (!newPassword.isEmpty()) {
                        db.execSQL("UPDATE " + tableName + " SET PASSWORD = ? WHERE EMAIL = ?",
                                new String[]{newPassword, userEmail});
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving changes.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // If one or both fields are empty, show the error message
            Toast.makeText(this, "Please fill in both fields!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showHelpAndSupport() {
        HelpSupportDialog helpDialog = new HelpSupportDialog();
        helpDialog.show(getSupportFragmentManager(), "HelpSupportDialog");
    }

    private void logout() {
        // Clear SharedPreferences to log out the user
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to SelectionActivity
        Intent intent = new Intent(SettingsActivity.this, SelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
