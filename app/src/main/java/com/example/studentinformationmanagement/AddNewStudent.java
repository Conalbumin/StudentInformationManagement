package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewStudent extends AppCompatActivity {
    private ImageView ic_close;
    private EditText txtStudentId, txtStudentName, txtUserGender,
            txtUserDate, txtStudentCer;
    private Button btnAddStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Initialize UI elements
        ic_close = findViewById(R.id.ic_close);
        txtStudentId = findViewById(R.id.txtStudentId);
        txtStudentName = findViewById(R.id.txtStudentName);
        txtUserGender = findViewById(R.id.txtUserGender);
        txtUserDate = findViewById(R.id.txtUserDate);
        txtStudentCer = findViewById(R.id.txtStudentCer);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        // Set onClickListener for close button (replace with your actual click logic)
        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        // Set onClickListener for add student button (replace with your actual click logic)
        btnAddStudent.setOnClickListener(view -> {
            // Handle btnAddStudent click
        });
    }
}
