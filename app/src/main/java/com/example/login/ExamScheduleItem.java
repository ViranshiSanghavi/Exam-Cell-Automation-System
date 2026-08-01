package com.example.login;

public class ExamScheduleItem {
    private String subject;
    private String date;
    private String time;

    public ExamScheduleItem(String subject, String date, String time) {
        this.subject = subject;
        this.date = date;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
