package com.example.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etName, etEnroll, etDept, etPhone;
    Button btnLogin;
    TextView tvLoginAuthority;

    DatabaseReference studentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI references
        etName = findViewById(R.id.etName);
        etEnroll = findViewById(R.id.etEnroll);
        etDept = findViewById(R.id.etDept);
        etPhone = findViewById(R.id.etPhone);
        btnLogin = findViewById(R.id.btnStudentSubmit);
        tvLoginAuthority = findViewById(R.id.tvLoginAuthority);

        // Firebase reference
        studentRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("students");

        // STUDENT LOGIN / REGISTER
        btnLogin.setOnClickListener(v -> handleStudentLogin());

        // AUTHORITY LOGIN
        tvLoginAuthority.setOnClickListener(v -> {
            startActivity(new Intent(
                    LoginActivity.this,
                    AdminLoginActivity.class
            ));
        });
    }

    /* ---------------- STUDENT LOGIN LOGIC ---------------- */

    private void handleStudentLogin() {

        String name = etName.getText().toString().trim();
        String enroll = etEnroll.getText().toString().trim();
        String dept = etDept.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (name.isEmpty() || enroll.isEmpty() || dept.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10) {
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase-safe key
        enroll = enroll.replace(".", "").replace(" ", "");
        String finalEnroll = enroll;

        studentRef.child(finalEnroll).get().addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                return;
            }

            DataSnapshot snapshot = task.getResult();

            if (!snapshot.exists()) {
                // New student
                HashMap<String, Object> studentData = new HashMap<>();
                studentData.put("name", name);
                studentData.put("department", dept);
                studentData.put("phone", phone);

                studentRef.child(finalEnroll).setValue(studentData);
                Toast.makeText(this, "New user registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            }

            saveStudentSession(name, finalEnroll, dept, phone);
            goToHome();
        });
    }

    /* ---------------- SESSION ---------------- */

    private void saveStudentSession(String name,
                                    String enroll,
                                    String dept,
                                    String phone) {

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        sp.edit()
                .putBoolean("loggedIn", true)
                .putString("role", "student")
                .putString("name", name)
                .putString("enrollment", enroll)
                .putString("department", dept)
                .putString("phone", phone)
                .apply();
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
