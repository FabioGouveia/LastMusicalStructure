package com.example.android.lastmusicalstructure.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class UserContract {

    static final String CONTENT_AUTHORITY = "com.example.android.lastmusicalstructure.folder";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_FOLDER = "folder";
    static final String PATH_ARTIST = "artist";
    static final String PATH_ARTIST_ID = "artist_id";
    static final String PATH_ALBUM = "album";
    static final String PATH_TRACK = "track";

    private UserContract() {
    }

    public static final class FolderEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOLDER);
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_FOLDER_ICON = "icon";
        public final static String COLUMN_FOLDER_NAME = "name";
        final static String TABLE_NAME = "folders";
    }

    public static final class ArtistEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ARTIST);
        public static final Uri CONTENT_URI_ID = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ARTIST_ID);
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_BIOGRAPHY = "artist_biography";
        public static final String COLUMN_FOLDER_ID = "folder_id";
        static final String TABLE_NAME = "artists";

    }

    public static final class AlbumEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALBUM);
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        static final String TABLE_NAME = "albums";
    }

    public static final class TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRACK);
        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_TRACK_DURATION = "track_duration";
        public static final String COLUMN_ALBUM_ID = "album_id";
        static final String TABLE_NAME = "tracks";
        static final String _ID = BaseColumns._ID;
    }
}
