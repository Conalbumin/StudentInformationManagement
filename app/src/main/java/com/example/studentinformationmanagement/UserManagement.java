package com.example.studentinformationmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Scanner;

public class UserManagement extends AppCompatActivity {

    private static final String TAG = "UserManagement";
    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference;
    private AppCompatButton userBtn, studentBtn, profileBtn;
    private SearchView searchBar;
    private RelativeLayout item_user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize views
        searchBar = findViewById(R.id.search);
        userBtn = findViewById(R.id.userBtn);
        studentBtn = findViewById(R.id.studentBtn);
        profileBtn = findViewById(R.id.profileBtn);
        item_user = findViewById(R.id.item_user);

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            finish();
        });

        studentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, StudentManagement.class);
            startActivity(intent);
            finish();
        });

        item_user.setOnClickListener(view -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
            finish();
        });
    }

    public UserManagement() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public static void addNewUser(String email, String password, String name, int age, String phoneNumber, boolean status, String isAdmin) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User userData = new User(email, name, age, phoneNumber, status, isAdmin);
                            addUserToDatabase(email, userData);
                        }
                    } else {
                        // Handle failure
                    }
                });
    }

    private static void addUserToDatabase(String email, User user) {
        databaseReference.child(email.replace(".", ",")).setValue(user);
    }

    public static void deleteExistingUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    });
        }
    }

    public static void modifyUserInfo(String displayName, Uri photoUri) {
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "User profile updated.");
                    }
                });
    }

    public static void viewLoginHistory() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nViewing login history of a user:");
        System.out.print("Enter the email of the user: ");
        String emailToView = scanner.nextLine();

        try {
            // Get the firebase auth instance
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // Fetch the user with the specified email address
            FirebaseUser user = auth.getCurrentUser();

            // Display user's last sign-in time if the user exists
            if (user != null) {
                System.out.println("Last Sign-In Time: " + user.getMetadata().getLastSignInTimestamp());
            } else {
                System.out.println("User with email " + emailToView + " does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewUserList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through each user in the "users" node
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve user information
                        String email = userSnapshot.child("email").getValue(String.class);
                        String displayName = userSnapshot.child("displayName").getValue(String.class);

                        System.out.println("User Email: " + email);
                        System.out.println("Display Name: " + displayName);
                    }
                } else {
                    System.out.println("No users found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error reading user information: " + databaseError.getMessage());
            }
        });
    }


    private void updateUI(FirebaseUser user) {
        // You can update the UI as needed, for example, show user information or redirect to another activity.
    }
    private void onDataChange() {
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
