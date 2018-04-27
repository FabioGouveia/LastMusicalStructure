package com.example.android.lastmusicalstructure.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.artist.TrackListAdapter;
import com.example.android.lastmusicalstructure.folder.FolderItem;
import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.model.Track;

import java.util.List;


public class FolderFragment extends Fragment {

    private static final String ARG_SECTION_FOLDER = "section_folder";
    private static final String ARG_SECTION_ITEM = "section_item";

    private Folder folder;
    private FolderItem item;

    public FolderFragment() {
    }

    public static FolderFragment newInstance(Folder folder, FolderItem itemToDisplay) {

        FolderFragment artistFragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SECTION_FOLDER, folder);
        args.putParcelable(ARG_SECTION_ITEM, itemToDisplay);
        artistFragment.setArguments(args);

        return artistFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder, container, false);

        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(Folder.class.getClassLoader());
            folder = savedInstanceState.getParcelable(ARG_SECTION_FOLDER);
            savedInstanceState.setClassLoader(FolderItem.class.getClassLoader());
            item = savedInstanceState.getParcelable(ARG_SECTION_ITEM);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                args.setClassLoader(Folder.class.getClassLoader());
                folder = args.getParcelable(ARG_SECTION_FOLDER);
                args.setClassLoader(FolderItem.class.getClassLoader());
                item = args.getParcelable(ARG_SECTION_ITEM);
            }
        }

        ImageView folderImageView = rootView.findViewById(R.id.folder_fragment_image_view);
        TextView itemInfoTab = rootView.findViewById(R.id.folder_fragment_info);
        Button itemListButton = rootView.findViewById(R.id.folder_fragment_list_button);
        ScrollView itemInfoScrollView = rootView.findViewById(R.id.folder_fragment_info_scroll_view);
        TextView itemInfo = rootView.findViewById(R.id.folder_fragment_info_text_view);
        ListView folderListView = rootView.findViewById(R.id.folder_fragment_list_view);
        folderImageView.setImageDrawable(item.getImage());

        if (item instanceof Artist) {
            if (isOrientationPortrait()) {
                itemInfoTab.setText(getResources().getString(R.string.artist_biography));
            }
            itemListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent folderIntent = new Intent(getContext(), FolderActivity.class);
                    folderIntent.putExtra(Folder.KEY, folder);
                    folderIntent.putExtra(Artist.KEY, item);
                    folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.ARTIST_OFF_LINE_DISPLAY_MODE);
                    startActivity(folderIntent);
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.overridePendingTransition(R.anim.enter_from_bottom_animation, R.anim.exit_to_top_animation);
                    }
                }
            });

            itemInfo.setText(((Artist) item).getInfo());

        } else {
            if (isOrientationPortrait()) {
                itemInfoTab.setText(getResources().getString(R.string.artist_tracks_tab));
                itemListButton.setVisibility(View.GONE);
            } else {
                itemListButton.setText(getResources().getString(R.string.artist_tracks_tab));
                itemListButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent));
            }

            itemInfoScrollView.setVisibility(View.GONE);
            folderListView.setVisibility(View.VISIBLE);

            List<Track> tracks = ((Album) item).getTracks();
            TrackListAdapter trackListAdapter = new TrackListAdapter(getActivity(), tracks);
            folderListView.setAdapter(trackListAdapter);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putParcelable(ARG_SECTION_FOLDER, folder);
        outState.putParcelable(ARG_SECTION_ITEM, item);

        super.onSaveInstanceState(outState);
    }

    private boolean isOrientationPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
