package com.example.sos;

import android.os.Bundle;

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

public class SosRecordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SOSAdapter adapter;

    ArrayList<SOSModel> sosList = new ArrayList<>();
    ArrayList<String> sosKeys = new ArrayList<>();

    DatabaseReference sosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_record);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerSOS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SOSAdapter(this, sosList, sosKeys);
        recyclerView.setAdapter(adapter);

        // Firebase reference
        sosRef = FirebaseDatabase.getInstance()
                .getReference("sos_history");

        loadSOSRecords();
    }

    private void loadSOSRecords() {

        sosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sosList.clear();
                sosKeys.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    SOSModel model = child.getValue(SOSModel.class);

                    if (model != null) {
                        sosList.add(model);
                        sosKeys.add(child.getKey());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: log error
            }
        });
    }
}
