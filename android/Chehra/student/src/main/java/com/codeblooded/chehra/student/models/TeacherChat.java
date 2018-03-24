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
    private String teacherID;
    private String name;

    public TeacherChat() {

    }

    public TeacherChat(String teacherID, String name) {

        this.teacherID = teacherID;
        this.name = name;
    }

    protected TeacherChat(Parcel in) {
        this.teacherID = in.readString();
        this.name = in.readString();
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
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
        dest.writeString(this.teacherID);
        dest.writeString(this.name);
    }
}
