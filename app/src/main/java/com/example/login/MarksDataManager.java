package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarksDataManager {
    private static MarksDataManager instance;
    private static final String PREFS_NAME = "MarksDataPrefs";
    private static final String CLASS_MARKS_KEY = "classMarksMap";
    private static final String TAG = "MarksDataManager";
    private static final String DEFAULT_CLASS_NAME = "Unclassified";
    private static final String DEFAULT_STUDENT_NAME = "Unknown Student";
    private static final String DEFAULT_SUBJECT = "Unknown Subject";

    private Context applicationContext;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private MarksDataManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.applicationContext = context.getApplicationContext();
        this.sharedPreferences = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder()
                .serializeNulls()
                .create();

        // Ensure initial data structure exists
        initializeDataIfNeeded();
    }

    public static synchronized MarksDataManager getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (instance == null) {
            instance = new MarksDataManager(context.getApplicationContext());
        }
        return instance;
    }

    // Ensure a default data structure always exists
    private void initializeDataIfNeeded() {
        String existingData = sharedPreferences.getString(CLASS_MARKS_KEY, null);
        if (TextUtils.isEmpty(existingData)) {
            // Create and save a default data structure
            Map<String, List<StudentMarks>> defaultMap = new HashMap<>();
            defaultMap.put(DEFAULT_CLASS_NAME, new ArrayList<>());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CLASS_MARKS_KEY, gson.toJson(defaultMap));
            editor.apply();
        }
    }

    public void saveStudentMarks(String className, StudentMarks studentMarks) {
        // Validate and sanitize inputs
        className = sanitizeClassName(className);

        if (studentMarks == null) {
            studentMarks = new StudentMarks(
                    DEFAULT_STUDENT_NAME,
                    DEFAULT_SUBJECT,
                    0, 0, 0, System.currentTimeMillis()
            );
        }

        // Sanitize student marks
        studentMarks.studentName = sanitizeStudentName(studentMarks.studentName);
        studentMarks.subject = sanitizeSubject(studentMarks.subject);

        try {
            Map<String, List<StudentMarks>> classMarksMap = getAllClassMarks();

            // Ensure the class exists in the map
            if (!classMarksMap.containsKey(className)) {
                classMarksMap.put(className, new ArrayList<>());
            }

            // Remove existing entry if it exists
            StudentMarks finalStudentMarks = studentMarks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                classMarksMap.get(className).removeIf(
                        marks -> marks != null &&
                                marks.studentName != null &&
                                marks.subject != null &&
                                marks.studentName.equals(finalStudentMarks.studentName) &&
                                marks.subject.equals(finalStudentMarks.subject)
                );
            }

            // Add new entry
            classMarksMap.get(className).add(studentMarks);

            // Save updated map
            saveAllClassMarks(classMarksMap);
        } catch (Exception e) {
            Log.e(TAG, "Error saving student marks", e);
            // Reinitialize data structure if something goes wrong
            initializeDataIfNeeded();
        }
    }

    public Map<String, List<StudentMarks>> getAllClassMarks() {
        try {
            // Ensure data structure exists
            initializeDataIfNeeded();

            String marksJson = sharedPreferences.getString(CLASS_MARKS_KEY, null);
            if (!TextUtils.isEmpty(marksJson)) {
                Type type = new TypeToken<Map<String, List<StudentMarks>>>(){}.getType();
                Map<String, List<StudentMarks>> savedMap = gson.fromJson(marksJson, type);

                // Fallback to default if deserialization fails
                return savedMap != null ? savedMap : createDefaultMarksMap();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all class marks", e);
        }

        // If all else fails, return a default map
        return createDefaultMarksMap();
    }

    private Map<String, List<StudentMarks>> createDefaultMarksMap() {
        Map<String, List<StudentMarks>> defaultMap = new HashMap<>();
        defaultMap.put(DEFAULT_CLASS_NAME, new ArrayList<>());
        return defaultMap;
    }

    private void saveAllClassMarks(Map<String, List<StudentMarks>> classMarksMap) {
        try {
            // Ensure we always have a valid map
            if (classMarksMap == null) {
                classMarksMap = createDefaultMarksMap();
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            String marksJson = gson.toJson(classMarksMap);
            editor.putString(CLASS_MARKS_KEY, marksJson);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving all class marks", e);
            // Reinitialize if save fails
            initializeDataIfNeeded();
        }
    }

    public List<StudentMarks> getMarksForClass(String className) {
        // Sanitize className
        className = sanitizeClassName(className);

        Map<String, List<StudentMarks>> allMarks = getAllClassMarks();
        List<StudentMarks> classMarks = allMarks.get(className);
        return classMarks != null ? classMarks : new ArrayList<>();
    }

    // Clear all marks (optional method)
    public void clearAllMarks() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CLASS_MARKS_KEY);
            editor.apply();

            // Immediately reinitialize with default structure
            initializeDataIfNeeded();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing marks", e);
        }
    }

    // Helper methods for sanitization
    private String sanitizeClassName(String className) {
        return TextUtils.isEmpty(className) ? DEFAULT_CLASS_NAME : className.trim();
    }

    private String sanitizeStudentName(String studentName) {
        return TextUtils.isEmpty(studentName) ? DEFAULT_STUDENT_NAME : studentName.trim();
    }

    private String sanitizeSubject(String subject) {
        return TextUtils.isEmpty(subject) ? DEFAULT_SUBJECT : subject.trim();
    }

    public static class StudentMarks {
        public String studentName;
        public String subject;
        public double marks;
        public double percentage;
        public double cgpa;
        public long timestamp;

        public StudentMarks(String studentName, String subject, double marks, double percentage, double cgpa, long timestamp) {
            // Sanitize inputs
            this.studentName = TextUtils.isEmpty(studentName) ? DEFAULT_STUDENT_NAME : studentName.trim();
            this.subject = TextUtils.isEmpty(subject) ? DEFAULT_SUBJECT : subject.trim();
            this.marks = Math.max(0, Math.min(marks, 100)); // Clamp marks between 0 and 100
            this.percentage = calculatePercentage(this.marks);
            this.cgpa = calculateCGPA(this.percentage);
            this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
        }

        public static double calculatePercentage(double marks) {
            return Math.max(0, Math.min(marks, 100)); // Ensure percentage is between 0 and 100
        }

        public static double calculateCGPA(double percentage) {
            return Math.max(0, Math.min(percentage / 9.5, 10)); // Clamp CGPA between 0 and 10
        }
    }
}