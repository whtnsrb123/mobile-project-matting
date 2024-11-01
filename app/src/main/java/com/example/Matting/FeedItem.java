package com.example.Matting;

public class FeedItem {
    private String username;
    private String postContent;
    private int imageResource;
    private int commentCount; // 댓글 수
    private int reactionCount; // 감정 수

    public FeedItem(String username, String postContent, int imageResource, int commentCount, int reactionCount) {
        this.username = username;
        this.postContent = postContent;
        this.imageResource = imageResource;
        this.commentCount = commentCount;
        this.reactionCount = reactionCount;
    }

    public String getUsername() {
        return username;
    }

    public String getPostContent() {
        return postContent;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getReactionCount() {
        return reactionCount;
    }
}
