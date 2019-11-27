package com.edufree.bookshoot.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Stats implements Parcelable {
    private String num_reading;
    private String num_likes;
    private String num_dislikes;
    private String id;

    public Stats(){}

    public Stats(String num_reading, String num_likes, String num_dislikes) {
        this.num_reading = num_reading;
        this.num_likes = num_likes;
        this.num_dislikes = num_dislikes;
    }

    protected Stats(Parcel in) {
        num_reading = in.readString();
        num_likes = in.readString();
        num_dislikes = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(num_reading);
        dest.writeString(num_likes);
        dest.writeString(num_dislikes);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Stats> CREATOR = new Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum_reading() {
        return num_reading;
    }

    public void setNum_reading(String num_reading) {
        this.num_reading = num_reading;
    }

    public String getNum_likes() {
        return num_likes;
    }

    public void setNum_likes(String num_likes) {
        this.num_likes = num_likes;
    }

    public String getNum_dislikes() {
        return num_dislikes;
    }

    public void setNum_dislikes(String num_dislikes) {
        this.num_dislikes = num_dislikes;
    }

}
