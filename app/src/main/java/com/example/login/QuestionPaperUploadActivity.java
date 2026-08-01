package com.example.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class QuestionPaperUploadActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private static final long MAX_FILE_SIZE = 73 * 1024; // 73 KB
    private static final int MAX_FILES_ALLOWED = 3;
    private LinearLayout uploadListContainer;
    private Button uploadButton, compressPdfButton;
    private int currentUploadCount = 0;

    private final Calendar deadline = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper_upload);

        // Set deadline: March 27, 2025
        deadline.set(2025, Calendar.MARCH, 27, 0, 0, 0);

        uploadListContainer = findViewById(R.id.uploadListContainer);
        uploadButton = findViewById(R.id.uploadButton);
        compressPdfButton = findViewById(R.id.compressPdfButton);

        TextView deadlineTextView = findViewById(R.id.deadlineTextView);
        deadlineTextView.setText("Deadline: 27-03-2025, 12 AM");

        checkStoragePermissions();

        uploadButton.setOnClickListener(view -> {
            if (isDeadlinePassed()) {
                showDeadlineError();
            } else {
                openFileChooser();
            }
        });

        compressPdfButton.setOnClickListener(view -> openCompressionLink());
    }

    private void checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            Log.d("FilePicker", "Selected File URI: " + fileUri.toString());
            addFileToUploadList(fileUri);
        } else {
            Log.e("FilePicker", "File selection failed");
        }
    }

    private void addFileToUploadList(Uri fileUri) {
        if (currentUploadCount >= MAX_FILES_ALLOWED) {
            Toast.makeText(this, "Maximum upload limit reached!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isFileSizeValid(fileUri)) {
            Toast.makeText(this, "File size exceeds 73 KB limit. Please select a smaller PDF.", Toast.LENGTH_LONG).show();
            return;
        }

        View fileUploadView = LayoutInflater.from(this).inflate(R.layout.item_file_upload, uploadListContainer, false);

        TextView fileNameTextView = fileUploadView.findViewById(R.id.fileNameTextView);
        ProgressBar progressBar = fileUploadView.findViewById(R.id.fileProgressBar);
        TextView statusTextView = fileUploadView.findViewById(R.id.statusTextView);
        TextView fileSizeTextView = fileUploadView.findViewById(R.id.fileSizeTextView);
        Button downloadButton = fileUploadView.findViewById(R.id.downloadButton);
        Button printButton = fileUploadView.findViewById(R.id.printButton);
        Button updateButton = fileUploadView.findViewById(R.id.updateButton);

        String fileName = getFileName(fileUri);
        fileNameTextView.setText(fileName);
        fileSizeTextView.setText(getFileSize(fileUri));
        progressBar.setProgress(0);

        uploadListContainer.addView(fileUploadView);
        currentUploadCount++;

        downloadButton.setOnClickListener(v -> downloadFile(fileUri));
        printButton.setOnClickListener(v -> printFile(fileUri));
        updateButton.setOnClickListener(v -> {
            if (isDeadlinePassed()) {
                showDeadlineError();
            } else {
                updateFile(fileUri);
            }
        });

        simulateFileUpload(progressBar, statusTextView);
    }

    private boolean isFileSizeValid(Uri fileUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            return inputStream != null && inputStream.available() <= MAX_FILE_SIZE;
        } catch (IOException e) {
            return false;
        }
    }

    private String getFileSize(Uri fileUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            return inputStream != null ? (inputStream.available() / 1024) + " KB" : "Unknown";
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            }
        }
        return result != null ? result : uri.getLastPathSegment();
    }

    private void simulateFileUpload(ProgressBar progressBar, TextView statusTextView) {
        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress += 10) {
                try {
                    Thread.sleep(500);
                    int currentProgress = progress;
                    runOnUiThread(() -> {
                        progressBar.setProgress(currentProgress);
                        statusTextView.setText(currentProgress + "% uploaded");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> statusTextView.setText("Upload Complete"));
        }).start();
    }

    private void openCompressionLink() {
        String url = "https://pdf.pi7.org/pdf-resize-in-kb";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void downloadFile(Uri fileUri) {
        Toast.makeText(this, "Download initiated for: " + getFileName(fileUri), Toast.LENGTH_SHORT).show();
    }

    private void printFile(Uri fileUri) {
        Toast.makeText(this, "Print initiated for: " + getFileName(fileUri), Toast.LENGTH_SHORT).show();
    }

    private void updateFile(Uri fileUri) {
        Toast.makeText(this, "Update initiated for: " + getFileName(fileUri), Toast.LENGTH_SHORT).show();
    }

    private boolean isDeadlinePassed() {
        return Calendar.getInstance().after(deadline);
    }

    private void showDeadlineError() {
        Toast.makeText(this, "The deadline has passed. Please contact the admin.", Toast.LENGTH_LONG).show();
    }
}