package com.example.android.lastmusicalstructure.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Folder implements Parcelable {

    public static final String KEY = "folder_key";
    public static final String DISPLAY_MODE_KEY = "display_mode_key";

    public static final int FOLDER_DISPLAY_MODE = 2201;
    public static final int FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED = 2202;
    public static final int ARTIST_OFF_LINE_DISPLAY_MODE = 2203;
    public static final int ARTIST_ON_LINE_DISPLAY_MODE = 2204;
    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
    private long id;
    private String name;
    private int icon;
    private List<Artist> artists;

    public Folder(long id, String name, int icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    protected Folder(Parcel in) {
        id = in.readLong();
        name = in.readString();
        icon = in.readInt();
        in.readList(artists, Artist.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public int getNumberOfArtists() {
        return artists.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(icon);
        dest.writeList(artists);
    }
}
