package com.example.studentinformationmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ListStudent extends AppCompatActivity {

    private static final String STUDENTS_PATH = "students";

    private DatabaseReference databaseReference;
    private DatabaseReference studentRef;
    private AdapterStudent studentAdapter;
    private RecyclerView recyclerView;
    private ImageView icClose, ic_delete_student;
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
        ic_delete_student = findViewById(R.id.ic_delete_student);

        setupSearchView();

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

                studentAdapter.setOnDeleteIconClickListener(new AdapterStudent.OnDeleteIconClickListener() {
                    @Override
                    public void onDeleteIconClick(int position) {
                        // Handle delete icon click
                        Student student = studentAdapter.getStudent(position);
                        if (student != null) {
                            deleteStudent(student);
                        }
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        studentAdapter.setOnItemClickListener(new AdapterStudent.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ListStudent.this, ProfileStudent.class);
                // Pass the position of the selected student to the ProfileStudent activity
                intent.putExtra("STUDENT_POSITION", position);
                Log.e("studentAdapter", "di toi profile student");
                startActivity(intent);
            }

            @Override
            public void onDeleteIconClick(int adapterPosition) {
                // Handle delete icon click
                Student student = studentAdapter.getStudent(adapterPosition);
                if (student != null) {
                    deleteStudent(student);
                    Log.e("studentAdapter", "deleteStudent");
                }
            }
        });


        btnSortByName.setOnClickListener(view -> {
            studentAdapter.sortByName();
        });

        btnSortByID.setOnClickListener(view -> {
            studentAdapter.sortByID();
        });

        icClose.setOnClickListener(view -> finish());
    }

    private void deleteStudent(Student student) {
        // Get a reference to the "students" node in the database
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("students");

        // Find the student key in the database
        Query query = studentsRef.orderByChild("ID").equalTo(student.getID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Remove the student from the database
                    snapshot.getRef().removeValue();
                }
                Log.e("Student delete", "Student deleted successfully");
                Toast.makeText(ListStudent.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Student delete", "Student deleted failed");
            }
        });
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
                studentAdapter.search(newText);
                return true;
            }
        });
    }


}
