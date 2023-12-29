package com.example.studentinformationmanagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddNewUser extends AppCompatActivity {
    private ImageView ic_close;
    private EditText txtUsername, txtUserAge, txtUserPhone, txtUserRole, editTextEmail, editTextPassword;
    private Switch txtUserStatus;
    private Button btnAddUser;
    private String currentUserEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Initialize UI elements
        ic_close = findViewById(R.id.ic_close);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        txtUsername = findViewById(R.id.txtUsername);
        txtUserAge = findViewById(R.id.txtUserAge);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        txtUserRole = findViewById(R.id.txtUserRole);
        txtUserStatus = findViewById(R.id.txtUserStatus);
        btnAddUser = findViewById(R.id.btnAddUser);

        // Set onClickListener for close button
        ic_close.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        // Set onClickListener for add user button
        btnAddUser.setOnClickListener(view -> {
            // Retrieve data from UI elements
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = txtUsername.getText().toString();
            int age = Integer.parseInt(txtUserAge.getText().toString());
            String phoneNumber = txtUserPhone.getText().toString();
            String role = txtUserRole.getText().toString();
            boolean status = txtUserStatus.isChecked();

            // Show password confirmation dialog
            showPasswordConfirmationDialog(email, password, name, age, phoneNumber, status, role);
        });
    }

    private void showPasswordConfirmationDialog(String email, String password, String name, int age, String phoneNumber, boolean status, String role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Action");
        builder.setMessage("Please enter your password to confirm.");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String confirmPassword = input.getText().toString();
            // Verify the password
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserEmail = currentUser.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(currentUserEmail, confirmPassword);
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                // Password confirmed, proceed with adding the user
                                UserManagement.addNewUser(email, password, name, age, phoneNumber, status, role, () -> {
                                    // Sign in again using the current user's credentials
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUserEmail, confirmPassword)
                                            .addOnCompleteListener(signInTask -> {
                                                if (signInTask.isSuccessful()) {
                                                    // Successfully signed in again
                                                    Toast.makeText(this, "User confirmation successful", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    // Handle sign-in failure
                                                    Toast.makeText(this, "Sign-in failed after confirmation", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            } else {
                                // Incorrect password, show a message or handle accordingly
                                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle cancellation if needed
        });

        builder.show();
    }
}

