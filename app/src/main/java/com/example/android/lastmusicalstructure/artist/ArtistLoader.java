package com.example.android.lastmusicalstructure.artist;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.utils.QueryUtils;

import java.io.IOException;
import java.util.List;

public class ArtistLoader extends AsyncTaskLoader<List<Artist>> {

    private static final String LOG_TAG = ArtistLoader.class.getSimpleName();

    private int pageNumber;

    public ArtistLoader(Context context, int pageNumber) {
        super(context);
        this.pageNumber = pageNumber;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Artist> loadInBackground() {

        List<Artist> artists = null;
        try {
            artists = QueryUtils.getOnlineArtists(pageNumber);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting the artists", e);
        }

        return artists;
    }
}
