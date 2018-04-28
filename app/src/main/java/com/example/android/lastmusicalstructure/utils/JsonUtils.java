package com.example.android.lastmusicalstructure.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class JsonUtils {

    private final static String LOG_TAG = JsonUtils.class.getSimpleName();

    static List<Artist> extractArtists(String jsonString) {
        List<Artist> artists = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONObject artistsJsonObject = baseJsonResponse.getJSONObject("artists");
            JSONArray artistsJsonArray = artistsJsonObject.getJSONArray("artist");

            int numArtists = artistsJsonArray.length();

            for (int i = 0; i < numArtists; i++) {
                JSONObject artistObject = artistsJsonArray.getJSONObject(i);
                JSONArray imagesArray = artistObject.getJSONArray("image");

                String artistName = artistObject.getString("name");

                String imageURL = imagesArray.getJSONObject(3).getString("#text");

                Artist artist = new Artist(artistName, extractArtistBiography(artistName), imageURL, HttpUtils.makeHttpRequest(imageURL));

                artists.add(artist);
            }

        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, "Problem parsing artists JSON results", e);
        }

        return artists;
    }

    @Nullable
    private static String extractArtistBiography(String artistName) {

        String biography = null;

        try {
            JSONObject rootJSONObject = new JSONObject(HttpUtils.makeApiRequest(artistName, null, HttpUtils.API_ACTION_REQUEST_ARTIST_INFO));

            biography = rootJSONObject.getJSONObject("artist").getJSONObject("bio").getString("content");
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, "Problem parsing artist biography JSON results", e);
        }

        return biography;
    }

    static List<Album> extractArtistAlbums(String jsonString) {

        List<Album> albums = new ArrayList<>();

        if (jsonString != null) {
            try {
                JSONObject baseJsonResponse = new JSONObject(jsonString);

                JSONArray albumsJsonArray = baseJsonResponse.getJSONObject("topalbums").getJSONArray("album");
                String artistName = baseJsonResponse.getJSONObject("topalbums").getJSONObject("@attr").getString("artist");

                if (albumsJsonArray != null) {

                    int numAlbums = albumsJsonArray.length();

                    for (int i = 0; i < numAlbums; i++) {
                        JSONObject albumObject = albumsJsonArray.getJSONObject(i);
                        JSONArray imagesArray = albumObject.getJSONArray("image");
                        String albumName = albumObject.getString("name");

                        List<Track> tracks = getAlbumTracks(artistName, albumName);

                        int tracksLength = tracks.size();
                        if (tracksLength > 5) {
                            albums.add(new Album(albumName, tracks, HttpUtils.makeHttpRequest(imagesArray.getJSONObject(3).getString("#text"))));
                        }
                    }
                }
            } catch (JSONException | IOException e) {
                Log.e(LOG_TAG, "Problem parsing artist albums JSON results", e);
            }
        }

        return albums;
    }

    private static List<Track> getAlbumTracks(String artistName, String albumName) throws JSONException, IOException {

        List<Track> tracks = new ArrayList<>();

        String jsonString = HttpUtils.makeApiRequest(artistName, albumName, HttpUtils.API_ACTION_REQUEST_ALBUM_TRACKS);

        if (jsonString != null) {
            JSONObject baseJSONObject = new JSONObject(jsonString);

            if (baseJSONObject.has("album")) {
                JSONArray tracksArray = baseJSONObject.getJSONObject("album").getJSONObject("tracks").getJSONArray("track");

                int tracksLength = tracksArray.length();

                for (int i = 0; i < tracksLength; i++) {
                    JSONObject trackObject = tracksArray.getJSONObject(i);

                    String trackDuration = trackObject.getString("duration");
                    String firstNumber = trackDuration.substring(0, 1);

                    trackDuration = firstNumber + "." + trackDuration.substring(1);

                    if (!firstNumber.equals("0"))
                        tracks.add(new Track(trackObject.getString("name"), trackDuration));
                }
            }
        }
        return tracks;
    }
}
