package com.example.queue_me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnViewDoctors;
    private LinearLayout btnScanQR;
    private TextView txtPatientName;
    private LinearLayout btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        btnViewDoctors = findViewById(R.id.btnViewDoctors);
        btnScanQR = findViewById(R.id.btnScanQR);
        txtPatientName = findViewById(R.id.txtPatientName);
        btnLogout = findViewById(R.id.btnLogout);

        // Load and display patient name
        loadPatientInfo();

        // Set click listeners
        btnViewDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Doctor List Activity
                Intent intent = new Intent(MainActivity.this, DoctorListActivity.class);
                startActivity(intent);
            }
        });

        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to QR Scanner Activity
                Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void loadPatientInfo() {
        SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
        String patientName = prefs.getString("patient_name", "Guest");

        // Display only the patient name (no phone number)
        txtPatientName.setText("Hello, " + patientName);
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}