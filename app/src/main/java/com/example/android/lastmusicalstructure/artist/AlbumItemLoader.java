package com.example.android.lastmusicalstructure.artist;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.utils.QueryUtils;

import java.io.IOException;
import java.util.List;

public class AlbumItemLoader extends AsyncTaskLoader<List<Album>> {

    private static final String LOG_TAG = AlbumItemLoader.class.getSimpleName();

    private String artistName;

    public AlbumItemLoader(Context context, String artistName) {
        super(context);
        this.artistName = artistName;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Album> loadInBackground() {

        List<Album> folderItems = null;

        try {
            folderItems = QueryUtils.getArtistAlbums(artistName);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting the albums", e);
        }
        return folderItems;
    }
}
