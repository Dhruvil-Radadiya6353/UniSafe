package com.example.sos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter
        extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    List<Student> studentList;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);

        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull StudentViewHolder holder, int position) {

        Student s = studentList.get(position);

        holder.tvName.setText(s.name != null ? s.name : "N/A");
        holder.tvEnroll.setText("Enrollment: " +
                (s.enrollment != null ? s.enrollment : "N/A"));
        holder.tvDept.setText("Department: " +
                (s.department != null ? s.department : "N/A"));
        holder.tvPhone.setText("Phone: " +
                (s.phone != null ? s.phone : "N/A"));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEnroll, tvDept, tvPhone;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEnroll = itemView.findViewById(R.id.tvEnroll);
            tvDept = itemView.findViewById(R.id.tvDept);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }
}
