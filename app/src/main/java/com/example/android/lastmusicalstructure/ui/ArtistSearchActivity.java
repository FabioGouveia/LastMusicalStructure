package com.example.android.lastmusicalstructure.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.artist.ArtistCardAdapter;
import com.example.android.lastmusicalstructure.artist.ArtistLoader;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;

import java.util.ArrayList;
import java.util.List;

public class ArtistSearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Artist>> {

    private final static String SEARCH_PAGE_NUMBER_KEY = "search_page_number";
    private static int pageNumber = 1;
    private ArtistCardAdapter artistCardAdapter = null;
    private Button artistSearchPreviousPageButton;
    private ConnectivityManager connManager;
    private LinearLayout artistSearchPageTab;
    private LinearLayout emptyRecyclerView;
    private NestedScrollView artistCardNestedScrollView;
    private ProgressBar artistLoadingProgressBar;
    private TextView emptyRecyclerViewTextView;
    private TextView pageNumberView;
    private Folder folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);

        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(Folder.class.getClassLoader());
            folder = savedInstanceState.getParcelable(Folder.KEY);
        } else {

            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                bundle.setClassLoader(Folder.class.getClassLoader());
                folder = bundle.getParcelable(Folder.KEY);
            } else {
                Toast.makeText(this, "Something wrong with folder data..!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        artistSearchPageTab = findViewById(R.id.artist_search_tab_pages_layout);
        artistSearchPreviousPageButton = findViewById(R.id.artist_search_tab_previous_page);
        Button artistSearchNextPageButton = findViewById(R.id.artist_search_tab_next_page);
        pageNumberView = findViewById(R.id.artist_search_page_number_display_view);
        artistLoadingProgressBar = findViewById(R.id.artist_loading_progress_bar);
        artistCardNestedScrollView = findViewById(R.id.artist_list_nested_scroll_view);
        RecyclerView artistListRecyclerView = findViewById(R.id.artist_list);
        emptyRecyclerView = findViewById(R.id.empty_view);
        emptyRecyclerViewTextView = findViewById(R.id.empty_view_text_view);
        Button emptyRecyclerViewButton = findViewById(R.id.empty_view_button);

        artistSearchPreviousPageButton.setOnClickListener(new SearchActivityOnClickListener());
        artistSearchNextPageButton.setOnClickListener(new SearchActivityOnClickListener());
        emptyRecyclerViewButton.setOnClickListener(new SearchActivityOnClickListener());

        if (pageNumber > 1) {
            artistSearchPreviousPageButton.setEnabled(true);
        }

        Toolbar toolBar = findViewById(R.id.artist_selection_toolbar);
        toolBar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add an artist");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }

        artistCardAdapter = new ArtistCardAdapter(this, folder);

        artistListRecyclerView.setLayoutManager(layoutManager);
        artistListRecyclerView.setAdapter(artistCardAdapter);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        runNetworkActivity();
        displayPageNumber();
    }

    @Override
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        pageNumberView.setVisibility(View.GONE);
        artistLoadingProgressBar.setVisibility(View.VISIBLE);
        artistCardNestedScrollView.setVisibility(View.INVISIBLE);
        return new ArtistLoader(ArtistSearchActivity.this, args.getInt(SEARCH_PAGE_NUMBER_KEY));
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {

        artistLoadingProgressBar.setVisibility(View.GONE);

        if (data.isEmpty()) {
            artistCardNestedScrollView.setVisibility(View.GONE);
            emptyRecyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyRecyclerView.setVisibility(View.GONE);
            artistSearchPageTab.setVisibility(View.VISIBLE);
            artistCardNestedScrollView.setVisibility(View.VISIBLE);
            pageNumberView.setVisibility(View.VISIBLE);

            artistCardAdapter.setArtist(data);
            displayPageNumber();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {
        artistCardAdapter.setArtist(new ArrayList<Artist>());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBackToFoldersActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Folder.KEY, folder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        goBackToFoldersActivity();
    }

    private void goBackToFoldersActivity() {
        Intent folderIntent = new Intent(ArtistSearchActivity.this, FolderActivity.class);
        folderIntent.putExtra(Folder.KEY, folder);
        folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.FOLDER_DISPLAY_MODE);
        startActivity(folderIntent);

        overridePendingTransition(R.anim.enter_from_left_animation, R.anim.exit_to_right_animation);
    }

    private void runNetworkActivity() {

        NetworkInfo networkInfo = null;

        if (connManager != null) {
            networkInfo = connManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {

            Bundle args = new Bundle();
            args.putInt(SEARCH_PAGE_NUMBER_KEY, pageNumber);

            getLoaderManager().initLoader(pageNumber, args, this);

        } else {
            artistLoadingProgressBar.setVisibility(View.GONE);
            emptyRecyclerViewTextView.setText(getString(R.string.no_internet_connection));
            emptyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void displayPageNumber() {
        pageNumberView.setText(getString(R.string.artist_search_page_number, pageNumber));
    }

    private class SearchActivityOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.artist_search_tab_previous_page:
                    if (pageNumber > 1) {
                        pageNumber--;
                        if (pageNumber == 1) {
                            artistSearchPreviousPageButton.setEnabled(false);
                        }

                        runNetworkActivity();
                        displayPageNumber();
                    }
                    break;
                case R.id.artist_search_tab_next_page:
                    pageNumber++;
                    artistSearchPreviousPageButton.setEnabled(true);
                    runNetworkActivity();
                    displayPageNumber();
                    break;
                case R.id.empty_view_button:
                    runNetworkActivity();
                    break;
            }
        }
    }
}
