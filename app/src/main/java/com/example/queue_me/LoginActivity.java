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

public class LoginActivity extends AppCompatActivity {

    private LinearLayout layoutLoginForm;
    private LinearLayout layoutAlreadyLoggedIn;
    private EditText etPhoneNumber;
    private LinearLayout btnLogin;
    private TextView txtRegisterHere;
    private TextView txtLoggedInPatientName;
    private LinearLayout btnContinueAsLoggedIn;
    private LinearLayout btnSwitchUser;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        layoutLoginForm = findViewById(R.id.layoutLoginForm);
        layoutAlreadyLoggedIn = findViewById(R.id.layoutAlreadyLoggedIn);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegisterHere = findViewById(R.id.txtRegisterHere);
        txtLoggedInPatientName = findViewById(R.id.txtLoggedInPatientName);
        btnContinueAsLoggedIn = findViewById(R.id.btnContinueAsLoggedIn);
        btnSwitchUser = findViewById(R.id.btnSwitchUser);

        // Check if user is already logged in
        checkLoginStatus();

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPatient();
            }
        });

        // Register link click listener
        txtRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Continue as logged in button
        btnContinueAsLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainActivity();
            }
        });

        // Switch user button
        btnSwitchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUser();
            }
        });
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
        int patientId = prefs.getInt("patient_id", -1);
        String patientName = prefs.getString("patient_name", "");

        if (patientId != -1 && !patientName.isEmpty()) {
            // User is already logged in
            showLoggedInLayout(patientName);
        } else {
            // Show login form
            showLoginForm();
        }
    }

    private void showLoginForm() {
        layoutLoginForm.setVisibility(View.VISIBLE);
        layoutAlreadyLoggedIn.setVisibility(View.GONE);
    }

    private void showLoggedInLayout(String patientName) {
        layoutLoginForm.setVisibility(View.GONE);
        layoutAlreadyLoggedIn.setVisibility(View.VISIBLE);
        txtLoggedInPatientName.setText("Welcome back, " + patientName + "!");
    }

    private void loginPatient() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate phone number
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

        // Get patient ID from database
        int patientId = databaseHelper.getPatientIdByPhone(phoneNumber);

        if (patientId != -1) {
            // Patient found, login successful
            Patient patient = databaseHelper.getPatientById(patientId);

            if (patient != null) {
                // Save patient details in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("patient_id", patient.getId());
                editor.putString("patient_name", patient.getName());
                editor.putString("patient_phone", patient.getPhone());
                editor.apply();

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        } else {
            // Patient not found
            Toast.makeText(this, "Phone number not registered. Please register first.", Toast.LENGTH_SHORT).show();
            etPhoneNumber.setText("");
            etPhoneNumber.requestFocus();
        }
    }

    private void switchUser() {
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Show login form
        showLoginForm();
        etPhoneNumber.setText("");
        etPhoneNumber.requestFocus();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}