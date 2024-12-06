package com.example.Matting;

import java.util.Date;

public class FeedItem {
    private String documentId;
    private String username;
    private String nicknames;
    private String postContent;
    private String imageUrl;
    private int reactionCount;
    private int commentCount;
    private Date timestamp;
    private boolean reacted;
    private String profileImage;

    public FeedItem(String documentId, String username, String nicknames, String postContent,
                    String imageUrl, int reactionCount, int commentCount, Date timestamp, String profileImage) {
        this.documentId = documentId;
        this.username = username;
        this.nicknames = nicknames;
        this.postContent = postContent;
        this.imageUrl = imageUrl;
        this.reactionCount = reactionCount;
        this.commentCount = commentCount;
        this.timestamp = timestamp;
        this.profileImage = profileImage; // 초기화
    }

    // Getter와 Setter 추가
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNicknames() {
        return nicknames;
    }

    public void setNicknames(String nicknames) {
        this.nicknames = nicknames;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(int reactionCount) {
        this.reactionCount = reactionCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReacted() {
        return reacted;
    }

    public void setReacted(boolean reacted) {
        this.reacted = reacted;
    }
}
