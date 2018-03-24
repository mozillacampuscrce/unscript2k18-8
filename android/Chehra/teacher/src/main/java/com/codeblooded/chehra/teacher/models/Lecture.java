package com.codeblooded.chehra.teacher.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Lecture implements Parcelable {

    public static final Parcelable.Creator<Lecture> CREATOR = new Parcelable.Creator<Lecture>() {
        @Override
        public Lecture createFromParcel(Parcel source) {
            return new Lecture(source);
        }

        @Override
        public Lecture[] newArray(int size) {
            return new Lecture[size];
        }
    };
    private String comment, start_time, end_time;
    private int lect_no, lect_id, course_id;
    private boolean isAttendanceTaken = false;

    public Lecture() {
    }

    public Lecture(String start_time, String end_time, int lect_id, int course_id, String comment, int lect_no) {
        this.comment = comment;
        this.start_time = start_time;
        this.end_time = end_time;
        this.lect_no = lect_no;
        this.lect_id = lect_id;
        this.course_id = course_id;
    }

    protected Lecture(Parcel in) {
        this.comment = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.lect_no = in.readInt();
        this.lect_id = in.readInt();
        this.course_id = in.readInt();
        this.isAttendanceTaken = in.readByte() != 0;
    }

    public boolean isAttendanceTaken() {
        return isAttendanceTaken;
    }

    public void setAttendanceTaken(boolean attendanceTaken) {
        isAttendanceTaken = attendanceTaken;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getLect_no() {
        return lect_no;
    }

    public void setLect_no(int lect_no) {
        this.lect_no = lect_no;
    }

    public int getLect_id() {
        return lect_id;
    }

    public void setLect_id(int lect_id) {
        this.lect_id = lect_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.comment);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeInt(this.lect_no);
        dest.writeInt(this.lect_id);
        dest.writeInt(this.course_id);
        dest.writeByte(this.isAttendanceTaken ? (byte) 1 : (byte) 0);
    }
}
