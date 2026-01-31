package com.example.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminLoginActivity extends AppCompatActivity {

    EditText etName, etPhone, etAdminId;
    Button btnSubmit;

    DatabaseReference adminRef;

    private static final String ADMIN_KEY = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etName = findViewById(R.id.etAdminName);
        etPhone = findViewById(R.id.etAdminPhone);
        etAdminId = findViewById(R.id.etAdminId);
        btnSubmit = findViewById(R.id.btnAdminSubmit);

        adminRef = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child("admins");

        btnSubmit.setOnClickListener(v -> handleAdminLogin());
    }

    private void handleAdminLogin() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String adminId = etAdminId.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || adminId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!adminId.equals(ADMIN_KEY)) {
            Toast.makeText(this, "Invalid Admin ID", Toast.LENGTH_SHORT).show();
            return;
        }

        adminRef.child(phone).get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                return;
            }

            DataSnapshot snapshot = task.getResult();

            if (!snapshot.exists()) {
                // New admin
                HashMap<String, String> adminData = new HashMap<>();
                adminData.put("name", name);
                adminData.put("adminId", adminId);

                adminRef.child(phone).setValue(adminData);
                Toast.makeText(this, "Admin registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Welcome back Admin", Toast.LENGTH_SHORT).show();
            }

            saveAdminSession(name, phone);
            goToHome();
        });
    }

    private void saveAdminSession(String name, String phone) {
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        sp.edit()
                .putBoolean("loggedIn", true)
                .putString("role", "admin")
                .putString("name", name)
                .putString("phone", phone)
                .putString("adminId", "admin123")
                .apply();
    }


    private void goToHome() {
        Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
