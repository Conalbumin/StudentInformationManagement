package com.example.studentinformationmanagement;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
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

public class ProfileStudent extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private FirebaseAuth auth;
    private CircleImageView avatar;
    private TextView id_fullName_TextView;
    private ImageView ic_close;
    private LinearLayout id_layout, gender_layout, date_layout, certificate_layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_student);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        avatar = findViewById(R.id.id_profile_image);
        id_fullName_TextView = findViewById(R.id.id_fullName_TextView);
        ic_close = findViewById(R.id.ic_close);
        id_layout = findViewById(R.id.id_layout);
        gender_layout = findViewById(R.id.gender_layout);
        date_layout = findViewById(R.id.date_layout);
        certificate_layout = findViewById(R.id.certificate_layout);

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        id_fullName_TextView.setOnClickListener(view -> {
        });

        id_layout.setOnClickListener(view -> {
        });

        gender_layout.setOnClickListener(view -> {
        });

        date_layout.setOnClickListener(view -> {
        });

        certificate_layout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileStudent.class);
            startActivity(intent);
            finish();
        });

        // Set up the profile picture click listener
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
