package com.example.android.lastmusicalstructure.folder;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;

public interface FolderItem extends Parcelable {
    long getId();

    String getName();

    String getImageURL();

    BitmapDrawable getImage();
}
