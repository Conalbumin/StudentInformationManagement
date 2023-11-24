package com.example.studentinformationmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView ic_add_user;
    private Spinner sort;
    private SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // khai bao
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        ic_add_user = findViewById(R.id.ic_add_user);
        auth = FirebaseAuth.getInstance();
        sort = findViewById(R.id.sort);
        searchBar = findViewById(R.id.search);

        // Check login status using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // If the user is not logged in, redirect to the Login activity
        if (!isLoggedIn) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();  // Finish the current activity to prevent going back to it when pressing back
        }

        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StudentManagement.class);
            startActivity(intent);
            finish();
        });

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            finish();
        });

        ic_add_user.setOnClickListener(view -> {
            // Call the method to add a new user
            UserManagement.addNewUser();
        });
    }
}
