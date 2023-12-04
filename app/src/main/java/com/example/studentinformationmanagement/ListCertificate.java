package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ListCertificate extends AppCompatActivity {
    private ImageView ic_close, ic_add_user, ic_delete_user;
    private TextView certificate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        ic_add_user = findViewById(R.id.ic_add_user);
        ic_delete_user = findViewById(R.id.ic_delete_user);
        certificate = findViewById(R.id.certificate);

        // Add onClickListener for close button (replace with your actual click logic)
        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        ic_add_user.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewCer.class);
            startActivity(intent);
            finish();
        });


    }
}
