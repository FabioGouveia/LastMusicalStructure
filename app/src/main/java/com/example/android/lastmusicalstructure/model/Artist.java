package com.example.android.lastmusicalstructure.model;

import android.graphics.Bitmap;
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
    private BitmapDrawable image;
    private List<Album> albums;

    public Artist(String name, String info, BitmapDrawable image) {
        this.name = name;
        this.info = info;
        this.image = image;
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
        image = new BitmapDrawable((Bitmap) in.readParcelable(Bitmap.class.getClassLoader()));
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
        dest.writeParcelable(image.getBitmap(), 0);
    }
}