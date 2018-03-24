package com.codeblooded.chehra.teacher.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class StudentChat implements Parcelable {
    public static final Creator<StudentChat> CREATOR = new Creator<StudentChat>() {
        @Override
        public StudentChat createFromParcel(Parcel source) {
            return new StudentChat(source);
        }

        @Override
        public StudentChat[] newArray(int size) {
            return new StudentChat[size];
        }
    };
    private String studentID;
    private String name;

    public StudentChat() {

    }

    public StudentChat(String studentID, String name) {

        this.studentID = studentID;
        this.name = name;
    }

    protected StudentChat(Parcel in) {
        this.studentID = in.readString();
        this.name = in.readString();
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.studentID);
        dest.writeString(this.name);
    }
}
