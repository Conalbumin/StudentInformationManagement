package com.example.studentinformationmanagement;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private ArrayList<String> Certificates;
    private String Gender;
    private String ID;
    private String Birth;
    private String Name;

    // Constructors, getters, setters

    // Example constructor
    public Student(String ID, String Name, String Birth, String Gender, ArrayList<String> Certificates) {
        this.ID = ID;
        this.Name = Name;
        this.Birth = Birth;
        this.Gender = Gender;
        this.Certificates = Certificates;
    }

    // Parameterized constructor
    public Student(ArrayList<String> certificates) {
        this.Certificates = certificates;
    }

    public Student() {
    }

    // Getters and setters for each field

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getBirth() {
        return Birth;
    }

    public void setBirth(String Birth) {
        this.Birth = Birth;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public ArrayList<String> getCertificates() {
        return Certificates;
    }

    public void setCertificates(ArrayList<String> Certificates) {
        this.Certificates = Certificates;
    }

    @Override
    public String toString() {
        return "Student{" +
                "Certificates=" + Certificates +
                ", Gender='" + Gender + '\'' +
                ", ID='" + ID + '\'' +
                ", Birth='" + Birth + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }
}
