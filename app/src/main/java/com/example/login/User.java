package com.example.login;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String type;
    private String department;
    private String classYear;

    public User(String name, String type, String department, String classYear) {
        this.name = name;
        this.type = type;
        this.department = department;
        this.classYear = classYear;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDepartment() {
        return department;
    }

    public String getClassYear() {
        return classYear;
    }

    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setClassYear(String classYear) {
        this.classYear = classYear;
    }
}
