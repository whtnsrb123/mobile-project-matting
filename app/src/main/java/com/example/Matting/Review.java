package com.example.Matting;

import com.google.firebase.Timestamp;

public class Review {
    private String content;
    private String username;
    private double rating;
    private Timestamp date; // Firestore의 Timestamp 형식 사용

    public Review() {} // Firestore에서 객체화할 때 필요

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public double getRating() {
        return rating;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
