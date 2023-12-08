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
    private EditText txtUsername, txtUserAge, txtUserPhone, txtUserRole, editTextEmail, editTextPassword;
    private Switch txtUserStatus;
    private Button btnAddUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize UI elements
        ic_close = findViewById(R.id.ic_close);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        txtUsername = findViewById(R.id.txtUsername);
        txtUserAge = findViewById(R.id.txtUserAge);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        txtUserRole = findViewById(R.id.txtUserRole);
        txtUserStatus = findViewById(R.id.txtUserStatus);
        btnAddUser = findViewById(R.id.btnAddUser);

        // Set onClickListener for close button
        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        // Set onClickListener for add user button
        btnAddUser.setOnClickListener(view -> {
            // Retrieve data from UI elements
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = txtUsername.getText().toString();
            int age = Integer.parseInt(txtUserAge.getText().toString());
            String phoneNumber = txtUserPhone.getText().toString();
            String role = txtUserRole.getText().toString();
            boolean status = txtUserStatus.isChecked();

            UserManagement.addNewUser(email, password, name, age, phoneNumber, status, role);
            finish();
        });
    }
}
