package com.example.android.lastmusicalstructure.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.artist.AlbumItemLoader;
import com.example.android.lastmusicalstructure.artist.ArtistDialogListener;
import com.example.android.lastmusicalstructure.folder.FolderDialogListener;
import com.example.android.lastmusicalstructure.folder.FolderFragmentAdapter;
import com.example.android.lastmusicalstructure.folder.FolderItem;
import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.utils.DialogUtils;
import com.example.android.lastmusicalstructure.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;


public class FolderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Album>>, PopupMenu.OnMenuItemClickListener {

    private ActionBar actionBar;
    private ProgressBar folderProgressBar;
    private TabLayout folderTabLayout;
    private ViewPager folderViewPager;

    private Folder folder;
    private Artist artist;
    private int displayMode;
    private boolean dataLoadSuccessfully;

    private DialogUtils folderNameUpdateDialog;
    private DialogUtils folderIconUpdateDialog;
    private DialogUtils folderDeleteDialog;
    private DialogUtils artistSaveDialog;
    private DialogUtils artistDeleteDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        if (savedInstanceState != null) {
            displayMode = savedInstanceState.getInt(Folder.DISPLAY_MODE_KEY);
            savedInstanceState.setClassLoader(Folder.class.getClassLoader());
            folder = savedInstanceState.getParcelable(Folder.KEY);
            savedInstanceState.setClassLoader(Artist.class.getClassLoader());
            artist = savedInstanceState.getParcelable(Artist.KEY);
        } else {
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                displayMode = bundle.getInt(Folder.DISPLAY_MODE_KEY);
                bundle.setClassLoader(Folder.class.getClassLoader());
                folder = bundle.getParcelable(Folder.KEY);
                bundle.setClassLoader(Artist.class.getClassLoader());
                artist = bundle.getParcelable(Artist.KEY);
            } else {
                Toast.makeText(this, "Something wrong with folder data..!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        actionBar = getSupportActionBar();


        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            if (artist != null) {
                actionBar.setTitle(artist.getName());
                actionBar.setSubtitle("Albums");
            } else {
                actionBar.setTitle(folder.getName());
            }
        }

        folderProgressBar = findViewById(R.id.folder_loading_artist_progress_bar);
        folderTabLayout = findViewById(R.id.folder_tab);
        folderViewPager = findViewById(R.id.folder_view_pager);
        TextView folderEmptyView = findViewById(R.id.folder_empty_view);

        if (displayMode == Folder.ARTIST_ON_LINE_DISPLAY_MODE) {
            Bundle args = new Bundle();
            args.putString(Artist.KEY, artist.getName());

            getLoaderManager().initLoader(0, args, this);
        } else {
            List<FolderItem> folderItems;

            if (artist != null) {
                folderItems = QueryUtils.getOfflineArtistAlbums(this, artist.getId());
            } else {
                folderItems = QueryUtils.getOfflineArtists(this, folder.getId());
            }

            if (folderItems == null || folderItems.isEmpty()) {
                folderProgressBar.setVisibility(View.GONE);
                folderTabLayout.setVisibility(View.GONE);
                folderViewPager.setVisibility(View.GONE);
                folderEmptyView.setVisibility(View.VISIBLE);
                folderEmptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToArtistSearchScreen();
                    }
                });
            } else {
                FolderFragmentAdapter folderFragmentAdapter = new FolderFragmentAdapter(getSupportFragmentManager(), folder, folderItems);

                int itemsSize = folderItems.size();

                setTabModeDependingOnDataSize(itemsSize);

                folderViewPager.setAdapter(folderFragmentAdapter);
                folderTabLayout.setupWithViewPager(folderViewPager);

                if (displayMode == Folder.FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED) {
                    folderViewPager.setCurrentItem(itemsSize, true);
                }

                if (displayMode == Folder.FOLDER_DISPLAY_MODE || displayMode == Folder.FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED) {
                    folderTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            View view = ((ViewGroup) folderTabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                            PopupMenu popupMenu = new PopupMenu(FolderActivity.this, view);
                            popupMenu.setOnMenuItemClickListener(FolderActivity.this);
                            popupMenu.inflate(R.menu.artist_tab_menu);
                            popupMenu.show();
                        }
                    });
                }

                dataLoadedSuccessfully();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (displayMode) {
            case Folder.FOLDER_DISPLAY_MODE:
            case Folder.FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED:
                inflater.inflate(R.menu.folder_menu, menu);
                return true;
            case Folder.ARTIST_ON_LINE_DISPLAY_MODE:
                inflater.inflate(R.menu.artist_online_menu, menu);
                return true;
            case Folder.ARTIST_OFF_LINE_DISPLAY_MODE:
                inflater.inflate(R.menu.artist_offline_menu, menu);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                processExit();
                break;
            case R.id.add_artist_to_folder:
                goToArtistSearchScreen();
                break;
            case R.id.rename_folder:
                folderNameUpdateDialog = new DialogUtils();

                Bundle folderUpdateNameArguments = new Bundle();
                folderUpdateNameArguments.putParcelable(Folder.KEY, folder);

                folderNameUpdateDialog.setArguments(folderUpdateNameArguments);
                folderNameUpdateDialog.setDialogType(DialogUtils.DIALOG_TYPE_UPDATE_FOLDER_NAME);
                folderNameUpdateDialog.addDialogListener(new FolderDialogListener() {
                    @Override
                    public void onFolderEmptyName() {
                        Toast.makeText(getApplicationContext(), R.string.err_folder_name_empty, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFolderNameAlreadyExists() {
                        Toast.makeText(getApplicationContext(), R.string.err_folder_name_already_exists, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFolderNameUpdated(String newFolderName) {
                        folder.setName(newFolderName);
                        actionBar.setTitle(newFolderName);
                        Toast.makeText(getApplicationContext(), R.string.folder_updated_successfully, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFolderIconUpdated() {
                    }

                    @Override
                    public void onFolderCreated(String folderName) {
                    }

                    @Override
                    public void onFolderDeleted(String folderName) {
                    }
                });

                folderNameUpdateDialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.change_folder_icon:
                folderIconUpdateDialog = new DialogUtils();

                Bundle folderUpdateIconArguments = new Bundle();
                folderUpdateIconArguments.putParcelable(Folder.KEY, folder);

                folderIconUpdateDialog.setArguments(folderUpdateIconArguments);
                folderIconUpdateDialog.setDialogType(DialogUtils.DIALOG_TYPE_UPDATE_FOLDER_ICON);
                folderIconUpdateDialog.addDialogListener(new FolderDialogListener() {
                    @Override
                    public void onFolderEmptyName() {
                    }

                    @Override
                    public void onFolderNameAlreadyExists() {
                    }

                    @Override
                    public void onFolderNameUpdated(String newFolderName) {
                    }

                    @Override
                    public void onFolderIconUpdated() {
                        Toast.makeText(getApplicationContext(), R.string.folder_updated_successfully, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFolderCreated(String folderName) {
                    }

                    @Override
                    public void onFolderDeleted(String folderName) {
                    }
                });

                folderIconUpdateDialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.delete_folder:
                folderDeleteDialog = new DialogUtils();

                Bundle folderArguments = new Bundle();
                folderArguments.putParcelable(Folder.KEY, folder);

                folderDeleteDialog.setArguments(folderArguments);
                folderDeleteDialog.setDialogType(DialogUtils.DIALOG_TYPE_DELETE_FOLDER);
                folderDeleteDialog.addDialogListener(new FolderDialogListener() {
                    @Override
                    public void onFolderNameAlreadyExists() {
                    }

                    @Override
                    public void onFolderNameUpdated(String newFolderName) {
                    }

                    @Override
                    public void onFolderIconUpdated() {
                    }

                    @Override
                    public void onFolderEmptyName() {
                    }

                    @Override
                    public void onFolderCreated(String folderName) {
                    }

                    @Override
                    public void onFolderDeleted(String folderName) {
                        Toast.makeText(getApplicationContext(), getString(R.string.folder_deleted_successfully, folderName), Toast.LENGTH_SHORT).show();
                        goBackToMainScreen();
                    }
                });

                folderDeleteDialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.save_artist:

                if (dataLoadSuccessfully) {
                    artistSaveDialog = new DialogUtils();

                    Bundle artistSaveArguments = new Bundle();
                    artistSaveArguments.putParcelable(Folder.KEY, folder);
                    artistSaveArguments.putParcelable(Artist.KEY, artist);

                    artistSaveDialog.setArguments(artistSaveArguments);
                    artistSaveDialog.setDialogType(DialogUtils.DIALOG_TYPE_SAVE_ARTIST);
                    artistSaveDialog.addDialogListener(new ArtistDialogListener() {

                        @Override
                        public void onArtistSaved(String artistName) {

                            Intent folderIntent = new Intent(FolderActivity.this, FolderActivity.class);
                            folderIntent.putExtra(Folder.KEY, folder);
                            folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED);
                            startActivity(folderIntent);
                            overridePendingTransition(R.anim.enter_from_left_animation, R.anim.exit_to_right_animation);

                            Toast.makeText(getApplicationContext(), getString(R.string.artist_saved_successfully, artistName), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onArtistDeleted(String artistName) {

                        }
                    });

                    artistSaveDialog.show(getSupportFragmentManager(), null);
                } else {
                    Toast.makeText(FolderActivity.this, "Whait for load to finish!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete_artist:
                deleteArtist(artist);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onPause() {
        if (folderNameUpdateDialog != null) folderNameUpdateDialog.dismiss();
        if (folderIconUpdateDialog != null) folderIconUpdateDialog.dismiss();
        if (folderDeleteDialog != null) folderDeleteDialog.dismiss();
        if (artistSaveDialog != null) artistSaveDialog.dismiss();
        if (artistDeleteDialog != null) artistDeleteDialog.dismiss();

        super.onPause();
    }

    @Override
    public void onBackPressed() {

        processExit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Folder.KEY, folder);
        outState.putInt(Folder.DISPLAY_MODE_KEY, displayMode);
        outState.putParcelable(Artist.KEY, artist);

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
        return new AlbumItemLoader(FolderActivity.this, args.getString(Artist.KEY));
    }

    @Override
    public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
        if (data.isEmpty()) {
            folderTabLayout.setVisibility(View.GONE);
            folderViewPager.setVisibility(View.GONE);
        } else {
            artist.setAlbums(data);

            List<FolderItem> folderItems = new ArrayList<>();
            folderItems.addAll(data);

            FolderFragmentAdapter folderFragmentAdapter = new FolderFragmentAdapter(getSupportFragmentManager(), folder, folderItems);

            setTabModeDependingOnDataSize(folderItems.size());

            folderViewPager.setAdapter(folderFragmentAdapter);
            folderTabLayout.setupWithViewPager(folderViewPager);

            dataLoadedSuccessfully();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Album>> loader) {
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_artist:
                List<FolderItem> artists = QueryUtils.getOfflineArtists(this, folder.getId());
                Artist artist = (Artist) artists.get(folderTabLayout.getSelectedTabPosition());
                deleteArtist(artist);
                return true;
            default:
                return false;
        }
    }

    private void processExit() {
        if (displayMode == Folder.FOLDER_DISPLAY_MODE || displayMode == Folder.FOLDER_DISPLAY_MODE_WITH_ARTIST_UPDATED) {
            goBackToMainScreen();
        } else {
            leaveArtistView();
        }
    }

    private void leaveArtistView() {
        if (displayMode == Folder.ARTIST_ON_LINE_DISPLAY_MODE) {
            goToArtistSearchScreen();
        } else {
            Intent folderIntent = new Intent(this, FolderActivity.class);
            folderIntent.putExtra(Folder.KEY, folder);
            folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.FOLDER_DISPLAY_MODE);
            startActivity(folderIntent);
            overridePendingTransition(R.anim.enter_from_left_animation, R.anim.exit_to_right_animation);
        }
    }

    private void goBackToMainScreen() {
        startActivity(new Intent(this, FolderListActivity.class));
        overridePendingTransition(R.anim.enter_from_left_animation, R.anim.exit_to_right_animation);
    }

    private void goToArtistSearchScreen() {
        Intent searchArtistIntent = new Intent(this, ArtistSearchActivity.class);
        searchArtistIntent.putExtra(Folder.KEY, folder);
        startActivity(searchArtistIntent);
        overridePendingTransition(R.anim.enter_from_right_animation, R.anim.exit_to_left_animation);
    }

    private void setTabModeDependingOnDataSize(int dataSize) {
        if (dataSize > 4) {
            folderTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            folderTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }

    private void dataLoadedSuccessfully() {

        dataLoadSuccessfully = true;

        folderProgressBar.setVisibility(View.GONE);
        folderTabLayout.setVisibility(View.VISIBLE);
        folderViewPager.setVisibility(View.VISIBLE);
    }

    private void deleteArtist(Artist artist) {
        artistDeleteDialog = new DialogUtils();

        Bundle artistArguments = new Bundle();
        artistArguments.putParcelable(Folder.KEY, folder);
        artistArguments.putParcelable(Artist.KEY, artist);

        artistDeleteDialog.setArguments(artistArguments);
        artistDeleteDialog.setDialogType(DialogUtils.DIALOG_TYPE_DELETE_ARTIST);
        artistDeleteDialog.addDialogListener(new ArtistDialogListener() {

            @Override
            public void onArtistSaved(String artistName) {

            }

            @Override
            public void onArtistDeleted(String artistName) {
                leaveArtistView();
                Toast.makeText(getApplicationContext(), artistName + " deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        artistDeleteDialog.show(getSupportFragmentManager(), null);
    }
}
