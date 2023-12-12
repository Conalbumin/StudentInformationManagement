package com.example.studentinformationmanagement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student {
    private ArrayList<Certificate> Certificates;
    private String Gender;
    private String ID;
    private String Birth;
    private String Name;

    // Constructors, getters, setters

    // Example constructor
    public Student(String ID, String Name, String Birth, String Gender, ArrayList<Certificate> Certificates) {
        this.ID = ID;
        this.Name = Name;
        this.Birth = Birth;
        this.Gender = Gender;
        this.Certificates = Certificates;
    }

    // Parameterized constructor
    public Student(ArrayList<Certificate> certificates) {
        this.Certificates = certificates;
    }

    public Student(Map<String, Object> data) {
        this.Certificates = parseCertificates(data);
        this.Gender = data.get("Gender").toString();
        this.ID = data.get("ID").toString();
        this.Birth = data.get("Birth").toString();
        this.Name = data.get("Name").toString();
    }

    private ArrayList<Certificate> parseCertificates(Map<String, Object> data) {
        ArrayList<Certificate> certificates = new ArrayList<>();

        // Get the "Certificates" field
        Object certificatesData = data.get("Certificates");

        // Check if it's a list
        if (certificatesData instanceof ArrayList) {
            for (Object certData : (ArrayList<Object>) certificatesData) {
                if (certData instanceof Map) {
                    // Use the proper key ("name") to retrieve the certificate name
                    Object certName = ((Map<String, Object>) certData).get("name");
                    if (certName != null && certName instanceof String) {
                        certificates.add(new Certificate((String) certName));
                    }
                }
            }
        }
        return certificates;
    }

    public static Student createStudentWithCertificate(String ID, String Name, String Birth, String Gender, String certificateName) {
        Student student = new Student();
        student.setID(ID);
        student.setName(Name);
        student.setGender(Gender);
        student.setBirth(Birth);

        if (certificateName != null && !certificateName.isEmpty()) {
            ArrayList<Certificate> certificates = new ArrayList<>();
            certificates.add(new Certificate(certificateName));
            student.setCertificates(certificates);
        }

        return student;
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

    public ArrayList<Certificate> getCertificates() {
        return Certificates;
    }

    public void setCertificates(ArrayList<Certificate> Certificates) {
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
