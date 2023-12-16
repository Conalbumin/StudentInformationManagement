package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class LoginHistory extends AppCompatActivity {
    private ImageView ic_close;
    private RecyclerView recyclerView;
    private AdapterHistory adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_history);

        // Initialize views
        ic_close = findViewById(R.id.ic_close);
        recyclerView = findViewById(R.id.recyclerViewHistory);

        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize your adapter here
        adapter = new AdapterHistory(LoginHistory.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Proceed to fetch and display login history for the current user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("loginHistory").child(userId);

        // Retrieve and display user history from Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<LoginHistoryItem> historyList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String timestamp = snapshot.getValue(String.class);
                    historyList.add(new LoginHistoryItem(timestamp));
                }

                // Update the adapter with the new data
                adapter.setHistoryList(historyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
