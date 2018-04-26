package com.example.android.lastmusicalstructure.artist;

import com.example.android.lastmusicalstructure.utils.DialogListener;

public interface ArtistDialogListener extends DialogListener {
    void onArtistSaved(String artistName);

    void onArtistDeleted(String artistName);
}
