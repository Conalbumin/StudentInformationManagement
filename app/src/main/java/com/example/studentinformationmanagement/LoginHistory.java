package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginHistory extends AppCompatActivity {
    private LinearLayout item_user;
    private ImageView ic_close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_history);

        // Initialize views
        item_user = findViewById(R.id.item_user);
        ic_close = findViewById(R.id.ic_close);

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

//        item_user.setOnClickListener(view -> {
//            Intent intent = new Intent(this, ProfileUser.class);
//            startActivity(intent);
//            finish();
//        });
    }
}
