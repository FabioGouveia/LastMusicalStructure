package com.example.android.lastmusicalstructure.model;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;

import com.example.android.lastmusicalstructure.folder.FolderItem;

import java.util.List;

public class Artist implements FolderItem {

    public static final String KEY = "artist_key";
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    private long id;
    private String name;
    private String info;
    private String imageURL;
    private BitmapDrawable image;
    private List<Album> albums;

    public Artist(String name, String info, String imageURL, BitmapDrawable image) {
        this(0, name, info, image);
        this.imageURL = imageURL;
    }

    public Artist(long id, String name, String info, BitmapDrawable image) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.image = image;
    }

    protected Artist(Parcel in) {
        id = in.readLong();
        name = in.readString();
        info = in.readString();
        imageURL = in.readString();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String getImageURL() {
        return imageURL;
    }

    @Override
    public BitmapDrawable getImage() {
        return image;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(info);
        dest.writeString(imageURL);
    }
}
