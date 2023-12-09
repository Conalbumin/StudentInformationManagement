package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ListStudent extends AppCompatActivity {

    private static final String STUDENTS_PATH = "students";

    private DatabaseReference databaseReference;
    private DatabaseReference studentRef;
    private AdapterStudent studentAdapter;
    private RecyclerView recyclerView;
    private ImageView icClose, ic_delete_user;
    private SearchView searchBar;
    private TextView btnSortByName, btnSortByID;
    private LinearLayout itemStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Initialize views
        icClose = findViewById(R.id.ic_close);
        searchBar = findViewById(R.id.searchBar);
        btnSortByName = findViewById(R.id.btnSortByName);
        btnSortByID = findViewById(R.id.btnSortByID);
        itemStudent = findViewById(R.id.item_student);
        ic_delete_user = findViewById(R.id.ic_delete_user);

        recyclerView = findViewById(R.id.list_student);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentRef = databaseReference.child(STUDENTS_PATH);

        studentAdapter = new AdapterStudent(this, new ArrayList<>());
        recyclerView.setAdapter(studentAdapter);

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Student> students = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = new Student((Map<String, Object>) snapshot.getValue());
                    students.add(student);
                }
                studentAdapter.setStudentList(students);

                studentAdapter.setOnItemClickListener(position -> {
                    Student selectedStudent = studentAdapter.getStudent(position);
                    Intent intent = new Intent(ListStudent.this, ProfileStudent.class);

                    // Pass the selected student's ID to the ProfileStudent activity
                    intent.putExtra("STUDENT_ID", selectedStudent.getID());
                    startActivity(intent);
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

//        ic_delete_user.setOnClickListener(view -> {
//            if (studentAdapter.getSelectedStudent() != null) {
//                deleteStudent(studentAdapter.getSelectedStudent().getID());
//            }
//        });

        icClose.setOnClickListener(view -> finish());
    }

    private void deleteStudent(String studentId) {
        DatabaseReference studentToDeleteRef = studentRef.child(studentId);
        studentToDeleteRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteStudent", "Student data deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteStudent", "Error deleting student data", e);
                });
    }
}
