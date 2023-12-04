package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewUser extends AppCompatActivity {
    private ImageView ic_close;
    private EditText txtUsername, txtUserAge, txtUserPhone, txtUserRole;
    private Switch txtUserStatus;
    private Button btnAddUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize UI elements
        ic_close = findViewById(R.id.ic_close);
        txtUsername = findViewById(R.id.txtUsername);
        txtUserAge = findViewById(R.id.txtUserAge);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        txtUserRole = findViewById(R.id.txtUserRole);
        txtUserStatus = findViewById(R.id.txtUserStatus);
        btnAddUser = findViewById(R.id.btnAddUser);

        // Set onClickListener for close button (replace with your actual click logic)
        ic_close.setOnClickListener(view -> {
            // Handle ic_close click
            finish(); // Close the activity
        });

        // Set onClickListener for add user button (replace with your actual click logic)
        btnAddUser.setOnClickListener(view -> {
            // Handle btnAddUser click
        });
    }
}
