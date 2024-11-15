package com.example.Matting;

public class FeedItem {
    private String username;
    private String postContent;
    private int imageResource;
    private int commentCount;
    private int reactionCount;
    private String timestamp;
    private boolean isReacted; // 감정 상태 추가

    public FeedItem(String username, String postContent, int imageResource, int commentCount, int reactionCount, String timestamp) {
        this.username = username;
        this.postContent = postContent;
        this.imageResource = imageResource;
        this.commentCount = commentCount;
        this.reactionCount = reactionCount;
        this.timestamp = timestamp;
        this.isReacted = false; // 초기 상태: 반응 없음
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

    public String getTimestamp() {
        return timestamp;
    }

    public boolean toggleReaction() {
        isReacted = !isReacted; // 상태 토글
        return isReacted;
    }

    public boolean isReacted() {
        return isReacted;
    }
}
