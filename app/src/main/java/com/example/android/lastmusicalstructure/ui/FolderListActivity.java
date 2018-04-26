package com.example.android.lastmusicalstructure.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.folder.FolderDialogListener;
import com.example.android.lastmusicalstructure.folder.UserFolderCardAdapter;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.utils.DialogUtils;
import com.example.android.lastmusicalstructure.utils.QueryUtils;

import java.util.List;

public class FolderListActivity extends AppCompatActivity {

    private DialogUtils createNewFolderDialog;
    private UserFolderCardAdapter folderCardAdapter;
    private FloatingActionButton createFolderFAB;
    private TextView folderListEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        Toolbar toolBar = findViewById(R.id.user_folder_toolbar);
        toolBar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.user_folder_collapsing_toolbar_layout);
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryText));
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        }

        createFolderFAB = findViewById(R.id.create_new_folder_fab);
        RecyclerView recyclerView = findViewById(R.id.user_folder_list);
        folderListEmptyView = findViewById(R.id.folder_list_empty_view);

        setSupportActionBar(toolBar);

        createFolderFAB.setOnClickListener(new CreateNewFolderOnClickListener());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }

        folderCardAdapter = new UserFolderCardAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(folderCardAdapter);
        displayFolders();

    }

    @Override
    protected void onPause() {
        if (createNewFolderDialog != null) createNewFolderDialog.dismiss();

        super.onPause();
    }

    private void displayFolders() {

        List<Folder> folders = QueryUtils.getFolders(this);

        if (folders.isEmpty()) {
            createFolderFAB.setVisibility(View.GONE);
            folderListEmptyView.setVisibility(View.VISIBLE);
            folderListEmptyView.setOnClickListener(new FolderListActivity.CreateNewFolderOnClickListener());
        } else {
            createFolderFAB.setVisibility(View.VISIBLE);
            folderListEmptyView.setVisibility(View.GONE);
            folderCardAdapter.setFolders(folders);

        }
    }

    private class CreateNewFolderOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            createNewFolderDialog = new DialogUtils();
            createNewFolderDialog.setDialogType(DialogUtils.DIALOG_TYPE_NEW_FOLDER);
            createNewFolderDialog.addDialogListener(new FolderDialogListener() {
                @Override
                public void onFolderNameAlreadyExists() {
                }

                @Override
                public void onFolderEmptyName() {
                    Toast.makeText(getApplicationContext(), R.string.err_folder_name_empty, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFolderNameUpdated(String newFolderName) {
                }

                @Override
                public void onFolderIconUpdated() {
                }

                @Override
                public void onFolderCreated(String folderName) {
                    Context context = getApplicationContext();
                    displayFolders();
                    Toast.makeText(context, context.getResources().getString(R.string.new_folder_successfully_created, folderName), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFolderDeleted(String folderName) {
                }
            });
            createNewFolderDialog.show(getSupportFragmentManager(), null);
        }
    }


}
