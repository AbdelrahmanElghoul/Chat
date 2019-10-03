package com.example.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class Friends implements Parcelable {
    private String Key;
    private String name;
    private String profile;
    private String email;
    private String chat_ID;
    private boolean chat_update;
    private String friendState;

    public Friends() {
    }

    public void UpdateFriend(Friends friends) {
        this.Key = friends.getKey();
        this.name = friends.getName();
        this.profile = friends.getProfile();
        this.email = friends.getEmail();
        this.chat_ID = friends.getChat_ID();
        this.chat_update = friends.isChat_update();
        this.friendState = friends.getFriendState();
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChat_ID() {
        return chat_ID;
    }

    public void setChat_ID(String chat_ID) {
        this.chat_ID = chat_ID;
    }

    public boolean isChat_update() {
        return chat_update;
    }

    public void setChat_update(boolean chat_update) {
        this.chat_update = chat_update;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        dest.writeString(this.name);
        dest.writeString(this.profile);
        dest.writeString(this.email);
        dest.writeString(this.chat_ID);
        dest.writeByte(this.chat_update ? (byte) 1 : (byte) 0);
        dest.writeString(this.friendState);
    }

    protected Friends(Parcel in) {
        this.Key = in.readString();
        this.name = in.readString();
        this.profile = in.readString();
        this.email = in.readString();
        this.chat_ID = in.readString();
        this.chat_update = in.readByte() != 0;
        this.friendState = in.readString();
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
