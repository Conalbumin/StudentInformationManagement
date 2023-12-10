package com.example.studentinformationmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import android.net.Uri;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUser extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private CircleImageView avatar;
    private TextView id_fullName_TextView;
    private LinearLayout role_layout, age_layout, phone_layout, logout_layout, loginHistory;
    private AppCompatButton userBtn, studentBtn, profileBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        avatar = findViewById(R.id.id_profile_image);
        id_fullName_TextView = findViewById(R.id.id_fullName_TextView);
        role_layout = findViewById(R.id.role_layout);
        age_layout = findViewById(R.id.age_layout);
        phone_layout = findViewById(R.id.phone_layout);
        loginHistory = findViewById(R.id.loginHistory);
        logout_layout = findViewById(R.id.logout_layout);
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);

        getInfoUser(); // Fetch and display user information

        age_layout.setOnClickListener(view -> {
            // Handle click on age_layout
            showEditDialog("Age", "Enter new age", age_layout, id_fullName_TextView.getText().toString());
        });

        phone_layout.setOnClickListener(view -> {
            // Handle click on phone_layout
            showEditDialog("Phone", "Enter new phone number", phone_layout, id_fullName_TextView.getText().toString());
        });

        role_layout.setOnClickListener(view -> {
            // Handle click on role_layout
            showEditDialog("Role", "Enter new role", role_layout, id_fullName_TextView.getText().toString());
        });

        id_fullName_TextView.setOnClickListener(view -> {
            // Handle click on id_fullName_TextView
            showEditDialog("Name", "Enter new name", id_fullName_TextView, id_fullName_TextView.getText().toString());
        });

        logout_layout.setOnClickListener(view -> {
            auth.signOut();
            Toast.makeText(ProfileUser.this, "Signed out", Toast.LENGTH_SHORT).show();

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

        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StudentManagement.class);
            startActivity(intent);
            finish();
        });

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileUser.class);
            startActivity(intent);
            finish();
        });

        loginHistory.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginHistory.class);
            startActivity(intent);
        });

        age_layout.setOnClickListener(view -> showEditDialog("Age", "Enter new age", age_layout, id_fullName_TextView.getText().toString()));
        phone_layout.setOnClickListener(view -> showEditDialog("Phone", "Enter new phone number", phone_layout, id_fullName_TextView.getText().toString()));
        role_layout.setOnClickListener(view -> showEditDialog("Role", "Enter new role", role_layout, id_fullName_TextView.getText().toString()));
        id_fullName_TextView.setOnClickListener(view -> showEditDialog("Name", "Enter new name", id_fullName_TextView, id_fullName_TextView.getText().toString()));
        avatar.setOnClickListener(view -> selectImage());
    }

    private void selectImage() {
        Toast.makeText(getApplicationContext(), "Profile Pic", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void showEditDialog(String field, String hint, View view, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + field);
        builder.setMessage(hint);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(currentValue);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString();
            updateUserInfo(field, newValue);

            if (view instanceof TextView) {
                ((TextView) view).setText(newValue);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    protected void getInfoUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            storage = FirebaseStorage.getInstance();
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        int age = snapshot.child("age").getValue(Integer.class);
                        String phone = snapshot.child("phoneNumber").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);

                        // Call the updateUI method with the obtained information
                        updateUI(name, age, phone, role);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Error", "Error getting user data", error.toException());
                }
            });
        }
    }

    private void updateUI(String name, int age, String phoneNumber, String role) {
        id_fullName_TextView.setText(name);

        // Update age
        TextView ageTextView = age_layout.findViewById(R.id.age); // Replace with the actual ID of the age TextView
        ageTextView.setText("Age: " + String.valueOf(age)); // Convert age to String before setting it

        // Update phone number
        TextView phoneTextView = phone_layout.findViewById(R.id.phone); // Replace with the actual ID of the phone TextView
        phoneTextView.setText(phoneNumber);

        // Update role (assuming you have a TextView for displaying the role)
        TextView roleTextView = role_layout.findViewById(R.id.role); // Replace with the actual ID of the role TextView
        roleTextView.setText(role);
    }
    private void updateUserInfo(String field, String newValue) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            userRef.child("name").setValue(newValue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase Update", field + " updated successfully.");
                        } else {
                            Log.e("Firebase Error", "Error updating " + field, task.getException());
                        }
                    });

            // Update the local user object with the new value
            if ("name".equals(field)) {
                updateDisplayName(newValue);
            } else if ("age".equals(field)) {
                // Handle age update if needed
            } else if ("phoneNumber".equals(field)) {
                // Handle phone number update if needed
            } else if ("role".equals(field)) {
                // Handle role update if needed
            }

            // Update the UI
            getInfoUser();
        }
    }

    private void updateDisplayName(String newName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase Update", "User display name updated successfully.");
                        } else {
                            Log.e("Firebase Error", "Error updating user display name", task.getException());
                        }
                    });
        }
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
