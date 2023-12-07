package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListCertificate extends AppCompatActivity {
    private static final String CERTIFICATE_PATH = "certificates";

    private DatabaseReference databaseReference;
    private DatabaseReference certificateRef;
    private AdapterCertificate certificateAdapter;
    private RecyclerView recyclerView;
    private ImageView ic_close, ic_add_user, ic_delete_user;
    private TextView certificate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        ic_add_user = findViewById(R.id.ic_add_user);
        ic_delete_user = findViewById(R.id.ic_delete_user);
        certificate = findViewById(R.id.certificate);

        recyclerView = findViewById(R.id.list_student);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        certificateRef = databaseReference.child(CERTIFICATE_PATH);

        certificateAdapter = new AdapterCertificate(this, new ArrayList<>());
        recyclerView.setAdapter(certificateAdapter);

        certificateRef.addValueEventListener(new ValueEventListener() {
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
                certificateAdapter.setStudentList(certificates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        ic_add_user.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewCer.class);
            startActivity(intent);
            finish();
        });


    }
}
