package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private ImageView ic_close, ic_add_user, ic_delete_user;
    private TextView certificate;
    private String studentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        ic_add_user = findViewById(R.id.ic_add_user);
        ic_delete_user = findViewById(R.id.ic_delete_user);
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

        ic_add_user.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewCer.class);
            intent.putExtra("STUDENT_ID", studentId);
            startActivity(intent);
        });

        certificateAdapter.setOnItemClickListener(position -> deleteCertificate(position));
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

        // Find the key of the certificate to be deleted
        findCertificateKey(studentCertificatesRef, certificate, position);
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
