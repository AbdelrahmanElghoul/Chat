package com.example.chat;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String profile;
    private List<Friends> friends;

    public User(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profile;
    }

    public void setProfilePic(String profilePic) {
        this.profile = profilePic;
    }

    public List<Friends> getFriends() {

        return friends==null ? new ArrayList<>():friends;
    }

    public void setFriends(List<Friends> friends) {
        this.friends = friends;
    }



}

