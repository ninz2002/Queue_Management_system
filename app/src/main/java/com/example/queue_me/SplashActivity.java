package com.example.queue_me;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Navigate after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginAndNavigate();
            }
        }, SPLASH_DURATION);
    }

    private void checkLoginAndNavigate() {
        SharedPreferences prefs = getSharedPreferences("QueueFlowPrefs", MODE_PRIVATE);
        int patientId = prefs.getInt("patient_id", -1);
        String patientName = prefs.getString("patient_name", "");

        Intent intent;
        if (patientId != -1 && !patientName.isEmpty()) {
            // User is logged in - go to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User is not logged in - go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}