package com.example.Matting;

import android.os.Parcel;
import android.os.Parcelable;

public class Community implements Parcelable {
    private String documentId, title, content, location, restaurant, date, time, mapx, mapy, userId, address;

    public Community(String documentId, String title, String content, String location, String restaurant, String date, String time, String mapx, String mapy, String userId, String address) {
        this.documentId = documentId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.restaurant = restaurant;
        this.date = date;
        this.time = time;
        this.mapx = mapx;
        this.mapy = mapy;
        this.userId = userId;
        this.address = address;
    }

    protected Community(Parcel in) {
        title = in.readString();
        content = in.readString();
        location = in.readString();
        restaurant = in.readString();
        date = in.readString();
        time = in.readString();
        mapx = in.readString();
        mapy = in.readString();
        userId = in.readString();
        address = in.readString();
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserid() {
        return userId;
    }

    public void setUserid(String userid) {
        this.userId = userid;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getLocation() {
        return location;
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

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(location);
        dest.writeString(address);
    }
}
