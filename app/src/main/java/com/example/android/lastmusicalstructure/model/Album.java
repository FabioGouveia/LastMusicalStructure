package com.example.android.lastmusicalstructure.model;


import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;

import com.example.android.lastmusicalstructure.folder.FolderItem;

import java.util.List;


public class Album implements FolderItem {

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private long id;
    private String name;
    private String imageURL;
    private BitmapDrawable image;
    private List<Track> tracks;

    public Album(String name, List<Track> tracks, String imageURL, BitmapDrawable image) {
        this(0, name, tracks, image);
        this.imageURL = imageURL;
    }

    public Album(long id, String name, List<Track> tracks, BitmapDrawable image) {
        this.id = id;
        this.name = name;
        this.tracks = tracks;
        this.image = image;
    }

    private Album(Parcel in) {
        name = in.readString();
        in.readList(tracks, Track.class.getClassLoader());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageURL() {
        return imageURL;
    }

    @Override
    public BitmapDrawable getImage() {
        return image;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(tracks);
    }
}
