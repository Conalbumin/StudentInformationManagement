package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String USER_PATH = "users";

    private DatabaseReference databaseReference;
    private DatabaseReference userRef;
    private AdapterUser userAdapter;
    private LinearLayout item_user;
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private AppCompatButton studentBtn, profileBtn, userBtn;
    private FirebaseAuth auth;

    private ImageView ic_add_user, ic_delete_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // khai bao
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        userBtn = findViewById(R.id.userBtn);
        ic_add_user = findViewById(R.id.ic_add_user);
        ic_delete_user = findViewById(R.id.ic_delete_user);
        item_user = findViewById(R.id.item_user);
        auth = FirebaseAuth.getInstance();
        searchBar = findViewById(R.id.searchBar);

        recyclerView = findViewById(R.id.listviewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userRef = databaseReference.child(USER_PATH);

        userAdapter = new AdapterUser(this, new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                    Log.e("TAG", "User: " + user);
                }
                userAdapter.setUserList(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        // Check login status using Firebase Authentication state listener
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }

        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StudentManagement.class);
            startActivity(intent);
            finish();
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

        ic_add_user.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewUser.class);
            startActivity(intent);
            // Call the method to add a new user
//            UserManagement.addNewUser();
        });

//        ic_delete_user.setOnClickListener(view -> {
////            UserManagement.deleteExistingUser();
//        });

        searchBar.setOnClickListener(view -> {

        });

//        item_user.setOnClickListener(view -> {
//            Intent intent = new Intent(this, ProfileUser.class);
//            startActivity(intent);
//            finish();
//        });
    }
}
