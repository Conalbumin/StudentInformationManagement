package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewCer extends AppCompatActivity {
    private ImageView ic_close;
    private EditText search;
    private Button btnAddCer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificates);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        search = findViewById(R.id.search);
        btnAddCer = findViewById(R.id.btnAddCer);

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        btnAddCer.setOnClickListener(view -> {
            // Handle btnAddCer click
        });
    }
}

