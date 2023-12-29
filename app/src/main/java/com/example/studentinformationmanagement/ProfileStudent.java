package com.example.studentinformationmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileStudent extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference studentRef;
    private FirebaseStorage storage;
    private CircleImageView avatar;
    private TextView id_fullName_student;
    private ImageView ic_close;
    private String studentKey;
    private LinearLayout id_layout, gender_layout, date_layout, certificate_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_student);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        avatar = findViewById(R.id.id_profile_image);
        id_fullName_student = findViewById(R.id.id_fullName_student);
        ic_close = findViewById(R.id.ic_close);
        id_layout = findViewById(R.id.id_layout);
        gender_layout = findViewById(R.id.gender_layout);
        date_layout = findViewById(R.id.date_layout);
        certificate_layout = findViewById(R.id.certificate_layout);

        // Retrieve the key from the intent
        String studentKey = getIntent().getStringExtra("STUDENT_KEY");

        if (studentKey != null) {
            // Fetch and display student information based on the key
            getInfoStudent(studentKey);
        } else {
            // Handle the case when the key is not passed correctly
            Log.e("ProfileStudent", "Invalid student key");
            finish(); // Close the activity
        }

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        id_fullName_student.setOnClickListener(view ->
                showEditStudentDialog("name", "Enter new name", id_fullName_student, id_fullName_student.getText().toString(), studentKey));

        gender_layout.setOnClickListener(view ->
                showEditStudentDialog("gender", "Enter new gender", gender_layout, ((TextView) gender_layout.findViewById(R.id.gender)).getText().toString(), studentKey));

        date_layout.setOnClickListener(view ->
                showEditStudentDialog("birth", "Enter new birthdate", date_layout, ((TextView) date_layout.findViewById(R.id.date)).getText().toString(), studentKey));


        certificate_layout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ListCertificate.class);
            // Pass the student key to the intent for ListCertificate
            intent.putExtra("STUDENT_KEY", studentKey);
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

    private void updateUI(String name, String id, String gender, String birth) {
        TextView id_fullName_student = findViewById(R.id.id_fullName_student);
        id_fullName_student.setText(name);

        // Find the TextView in id_layout and set the ID
        TextView idTextView = id_layout.findViewById(R.id.id);
        idTextView.setText("ID: " + id);

        // Find the TextView in gender_layout and set the gender
        TextView genderTextView = gender_layout.findViewById(R.id.gender);
        genderTextView.setText("Gender: " + gender);

        // Find the TextView in date_layout and set the birth date
        TextView dateTextView = date_layout.findViewById(R.id.date);
        dateTextView.setText(birth);
    }
    private void showEditStudentDialog(String field, String hint, View view, String currentValue, String studentKey) {
        UserManagement.getCurrentRole(currentRole -> {
            if ("Admin".equals(currentRole) || "Manager".equals(currentRole)) {
                // User has Admin or Manager role, proceed with showing the edit dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit " + field);
                builder.setMessage(hint);

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                // Check if the view is an instance of TextView before casting
                if (view instanceof TextView) {
                    // Cast the view to TextView and get the text
                    String currentText = ((TextView) view).getText().toString();
                    input.setHint(currentText);
                }
                builder.setView(input);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newValue = input.getText().toString();
                    updateStudentInfo(field, newValue, studentKey);

                    if (view instanceof TextView) {
                        ((TextView) view).setText(newValue);
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            } else {
                // User does not have the required role, show a message or take appropriate action
                Toast.makeText(ProfileStudent.this, "You do not have the required role to edit student information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStudentInfo(String field, String newValue, String studentKey) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        DatabaseReference studentRef = studentsRef.child(studentKey);

        switch (field.toLowerCase()) {
            case "name":
                studentRef.child("Name").setValue(newValue);
                Toast.makeText(ProfileStudent.this, "Student name updated successfully", Toast.LENGTH_SHORT).show();
                break;
            case "gender":
                studentRef.child("Gender").setValue(newValue);
                Toast.makeText(ProfileStudent.this, "Student gender updated successfully", Toast.LENGTH_SHORT).show();
                break;
            case "birth":
                studentRef.child("Birth").setValue(newValue);
                Toast.makeText(ProfileStudent.this, "Student birth updated successfully", Toast.LENGTH_SHORT).show();
                break;
            default:
                return;
        }
    }


    private void getInfoStudent(String studentKey) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("students");

        studentsRef.child(studentKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String studentId = dataSnapshot.child("ID").getValue(String.class);
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String gender = dataSnapshot.child("Gender").getValue(String.class);
                    String birth = dataSnapshot.child("Birth").getValue(String.class);

                    // Fetch certificates information from the HashMap
                    ArrayList<String> certificates = new ArrayList<>();
                    DataSnapshot certificatesSnapshot = dataSnapshot.child("Certificates");
                    for (DataSnapshot certificateSnapshot : certificatesSnapshot.getChildren()) {
                        // Assuming that each certificate is stored as a HashMap with a "name" property
                        HashMap<String, Object> certificateMap = (HashMap<String, Object>) certificateSnapshot.getValue();
                        if (certificateMap != null && certificateMap.containsKey("name")) {
                            String certificateName = (String) certificateMap.get("name");
                            certificates.add(certificateName);
                        }
                    }

                    Log.e("ProfileStudent", "Student ID: " + studentId);
                    Log.e("ProfileStudent", "Student Name: " + name);
                    Log.e("ProfileStudent", "Student Gender: " + gender);
                    Log.e("ProfileStudent", "Student Birth: " + birth);
                    Log.e("ProfileStudent", "Certificates: " + certificates);

                    // Call the updateUI method with the obtained information
                    updateUI(name, studentId, gender, birth);
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
