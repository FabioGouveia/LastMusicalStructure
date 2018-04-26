package com.example.android.lastmusicalstructure.artist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.ui.FolderActivity;
import com.example.android.lastmusicalstructure.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

public class ArtistCardAdapter extends RecyclerView.Adapter<ArtistCardAdapter.ViewHolder> {

    private Context context;
    private Folder folder;
    private ArrayList<Artist> artists;

    public ArtistCardAdapter(Context context, Folder folder) {
        this.context = context;
        this.folder = folder;
        this.artists = new ArrayList<>();
    }

    public ArtistCardAdapter setArtist(List<Artist> artists) {
        this.artists = (ArrayList<Artist>) artists;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public ArtistCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Artist artist = artists.get(position);

        final String artistName = artist.getName();

        holder.artistImage.setImageDrawable(artist.getImage());
        holder.artistName.setText(artistName);

        final boolean artistAlreadyStored = QueryUtils.artistIsAlreadyStored(context, artistName);

        if (artistAlreadyStored) {
            holder.artistSavedLabel.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!artistAlreadyStored) {
                    Intent folderIntent = new Intent(context, FolderActivity.class);
                    folderIntent.putExtra(Folder.KEY, folder);
                    folderIntent.putExtra(Artist.KEY, artist);
                    folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.ARTIST_ON_LINE_DISPLAY_MODE);
                    context.startActivity(folderIntent);
                    ((Activity) context).overridePendingTransition(R.anim.enter_from_bottom_animation, R.anim.exit_to_top_animation);
                } else {
                    Toast.makeText(context, "Artist already saved..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView artistImage;
        private TextView artistName;
        private TextView artistSavedLabel;

        private ViewHolder(View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artist_card_imageView);
            artistName = itemView.findViewById(R.id.artist_name_text_view);
            artistSavedLabel = itemView.findViewById(R.id.artist_saved_label_text_view);
        }
    }
}
