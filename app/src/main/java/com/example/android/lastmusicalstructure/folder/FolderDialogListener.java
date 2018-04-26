package com.example.android.lastmusicalstructure.folder;

import com.example.android.lastmusicalstructure.utils.DialogListener;

public interface FolderDialogListener extends DialogListener {
    void onFolderEmptyName();

    void onFolderNameAlreadyExists();

    void onFolderNameUpdated(String newFolderName);

    void onFolderIconUpdated();

    void onFolderCreated(String folderName);

    void onFolderDeleted(String folderName);
}
