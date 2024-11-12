package com.example.Matting;

import android.os.Parcel;
import android.os.Parcelable;

public class Community implements Parcelable {
    private String title;
    private String content;
    private String info;

    public Community(String title, String content, String info) {
        this.title = title;
        this.content = content;
        this.info = info;
    }

    protected Community(Parcel in) {
        title = in.readString();
        content = in.readString();
        info = in.readString();
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

    public String getInfo() {
        return info;
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
