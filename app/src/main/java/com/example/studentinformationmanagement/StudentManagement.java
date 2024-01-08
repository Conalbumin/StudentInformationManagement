package com.example.studentinformationmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentManagement extends AppCompatActivity {
    private static final String TAG = "StudentManagement";
    private static FirebaseAuth auth;
    private static FirebaseUser currentUser;
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference studentRef = databaseReference.child("students");
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private LinearLayout btnStudentList, btnAddStudent, btnAddStudentFromCSV, item_user,
            btnExportStudentToCSV, btnAddCertificateFromCSV, btnExportCertificateToCSV;
    private ArrayList<Student> studentList = new ArrayList<>();


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

        AdapterStudent studentAdapter = new AdapterStudent(this, new ArrayList<>());

        fetchAndDisplayUserInfo();
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear(); // Clear the list before adding new data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey(); // Get the key from Firebase
                    Student student = new Student(key, (Map<String, Object>) snapshot.getValue());

                    // Parse certificates data
                    DataSnapshot certificatesSnapshot = snapshot.child("Certificates");
                    if (certificatesSnapshot.exists()) {
                        ArrayList<Certificate> certificates = new ArrayList<>();
                        for (DataSnapshot certDataSnapshot : certificatesSnapshot.getChildren()) {
                            String certName = (String) certDataSnapshot.child("name").getValue();
                            certificates.add(new Certificate(certName));
                        }
                        student.setCertificates(certificates);
                    }

                    studentList.add(student);
                }

                // Log the size of the studentList
                Log.e(TAG, "Number of students: " + studentList.size());

                // Update the list in your adapter
                studentAdapter.setStudentList(studentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });




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
            UserManagement.getCurrentRole(currentRole -> {
                if ("Admin".equals(currentRole) || "Manager".equals(currentRole)) {
                    // If the user has Admin or Manager role, proceed to add a new student
                    Intent intent = new Intent(this, AddNewStudent.class);
                    startActivity(intent);
                } else {
                    // If the user doesn't have the required role, show a message or take appropriate action
                    Toast.makeText(StudentManagement.this, "You do not have the required role to add a student", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnAddStudentFromCSV.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        });

        btnExportStudentToCSV.setOnClickListener(view -> {
            exportStudentListToCSV();
        });

        btnAddCertificateFromCSV.setOnClickListener(view -> {
            // Handle btnAddCertificateFromCSV click
        });

        btnExportCertificateToCSV.setOnClickListener(view -> {
            // Handle btnExportCertificateToCSV click
        });
    }

    private void fetchAndDisplayUserInfo() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
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

     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                try {
                    importStudentsFromCSV(selectedFileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Function to import students from a CSV file
    private void importStudentsFromCSV(Uri fileUri) throws IOException {
        // Read the content of the CSV file
        BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));
        String line;
        List<Student> importedStudents = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            // Split the CSV line into fields
            String[] fields = line.split(",");

            // Assuming the CSV format is: ID,Name,Gender,Birth
            if (fields.length == 4) {
                String id = fields[0].trim();
                String name = fields[1].trim();
                String gender = fields[2].trim();
                String birth = fields[3].trim();

                // Create a new student and add it to the list
                Student student = new Student(null, id, name, birth, gender, null);
                importedStudents.add(student);
            }
        }

        // Add imported students to the database
        for (Student student : importedStudents) {
            addNewStudentToDatabase(student);
        }

        // Close the reader
        reader.close();
    }

    private void exportStudentListToCSV() {
        try {
            // Create a new file in the Downloads directory
            File csvFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "student_list.csv");
            csvFile.createNewFile();

            // Write the header line to the CSV file
            FileOutputStream headerStream = new FileOutputStream(csvFile);
            String headerLine = "ID,Name,Gender,Birth,Certificates\n";
            headerStream.write(headerLine.getBytes());
            headerStream.close();

            // Append the student list to the CSV file
            FileOutputStream outputStream = new FileOutputStream(csvFile, true);
            for (Student student : studentList) {
                String certificatesString = getCertificatesString(student.getCertificates());
                String csvLine = student.getID() + "," + student.getName() + "," + student.getGender()
                        + "," + student.getBirth() + "," + certificatesString + "\n";
                outputStream.write(csvLine.getBytes());
                Log.e(TAG, "Student list exported " + csvLine);

            }
            outputStream.close();
            Log.e(TAG, "Student list exported to " + csvFile.getAbsolutePath());
            Toast.makeText(this, "Student list exported to " + csvFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to export student list", e);
            Toast.makeText(this, "Failed to export student list", Toast.LENGTH_SHORT).show();
        }
    }


    private String getCertificatesString(ArrayList<Certificate> certificates) {
        if (certificates == null || certificates.isEmpty()) {
            return ""; // Return an empty string if certificates are null or empty
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Certificate certificate : certificates) {
            if (certificate != null && certificate.getName() != null) {
                stringBuilder.append(certificate.getName()).append(" & ");
            }
        }
        // Remove the trailing comma and space
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }



}


