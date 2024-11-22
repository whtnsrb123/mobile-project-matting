package com.example.Matting;

import android.os.Parcel;
import android.os.Parcelable;

public class Community implements Parcelable {
    private String title;
    private String content;
    private String info;
    private String restaurant;
    private String date;
    private String time;

    public Community(String title, String content, String info, String restaurant, String date, String time) {
        this.title = title;
        this.content = content;
        this.info = info;
        this.restaurant = restaurant;
        this.date = date;
        this.time = time;
    }

    protected Community(Parcel in) {
        title = in.readString();
        content = in.readString();
        info = in.readString();
        restaurant = in.readString();
        date = in.readString();
        time = in.readString();
    }

    public static final Creator<Community> CREATOR = new Creator<Community>() {
        @Override
        public Community createFromParcel(Parcel in) {
            return new Community(in);
        }

        @Override
        public Community[] newArray(int size) {
            return new Community[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getInfo() {
        return info;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(info);
    }
}
