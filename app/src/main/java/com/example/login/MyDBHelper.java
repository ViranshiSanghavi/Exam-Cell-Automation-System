package com.example.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    public static final String Database_Name = "MyDatabase";
    public static final int Database_Version = 3; // Incremented version

    public static final String TABLE_NAME = "Student_Table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "FULL_NAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "CONTACT_NUMBER";
    public static final String COL_5 = "COURSE";
    public static final String COL_6 = "DIVISION";

    public static final String TEACHER_TABLE_NAME = "Teacher_Table";
    public static final String TEACHER_COL_1 = "ID";
    public static final String TEACHER_COL_2 = "FULL_NAME";
    public static final String TEACHER_COL_3 = "EMAIL";
    public static final String TEACHER_COL_4 = "CONTACT_NUMBER";
    public static final String TEACHER_COL_5 = "SUBJECT";
    public static final String TEACHER_COL_6 = "DEPARTMENT";

    // Added missing ADMIN table constants
    public static final String ADMIN_TABLE_NAME = "Admin_Table";
    public static final String ADMIN_COL_1 = "ID";
    public static final String ADMIN_COL_2 = "FULL_NAME";
    public static final String ADMIN_COL_3 = "EMAIL";
    public static final String ADMIN_COL_4 = "CONTACT_NUMBER";
    public static final String ADMIN_COL_5 = "ROLE";

    public MyDBHelper(@Nullable Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentTableQuery = "CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FULL_NAME TEXT, EMAIL TEXT, CONTACT_NUMBER TEXT, COURSE TEXT, DIVISION TEXT)";
        db.execSQL(createStudentTableQuery);

        String createTeacherTableQuery = "CREATE TABLE " + TEACHER_TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FULL_NAME TEXT, EMAIL TEXT, CONTACT_NUMBER TEXT, SUBJECT TEXT, DEPARTMENT TEXT)";
        db.execSQL(createTeacherTableQuery);

        String createAdminTableQuery = "CREATE TABLE " + ADMIN_TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FULL_NAME TEXT, EMAIL TEXT, CONTACT_NUMBER TEXT, ROLE TEXT)";
        db.execSQL(createAdminTableQuery);

        // Insert default data
        insertDefaultStudentData(db);
        insertDefaultTeacherData(db);
        insertDefaultAdminData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEACHER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ADMIN_TABLE_NAME);
        onCreate(db);
    }

    private void insertDefaultStudentData(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, "Viranshi Alpesh Sanghavi");
        contentValues.put(COL_3, "sanghaviviranshi@gmail.com");
        contentValues.put(COL_4, "9930701402");
        contentValues.put(COL_5, "TYBSc DS");
        contentValues.put(COL_6, "B");
        db.insert(TABLE_NAME, null, contentValues);

        contentValues.put(COL_2, "Devanshi Nimesh Joshi");
        contentValues.put(COL_3, "devanshi@gmail.com");
        contentValues.put(COL_4, "9833300151");
        contentValues.put(COL_5, "TYBSc IT");
        contentValues.put(COL_6, "B");
        db.insert(TABLE_NAME, null, contentValues);
    }

    private void insertDefaultTeacherData(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TEACHER_COL_2, "Pranav Milind Kawatkar");
        contentValues.put(TEACHER_COL_3, "pranav@tcsc.com");
        contentValues.put(TEACHER_COL_4, "8850462799");
        contentValues.put(TEACHER_COL_5, "Artificial Intelligence");
        contentValues.put(TEACHER_COL_6, "IT");
        db.insert(TEACHER_TABLE_NAME, null, contentValues);

        contentValues.put(TEACHER_COL_2, "Khushi Ravi Gorakh");
        contentValues.put(TEACHER_COL_3, "khushi@tcsc.com");
        contentValues.put(TEACHER_COL_4, "9167345807");
        contentValues.put(TEACHER_COL_5, "Software Project Management");
        contentValues.put(TEACHER_COL_6, "DS");
        db.insert(TEACHER_TABLE_NAME, null, contentValues);
    }

    private void insertDefaultAdminData(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ADMIN_COL_2, "Rajesh Kumar");
        contentValues.put(ADMIN_COL_3, "rajesh@admin.com");
        contentValues.put(ADMIN_COL_4, "9876543210");
        contentValues.put(ADMIN_COL_5, "Principal");
        db.insert(ADMIN_TABLE_NAME, null, contentValues);

        contentValues.put(ADMIN_COL_2, "Priya Sharma");
        contentValues.put(ADMIN_COL_3, "priya@admin.com");
        contentValues.put(ADMIN_COL_4, "8765432109");
        contentValues.put(ADMIN_COL_5, "Vice Principal");
        db.insert(ADMIN_TABLE_NAME, null, contentValues);
    }

    public String getStudentNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FULL_NAME FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public String getStudentDepartmentByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COURSE FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public String getStudentDivisionByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DIVISION FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public String getTeacherNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FULL_NAME FROM " + TEACHER_TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public String getAdminNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FULL_NAME FROM " + ADMIN_TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    // New method added to fetch student details by course and year
    public Cursor getStudentDetailsByCourseAndYear(String course, String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE COURSE LIKE ? AND FULL_NAME LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + course + "%", "%" + year + "%"});
        return cursor;
    }
}
