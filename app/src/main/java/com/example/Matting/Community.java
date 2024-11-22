package com.example.Matting;

import android.os.Parcel;
import android.os.Parcelable;

public class Community implements Parcelable {
    private String title, content, info, restaurant, date, time, mapx, mapy;

    public Community(String title, String content, String info, String restaurant, String date, String time, String mapx, String mapy) {
        this.title = title;
        this.content = content;
        this.info = info;
        this.restaurant = restaurant;
        this.date = date;
        this.time = time;
        this.mapx = mapx;
        this.mapy = mapy;
    }

    protected Community(Parcel in) {
        title = in.readString();
        content = in.readString();
        info = in.readString();
        restaurant = in.readString();
        date = in.readString();
        time = in.readString();
        mapx = in.readString();
        mapy = in.readString();
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

    public String getMapX() {
        return mapx;
    }

    public String getMapY() {
        return mapy;
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
