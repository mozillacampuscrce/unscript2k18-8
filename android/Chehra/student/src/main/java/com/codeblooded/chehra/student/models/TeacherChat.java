package com.codeblooded.chehra.student.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class TeacherChat implements Parcelable {
    public static final Creator<TeacherChat> CREATOR = new Creator<TeacherChat>() {
        @Override
        public TeacherChat createFromParcel(Parcel source) {
            return new TeacherChat(source);
        }

        @Override
        public TeacherChat[] newArray(int size) {
            return new TeacherChat[size];
        }
    };
    private String studentID;
    private String name;

    public TeacherChat() {

    }

    public TeacherChat(String studentID, String name) {

        this.studentID = studentID;
        this.name = name;
    }

    protected TeacherChat(Parcel in) {
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
