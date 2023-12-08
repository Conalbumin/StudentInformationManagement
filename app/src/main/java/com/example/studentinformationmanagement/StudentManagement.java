package com.example.studentinformationmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

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

public class StudentManagement extends AppCompatActivity {
    private static final String TAG = "StudentManagement";
    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference studentRef = databaseReference.child("student");
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private LinearLayout btnStudentList, btnAddStudent, btnAddStudentFromCSV,
            btnExportStudentToCSV, btnAddCertificateFromCSV, btnExportCertificateToCSV;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Initialize UI elements
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        btnStudentList = findViewById(R.id.btnStudentList);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddStudentFromCSV = findViewById(R.id.btnAddStudentFromCSV);
        btnExportStudentToCSV = findViewById(R.id.btnExportStudentToCSV);
        btnAddCertificateFromCSV = findViewById(R.id.btnAddCertificateFromCSV);
        btnExportCertificateToCSV = findViewById(R.id.btnExportCertificateToCSV);

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileUser.class);
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

        btnStudentList.setOnClickListener(view -> {
            Intent intent = new Intent(this, ListStudent.class);
            startActivity(intent);
        });

        btnAddStudent.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewStudent.class);
            startActivity(intent);
        });

        btnAddStudentFromCSV.setOnClickListener(view -> {
            // Handle btnAddStudentFromCSV click
        });

        btnExportStudentToCSV.setOnClickListener(view -> {
            // Handle btnExportStudentToCSV click
        });

        btnAddCertificateFromCSV.setOnClickListener(view -> {
            // Handle btnAddCertificateFromCSV click
        });

        btnExportCertificateToCSV.setOnClickListener(view -> {
            // Handle btnExportCertificateToCSV click
        });
    }

    public static void addNewStudent(String email, String password, String name, int age, String phoneNumber, boolean status, String isAdmin) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User userData = new User(email, name, age, phoneNumber, status, isAdmin);
                            addStudentToDatabase(email, userData);
                        }
                    } else {
                        // Handle failure
                    }
                });
    }

    private static void addStudentToDatabase(String email, User user) {
        databaseReference.child(email.replace(".", ",")).setValue(user);
    }

    public static void deleteExistingStudent() {
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

    public static void modifyStudentInfo(String displayName, Uri photoUri) {
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
