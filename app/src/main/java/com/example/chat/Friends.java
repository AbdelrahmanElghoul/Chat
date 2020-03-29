package com.example.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class Friends extends User{
    private String Key;
    private String sessionID;
    private boolean chat_update;
    private String friendState;
    private boolean new_message;

    public Friends() {
    }

    public boolean isNew_message() {
        return new_message;
    }

    public void setNew_message(boolean new_message) {
        this.new_message = new_message;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public boolean isChat_update() {
        return chat_update;
    }

    public void setChat_update(boolean chat_update) {
        this.chat_update = chat_update;
    }

    public String getFriendState() {
        return friendState;
    }

    public void setFriendState(String friendState) {
        this.friendState = friendState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Key);
        dest.writeString(this.sessionID);
        dest.writeByte(this.chat_update ? (byte) 1 : (byte) 0);
        dest.writeString(this.friendState);
        dest.writeByte(this.new_message ? (byte) 1 : (byte) 0);
    }

    protected Friends(Parcel in) {
        this.Key = in.readString();
        this.sessionID = in.readString();
        this.chat_update = in.readByte() != 0;
        this.friendState = in.readString();
        this.new_message = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Friends> CREATOR = new Parcelable.Creator<Friends>() {
        @Override
        public Friends createFromParcel(Parcel source) {
            return new Friends(source);
        }

        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };
}
