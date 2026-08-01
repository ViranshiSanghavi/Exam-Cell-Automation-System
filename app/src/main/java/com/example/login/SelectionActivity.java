package com.example.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SelectionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userRole = sharedPreferences.getString("userRole", "");

        setContentView(R.layout.activity_selection);

        Button buttonStudent = findViewById(R.id.button_student);
        Button buttonTeacher = findViewById(R.id.button_teacher);
        Button buttonAdmin = findViewById(R.id.button_admin); // Added Admin button

        // Check permissions on create
        if (!checkPermissions()) {
            requestPermissions();
        }

        buttonStudent.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, StudentLoginActivity.class);
            startActivity(intent);
        });

        buttonTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
        });

        // Set click listener for Admin button
        buttonAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int identityPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED &&
                identityPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.GET_ACCOUNTS
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
