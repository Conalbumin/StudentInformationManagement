package com.example.studentinformationmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentManagement extends AppCompatActivity {
    private static final String TAG = "StudentManagement";
    private static FirebaseAuth auth;
    private static FirebaseUser currentUser;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference studentRef = databaseReference.child("student");
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private LinearLayout btnStudentList, btnAddStudent, btnAddStudentFromCSV, item_user,
            btnExportStudentToCSV, btnAddCertificateFromCSV, btnExportCertificateToCSV;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize UI elements
        item_user = findViewById(R.id.item_user);
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        btnStudentList = findViewById(R.id.btnStudentList);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddStudentFromCSV = findViewById(R.id.btnAddStudentFromCSV);
        btnExportStudentToCSV = findViewById(R.id.btnExportStudentToCSV);
        btnAddCertificateFromCSV = findViewById(R.id.btnAddCertificateFromCSV);
        btnExportCertificateToCSV = findViewById(R.id.btnExportCertificateToCSV);

        fetchAndDisplayUserInfo();

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

    private void fetchAndDisplayUserInfo() {
        // Get the user ID of the current user
        String userId = currentUser.getUid();

        // Assuming you have a method to fetch user information from the database
        // Modify the method accordingly based on your database structure
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User information found, create a User object
                    User user = dataSnapshot.getValue(User.class);

                    // Assuming you have a method to display user information in the list
                    // Modify the method accordingly based on your list structure
                    displayUserInList(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void displayUserInList(User user) {
        displayUserDetails(user);
    }

    private void displayUserDetails(User user) {
        TextView userNameTextView = item_user.findViewById(R.id.personName);
        TextView userPhoneTextView = item_user.findViewById(R.id.personNumber);
        TextView userRoleTextView = item_user.findViewById(R.id.userRole);

        userNameTextView.setText(user.getName());
        userPhoneTextView.setText(user.getPhoneNumber());
        userRoleTextView.setText(user.getRole());
    }

    public static void addNewStudentToDatabase(Student student) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("students");

        // Create a map with field names matching your data structure
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("ID", student.getID());
        studentData.put("Name", student.getName());
        studentData.put("Gender", student.getGender());
        studentData.put("Birth", student.getBirth());

        // Convert Certificates to a list of maps with "name" as the key
        ArrayList<Map<String, Object>> certificatesData = new ArrayList<>();
        if (student.getCertificates() != null) {
            for (Certificate certificate : student.getCertificates()) {
                Map<String, Object> certData = new HashMap<>();
                certData.put("name", certificate.getName());
                certificatesData.add(certData);
            }
        }
        studentData.put("Certificates", certificatesData);

        // Add the new student to the "students" node in the database
        studentsRef.push().setValue(studentData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d("AddNewStudent", "New student added to database successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("AddNewStudent", "Error adding new student to database", e);
                });
    }
}
