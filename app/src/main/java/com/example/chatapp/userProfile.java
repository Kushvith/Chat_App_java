package com.example.chatapp;

public class userProfile {
    public  String username,userUid;

    public userProfile(String username,String userUid) {
        this.username = username;
        this.userUid = userUid;
    }
public userProfile(){}
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
