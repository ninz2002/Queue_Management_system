package com.example.queue_me;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "QueueFlowDB";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_DOCTORS = "doctors";
    private static final String TABLE_QUEUE = "queue";
    private static final String TABLE_PATIENTS = "patients";

    // Doctors Table Columns
    private static final String COL_DOCTOR_ID = "doctor_id";
    private static final String COL_DOCTOR_NAME = "doctor_name";
    private static final String COL_DOCTOR_DEPARTMENT = "department";

    // Patients Table Columns ⭐ NEW
    private static final String COL_PATIENT_ID = "patient_id";
    private static final String COL_PATIENT_NAME = "patient_name";
    private static final String COL_PATIENT_PHONE = "patient_phone";
    private static final String COL_CREATED_AT = "created_at";

    // Queue Table Columns
    private static final String COL_QUEUE_ID = "queue_id";
    private static final String COL_QUEUE_DOCTOR_ID = "doctor_id";
    private static final String COL_QUEUE_PATIENT_ID = "patient_id";
    private static final String COL_TOKEN_NUMBER = "token_number";
    private static final String COL_STATUS = "status";
    private static final String COL_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Doctors Table
        String createDoctorsTable = "CREATE TABLE " + TABLE_DOCTORS + " (" +
                COL_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DOCTOR_NAME + " TEXT NOT NULL, " +
                COL_DOCTOR_DEPARTMENT + " TEXT NOT NULL)";
        db.execSQL(createDoctorsTable);

        // Create Patients Table ⭐ NEW
        String createPatientsTable = "CREATE TABLE " + TABLE_PATIENTS + " (" +
                COL_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PATIENT_NAME + " TEXT NOT NULL, " +
                COL_PATIENT_PHONE + " TEXT NOT NULL UNIQUE, " +
                COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createPatientsTable);

        // Create Queue Table (MODIFIED - uses patient_id)
        String createQueueTable = "CREATE TABLE " + TABLE_QUEUE + " (" +
                COL_QUEUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_QUEUE_DOCTOR_ID + " INTEGER, " +
                COL_QUEUE_PATIENT_ID + " INTEGER, " +
                COL_TOKEN_NUMBER + " INTEGER, " +
                COL_STATUS + " TEXT DEFAULT 'waiting', " +
                COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COL_QUEUE_DOCTOR_ID + ") REFERENCES " +
                TABLE_DOCTORS + "(" + COL_DOCTOR_ID + "), " +
                "FOREIGN KEY(" + COL_QUEUE_PATIENT_ID + ") REFERENCES " +
                TABLE_PATIENTS + "(" + COL_PATIENT_ID + "))";
        db.execSQL(createQueueTable);

        // Insert sample doctors
        insertSampleDoctors(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTORS);
        onCreate(db);
    }

    // Insert sample doctors for testing
    private void insertSampleDoctors(SQLiteDatabase db) {
        String[] doctors = {
                "Dr. Rajesh Kumar, Cardiology",
                "Dr. Priya Sharma, Neurology",
                "Dr. Amit Patel, Orthopedics",
                "Dr. Sneha Reddy, Pediatrics",
                "Dr. Vikram Singh, General Medicine",
                "Dr. Meera Nair, Dermatology",
                "Dr. Arjun Desai, ENT",
                "Dr. Kavya Iyer, Gynecology"
        };

        for (String doctor : doctors) {
            String[] parts = doctor.split(", ");
            ContentValues values = new ContentValues();
            values.put(COL_DOCTOR_NAME, parts[0]);
            values.put(COL_DOCTOR_DEPARTMENT, parts[1]);
            db.insert(TABLE_DOCTORS, null, values);
        }
    }

    // ===== PATIENT OPERATIONS ⭐ NEW =====

    // Register a new patient
    public long registerPatient(String patientName, String patientPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATIENT_NAME, patientName);
        values.put(COL_PATIENT_PHONE, patientPhone);

        long result = db.insert(TABLE_PATIENTS, null, values);
        db.close();
        return result;
    }

    // Get patient ID by phone number (for login)
    public int getPatientIdByPhone(String patientPhone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PATIENT_ID + " FROM " + TABLE_PATIENTS +
                " WHERE " + COL_PATIENT_PHONE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{patientPhone});
        int patientId = -1;

        if (cursor.moveToFirst()) {
            patientId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return patientId;
    }

    // Get patient details by ID
    public Patient getPatientById(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PATIENT_ID + ", " + COL_PATIENT_NAME +
                ", " + COL_PATIENT_PHONE + " FROM " + TABLE_PATIENTS +
                " WHERE " + COL_PATIENT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(patientId)});
        Patient patient = null;

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            patient = new Patient(id, name, phone);
        }

        cursor.close();
        db.close();
        return patient;
    }

    // ===== DOCTOR OPERATIONS =====

    // Get all doctors with queue count
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctorList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d." + COL_DOCTOR_ID + ", d." + COL_DOCTOR_NAME +
                ", d." + COL_DOCTOR_DEPARTMENT +
                ", COUNT(q." + COL_QUEUE_ID + ") as queue_count " +
                "FROM " + TABLE_DOCTORS + " d " +
                "LEFT JOIN " + TABLE_QUEUE + " q ON d." + COL_DOCTOR_ID +
                " = q." + COL_QUEUE_DOCTOR_ID +
                " AND q." + COL_STATUS + " = 'waiting' " +
                "GROUP BY d." + COL_DOCTOR_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String department = cursor.getString(2);
                int queueCount = cursor.getInt(3);

                doctorList.add(new Doctor(id, name, department, queueCount));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return doctorList;
    }

    // Get doctor by ID
    public Doctor getDoctorById(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Doctor doctor = null;

        String query = "SELECT d." + COL_DOCTOR_ID + ", d." + COL_DOCTOR_NAME +
                ", d." + COL_DOCTOR_DEPARTMENT +
                ", COUNT(q." + COL_QUEUE_ID + ") as queue_count " +
                "FROM " + TABLE_DOCTORS + " d " +
                "LEFT JOIN " + TABLE_QUEUE + " q ON d." + COL_DOCTOR_ID +
                " = q." + COL_QUEUE_DOCTOR_ID +
                " AND q." + COL_STATUS + " = 'waiting' " +
                "WHERE d." + COL_DOCTOR_ID + " = ? " +
                "GROUP BY d." + COL_DOCTOR_ID;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String department = cursor.getString(2);
            int queueCount = cursor.getInt(3);
            doctor = new Doctor(id, name, department, queueCount);
        }

        cursor.close();
        db.close();
        return doctor;
    }

    // ===== QUEUE OPERATIONS =====

    // Add patient to queue (MODIFIED - uses patient_id)
    public long addToQueue(int doctorId, int patientId) { // ⭐ CHANGED PARAMETERS
        SQLiteDatabase db = this.getWritableDatabase();

        // Get next token number for this doctor
        int tokenNumber = getNextTokenNumber(doctorId);

        ContentValues values = new ContentValues();
        values.put(COL_QUEUE_DOCTOR_ID, doctorId);
        values.put(COL_QUEUE_PATIENT_ID, patientId); // ⭐ CHANGED
        values.put(COL_TOKEN_NUMBER, tokenNumber);
        values.put(COL_STATUS, "waiting");

        long result = db.insert(TABLE_QUEUE, null, values);
        db.close();
        return result;
    }

    // Get next token number for a doctor
    private int getNextTokenNumber(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COL_TOKEN_NUMBER + ") FROM " + TABLE_QUEUE +
                " WHERE " + COL_QUEUE_DOCTOR_ID + " = ? " +
                " AND DATE(" + COL_TIMESTAMP + ") = DATE('now')";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});
        int nextToken = 1;

        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            nextToken = cursor.getInt(0) + 1;
        }

        cursor.close();
        return nextToken;
    }

    // Get queue count for a doctor
    public int getQueueCount(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_QUEUE +
                " WHERE " + COL_QUEUE_DOCTOR_ID + " = ? AND " +
                COL_STATUS + " = 'waiting'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }
}