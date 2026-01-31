package com.example.sos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentRecordActivity extends AppCompatActivity {

    RecyclerView rvStudents;
    StudentAdapter adapter;
    List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_record);

        rvStudents = findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentAdapter(studentList);
        rvStudents.setAdapter(adapter);

        fetchStudents();
    }

    private void fetchStudents() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("students");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                studentList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Student student = snap.getValue(Student.class);
                    if (student != null) {
                        student.enrollment = snap.getKey(); // key = enrollment
                        studentList.add(student);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentRecordActivity.this,
                        "Failed to load students",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
