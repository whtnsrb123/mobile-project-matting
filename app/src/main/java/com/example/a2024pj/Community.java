package com.example.a2024pj;

public class Community {
    private String title;
    private String subtitle;
    private String info;

    public Community(String title, String subtitle, String info) {
        this.title = title;
        this.subtitle = subtitle;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getInfo() {
        return info;
    }
}
