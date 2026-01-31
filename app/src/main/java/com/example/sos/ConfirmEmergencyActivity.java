package com.example.sos;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfirmEmergencyActivity extends AppCompatActivity {

    TextView tvEmergencyType;
    Spinner spBuilding;
    EditText etRoom;
    Button btnCancel, btnConfirm;

    String emergencyType;
    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_emergency);

        emergencyType = getIntent().getStringExtra("type");

        tvEmergencyType = findViewById(R.id.tvEmergencyType);
        spBuilding = findViewById(R.id.spBuilding);
        etRoom = findViewById(R.id.etRoom);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        tvEmergencyType.setText(emergencyType + " Emergency");

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        setupSpinner();

        btnCancel.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> sendEmergency());
    }

    private void setupSpinner() {
        String[] buildings = {
                "Select Building",
                "EC Building",
                "Mechanical Building",
                "IT Building",
                "Civil Building"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_black,   // 👈 selected item
                buildings
        );

        adapter.setDropDownViewResource(
                R.layout.spinner_item_black    // 👈 dropdown list
        );

        spBuilding.setAdapter(adapter);
    }


    /* ---------------- SEND EMERGENCY ---------------- */

    private void sendEmergency() {

        String building = spBuilding.getSelectedItem().toString();
        String room = etRoom.getText().toString().trim();

        if (building.equals("Select Building") || room.isEmpty()) {
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,
                    "Location & SMS permission required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(loc -> {

            double lat = 0, lng = 0;
            String mapLink = "Location unavailable";

            if (loc != null) {
                lat = loc.getLatitude();
                lng = loc.getLongitude();
                mapLink = "https://maps.google.com/?q=" + lat + "," + lng;
            }

            SharedPreferences sp =
                    getSharedPreferences("session", MODE_PRIVATE);

            HashMap<String, Object> data = new HashMap<>();
            data.put("type", emergencyType);
            data.put("name", sp.getString("name", "Unknown"));
            data.put("phone", sp.getString("phone", "N/A"));
            data.put("role", sp.getString("role", "unknown"));
            data.put("building", building);
            data.put("room", room);
            data.put("latitude", lat);
            data.put("longitude", lng);
            data.put("mapLink", mapLink);
            data.put("status", "active");
            data.put("timestamp", System.currentTimeMillis());

            FirebaseDatabase.getInstance()
                    .getReference("sos_history")
                    .push()
                    .setValue(data);

            sendSmsToAdmins(data, mapLink);

            Toast.makeText(this,
                    "Emergency Alert Sent",
                    Toast.LENGTH_LONG).show();

            finish();
        });
    }

    /* ---------------- SMS TO ADMINS ---------------- */

    private void sendSmsToAdmins(HashMap<String, Object> d, String mapLink) {

        FirebaseDatabase.getInstance()
                .getReference("users/admins")
                .get()
                .addOnSuccessListener(snapshot -> {

                    SmsManager sms = SmsManager.getDefault();

                    String message =
                            "🚨 UNI SAFE - " + d.get("type") + " EMERGENCY 🚨\n\n" +
                                    "Name: " + d.get("name") + "\n" +
                                    "Phone: " + d.get("phone") + "\n" +
                                    "Building: " + d.get("building") + "\n" +
                                    "Room: " + d.get("room") + "\n\n" +
                                    "Location:\n" + mapLink;

                    ArrayList<String> parts =
                            sms.divideMessage(message);

                    for (DataSnapshot admin : snapshot.getChildren()) {

                        String phone = admin.getKey().trim();

                        // 🇮🇳 SAFE INDIA FORMAT
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
                });
    }
}
