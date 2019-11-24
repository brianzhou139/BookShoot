package com.edufree.bookshoot.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String email,username,thumbnail,content,rating,type,id;

    public Comment(){};

    public Comment(String email, String username, String thumbnail, String content, String rating, String type) {
        this.email = email;
        this.username = username;
        this.thumbnail = thumbnail;
        this.content = content;
        this.rating = rating;
        this.type = type;
    }

    protected Comment(Parcel in) {
        email = in.readString();
        username = in.readString();
        thumbnail = in.readString();
        content = in.readString();
        rating = in.readString();
        type = in.readString();
        id = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeString(thumbnail);
        parcel.writeString(content);
        parcel.writeString(rating);
        parcel.writeString(type);
        parcel.writeString(id);
    }
}
