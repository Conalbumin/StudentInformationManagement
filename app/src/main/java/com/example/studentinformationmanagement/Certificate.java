package com.example.studentinformationmanagement;

public class Certificate {
    private String name;
    public Certificate() {
    }

    // Parameterized constructor
    public Certificate(String name) {
        this.name = name;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "name='" + name + '\'' +
                '}';
    }
}