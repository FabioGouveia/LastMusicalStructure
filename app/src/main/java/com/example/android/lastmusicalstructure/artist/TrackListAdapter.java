package com.example.android.lastmusicalstructure.artist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.model.Track;

import java.util.List;

public class TrackListAdapter extends ArrayAdapter<Track> {

    public TrackListAdapter(Context context, List<Track> tracks) {
        super(context, 0, tracks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_tracks, parent, false);
        }

        Track track = getItem(position);
        if (track != null) {
            ((TextView) convertView.findViewById(R.id.track_name_list_item_text_view)).setText(track.getName());
            ((TextView) convertView.findViewById(R.id.track_duration_list_item_text_view)).setText(getContext().getResources().getString(R.string.track_time, track.getDuration()));
        }

        return convertView;
    }
}