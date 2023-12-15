package com.example.studentinformationmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference userRef = databaseReference.child("users");

    public static void addNewUser(String email, String password, String name, int age, String phoneNumber, boolean status, String role) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Update user profile in authentication
                            User userData = new User(email, name, age, phoneNumber, status, role);
                            addUserToDatabase(user.getUid(), userData);
                        }
                    } else {
                        Log.e(TAG, "Failed to add user to database: " + task.getException());
                    }
                });
    }

    private static void addUserToDatabase(String userId, User user) {
        userRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "User added to database successfully.");
                    } else {
                        // Handle failure
                        Log.e(TAG, "Failed to add user to database: " + task.getException());
                    }
                });
    }

    public static boolean isCurrentUserAllowedToAddUser(String currentRole) {
        if (currentRole.equals("Admin")) {
            return true;
        }
        return false;
    }

    public static void getCurrentRole(RoleCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String role = user.getRole();
                            Log.e("currentRole", "getCurrentRole " + role);
                            callback.onRoleReceived(role);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    interface RoleCallback {
        void onRoleReceived(String role);
    }
}
