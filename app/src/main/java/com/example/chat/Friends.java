package com.example.chat;

public class Friends {
    private String ID;
    private String name;
    private String profile;
    private String email;
    private String chat_ID;
    private boolean chat_update;
    private String friendState;

    public Friends() {
    }

    public void UpdateFriend(Friends friends) {
        this.ID = friends.getID();
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
}
