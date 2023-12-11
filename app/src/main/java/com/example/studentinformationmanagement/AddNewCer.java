package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewCer extends AppCompatActivity {
    private ImageView ic_close;
    private EditText certificateNameEditText;
    private Button btnAddCer;

    private DatabaseReference databaseReference;
    private String studentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificates);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        certificateNameEditText = findViewById(R.id.certificateNameEditText);
        btnAddCer = findViewById(R.id.btnAddCer);

        // Retrieve the student ID from the intent
        studentId = getIntent().getStringExtra("STUDENT_ID");

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        btnAddCer.setOnClickListener(view -> {
            addCertificate();
        });
    }

    private void addCertificate() {
        String certificateName = certificateNameEditText.getText().toString().trim();

        if (!certificateName.isEmpty()) {
            // Create a new certificate object
            Certificate certificate = new Certificate(certificateName);

            // Get a reference to the Certificates of the current student
            DatabaseReference studentCertificatesRef = databaseReference.child("students")
                    .child(studentId)
                    .child("Certificates");

            // Generate a new key for the certificate
            String certificateKey = studentCertificatesRef.push().getKey();

            // Add the certificate to the database
            studentCertificatesRef.child(certificateKey).setValue(certificate);

            // Notify the user that the certificate has been added
            Toast.makeText(this, "Certificate added successfully", Toast.LENGTH_SHORT).show();

            // Clear the input field
            certificateNameEditText.setText("");
        } else {
            // Notify the user that the certificate name is empty
            Toast.makeText(this, "Please enter a certificate name", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
