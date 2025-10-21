package com.example.queue_me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etPatientName;
    private EditText etPhoneNumber;
    private LinearLayout btnRegister;
    private TextView txtLoginHere;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        etPatientName = findViewById(R.id.etPatientName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);
        txtLoginHere = findViewById(R.id.txtLoginHere);

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPatient();
            }
        });

        // Login link click listener
        txtLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerPatient() {
        String patientName = etPatientName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate inputs
        if (patientName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            etPatientName.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            etPhoneNumber.requestFocus();
            return;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            etPhoneNumber.requestFocus();
            return;
        }

        // Check if phone number already exists
        int existingPatientId = databaseHelper.getPatientIdByPhone(phoneNumber);
        if (existingPatientId != -1) {
            Toast.makeText(this, "This phone number is already registered", Toast.LENGTH_SHORT).show();
            etPhoneNumber.setText("");
            etPhoneNumber.requestFocus();
            return;
        }

        // Register patient in database
        long result = databaseHelper.registerPatient(patientName, phoneNumber);

        if (result != -1) {
            // Registration successful
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Get the registered patient ID
            int patientId = databaseHelper.getPatientIdByPhone(phoneNumber);

            // Save patient details in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("patient_id", patientId);
            editor.putString("patient_name", patientName);
            editor.putString("patient_phone", phoneNumber);
            editor.apply();

            // Navigate to MainActivity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}