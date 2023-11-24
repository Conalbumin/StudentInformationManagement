package com.example.studentinformationmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private List<User> userList;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_user, parent, false);
        }

        TextView personName = convertView.findViewById(R.id.personName);
        TextView personNumber = convertView.findViewById(R.id.personNumber);
        TextView userRole = convertView.findViewById(R.id.userRole);

        User user = userList.get(position);

        personName.setText("Name: " + user.getName());
        personNumber.setText("Age: " + user.getPhoneNumber());
        userRole.setText("Role: " + (user.isAdmin()));

        return convertView;
    }
}
