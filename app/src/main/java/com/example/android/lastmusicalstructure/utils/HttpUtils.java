package com.example.android.lastmusicalstructure.utils;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.lastmusicalstructure.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

final class HttpUtils {

    final static int API_ACTION_REQUEST_ARTIST_INFO = 102;
    final static int API_ACTION_REQUEST_ARTIST_ALBUMS = 103;
    final static int API_ACTION_REQUEST_ALBUM_TRACKS = 104;
    private final static String LOG_TAG = HttpUtils.class.getSimpleName();
    private final static String API_URL = "http://ws.audioscrobbler.com/2.0/";
    private final static String API_KEY = BuildConfig.API_KEY;
    private final static String API_RESPONSE_FORMAT = "json";
    private final static String API_ARTIST_METHOD = "chart.gettopartists";
    private final static String API_ARTIST_INFO_METHOD = "artist.getinfo";
    private final static String API_ARTIST_ALBUMS_METHOD = "artist.gettopalbums";
    private final static String API_ARTIST_ALBUM_TRACKS_METHOD = "album.getinfo";

    static String apiRequestArtists(int pageNumber) throws IOException {
        return (String) executeRequest(createQuery(null, null, pageNumber, 0), false);
    }

    static String makeApiRequest(String artistName, String albumName, int requestType) throws IOException {
        return (String) executeRequest(createQuery(artistName, albumName, 0, requestType), false);
    }

    @Nullable
    static BitmapDrawable makeHttpRequest(String url) {
        try {
            return (BitmapDrawable) executeRequest(url, true);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error requesting the image: ", e);
        }
        return null;
    }

    private static String createQuery(String artistName, String albumName, int pageNumber, int requestType) {

        String query = null;

        if (artistName != null && requestType != 0) {
            switch (requestType) {
                case API_ACTION_REQUEST_ARTIST_INFO:
                    query = API_URL + "?method=" + API_ARTIST_INFO_METHOD + "&artist=" + artistName.replaceAll("\\s+", "%20") + "&api_key=" + API_KEY + "&format=" + API_RESPONSE_FORMAT;
                    break;
                case API_ACTION_REQUEST_ARTIST_ALBUMS:
                    query = API_URL + "?method=" + API_ARTIST_ALBUMS_METHOD + "&artist=" + artistName.replaceAll("\\s+", "%20") + "&api_key=" + API_KEY + "&format=" + API_RESPONSE_FORMAT;
                    break;
                case API_ACTION_REQUEST_ALBUM_TRACKS:
                    query = API_URL + "?method=" + API_ARTIST_ALBUM_TRACKS_METHOD + "&artist=" + artistName.replaceAll("\\s+", "%20") + "&album=" + albumName.replaceAll("\\s+", "%20") + "&api_key=" + API_KEY + "&format=" + API_RESPONSE_FORMAT;
                    break;
            }
        } else {
            query = API_URL + "?method=" + API_ARTIST_METHOD + "&page=" + pageNumber + "&limit=43&api_key=" + API_KEY + "&format=" + API_RESPONSE_FORMAT;
        }

        return query;
    }

    private static Object executeRequest(String query, boolean requestImage) throws IOException {

        Object responseObject = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) createURL(query).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            inputStream = connection.getInputStream();
            if (inputStream != null) {
                if (requestImage) {
                    responseObject = new BitmapDrawable(BitmapFactory.decodeStream(inputStream));
                } else {
                    responseObject = readFromStream(inputStream);
                }
            }
        } catch (IOException e) {
            Log.i(LOG_TAG, "Error with connection request");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return responseObject;
    }

    private static URL createURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            inputStreamReader.close();
            reader.close();
        }
        return output.toString();
    }

}
