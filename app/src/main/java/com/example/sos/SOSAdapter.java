package com.example.sos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class SOSAdapter extends RecyclerView.Adapter<SOSAdapter.ViewHolder> {

    Context context;
    ArrayList<SOSModel> list;
    ArrayList<String> keys;

    DatabaseReference sosRef =
            FirebaseDatabase.getInstance().getReference("sos_history");

    public SOSAdapter(Context context,
                      ArrayList<SOSModel> list,
                      ArrayList<String> keys) {
        this.context = context;
        this.list = list;
        this.keys = keys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        SOSModel s = list.get(position);
        String key = keys.get(position);

        // Safety check
        if (s == null) return;

        // Emergency type
        String type = (s.type == null) ? "Emergency" : s.type + " Emergency";
        h.tvType.setText(type);

        // User
        h.tvUser.setText("User: " + (s.name == null ? "Unknown" : s.name));

        // Time
        h.tvTime.setText("Time: " + new Date(s.timestamp).toString());

        // Status
        String status = (s.status == null) ? "active" : s.status;

        switch (status) {
            case "in_progress":
                h.tvStatus.setText("In Progress");
                h.tvStatus.setBackgroundResource(R.drawable.status_progress);
                break;

            case "resolved":
                h.tvStatus.setText("Resolved");
                h.tvStatus.setBackgroundResource(R.drawable.status_resolved);
                break;

            default:
                h.tvStatus.setText("Active");
                h.tvStatus.setBackgroundResource(R.drawable.status_active);
                break;
        }

        // Buttons
        h.btnProgress.setOnClickListener(v ->
                sosRef.child(key).child("status").setValue("in_progress"));

        h.btnResolved.setOnClickListener(v ->
                sosRef.child(key).child("status").setValue("resolved"));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvStatus, tvUser, tvTime;
        Button btnProgress, btnResolved;

        ViewHolder(@NonNull View v) {
            super(v);
            tvType = v.findViewById(R.id.tvType);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvUser = v.findViewById(R.id.tvUser);
            tvTime = v.findViewById(R.id.tvTime);
            btnProgress = v.findViewById(R.id.btnProgress);
            btnResolved = v.findViewById(R.id.btnResolved);
        }
    }
}
