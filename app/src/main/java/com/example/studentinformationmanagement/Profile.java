package com.example.studentinformationmanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;
    private CircleImageView avatar;
    private TextView id_fullName_TextView;
    private LinearLayout email_layout, age_layout, phone_layout, logout_layout;
    private AppCompatButton userBtn, studentBtn, profileBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        // Initialize views
        avatar = findViewById(R.id.id_profile_image);
        id_fullName_TextView = findViewById(R.id.id_fullName_TextView);
        email_layout = findViewById(R.id.email_layout);
        age_layout = findViewById(R.id.age_layout);
        phone_layout = findViewById(R.id.phone_layout);
        logout_layout = findViewById(R.id.logout_layout);
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);

        logout_layout.setOnClickListener(view -> {
            auth.signOut();
            Toast.makeText(Profile.this, "Signed out", Toast.LENGTH_SHORT).show();

            // Redirect to the login activity
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        userBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        avatar.setOnClickListener(view -> {
            // change profile picture
            FirebaseUser user = auth.getCurrentUser();
            UserProfileChangeRequest avatarUpdate = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUri)
                    .build();
            user.updateProfile(avatarUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Avatar updated.");
                        }
                    });
        });
    }

}
