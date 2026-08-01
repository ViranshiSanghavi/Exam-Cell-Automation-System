package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class QuestionPaperActivity extends AppCompatActivity {

    private LinearLayout questionPaperContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper);

        questionPaperContainer = findViewById(R.id.questionPaperContainer);

        fetchQuestionPapers();
    }

    private void fetchQuestionPapers() {
        // Simulate fetching question papers from a server
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate network delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Simulate data fetched from server
            List<QuestionPaper> fetchedPapers = new ArrayList<>();
            fetchedPapers.add(new QuestionPaper("AI Question Paper - CE1", "Pranav Kawatkar", "2025-01-02", "10:30 AM",
                    Uri.parse("https://drive.google.com/file/d/11HmpyqtHMsaEephQvsvssiYl2s0mNas8/view?usp=sharing")));
            fetchedPapers.add(new QuestionPaper("Software Project Management - CE1", "Khushi Gorakh", "2025-01-01", "11:45 AM",
                    Uri.parse("https://drive.google.com/file/d/11HmpyqtHMsaEephQvsvssiYl2s0mNas8/view?usp=sharing")));

            // Update UI on the main thread
            runOnUiThread(() -> {
                displayQuestionPapers(fetchedPapers);
            });
        }).start();
    }

    private void displayQuestionPapers(List<QuestionPaper> questionPapers) {
        questionPaperContainer.removeAllViews(); // Clear existing views

        for (QuestionPaper paper : questionPapers) {
            View itemView = getLayoutInflater().inflate(R.layout.item_question_paper, questionPaperContainer, false);

            TextView titleTextView = itemView.findViewById(R.id.titleTextView);
            TextView teacherTextView = itemView.findViewById(R.id.teacherTextView);
            TextView dateTextView = itemView.findViewById(R.id.dateTextView);
            TextView timeTextView = itemView.findViewById(R.id.timeTextView);
            Button openButton = itemView.findViewById(R.id.openButton);
            Button downloadButton = itemView.findViewById(R.id.downloadButton);

            titleTextView.setText(paper.getTitle());
            teacherTextView.setText("Teacher: " + paper.getTeacherName());
            dateTextView.setText("Date: " + paper.getDate());
            timeTextView.setText("Time: " + paper.getTime());

            // Open PDF file
            openButton.setOnClickListener(v -> openPdfFile(paper.getFileUri()));

            // Download PDF file (This will use the Google Drive file link)
            downloadButton.setOnClickListener(v -> downloadPdfFile(paper.getFileUri(), paper.getTitle()));

            questionPaperContainer.addView(itemView);
        }
    }

    private void openPdfFile(Uri fileUri) {
        // Convert Google Drive URL to a direct download link
        String driveUrl = fileUri.toString();
        String fileId = driveUrl.split("/d/")[1].split("/")[0];
        String directDownloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;

        // Intent to view PDF from Google Drive URL
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(directDownloadUrl)); // Set the correct format for direct download
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void downloadPdfFile(Uri fileUri, String fileName) {
        // For Google Drive, downloading is not implemented in the same way as local files.
        // You need to manage the file download via external methods or use a download manager.
        Toast.makeText(this, "Download functionality not implemented for Google Drive link", Toast.LENGTH_SHORT).show();
    }

    // Dummy QuestionPaper class to hold data (you should replace it with your own implementation)
    public static class QuestionPaper {
        private String title;
        private String teacherName;
        private String date;
        private String time;
        private Uri fileUri;

        public QuestionPaper(String title, String teacherName, String date, String time, Uri fileUri) {
            this.title = title;
            this.teacherName = teacherName;
            this.date = date;
            this.time = time;
            this.fileUri = fileUri;
        }

        public String getTitle() {
            return title;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public Uri getFileUri() {
            return fileUri;
        }
    }
}
