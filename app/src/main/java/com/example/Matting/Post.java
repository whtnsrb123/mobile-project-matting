package com.example.Matting;

// Post.java
public class Post {
    //    private String description;
    private int imageResId;

    public Post(String description, int imageResId) {
//        this.description = description;
        this.imageResId = imageResId;
    }

/*    public String getDescription() {
        return description;
    }*/

    public int getImageResId() {
        return imageResId;
    }
}