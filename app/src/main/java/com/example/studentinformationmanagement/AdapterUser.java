package com.example.studentinformationmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder> {
    private ArrayList<User> userList;
    private Context context;

    public AdapterUser(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdapterUser.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_remove, parent, false);
        return new AdapterUser.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUser.UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.personName.setText(user.getName());
        holder.personNumber.setText(user.getPhoneNumber());
        holder.userRole.setText(user.getRole());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView personName, personNumber, userRole;
        CircleImageView profile_pic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.personName);
            personNumber = itemView.findViewById(R.id.personNumber);
            userRole = itemView.findViewById(R.id.userRole);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }


}
