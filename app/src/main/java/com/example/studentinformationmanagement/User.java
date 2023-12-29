package com.example.studentinformationmanagement;

import java.util.ArrayList;

public class User {
    private String Email;
    private String Name;
    private int Age;
    private String PhoneNumber;
    private boolean Status;
    private String Role;
    private String uid;

    // Constructor
    public User(String Email, String Name, int Age, String PhoneNumber, boolean Status, String Role) {
        this.Email = Email;
        this.Name = Name;
        this.Age = Age;
        this.PhoneNumber = PhoneNumber;
        this.Status = Status; // on/off
        this.Role = Role; // admin/manAger/employee
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "Email='" + Email + '\'' +
                ", Name='" + Name + '\'' +
                ", Age=" + Age +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Status=" + Status +
                ", Role='" + Role + '\'' +
                '}';
    }
}
