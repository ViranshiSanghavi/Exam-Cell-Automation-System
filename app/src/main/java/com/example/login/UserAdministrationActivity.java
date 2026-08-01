package com.example.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserAdministrationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FloatingActionButton fabAdd;
    private SearchView searchView;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_administration);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        searchView = findViewById(R.id.searchView);

        loadUsers();

        userAdapter = new UserAdapter(userList, position -> {
            userList.remove(position);
            userAdapter.notifyItemRemoved(position);
            saveUsers();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        fabAdd.setOnClickListener(v -> showUserTypeDialog());
        setupSearch();
        setupBackButtonHandler();
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveUsers();
                finish();
            }
        });
    }

    private void loadUsers() {
        String jsonUsers = sharedPreferences.getString(KEY_USERS, null);
        if (jsonUsers != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<User>>() {}.getType();
            userList = gson.fromJson(jsonUsers, type);
        } else {
            userList = new ArrayList<>();
        }
    }

    private void saveUsers() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(KEY_USERS, gson.toJson(userList));
        editor.apply();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void showUserTypeDialog() {
        final String[] userTypes = {"Student", "Teacher", "Admin"};
        new AlertDialog.Builder(this)
                .setTitle("Select User Type")
                .setItems(userTypes, (dialog, which) -> {
                    if (which == 0) showStudentDialog();
                    else if (which == 1) showTeacherDialog();
                    else showAdminDialog();
                }).show();
    }

    private void showStudentDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        EditText etName = view.findViewById(R.id.etName);
        Spinner spinnerDepartment = view.findViewById(R.id.spinnerDepartment);
        Spinner spinnerClass = view.findViewById(R.id.spinnerClass);

        setupSpinner(spinnerDepartment, new String[]{"IT", "DS", "AI/ML"});
        setupSpinner(spinnerClass, new String[]{"FY", "SY", "TY"});

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Add Student")
                .setPositiveButton("Add", (dialog, which) -> addUser(etName, "Student", spinnerDepartment, spinnerClass))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTeacherDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_teacher, null);
        EditText etName = view.findViewById(R.id.etName);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Add Teacher")
                .setPositiveButton("Add", (dialog, which) -> addUser(etName, "Teacher", null, null))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAdminDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_admin, null);
        EditText etName = view.findViewById(R.id.etName);
        EditText etRole = view.findViewById(R.id.etRole);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Add Admin")
                .setPositiveButton("Add", (dialog, which) -> {
                    if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etRole.getText())) {
                        User admin = new User(etName.getText().toString(), "Admin", etRole.getText().toString(), "");
                        userList.add(admin);
                        userAdapter.notifyItemInserted(userList.size() - 1);
                        saveUsers();
                    } else {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void addUser(EditText etName, String role, Spinner spinnerDept, Spinner spinnerClass) {
        if (!TextUtils.isEmpty(etName.getText())) {
            String dept = spinnerDept != null ? spinnerDept.getSelectedItem().toString() : "";
            String classYear = spinnerClass != null ? spinnerClass.getSelectedItem().toString() : "";
            User user = new User(etName.getText().toString(), role, dept, classYear);
            userList.add(user);
            userAdapter.notifyItemInserted(userList.size() - 1);
            saveUsers();
        } else {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUsers();
    }
}