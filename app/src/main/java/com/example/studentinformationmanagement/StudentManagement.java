package com.example.studentinformationmanagement;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    private int selectedPosition;


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
                Log.e(TAG, "Number of students: " + studentList.size());
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
            intent.setType("text/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        });

        btnExportStudentToCSV.setOnClickListener(view -> {
            exportStudentListToCSV();
        });

        btnAddCertificateFromCSV.setOnClickListener(view -> {
            showChooseStudentDialog(true);
        });

        btnExportCertificateToCSV.setOnClickListener(view -> {
            showChooseStudentDialog(false);
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

    // Function to import students from a CSV file
    private void importStudentsFromCSV(Uri fileUri) throws IOException {
        // Read the content of the CSV file
        BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));
        String line;
        int importedStudentCount = 0;
        boolean isFirstLine = true; // Flag to skip the first line (header)

        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            // Split the CSV line into fields
            String[] fields = line.split(",");

            // Assuming the CSV format is: ID,Name,Gender,Birth,Certificates
            if (fields.length >= 4) {
                String id = fields[0].trim();
                String name = fields[1].trim();
                String gender = fields[2].trim();
                String birth = fields[3].trim();

                // Parse Certificates if available
                ArrayList<Certificate> certificates = new ArrayList<>();
                if (fields.length > 4) {
                    String[] certificatesArray = fields[4].split("&");
                    for (String cert : certificatesArray) {
                        certificates.add(new Certificate(cert.trim()));
                    }
                }

                Student student = new Student(null, id, name, birth, gender, certificates);
                addNewStudentToDatabase(student);
                importedStudentCount++;
            }
        }
        reader.close();
        String toastMessage = (importedStudentCount > 0) ?
                "Successfully imported " + importedStudentCount + " students" :
                "No students imported";
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
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

    private void showChooseStudentDialog(boolean isImport) {
        List<String> studentNames = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            studentNames.add(studentList.get(i).getName());
        }

        CharSequence[] items = studentNames.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a student");

        builder.setItems(items, (dialog, item) -> {
            // Pass both the selected student and its position to the handling method
            handleSelectedStudent(studentList.get(item), item, isImport);
        });

        builder.show();
    }

    private void handleSelectedStudent(Student selectedStudent, int position, boolean isImport) {
        // Store the selected position in the class variable
        selectedPosition = position;

        if (isImport) {
            // Open file picker to import certificates for the selected student
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 2); // Use a different requestCode for certificates
        } else {
            exportCertificatesToCSV(selectedStudent);
        }
    }

    private void importCertificatesFromCSV(Student student, Uri fileUri) throws IOException {
        student.getCertificates().clear();

        // Read the content of the CSV file
        BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));
        String line;
        int importedCertificateCount = 0;
        boolean isFirstLine = true; // Flag to skip the first line (header)

        while ((line = reader.readLine()) != null) {
            // Skip the first line (header)
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            // Assuming the CSV format is: Certificate
            String certificateName = line.trim();

            // Assuming you have a Certificate object created while reading from CSV
            Certificate certificate = new Certificate(certificateName);
            Student selectedStudent = studentList.get(selectedPosition);
            addNewCertificateToDatabase(selectedStudent, certificate);
            importedCertificateCount++;

            // Log the imported certificate
            Log.e(TAG, "Imported certificate for " + student.getName() + ": " + certificateName);
        }
        reader.close();
        String toastMessage = (importedCertificateCount > 0) ?
                "Successfully imported " + importedCertificateCount + " certificates for " + student.getName() :
                "No certificates imported";
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static void addNewCertificateToDatabase(Student student, Certificate certificate) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("students").child(student.getKey()).child("Certificates");
        Log.e(TAG, "studentRef " + studentRef);

        // Create a map with field names matching your certificate data structure
        Map<String, Object> certificateData = new HashMap<>();
        certificateData.put("name", certificate.getName());

        // Add the new certificate to the "Certificates" node under the student in the database
        studentRef.push().setValue(certificateData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d("AddNewCertificate", "New certificate added to database successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("AddNewCertificate", "Error adding new certificate to database", e);
                });
    }

    private void exportCertificatesToCSV(Student student) {
        try {
            // Create a new file in the Downloads directory
            File csvFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "certificates_" + student.getName() + ".csv");
            csvFile.createNewFile();

            // Use BufferedWriter with explicitly specified line separator
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

            // Write the header line to the CSV file
            String headerLine = "Certificate\n";
            writer.write(headerLine);

            // Append the certificates to the CSV file
            for (Certificate certificate : student.getCertificates()) {
                String csvLine = certificate.getName() + "\t\n";
                writer.write(csvLine);
                Log.e(TAG, "Certificates exported to " + csvLine); // Add this log statement
            }

            // Close the writer
            writer.close();

            Log.e(TAG, "Certificates exported to " + csvFile.getAbsolutePath()); // Add this log statement
            Toast.makeText(this, "Certificates exported to " + csvFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to export certificates", e); // Add this log statement
            Toast.makeText(this, "Failed to export certificates", Toast.LENGTH_SHORT).show();
        }
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
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri selectedCertificateFileUri = data.getData();
            if (selectedCertificateFileUri != null) {
                // Use the stored position variable
                int studentPosition = selectedPosition;
                try {
                    importCertificatesFromCSV(studentList.get(studentPosition), selectedCertificateFileUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}


