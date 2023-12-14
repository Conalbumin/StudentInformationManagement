package com.example.studentinformationmanagement;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder> {
    private static ArrayList<User> userList;
    private String currentUserRole;
    private Context context;
    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position, String userEmail);
    }

    public AdapterUser(Context context, ArrayList<User> userList, OnItemClickListener listener, String currentUserRole) {
        this.context = context;
        this.userList = userList;
        this.onItemClickListener = listener;
        this.currentUserRole = currentUserRole;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void deleteUserByEmail(String userEmail, String userUid) {
        // Call the private method to handle the deletion from the database
        Log.e("TAG", userEmail);
        deleteUserFromDatabase(userEmail, userUid);
    }

    private void deleteUserFromDatabase(String userEmail, String userUid) {
        // Implement the logic to delete the user from the database using email and UID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

        Query query = userRef.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User deleted from the database using email: " + userEmail);
                            // Now, delete the corresponding user from Firebase Authentication
                        } else {
                            Log.e("TAG", "Error deleting user from the database: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Error querying the database: " + error.getMessage());
            }
        });
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_remove, parent, false);
        return new UserViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.personName.setText(user.getName());
        holder.personNumber.setText(user.getPhoneNumber());
        holder.userRole.setText(user.getRole());

        // Set a click listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (onItemClickListener != null && currentUserRole.equals("Admin")) {
                // Call onDeleteClick method when the delete button is clicked
                Log.e("TAG", "onBindViewHolder" + user.getEmail());
                onItemClickListener.onDeleteClick(position, user.getEmail()); // Pass user email
            } else {
                // Handle the case when the current user is not an admin
                Toast.makeText(context, "You are not allowed to delete users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView personName, personNumber, userRole;
        CircleImageView profile_pic;
        ImageView deleteButton;
        OnItemClickListener onItemClickListener;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener; // Initialize the variable
            personName = itemView.findViewById(R.id.personName);
            personNumber = itemView.findViewById(R.id.personNumber);
            userRole = itemView.findViewById(R.id.userRole);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            deleteButton = itemView.findViewById(R.id.ic_delete_user);

            deleteButton.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    // Pass both position and email to onDeleteClick
                    int position = getAdapterPosition();
                    String email = userList.get(position).getEmail();
                    onItemClickListener.onDeleteClick(position, email);
                }
            });
        }
    }


}
