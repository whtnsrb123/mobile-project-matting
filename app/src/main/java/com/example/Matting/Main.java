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
}
