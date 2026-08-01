package com.example.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText emailField, passwordField;
    private Button loginButton;
    private TextView forgotPasswordLink;
    private MyDBHelper dbHelper;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userRole", "Admin"); // Change to "Teacher" or "Admin" accordingly
        editor.apply();

        setContentView(R.layout.activity_admin_login);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        dbHelper = new MyDBHelper(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> checkEmailAndPassword());
        forgotPasswordLink.setOnClickListener(v -> forgotPassword());
    }

    private void checkEmailAndPassword() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }

        String adminName = dbHelper.getAdminNameByEmail(email);
        if (adminName != null && password.equals("admin")) {
            loginSuccessful(email);
        } else if (adminName == null) {
            Toast.makeText(this, "Not a registered admin", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginSuccessful(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.apply();

        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        intent.putExtra("adminEmail", email);
        startActivity(intent);
        finish();
    }

    private void forgotPassword() {
        String email = emailField.getText().toString().trim();
        if (email.isEmpty()) {
            emailField.setError("Please enter your email address");
            emailField.requestFocus();
            return;
        }

        String adminName = dbHelper.getAdminNameByEmail(email);
        if (adminName != null) {
            sendPasswordResetEmail(email);
            Toast.makeText(this, "Password reset instructions sent to your email", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Email not found in our records", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPasswordResetEmail(String email) {
        // TODO: Implement actual email sending logic
        System.out.println("Sending password reset email to: " + email);
        System.out.println("Email content: Your password is 'admin'. Please change it after logging in.");
    }
}