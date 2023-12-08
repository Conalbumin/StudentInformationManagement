package com.example.studentinformationmanagement;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button btnAddUser;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView closeIcon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnAddUser = findViewById(R.id.btnAddUser);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        closeIcon = findViewById(R.id.ic_close);

        closeIcon.setOnClickListener(v -> finish());
        textView.setOnClickListener(v -> navigateToLogin());

        btnAddUser.setOnClickListener(v -> registerUser());
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast("Enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("Enter password");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        showToast("Account created");
                        navigateToLogin();
                    } else {
                        showToast("Authentication failed.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }
}
