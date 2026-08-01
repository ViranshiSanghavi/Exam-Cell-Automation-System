package com.example.login;

import android.net.Uri;

public class QuestionPaper {

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
