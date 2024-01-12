package com.example.studentinformationmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.net.Uri;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUser extends AppCompatActivity {
    private static final int SELECT_FILE = 1;
    private FirebaseAuth auth;
    private static final String DEFAULT_AVATAR_PATH = "android.resource://your_package_name/drawable/profile.jpg";
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private CircleImageView avatar;
    private TextView id_fullName_TextView;
    private LinearLayout role_layout, age_layout, phone_layout, logout_layout, loginHistory;
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
        avatar.setOnClickListener(view -> {
            selectImage();
        });
    }
    private void showConfirmationDialog(Uri selectedImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to change the profile picture?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User confirmed, proceed with image upload
            Log.e("Avatar Click", "Avatar clicked yes");

            // Convert Uri to String
            uploadImage(userId, selectedImage); // Assuming you have userId available
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User canceled, do nothing
        });

        builder.show();
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

    protected void getInfoUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Store the user ID
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
                        loadProfileImage(userId);

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

        TextView ageTextView = age_layout.findViewById(R.id.age);
        ageTextView.setText("Age: " + age);

        TextView phoneTextView = phone_layout.findViewById(R.id.phone);
        phoneTextView.setText(phoneNumber);

        TextView roleTextView = role_layout.findViewById(R.id.role);
        roleTextView.setText(role);
    }

    private void updateUserInfo(String field, String newValue) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            switch (field.toLowerCase()) {
                case "name":
                    userRef.child("name").setValue(newValue);
                    updateDisplayName(newValue);
                    break;
                case "age":
                    userRef.child("age").setValue(Integer.parseInt(newValue));
                    updateAge(Integer.parseInt(newValue));
                    break;
                case "phone":
                    userRef.child("phoneNumber").setValue(newValue);
                    updatePhone(newValue);
                    break;
                case "role":
                    updateRole(newValue);
                    userRef.child("role").setValue(newValue);
                    break;
                default:
                    // Handle the default case or log an error
                    return;
            }

            // Update the UI
            getInfoUser();
        }
    }

    private void handleUpdateCompletion(String field, Task<Void> task) {
        if (task.isSuccessful()) {
            Log.d("Firebase Update", "User " + field + " updated in Realtime Database successfully.");
        } else {
            Log.e("Firebase Error", "Error updating user " + field + " in Realtime Database", task.getException());
        }

        // Now, you can proceed with other updates or refreshing the UI
        getInfoUser();
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

    private void updateAge(int newAge) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Update the role in Realtime Database
        userRef.child("age").setValue(newAge)
                .addOnCompleteListener(task -> {
                    handleUpdateCompletion("age", task);
                    // Now, you can proceed with other updates or refreshing the UI
                    getInfoUser();
                });
    }

    private void updateRole(String newRole) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Update the role in Realtime Database
        userRef.child("role").setValue(newRole)
                .addOnCompleteListener(task -> {
                    handleUpdateCompletion("role", task);
                    // Now, you can proceed with other updates or refreshing the UI
                    getInfoUser();
                });
    }

    private void updatePhone(String newPhone) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Update the role in Realtime Database
        userRef.child("phoneNumber").setValue(newPhone)
                .addOnCompleteListener(task -> {
                    handleUpdateCompletion("phoneNumber", task);
                    // Now, you can proceed with other updates or refreshing the UI
                    getInfoUser();
                });
    }
    // Add onActivityResult method to handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE && data != null) {
                // Show the confirmation dialog before proceeding with image upload
                showConfirmationDialog(data.getData());
            }
        }
    }
    private void selectImage() {
        Log.e("Avatar Click", "Avatar clicked. Starting image selection...");
        Toast.makeText(getApplicationContext(), "Profile Pic", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }
    private void uploadImage(String userId, Uri selectedImage) {
        if (selectedImage != null) {
            Log.e("Avatar Click", "Yes. " + selectedImage);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("profile_images/" + userId + "/profile_picture.jpg");
            ref.putFile(selectedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileUser.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded image
                        Task<Uri> downloadUriTask = ref.getDownloadUrl();
                        downloadUriTask.addOnSuccessListener(uri -> {
                            // Update the user's profile image URL directly in Firebase Storage
                            updateUserProfileImageUrl(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileUser.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Avatar Click", "Upload failed: " + e.getMessage());
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void loadProfileImage(String userId) {
        String userProfileImagePath = "profile_images/" + userId + "/profile_picture.jpg";
        StorageReference userProfileImageRef = storageReference.child(userProfileImagePath);
        Log.e("userProfileImageRef", "userProfileImageRef "+ userProfileImageRef);
        // Try to get the download URL for the user's profile image
        userProfileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the user's profile image if it exists
            Glide.with(ProfileUser.this).load(uri.toString()).into(avatar);
        }).addOnFailureListener(e -> {
            // If the user's profile image doesn't exist, load the default profile image
            Glide.with(ProfileUser.this).load(Uri.parse(DEFAULT_AVATAR_PATH)).into(avatar);
        });
    }

    private void updateUserProfileImageUrl(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.e("Firebase Update", "User profile image URL updated successfully.");

                            // Load and display the updated profile image
                            loadProfileImage(userId);

                            // Now, you need to fetch the updated user information
                            FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (updatedUser != null) {
                                // Get the updated display name and photo URL
                                String updatedDisplayName = updatedUser.getDisplayName();
                                Uri updatedPhotoUrl = updatedUser.getPhotoUrl();

                                // Proceed with other updates or refreshing the UI
                                getInfoUser();
                            }
                        } else {
                            Log.e("Firebase Error", "Error updating user profile image URL", task.getException());
                            Toast.makeText(ProfileUser.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}


