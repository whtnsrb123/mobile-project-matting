package com.example.Matting;

public class Follower {
    private String userId;
    private String username;
    private String subText;
    private String profileImage;

    public Follower(String userId, String username, String subText, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.subText = subText;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
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
