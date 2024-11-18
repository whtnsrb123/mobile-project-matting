package com.example.Matting;

import java.util.Date;

public class Comment {
    private String username;
    private String content;
    private Date timestamp;

    public Comment() {} // Firestore에서 객체 변환을 위해 기본 생성자 필요

    public Comment(String username, String content, Date timestamp) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
