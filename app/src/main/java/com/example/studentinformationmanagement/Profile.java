package com.example.studentinformationmanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.net.Uri;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

//        studentBtn.setOnClickListener(view -> {
//            Intent intent = new Intent(this, Login.class);
//            startActivity(intent);
//            finish();
//        });

        avatar.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Profile Pic", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        });
    }

    // Add onActivityResult method to handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE && data != null) {
                // Use the onSelectFromGalleryResult method to handle the selected image
                onSelectFromGalleryResult(data);
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                // Update the user's profile picture on Firebase Authentication
                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest avatarUpdate = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(selectedImage)
                        .build();

                user.updateProfile(avatarUpdate)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "Avatar updated.");
                            }
                        });

                // Update the CircleImageView in your layout with the new image
                avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }
    }
}
