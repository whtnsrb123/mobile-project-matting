package com.example.Matting;

import com.google.firebase.Timestamp;



public class Post {
    private String username;
    private String postContent;
    private String imageResource; // Base64 이미지 또는 URI
    private Timestamp timestamp; // 시간 (문자열)
    private int commentCount;
    private int reactionCount;
    private boolean reacted;

    public Post(String username, String postContent, String imageResource, Timestamp timestamp, int commentCount, int reactionCount, boolean reacted) {
        this.username = username;
        this.postContent = postContent;
        this.imageResource = imageResource;
        this.timestamp = timestamp;
        this.commentCount = commentCount;
        this.reactionCount = reactionCount;
        this.reacted = reacted;
    }

    public String getUsername() {
        return username;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getImageResource() {
        return imageResource;
    }

    public Timestamp  getTimestamp() {
        return timestamp;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getReactionCount() {
        return reactionCount;
    }

    public boolean isReacted() {
        return reacted;
    }

    public String getFormattedTimestamp() {
        return timestamp != null
                ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp.toDate())
                : "Unknown Time";
    }
}
