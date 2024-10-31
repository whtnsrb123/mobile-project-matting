package com.example.Matting;

public class Chat_Message {
    private String userId;
    private String message;
    private long timestamp;

    public Chat_Message() {
    }

    public Chat_Message(String userId, String message, long timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
