package com.example.Matting;

public class Post {
    private String username;
    private String description;
    private int imageResId;  // 이미지 리소스 ID 필드
    private String timestamp;

    public Post() {}

    public Post(String username, String description, int imageResId, String timestamp) {
        this.username = username;
        this.description = description;
        this.imageResId = imageResId;
        this.timestamp = timestamp;
    }

    public String getUsername() { return username; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }  // 리소스 ID 반환
    public String getTimestamp() { return timestamp; }
}
