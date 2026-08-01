package com.example.login;

import java.util.Date;

public class Deadline {
    private String title;
    private Date deadlineDate;
    private String recipientType;

    public Deadline(String title, Date deadlineDate, String recipientType) {
        this.title = title;
        this.deadlineDate = deadlineDate;
        this.recipientType = recipientType;
    }

    public String getTitle() {
        return title;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public String getRecipientType() {
        return recipientType;
    }
}
