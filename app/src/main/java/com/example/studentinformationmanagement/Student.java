package com.example.studentinformationmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {
    private String id;
    private String name;
    private String gender;
    private Date birth;
    private ArrayList certificates;

    public Student(String id, String name, String gender, Date birth, ArrayList certificates) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.certificates = certificates;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public Date getBirth() {
        return birth;
    }
    public void setBirth(Date birth) {
        this.birth = birth;
    }
    public ArrayList getCertificates() {
        return certificates;
    }
    public void setCertificates(ArrayList certificates) {
        this.certificates = certificates;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birth=" + birth +
                ", certificates=" + certificates +
                '}';
    }


}
