package com.example.sos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Header
    TextView btnLogin, btnLogout;

    // Emergency buttons
    Button btnSOS, btnFire, btnMedical, btnSafety;

    long lastClickTime = 0;
    static final long DOUBLE_CLICK_TIME = 600;

    FusedLocationProviderClient fusedLocationClient;

    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI refs
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        btnSOS = findViewById(R.id.btnSOS);
        btnFire = findViewById(R.id.btnFire);
        btnMedical = findViewById(R.id.btnMedical);
        btnSafety = findViewById(R.id.btnSafety);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestPermissionsIfNeeded();
        handleLoginAndLogout();
        setupEmergencyButtons();
        setupSOSDoubleTap();
    }

    /* ---------------- PERMISSIONS ---------------- */

    private void requestPermissionsIfNeeded() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
    }

    /* ---------------- LOGIN / LOGOUT ---------------- */

    private void handleLoginAndLogout() {

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String role = sp.getString("role", "");

        if (!loggedIn) {
            // ❌ Not logged in
            btnLogin.setText("Login");
            btnLogin.setEnabled(true);
            btnLogin.setOnClickListener(v ->
                    startActivity(new Intent(this, LoginActivity.class)));

            btnLogout.setVisibility(View.GONE);

        } else {
            // ✅ Logged in
            btnLogout.setVisibility(View.VISIBLE);

            btnLogout.setOnClickListener(v -> {
                sp.edit().clear().apply();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });

            if ("admin".equals(role)) {
                btnLogin.setText("Live Dashboard");
                btnLogin.setEnabled(true);
                btnLogin.setOnClickListener(v ->
                        startActivity(new Intent(
                                this,
                                AdminDashboardActivity.class
                        )));
            } else {
                btnLogin.setText("Logged In");
                btnLogin.setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleLoginAndLogout();
    }

    /* ---------------- FIRE / MEDICAL / SAFETY ---------------- */

    private void setupEmergencyButtons() {

        btnFire.setOnClickListener(v -> openConfirm("Fire"));
        btnMedical.setOnClickListener(v -> openConfirm("Medical"));
        btnSafety.setOnClickListener(v -> openConfirm("Safety"));
    }

    private void openConfirm(String type) {
        Intent i = new Intent(this, ConfirmEmergencyActivity.class);
        i.putExtra("type", type);
        startActivity(i);
    }

    /* ---------------- SOS DOUBLE TAP ---------------- */

    private void setupSOSDoubleTap() {

        btnSOS.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < DOUBLE_CLICK_TIME) {
                    sendInstantSOS();
                }
                lastClickTime = now;
            }
            return true;
        });
    }

    /* ---------------- INSTANT SOS ---------------- */

    private void sendInstantSOS() {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "SMS permission required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);

        String name = sp.getString("name", "Unknown");
        String phone = sp.getString("phone", "N/A");
        String role = sp.getString("role", "unknown");

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            double lat = 0, lng = 0;
            String mapLink = "Location unavailable";

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                mapLink = "https://maps.google.com/?q=" + lat + "," + lng;
            }

            saveSOS(role, name, phone, lat, lng, mapLink);

            String message =
                    "🚨 UNI SAFE - EMERGENCY ALERT 🚨\n\n" +
                            "Type: SOS\n" +
                            "Name: " + name + "\n" +
                            "Phone: " + phone + "\n\n" +
                            "Location:\n" + mapLink;

            sendSMSToAdmins(message);
        });
    }

    /* ---------------- FIREBASE ---------------- */

    private void saveSOS(String role,
                         String name,
                         String phone,
                         double lat,
                         double lng,
                         String mapLink) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("sos_history")
                .push();

        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "SOS");
        data.put("role", role);
        data.put("name", name);
        data.put("phone", phone);
        data.put("latitude", lat);
        data.put("longitude", lng);
        data.put("mapLink", mapLink);
        data.put("timestamp", System.currentTimeMillis());
        data.put("status", "active");

        ref.setValue(data);
    }

    /* ---------------- SMS ---------------- */

    private void sendSMSToAdmins(String message) {

        DatabaseReference adminRef =
                FirebaseDatabase.getInstance()
                        .getReference("users/admins");

        adminRef.get().addOnSuccessListener(snapshot -> {

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);

            for (DataSnapshot admin : snapshot.getChildren()) {

                String phone = admin.getKey().trim();

                // 🇮🇳 INDIA FIX
                if (!phone.startsWith("+")) {
                    if (phone.length() == 10)
                        phone = "+91" + phone;
                    else if (phone.startsWith("91"))
                        phone = "+" + phone;
                }

                sms.sendMultipartTextMessage(
                        phone,
                        null,
                        parts,
                        null,
                        null
                );
            }

            Toast.makeText(
                    this,
                    "🚨 SOS SENT SUCCESSFULLY",
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}
