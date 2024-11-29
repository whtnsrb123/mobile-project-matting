package com.example.Matting;

public class Follower {
    private String username;
    private String subText;
    private String profileImage; // URL로 변경

    public Follower(String username, String subText, String profileImage) {
        this.username = username;
        this.subText = subText;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getSubText() {
        return subText;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
