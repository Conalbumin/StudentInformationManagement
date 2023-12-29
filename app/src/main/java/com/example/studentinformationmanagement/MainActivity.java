package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements AdapterUser.OnItemClickListener {
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

        // Initialize UI elements
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        userBtn = findViewById(R.id.userBtn);
        ic_add_user = findViewById(R.id.ic_add_user);
        ic_delete_user = findViewById(R.id.ic_delete_user);
        item_user = findViewById(R.id.item_user);
        auth = FirebaseAuth.getInstance();
        searchBar = findViewById(R.id.searchBar);

        setupSearchView();

        recyclerView = findViewById(R.id.listviewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userRef = databaseReference.child(USER_PATH);

        // Check login status using Firebase Authentication state listener
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            // Obtain the current user's role
            UserManagement.getCurrentRole(currentRole -> {
                String currentUserRole = currentRole;

                // Initialize AdapterUser with the obtained role
                userAdapter = new AdapterUser(this, new ArrayList<>(), currentUserRole);
                recyclerView.setAdapter(userAdapter);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<User> users = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            user.setUid(snapshot.getKey()); // Set the UID from the snapshot key
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

                userAdapter.setOnItemClickListener(this);

                studentBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, StudentManagement.class);
                    startActivity(intent);
                    finish();
                });

                profileBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, ProfileUser.class);
                    startActivity(intent);
                    finish();
                });

                userBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });

                ic_add_user.setOnClickListener(view -> {
                    // Check if the current user has the permission to add a new user
                    if (UserManagement.isCurrentUserAllowedToAddUser(currentUserRole)) {
                        // User has permission, proceed to AddNewUser activity
                        Intent intent = new Intent(MainActivity.this, AddNewUser.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "You are not allowed to add user", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        // Retrieve the user clicked
        User selectedUser = userAdapter.getUserList().get(position);
        Log.e("TAG", "selectedUser " + selectedUser);
        // Start a new activity (ProfileUserEdit.class) and pass the selected user's UID
        Intent intent = new Intent(MainActivity.this, ProfileUserEdit.class);
        intent.putExtra("userId", selectedUser.getUid());
        Log.e("TAG", "selectedUser id " + selectedUser.getUid());

        startActivity(intent);
    }


    @Override
    public void onDeleteClick(int position, String userEmail) {
        String userUid = userAdapter.getUserList().get(position).getUid();
        // Call the public method in AdapterUser to delete the user by email and UID
        userAdapter.deleteUserByEmail(userEmail, userUid);
    }

    private void setupSearchView() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the query submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchQuery", "Query: " + newText);
                userAdapter.search(newText);
                return true;
            }
        });
    }



}
