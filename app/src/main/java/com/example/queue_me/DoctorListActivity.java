package com.example.queue_me;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDoctors;
    private EditText searchDoctor;
    private TextView txtEmptyState;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        recyclerViewDoctors = findViewById(R.id.recyclerViewDoctors);
        searchDoctor = findViewById(R.id.searchDoctor);
        txtEmptyState = findViewById(R.id.txtEmptyState);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));

        // Load doctors from database
        loadDoctors();

        // Setup search functionality
        searchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doctorAdapter != null) {
                    doctorAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDoctors() {
        // Get doctors from database
        doctorList = databaseHelper.getAllDoctors();

        if (doctorList.isEmpty()) {
            // Show empty state
            txtEmptyState.setVisibility(View.VISIBLE);
            recyclerViewDoctors.setVisibility(View.GONE);
        } else {
            // Show doctor list
            txtEmptyState.setVisibility(View.GONE);
            recyclerViewDoctors.setVisibility(View.VISIBLE);

            // Setup adapter
            doctorAdapter = new DoctorAdapter(this, doctorList);
            recyclerViewDoctors.setAdapter(doctorAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload doctors when returning to this activity
        loadDoctors();
    }
}