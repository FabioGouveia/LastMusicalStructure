package com.example.android.lastmusicalstructure.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.lastmusicalstructure.data.UserContract.AlbumEntry;
import com.example.android.lastmusicalstructure.data.UserContract.ArtistEntry;
import com.example.android.lastmusicalstructure.data.UserContract.FolderEntry;
import com.example.android.lastmusicalstructure.data.UserContract.TrackEntry;

import static com.example.android.lastmusicalstructure.data.UserContract.CONTENT_AUTHORITY;
import static com.example.android.lastmusicalstructure.data.UserContract.PATH_ALBUM;
import static com.example.android.lastmusicalstructure.data.UserContract.PATH_ARTIST;
import static com.example.android.lastmusicalstructure.data.UserContract.PATH_ARTIST_ID;
import static com.example.android.lastmusicalstructure.data.UserContract.PATH_FOLDER;
import static com.example.android.lastmusicalstructure.data.UserContract.PATH_TRACK;

public class UserProvider extends ContentProvider {

    private static final String LOG_TAG = UserProvider.class.getSimpleName();
    private static final int FOLDERS = 100;
    private static final int FOLDER_ID = 101;
    private static final int ARTISTS = 102;
    private static final int ARTISTS_ID = 103;
    private static final int ARTIST_FOLDER_ID = 104;
    private static final int ALBUMS = 105;
    private static final int ALBUM_ARTIST_ID = 106;
    private static final int TRACKS = 107;
    private static final int TRACK_ALBUM_ID = 108;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FOLDER, FOLDERS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FOLDER + "/#", FOLDER_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ARTIST, ARTISTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ARTIST_ID + "/#", ARTISTS_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ARTIST + "/#", ARTIST_FOLDER_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ALBUM, ALBUMS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ALBUM + "/#", ALBUM_ARTIST_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TRACK, TRACKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TRACK + "/#", TRACK_ALBUM_ID);
    }

    private UserDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new UserDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FOLDERS:
                cursor = database.query(FolderEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case FOLDER_ID:
                selection = FolderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(FolderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case ARTISTS:
                cursor = database.query(ArtistEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case ARTIST_FOLDER_ID:
                selection = ArtistEntry.COLUMN_FOLDER_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ArtistEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case ALBUMS:
                cursor = database.query(AlbumEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case ALBUM_ARTIST_ID:
                selection = AlbumEntry.COLUMN_ARTIST_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(AlbumEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case TRACKS:
                cursor = database.query(TrackEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case TRACK_ALBUM_ID:
                selection = TrackEntry.COLUMN_ALBUM_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TrackEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOLDERS:
                return insertFolder(uri, values);
            case ARTISTS:
                return insertArtist(uri, values);
            case ALBUMS:
                return insertAlbum(uri, values);
            case TRACKS:
                return insertTrack(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFolder(Uri uri, ContentValues values) {
        String folderName = values.getAsString(FolderEntry.COLUMN_FOLDER_NAME);
        Integer folderIcon = values.getAsInteger(FolderEntry.COLUMN_FOLDER_ICON);

        if (folderName == null || folderName.isEmpty()) {
            throw new IllegalArgumentException("Folder needs a name");
        }

        if (folderIcon == null || folderIcon <= 0) {
            throw new IllegalArgumentException("Folder needs an icon");
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(FolderEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertArtist(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(ArtistEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAlbum(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(AlbumEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTrack(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(TrackEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOLDER_ID:
                selection = FolderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(FolderEntry.TABLE_NAME, selection, selectionArgs);
            case ARTISTS_ID:
                selection = ArtistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(ArtistEntry.TABLE_NAME, selection, selectionArgs);
            case ARTIST_FOLDER_ID:
                selection = FolderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(ArtistEntry.TABLE_NAME, selection, selectionArgs);
            case ALBUM_ARTIST_ID:
                selection = ArtistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(AlbumEntry.TABLE_NAME, selection, selectionArgs);
            case TRACK_ALBUM_ID:
                selection = AlbumEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.delete(TrackEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOLDER_ID:
                selection = FolderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return database.update(FolderEntry.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }
}
