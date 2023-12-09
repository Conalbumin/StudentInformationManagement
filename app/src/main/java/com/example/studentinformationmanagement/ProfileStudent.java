package com.example.studentinformationmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileStudent extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference studentRef;
    private FirebaseStorage storage;
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

        getInfoStudent(); // Fetch and display user information

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
            Intent intent = new Intent(this, ListCertificate.class);
            startActivity(intent);
            finish();
        });

        // Set up the profile picture click listener
        avatar.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Profile Pic", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivity(Intent.createChooser(intent, "Select File"));
        });
    }

    private void updateUI(String name, String id, String gender, String date) {
        // Update UI elements with the data
        id_fullName_TextView.setText(name);

        // Find the TextView in id_layout and set the ID
        TextView idTextView = id_layout.findViewById(R.id.id);
        idTextView.setText("ID: " + id);

        // Find the TextView in gender_layout and set the gender
        TextView genderTextView = gender_layout.findViewById(R.id.gender);
        genderTextView.setText("Gender: " + gender);

        // Find the TextView in date_layout and set the birth date
        TextView dateTextView = date_layout.findViewById(R.id.date);
        dateTextView.setText("Birth Date: " + date);
    }

    private void getInfoStudent() {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("students");
        Log.e("ProfileStudent", "StudentRef " + studentRef);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ProfileStudent", "onDataChange called");
                if (dataSnapshot.exists()) {
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        // Access the unique identifier (key) for each student
                        String studentId = studentSnapshot.child("ID").getValue(String.class);
                        String name = studentSnapshot.child("Name").getValue(String.class);
                        String gender = studentSnapshot.child("Gender").getValue(String.class);
                        String birth = studentSnapshot.child("Birth").getValue(String.class);

                        // Access the Certificates array
                        ArrayList<String> certificates = new ArrayList<>();
                        for (DataSnapshot certificateSnapshot : studentSnapshot.child("Certificates").getChildren()) {
                            String certificateName = certificateSnapshot.child("name").getValue(String.class);
                            certificates.add(certificateName);
                        }
                        Log.e("ProfileStudent", "Student " + studentId);
                        Log.e("ProfileStudent", "Student " + name);
                        Log.e("ProfileStudent", "Student " + gender);
                        Log.e("ProfileStudent", "Student " + birth);
                        Log.e("ProfileStudent", "Student " + certificates);

                        // Call the updateUI method with the obtained information
                        updateUI(name, studentId, gender, birth);
                        break;  // Assuming there's only one matching student
                    }
                } else {
                    Log.e("ProfileStudent", "onDataChange failed: Snapshot does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error getting student data", error.toException());
            }
        });
    }




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

            if (selectedImage != null) {
                // Log the selectedImage Uri to check if it is valid
                Log.e("TAG", "Selected Image Uri: " + selectedImage);

                // Get a reference to the Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                // Create a reference to the file you want to upload
                String fileName = "profile_images/" + auth.getCurrentUser().getUid() + ".jpg";
                StorageReference imageRef = storageRef.child(fileName);

                // Upload the file to Firebase Storage
                imageRef.putFile(selectedImage)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get the download URL of the uploaded file
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Update the user's profile picture on Firebase Authentication
                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest avatarUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();

                                user.updateProfile(avatarUpdate)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "Avatar updated.");
                                            }
                                        });

                                // Update the CircleImageView in your layout with the new image
                                avatar.setImageURI(uri);
                            }).addOnFailureListener(e -> {
                                Log.e("TAG", "Error getting download URL", e);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TAG", "Error uploading image", e);
                        });
            } else {
                // Handle the case when selectedImage is null
                Log.e("TAG", "Selected Image Uri is null");
                Toast.makeText(this, "Failed to retrieve selected image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
