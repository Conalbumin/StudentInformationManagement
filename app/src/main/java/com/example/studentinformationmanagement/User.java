package com.example.studentinformationmanagement;

import java.util.ArrayList;

public class User {
    private String email;
    private String name;
    private int age;
    private String phoneNumber;
    private boolean status;
    private String isAdmin;

    // Constructor
    public User(String email, String name, int age, String phoneNumber, boolean status, String isAdmin) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.status = status;      // on/off
        this.isAdmin = isAdmin;    // admin/manager/employee
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String isAdmin() {
        return isAdmin;
    }
    public void setAdmin(String admin) {
        isAdmin = admin;
    }


    // Override toString method to display user details
    @Override
    public String toString() {
        return  "Email: " + email +
                "\nName: " + name +
                "\nAge: " + age +
                "\nPhone Number: " + phoneNumber +
                "\nStatus: " + status +
                "\nRole: " + isAdmin;
    }
}
