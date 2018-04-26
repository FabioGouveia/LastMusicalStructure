package com.example.android.lastmusicalstructure.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();


    static void saveArtistImage(Context context, BitmapDrawable artistImage, long artistId) {

        Bitmap bitmap = artistImage.getBitmap();

        try {
            OutputStream outputStream = context.openFileOutput("artist_" + artistId + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting internal file system!", e);
        }
    }

    static BitmapDrawable getSavedArtistImage(Context context, long artistId) {

        BitmapDrawable bitmapDrawable = null;

        try {
            InputStream inputStream = context.openFileInput("artist_" + artistId + ".png");
            bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeStream(inputStream));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting internal file system!", e);
        }

        return bitmapDrawable;
    }

    static void saveArtistAlbumImage(Context context, BitmapDrawable albumImage, long artistId, long albumId) {
        Bitmap bitmap = albumImage.getBitmap();

        try {
            OutputStream outputStream = context.openFileOutput("artist_" + artistId + "_album_" + albumId + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting internal file system!", e);
        }
    }

    static BitmapDrawable getSavedArtistAlbumImage(Context context, long artistId, long albumId) {

        BitmapDrawable bitmapDrawable = null;

        try {
            InputStream inputStream = context.openFileInput("artist_" + artistId + "_album_" + albumId + ".png");
            bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeStream(inputStream));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting internal file system!", e);
        }

        return bitmapDrawable;
    }

    static void deleteArtistImage(Context context, long artistId) {
        context.deleteFile("artist_" + artistId + ".png");
    }

    static void deleteArtistAlbumImage(Context context, long artistId, long albumId) {
        context.deleteFile("artist_" + artistId + "_album_" + albumId + ".png");
    }
}
