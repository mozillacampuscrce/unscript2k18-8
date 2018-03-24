package com.codeblooded.chehra.teacher.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

@IgnoreExtraProperties
public class Chat implements Parcelable {

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
    private String msg, timestamp, sender;

    public Chat(String msg, String timestamp, String sender) {
        this.msg = msg;
        this.timestamp = timestamp;
        this.sender = sender;
    }

    protected Chat(Parcel in) {
        this.msg = in.readString();
        this.timestamp = in.readString();
        this.sender = in.readString();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeString(this.timestamp);
        dest.writeString(this.sender);
    }
}
