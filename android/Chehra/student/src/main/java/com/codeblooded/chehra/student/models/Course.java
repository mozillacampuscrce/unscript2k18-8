package com.codeblooded.chehra.student.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.codeblooded.chehra.student.db.AttendanceDbHelper;

public class Course implements Parcelable {
    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    private String course_id, dept_id, teacher_id, name, description, academic_yr, year, updated, created;

    public Course(String course_id, String dept_id, String teacher_id, String name, String description, String academic_yr, String year, String updated, String created) {
        this.course_id = course_id;
        this.dept_id = dept_id;
        this.teacher_id = teacher_id;
        this.name = name;
        this.description = description;
        this.academic_yr = academic_yr;
        this.year = year;
        this.updated = updated;
        this.created = created;
    }

    protected Course(Parcel in) {
        this.course_id = in.readString();
        this.dept_id = in.readString();
        this.teacher_id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.academic_yr = in.readString();
        this.year = in.readString();
        this.updated = in.readString();
        this.created = in.readString();
    }

    public String getInfoText(Context context) {
        StringBuilder info = new StringBuilder();
        switch (this.year) {
            case "1":
                info = info.append("1st ");
                break;
            case "2":
                info = info.append("2nd ");
                break;
            case "3":
                info = info.append("3rd ");
                break;
            case "4":
                info = info.append("4th ");
                break;
        }
        BiMap<Integer, String> map = new AttendanceDbHelper(context).getDepartments(false);
        info = info.append("year, ").append(map.get(Integer.valueOf(dept_id))).append(" department, ");
        info = (academic_yr.equals("2017")) ? info.append("2017-18") : info.append("2018-19");
        return info.toString();
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcademic_yr() {
        return academic_yr;
    }

    public void setAcademic_yr(String academic_yr) {
        this.academic_yr = academic_yr;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.course_id);
        dest.writeString(this.dept_id);
        dest.writeString(this.teacher_id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.academic_yr);
        dest.writeString(this.year);
        dest.writeString(this.updated);
        dest.writeString(this.created);
    }
}
