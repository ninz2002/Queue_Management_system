package com.example.queue_me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorDetailsActivity extends AppCompatActivity {

    private TextView txtDoctorName;
    private TextView txtDepartment;
    private TextView txtQueueCount;
    private TextView txtWaitTime;
    private EditText etPatientName;
    private EditText etPatientPhone;
    private LinearLayout btnJoinQueue;

    private int doctorId;
    private String doctorName;
    private String doctorDepartment;
    private int queueCount;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        txtDoctorName = findViewById(R.id.txtDoctorName);
        txtDepartment = findViewById(R.id.txtDepartment);
        txtQueueCount = findViewById(R.id.txtQueueCount);
        txtWaitTime = findViewById(R.id.txtWaitTime);
        etPatientName = findViewById(R.id.etPatientName);
        etPatientPhone = findViewById(R.id.etPatientPhone);
        btnJoinQueue = findViewById(R.id.btnJoinQueue);

        // Get data from intent
        Intent intent = getIntent();
        doctorId = intent.getIntExtra("doctor_id", 0);
        doctorName = intent.getStringExtra("doctor_name");
        doctorDepartment = intent.getStringExtra("doctor_department");
        queueCount = intent.getIntExtra("queue_count", 0);

        // Display doctor details
        displayDoctorDetails();

        // Setup button click listener
        btnJoinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQueue();
            }
        });
    }

    private void displayDoctorDetails() {
        txtDoctorName.setText(doctorName);
        txtDepartment.setText(doctorDepartment);
        txtQueueCount.setText(String.valueOf(queueCount));

        // Calculate estimated wait time (5 minutes per patient)
        int waitTime = queueCount * 5;
        txtWaitTime.setText(String.valueOf(waitTime));
    }

    private void joinQueue() {
        String patientName = etPatientName.getText().toString().trim();
        String patientPhone = etPatientPhone.getText().toString().trim();

        // Validate inputs
        if (patientName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            etPatientName.requestFocus();
            return;
        }

        if (patientPhone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            etPatientPhone.requestFocus();
            return;
        }

        if (patientPhone.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            etPatientPhone.requestFocus();
            return;
        }

        // Add to queue
        long result = databaseHelper.addToQueue(doctorId, patientName, patientPhone);

        if (result != -1) {
            Toast.makeText(this, "Successfully joined the queue!", Toast.LENGTH_SHORT).show();

            // Navigate to Queue Status Activity
            Intent intent = new Intent(DoctorDetailsActivity.this, QueueStatusActivity.class);
            intent.putExtra("queue_id", result);
            intent.putExtra("doctor_name", doctorName);
            intent.putExtra("doctor_department", doctorDepartment);
            intent.putExtra("patient_name", patientName);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to join queue. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh queue count
        queueCount = databaseHelper.getQueueCount(doctorId);
        displayDoctorDetails();
    }
}