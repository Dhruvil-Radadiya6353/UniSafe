package com.example.sos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnStudentRecords, btnSosRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnStudentRecords = findViewById(R.id.btnStudentRecords);
        btnSosRecords = findViewById(R.id.btnSosRecords);

        // Go to Student Records
        btnStudentRecords.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDashboardActivity.this,
                    StudentRecordActivity.class
            ));
        });

        // Go to SOS Records
        btnSosRecords.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminDashboardActivity.this,
                    SosRecordActivity.class
            ));
        });
    }
}
