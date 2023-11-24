package com.example.studentinformationmanagement;

import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentManagement {
    private static final String TAG = "StudentManagement";
    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference;
    private LinearLayout btnStudentList, btnAddStudent, btnAddStudentFromCSV,
            btnExportStudentToCSV, btnAddCertificateFromCSV, btnExportCertificateToCSV;


    public StudentManagement() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public static void addNewUser(String email, String password, String name, int age, String phoneNumber, boolean status, String isAdmin) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User userData = new User(email, name, age, phoneNumber, status, isAdmin);
                            addUserToDatabase(email, userData);
                        }
                    } else {
                        // Handle failure
                    }
                });
    }

    private static void addUserToDatabase(String email, User user) {
        databaseReference.child(email.replace(".", ",")).setValue(user);
    }

    public static void deleteExistingUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    });
        }
    }

    public static void modifyUserInfo(String displayName, Uri photoUri) {
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "User profile updated.");
                    }
                });
    }
}
