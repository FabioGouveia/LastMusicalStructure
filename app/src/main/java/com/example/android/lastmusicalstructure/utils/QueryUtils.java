package com.example.android.lastmusicalstructure.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.lastmusicalstructure.data.UserContract.AlbumEntry;
import com.example.android.lastmusicalstructure.data.UserContract.ArtistEntry;
import com.example.android.lastmusicalstructure.data.UserContract.FolderEntry;
import com.example.android.lastmusicalstructure.data.UserContract.TrackEntry;
import com.example.android.lastmusicalstructure.folder.FolderItem;
import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.model.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Folder> getFolders(Context context) {

        List<Folder> folders = new ArrayList<>();

        String[] projection = {FolderEntry._ID, FolderEntry.COLUMN_FOLDER_ICON, FolderEntry.COLUMN_FOLDER_NAME};

        Cursor cursor = context.getContentResolver().query(FolderEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            try {
                int idColumnIndex = cursor.getColumnIndex(FolderEntry._ID);
                int iconColumnIndex = cursor.getColumnIndex(FolderEntry.COLUMN_FOLDER_ICON);
                int nameColumnIndex = cursor.getColumnIndex(FolderEntry.COLUMN_FOLDER_NAME);

                while (cursor.moveToNext()) {
                    folders.add(new Folder(cursor.getInt(idColumnIndex), cursor.getString(nameColumnIndex), cursor.getInt(iconColumnIndex)));
                }
            } finally {
                cursor.close();
            }

        } else {
            Log.e(LOG_TAG, "Folders cursor is null!");
        }
        return folders;
    }

    static boolean folderNameAlreadyExists(Context context, String folderName) {

        boolean folderNameExists = false;

        String[] projection = {FolderEntry.COLUMN_FOLDER_NAME};

        Cursor cursor = context.getContentResolver().query(FolderEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            try {
                int nameColumnIndex = cursor.getColumnIndex(FolderEntry.COLUMN_FOLDER_NAME);

                while (cursor.moveToNext()) {
                    if (folderName.equalsIgnoreCase(cursor.getString(nameColumnIndex))) {
                        folderNameExists = true;
                        break;
                    }
                }

            } finally {
                cursor.close();
            }

        } else {
            Log.e(LOG_TAG, "Folder cursor is null!");
        }

        return folderNameExists;
    }

    public static List<FolderItem> getOfflineArtists(Context context, long folderId) {

        List<FolderItem> artistItems = new ArrayList<>();

        String[] projection = {ArtistEntry._ID, ArtistEntry.COLUMN_ARTIST_NAME, ArtistEntry.COLUMN_ARTIST_BIOGRAPHY};

        Uri uriWithFolderId = ContentUris.withAppendedId(ArtistEntry.CONTENT_URI, folderId);

        Cursor cursor = context.getContentResolver().query(uriWithFolderId, projection, null, null, null);

        if (cursor != null) {
            try {
                int idColumnIndex = cursor.getColumnIndex(ArtistEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(ArtistEntry.COLUMN_ARTIST_NAME);
                int biographyColumnIndex = cursor.getColumnIndex(ArtistEntry.COLUMN_ARTIST_BIOGRAPHY);

                while (cursor.moveToNext()) {

                    int artistId = cursor.getInt(idColumnIndex);

                    artistItems.add(new Artist(artistId, cursor.getString(nameColumnIndex), cursor.getString(biographyColumnIndex), FileUtils.getSavedArtistImage(context, artistId)));
                }

            } finally {
                cursor.close();
            }
        } else {
            Log.e(LOG_TAG, "Artist cursor is null!");
        }

        return artistItems;
    }

    public static List<FolderItem> getOfflineArtistAlbums(Context context, long artistId) {
        List<FolderItem> albumItems = new ArrayList<>();

        String[] projection = {AlbumEntry._ID, AlbumEntry.COLUMN_ALBUM_NAME};

        Uri uriWithArtistId = ContentUris.withAppendedId(AlbumEntry.CONTENT_URI, artistId);

        Cursor cursor = context.getContentResolver().query(uriWithArtistId, projection, null, null, null);

        if (cursor != null) {
            try {
                int idColumnIndex = cursor.getColumnIndex(AlbumEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(AlbumEntry.COLUMN_ALBUM_NAME);

                while (cursor.moveToNext()) {

                    int albumId = cursor.getInt(idColumnIndex);

                    albumItems.add(new Album(albumId, cursor.getString(nameColumnIndex), getOfflineAlbumTracks(context, albumId), FileUtils.getSavedArtistAlbumImage(context, artistId, albumId)));
                }

            } finally {
                cursor.close();
            }
        } else {
            Log.e(LOG_TAG, "Artist cursor is null!");
        }

        return albumItems;
    }

    private static List<Track> getOfflineAlbumTracks(Context context, long albumId) {
        List<Track> tracks = new ArrayList<>();

        String[] projection = {TrackEntry.COLUMN_TRACK_NAME, TrackEntry.COLUMN_TRACK_DURATION};

        Uri uriWithAlbumId = ContentUris.withAppendedId(TrackEntry.CONTENT_URI, albumId);

        Cursor cursor = context.getContentResolver().query(uriWithAlbumId, projection, null, null, null);

        if (cursor != null) {
            try {
                int nameColumnIndex = cursor.getColumnIndex(TrackEntry.COLUMN_TRACK_NAME);
                int durationColumnIndex = cursor.getColumnIndex(TrackEntry.COLUMN_TRACK_DURATION);

                while (cursor.moveToNext()) {

                    tracks.add(new Track(cursor.getString(nameColumnIndex), cursor.getString(durationColumnIndex)));
                }

            } finally {
                cursor.close();
            }
        } else {
            Log.e(LOG_TAG, "Artist cursor is null!");
        }

        return tracks;
    }

    public static List<Artist> getOnlineArtists(int pageNumber) throws IOException {
        return JsonUtils.extractArtists(HttpUtils.apiRequestArtists(pageNumber));
    }

    public static List<Album> getArtistAlbums(String artistName) throws IOException {
        return JsonUtils.extractArtistAlbums(HttpUtils.makeApiRequest(artistName, null, HttpUtils.API_ACTION_REQUEST_ARTIST_ALBUMS));
    }

    public static boolean artistIsAlreadyStored(Context context, String artistName) {

        boolean artistExists = false;

        String[] projection = {ArtistEntry.COLUMN_ARTIST_NAME};

        Cursor cursor = context.getContentResolver().query(ArtistEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            try {
                int nameColumnIndex = cursor.getColumnIndex(ArtistEntry.COLUMN_ARTIST_NAME);

                while (cursor.moveToNext()) {
                    if (artistName.equalsIgnoreCase(cursor.getString(nameColumnIndex))) {
                        artistExists = true;
                        break;
                    }
                }

            } finally {
                cursor.close();
            }

        } else {
            Log.e(LOG_TAG, "Artist cursor is null!");
        }

        return artistExists;
    }
}
