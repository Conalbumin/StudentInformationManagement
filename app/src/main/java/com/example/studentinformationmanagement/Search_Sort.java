package com.example.studentinformationmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Search_Sort extends AppCompatActivity {
    private Spinner spinnerUser;
    private ArrayList<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_sort);

        this.spinnerUser = findViewById(R.id.sort);
        usersList = User.getUsersList();

        // Create an ArrayAdapter using a simple spinner layout and the list of users
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usersList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        this.spinnerUser.setAdapter(adapter);

        // When user selects an item from the spinner
        this.spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onItemSelectedHandler(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here for now
            }
        });
    }

    private void onItemSelectedHandler(int position) {
        User selectedUser = usersList.get(position);
        Toast.makeText(getApplicationContext(), selectedUser.toString(), Toast.LENGTH_SHORT).show();
    }
}
