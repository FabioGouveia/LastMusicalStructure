package com.example.android.lastmusicalstructure.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
    private int id;
    private String name;
    private String duration;

    public Track(String name, String duration) {
        this.name = name;
        this.duration = duration;
    }

    private Track(Parcel in) {
        id = in.readInt();
        name = in.readString();
        duration = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(duration);
    }
}
