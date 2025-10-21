package com.example.queue_me;

public class Doctor {

    private int id;
    private String name;
    private String department;
    private int queueCount;

    // Constructor
    public Doctor(int id, String name, String department, int queueCount) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.queueCount = queueCount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getQueueCount() {
        return queueCount;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }
}
