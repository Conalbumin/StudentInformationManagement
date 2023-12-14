package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListCertificate extends AppCompatActivity {
    private static final String CERTIFICATES_PATH = "certificates";
    private static final String STUDENTS_PATH = "students";

    private DatabaseReference databaseReference;
    private DatabaseReference certificateRef;
    private AdapterCertificate certificateAdapter;
    private RecyclerView recyclerView;
    private ImageView ic_close, ic_add_cer, ic_delete_cer;
    private TextView certificate;
    private String studentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        ic_add_cer = findViewById(R.id.ic_add_cer);
        ic_delete_cer = findViewById(R.id.ic_delete_cer);
        certificate = findViewById(R.id.certificate);

        recyclerView = findViewById(R.id.listviewCer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        certificateRef = databaseReference.child(CERTIFICATES_PATH);

        certificateAdapter = new AdapterCertificate(this, new ArrayList<>());
        recyclerView.setAdapter(certificateAdapter);

        // Retrieve the student ID from the intent
        studentId = getIntent().getStringExtra("STUDENT_ID");

        if (studentId != null) {
            // Đặt đường dẫn đến Certificates của sinh viên hiện tại
            DatabaseReference studentCertificatesRef = databaseReference.child(STUDENTS_PATH)
                    .child(studentId)
                    .child("Certificates");
            studentCertificatesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Certificate> certificates = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check if the certificate is a string or an object
                        if (snapshot.getValue() instanceof String) {
                            certificates.add(new Certificate((String) snapshot.getValue()));
                        } else {
                            Certificate certificate = snapshot.getValue(Certificate.class);
                            certificates.add(certificate);
                        }
                    }
                    Log.e("Certificate", "student certificate " + certificates);
                    certificateAdapter.setStudentList(certificates);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if needed
                }
            });
        }

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        ic_add_cer.setOnClickListener(view -> {
            UserManagement.getCurrentRole(currentRole -> {
                if ("Admin".equals(currentRole) || "Manager".equals(currentRole)) {
                    // If the user has Admin or Manager role, proceed to add a new certificate
                    Intent intent = new Intent(this, AddNewCer.class);
                    intent.putExtra("STUDENT_ID", studentId);
                    startActivity(intent);
                } else {
                    // If the user doesn't have the required role, show a message or take appropriate action
                    Toast.makeText(ListCertificate.this, "You do not have the required role to add a certificate", Toast.LENGTH_SHORT).show();
                }
            });
        });


        certificateAdapter.setOnItemClickListener(new AdapterCertificate.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteCertificate(position);
            }

            @Override
            public void onModifyClick(int position) {
                Certificate certificate = certificateAdapter.getItem(position);

                // Check user role before allowing modification
                UserManagement.getCurrentRole(currentRole -> {
                    if ("Admin".equals(currentRole) || "Manager".equals(currentRole)) {
                        // User has Admin or Manager role, proceed with modification
                        showConfirmationDialog(certificate, position);
                    } else {
                        // User does not have the required role, show a message or take appropriate action
                        Toast.makeText(ListCertificate.this, "You do not have the required role to modify a certificate", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void showConfirmationDialog(Certificate certificate, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to change the certificate name?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked Yes, show a dialog to change the certificate name
            showEditCertificateNameDialog(certificate, position);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, do nothing
        });

        builder.show();
    }

    private void showEditCertificateNameDialog(Certificate certificate, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Certificate Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter new certificate name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCertificateName = input.getText().toString();
            // Update the certificate name
            updateCertificateName(certificate, newCertificateName, position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateCertificateName(Certificate certificate, String newCertificateName, int position) {
        // Update the certificate name in the database
        DatabaseReference studentCertificatesRef = databaseReference.child("students")
                .child(studentId)
                .child("Certificates");

        // Find the certificate with the same name and update its name
        studentCertificatesRef.orderByChild("name").equalTo(certificate.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.child("name").getRef().setValue(newCertificateName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });

        // Update the certificate name in the RecyclerView
        certificateAdapter.updateCertificateName(position, newCertificateName);
    }

    public void onDeleteClick(View view) {
        // Extract the position from the view if needed
        int position = recyclerView.getChildLayoutPosition((View) view.getParent());
        deleteCertificate(position);
    }

    private void deleteCertificate(int position) {
        Certificate certificate = certificateAdapter.getItem(position);

        // Get a reference to the Certificates of the current student
        DatabaseReference studentCertificatesRef = databaseReference.child("students")
                .child(studentId)
                .child("Certificates");

        // Check user role before allowing deletion
        UserManagement.getCurrentRole(currentRole -> {
            if ("Admin".equals(currentRole) || "Manager".equals(currentRole)) {
                // User has Admin or Manager role, proceed with deletion
                findCertificateKey(studentCertificatesRef, certificate, position);
            } else {
                // User does not have the required role, show a message or take appropriate action
                Toast.makeText(ListCertificate.this, "You do not have the required role to delete a certificate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findCertificateKey(DatabaseReference ref, Certificate certificate, int position) {
        Query query = ref.orderByChild("name").equalTo(certificate.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Remove the certificate from the database
                    snapshot.getRef().removeValue();

                    // Remove the certificate from the RecyclerView
                    certificateAdapter.removeCertificate(position);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

}
