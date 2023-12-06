package com.example.studentinformationmanagement;

import java.util.ArrayList;

public class Certificate {
    private ArrayList<String> name;
    public Certificate() {
    }

    // Parameterized constructor
    public Certificate(ArrayList<String> name) {
        this.name = name;
    }

    // Getter and Setter
    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "name=" + name +
                '}';
    }
}