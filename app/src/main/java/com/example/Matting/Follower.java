package com.example.Matting;

public class Follower {
    private String username;
    private String subText;
    private int profileImageRes;

    public Follower(String username, String subText, int profileImageRes) {
        this.username = username;
        this.subText = subText;
        this.profileImageRes = profileImageRes;
    }

    public String getUsername() {
        return username;
    }

    public String getSubText() {
        return subText;
    }

    public int getProfileImageRes() {
        return profileImageRes;
    }
}
