package com.example.android.lastmusicalstructure.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.lastmusicalstructure.data.UserContract.AlbumEntry;
import com.example.android.lastmusicalstructure.data.UserContract.ArtistEntry;
import com.example.android.lastmusicalstructure.data.UserContract.FolderEntry;
import com.example.android.lastmusicalstructure.data.UserContract.TrackEntry;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FOLDERS_TABLE = "CREATE TABLE " + FolderEntry.TABLE_NAME + " ("
                + FolderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FolderEntry.COLUMN_FOLDER_ICON + " INTEGER NOT NULL, "
                + FolderEntry.COLUMN_FOLDER_NAME + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FOLDERS_TABLE);

        String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " ("
                + ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL,"
                + ArtistEntry.COLUMN_ARTIST_BIOGRAPHY + " TEXT NOT NULL, "
                + ArtistEntry.COLUMN_FOLDER_ID + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ARTISTS_TABLE);

        String SQL_CREATE_ALBUMS_TABLE = "CREATE TABLE " + AlbumEntry.TABLE_NAME + " ("
                + AlbumEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AlbumEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, "
                + AlbumEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ALBUMS_TABLE);

        String SQL_CREATE_TRACKS_TABLE = "CREATE TABLE " + TrackEntry.TABLE_NAME + " ("
                + TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, "
                + TrackEntry.COLUMN_TRACK_DURATION + " TEXT NOT NULL, "
                + TrackEntry.COLUMN_ALBUM_ID + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FolderEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlbumEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);

        onCreate(db);
    }
}
