package com.example.Matting;

import com.google.firebase.Timestamp;

public class Main_Review {
    private String address;
    private String content;
    private String username;
    private double rating;
    private Timestamp date; // Firestore의 Timestamp 형식 사용

    public Main_Review(String address, String content, Timestamp date, float rating, String username) {
        this.address = address;
        this.content = content;
        this.date = date;
        this.rating = rating;
        this.username = username;
    }

    // 기본 생성자 (Firestore에서 객체 생성 시 필요)
    public Main_Review() {}

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
}
