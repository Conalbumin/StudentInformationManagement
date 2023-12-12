package com.example.studentinformationmanagement;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterStudent extends RecyclerView.Adapter<AdapterStudent.StudentViewHolder> {

    private ArrayList<Student> studentList;
    private ArrayList<Student> backupList;
    private Student selectedStudent;
    private Context context;
    private OnItemClickListener mListener;

    public void setSelectedStudent(Student student) {
        selectedStudent = student;
        notifyDataSetChanged();
    }

    public void setStudentList(ArrayList<Student> studentList) {
        this.studentList = studentList;
        // Update the backup list when the original data changes
        this.backupList = new ArrayList<>(studentList);
        notifyDataSetChanged();
    }

    public Student getStudent(int position) {
        return studentList.get(position);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
    public void sortByName() {
        Collections.sort(studentList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        notifyDataSetChanged();
    }

    public void sortByID() {
        Collections.sort(studentList, (s1, s2) -> s1.getID().compareToIgnoreCase(s2.getID()));
        notifyDataSetChanged();
    }

    public void search(String query) {
        ArrayList<Student> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(query)) {
            // If the query is empty, restore the original list
            filteredList.addAll(backupList);
        } else {
            // Filter the list based on the query
            for (Student student : backupList) {
                if (student.getName().toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())) ||
                        student.getID().toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))) {
                    filteredList.add(student);
                }
            }
        }

        // Update the adapter with the filtered list
        studentList.clear();
        studentList.addAll(filteredList);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdapterStudent(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = new ArrayList<>(studentList);
        this.backupList = new ArrayList<>(studentList);
    }


    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentName.setText(student.getName());
        holder.studentId.setText(student.getID());

        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentId;
        CircleImageView profile_pic;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentId = itemView.findViewById(R.id.studentId);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
