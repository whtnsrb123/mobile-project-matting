package com.example.Matting;

public class Main {
    private String title;
    private String category;
    private String description;
    private double rating;

    public Main(String title, String category, String description, double rating) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public void setSubtitle(String subtitle) {
//        this.subtitle = subtitle;
//    }
//
//    public void setInfo(String info) {
//        this.info = info;
//    }
//
//    public void setRating(double rating) {
//        this.rating = rating;
//    }
}
