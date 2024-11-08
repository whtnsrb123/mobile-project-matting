package com.example.Matting;
public class Follower {
    private String username;
    private String profileImageUrl;

    public Follower(String username, String profileImageUrl) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
