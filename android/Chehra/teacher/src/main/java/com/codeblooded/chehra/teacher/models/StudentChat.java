package com.codeblooded.chehra.teacher.models;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class StudentChat{

    private String studentID;
    private String firebaseID;

    private String name;

    public StudentChat() {
    }

    public StudentChat(String studentID, String firebaseID, String name) {
        this.studentID = studentID;
        this.firebaseID = firebaseID;
        this.name = name;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
