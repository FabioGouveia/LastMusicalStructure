package com.example.android.lastmusicalstructure.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.artist.ArtistDialogListener;
import com.example.android.lastmusicalstructure.data.UserContract.AlbumEntry;
import com.example.android.lastmusicalstructure.data.UserContract.ArtistEntry;
import com.example.android.lastmusicalstructure.data.UserContract.FolderEntry;
import com.example.android.lastmusicalstructure.data.UserContract.TrackEntry;
import com.example.android.lastmusicalstructure.folder.FolderDialogListener;
import com.example.android.lastmusicalstructure.folder.FolderIconSpinnerAdapter;
import com.example.android.lastmusicalstructure.folder.FolderItem;
import com.example.android.lastmusicalstructure.model.Album;
import com.example.android.lastmusicalstructure.model.Artist;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.model.Track;

import java.util.List;


public class DialogUtils extends DialogFragment {

    public static final int DIALOG_TYPE_NEW_FOLDER = 4001;
    public static final int DIALOG_TYPE_UPDATE_FOLDER_NAME = 4002;
    public static final int DIALOG_TYPE_UPDATE_FOLDER_ICON = 4003;
    public static final int DIALOG_TYPE_DELETE_FOLDER = 4004;
    public static final int DIALOG_TYPE_SAVE_ARTIST = 4005;
    public static final int DIALOG_TYPE_DELETE_ARTIST = 4006;
    private static final String LOG_TAG = DialogUtils.class.getSimpleName();
    private static Album album;
    private Context context;
    private ContentResolver contentResolver;
    private Folder folder;
    private Artist artist;
    private int dialogType;
    private int folderIconResourceId;
    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    @NonNull
    private Dialog createDialog() {

        AlertDialog.Builder builder = null;

        try {
            builder = buildDialog();
        } catch (DialogException e) {
            e.printStackTrace();
        }

        return builder != null ? builder.create() : createDialog();
    }

    private AlertDialog.Builder buildDialog() throws DialogException {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (dialogType != 0) {
            switch (dialogType) {
                case DIALOG_TYPE_NEW_FOLDER:
                    return createNewFolderDialogBuilder(builder);
                case DIALOG_TYPE_UPDATE_FOLDER_NAME:
                    return createUpdateFolderNameDialogBuilder(builder);
                case DIALOG_TYPE_UPDATE_FOLDER_ICON:
                    return createUpdateFolderIconDialogBuilder(builder);
                case DIALOG_TYPE_DELETE_FOLDER:
                    return createDeleteFolderDialogBuilder(builder);
                case DIALOG_TYPE_DELETE_ARTIST:
                    return createDeleteArtistDialogBuilder(builder);
                case DIALOG_TYPE_SAVE_ARTIST:
                    return createSaveArtistDialogBuilder(builder);

                default:
                    throw new DialogException("Incompatible type!");
            }
        } else {
            throw new DialogException("Type not found!");
        }
    }

    private AlertDialog.Builder createDeleteFolderDialogBuilder(AlertDialog.Builder builder) {
        builder.setMessage(R.string.dialog_delete_folder_confirmation_message)
                .setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Bundle args = getArguments();
                        context = getContext();
                        if (args != null && context != null) {
                            args.setClassLoader(Folder.class.getClassLoader());
                            folder = args.getParcelable(Folder.KEY);
                            contentResolver = context.getContentResolver();

                            if (folder != null) {

                                long folderId = folder.getId();

                                List<FolderItem> artistItems = QueryUtils.getOfflineArtists(context, folderId);

                                for (FolderItem artistItem : artistItems) {

                                    long artistId = artistItem.getId();

                                    List<FolderItem> albumItems = QueryUtils.getOfflineArtistAlbums(context, artistId);

                                    for (FolderItem albumItem : albumItems) {

                                        long albumId = albumItem.getId();

                                        int deleteTrackResult = contentResolver.delete(ContentUris.withAppendedId(TrackEntry.CONTENT_URI, albumId), null, null);

                                        if (deleteTrackResult == 1) {
                                            FileUtils.deleteArtistAlbumImage(context, artistId, albumId);
                                        } else {
                                            Log.e(LOG_TAG, "Error deleting the album tracks!");
                                        }
                                    }

                                    int deleteAlbumResult = contentResolver.delete(ContentUris.withAppendedId(AlbumEntry.CONTENT_URI, artistId), null, null);

                                    if (deleteAlbumResult == 1) {
                                        int deleteArtistResult = contentResolver.delete(ContentUris.withAppendedId(ArtistEntry.CONTENT_URI_ID, artistId), null, null);

                                        if (deleteArtistResult == 1) {
                                            FileUtils.deleteArtistImage(context, artistId);
                                        } else {
                                            Log.e(LOG_TAG, "Error deleting the folder artists!");
                                        }
                                    } else {
                                        Log.e(LOG_TAG, "Error deleting the artist albums!");
                                    }
                                }

                                int deleteFolderResult = contentResolver.delete(ContentUris.withAppendedId(FolderEntry.CONTENT_URI, folderId), null, null);

                                if (deleteFolderResult == 1) {
                                    ((FolderDialogListener) listener).onFolderDeleted(folder.getName());
                                } else {
                                    Log.e(LOG_TAG, "Error deleting the folder!");
                                }
                            } else {
                                Log.e(LOG_TAG, "Null folder object!");
                            }
                        } else {
                            Log.e(LOG_TAG, "Error creating folder delete dialog!");
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogUtils.this.getDialog().cancel();
                    }
                });

        return builder;
    }

    private AlertDialog.Builder createNewFolderDialogBuilder(AlertDialog.Builder builder) {

        Activity activity = getActivity();
        context = getContext();
        if (activity != null && context != null) {
            contentResolver = context.getContentResolver();

            ViewGroup dialogRootView = activity.findViewById(R.id.insert_new_folder_dialog_root_view);
            View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_create_new_folder, dialogRootView, false);

            final TextInputEditText folderNameInputText = dialogView.findViewById(R.id.new_folder_name_input_field);

            Spinner folderIconSpinner = dialogView.findViewById(R.id.create_new_folder_icon_spinner);

            FolderIconSpinnerAdapter folderIconSpinnerAdapter = new FolderIconSpinnerAdapter(context, R.array.folder_icon, getResources().getStringArray(R.array.folder_icon_text));
            folderIconSpinner.setAdapter(folderIconSpinnerAdapter);
            folderIconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    folderIconResourceId = (int) view.getTag();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            folderNameInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    boolean handled = false;

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String folderName = folderNameInputText.getText().toString().trim();
                        if (!folderName.isEmpty()) {

                            ContentValues values = new ContentValues();
                            values.put(FolderEntry.COLUMN_FOLDER_ICON, folderIconResourceId);
                            values.put(FolderEntry.COLUMN_FOLDER_NAME, folderName);

                            Uri uri = contentResolver.insert(FolderEntry.CONTENT_URI, values);

                            if (ContentUris.parseId(uri) != -1) {
                                DialogUtils.this.getDialog().dismiss();
                                ((FolderDialogListener) listener).onFolderCreated(folderName);
                            }
                        } else {
                            ((FolderDialogListener) listener).onFolderEmptyName();
                        }

                        handled = true;
                    }

                    return handled;
                }
            });

            builder.setTitle(getResources().getString(R.string.create_new_folder_dialog_title))
                    .setView(dialogView)
                    .setPositiveButton(getResources().getString(R.string.dialog_save_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String folderName = folderNameInputText.getText().toString().trim();
                            if (!folderName.isEmpty()) {

                                ContentValues values = new ContentValues();
                                values.put(FolderEntry.COLUMN_FOLDER_ICON, folderIconResourceId);
                                values.put(FolderEntry.COLUMN_FOLDER_NAME, folderName);

                                Uri uri = contentResolver.insert(FolderEntry.CONTENT_URI, values);

                                if (ContentUris.parseId(uri) != -1) {
                                    ((FolderDialogListener) listener).onFolderCreated(folderName);
                                }
                            } else {
                                ((FolderDialogListener) listener).onFolderEmptyName();
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogUtils.this.getDialog().dismiss();
                        }
                    });
        }

        return builder;
    }

    private AlertDialog.Builder createSaveArtistDialogBuilder(AlertDialog.Builder builder) {

        Bundle args = getArguments();
        context = getContext();

        if (args != null && context != null) {
            args.setClassLoader(Artist.class.getClassLoader());
            artist = args.getParcelable(Artist.KEY);
            args.setClassLoader(Folder.class.getClassLoader());
            folder = args.getParcelable(Folder.KEY);
            contentResolver = context.getContentResolver();

            if (artist != null && folder != null) {
                builder.setMessage(getString(R.string.dialog_save_artist_confirmation_message, artist.getName(), folder.getName()))
                        .setPositiveButton(R.string.dialog_save_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String artistName = artist.getName();

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ArtistEntry.COLUMN_ARTIST_NAME, artistName);
                                contentValues.put(ArtistEntry.COLUMN_ARTIST_BIOGRAPHY, artist.getInfo());
                                contentValues.put(ArtistEntry.COLUMN_FOLDER_ID, folder.getId());

                                Uri artistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, contentValues);

                                if (artistUri == null || ContentUris.parseId(artistUri) == -1) {
                                    Log.e(LOG_TAG, "Error saving the artist!");
                                } else {
                                    final long artistId = ContentUris.parseId(artistUri);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FileUtils.saveArtistImage(context, HttpUtils.makeHttpRequest(artist.getImageURL()), artistId);
                                        }
                                    }).start();

                                    for (Album album : artist.getAlbums()) {

                                        DialogUtils.album = album;
                                        ContentValues albumContentValues = new ContentValues();
                                        albumContentValues.put(AlbumEntry.COLUMN_ALBUM_NAME, album.getName());
                                        albumContentValues.put(AlbumEntry.COLUMN_ARTIST_ID, artistId);

                                        Uri albumUri = contentResolver.insert(AlbumEntry.CONTENT_URI, albumContentValues);

                                        if (albumUri == null || ContentUris.parseId(albumUri) == -1) {
                                            Log.e(LOG_TAG, "Error saving artist albums!");
                                        } else {
                                            final long albumId = ContentUris.parseId(albumUri);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    FileUtils.saveArtistAlbumImage(context, HttpUtils.makeHttpRequest(DialogUtils.album.getImageURL()), artistId, albumId);
                                                }
                                            }).start();

                                            for (Track track : album.getTracks()) {
                                                ContentValues trackContentValues = new ContentValues();
                                                trackContentValues.put(TrackEntry.COLUMN_TRACK_NAME, track.getName());
                                                trackContentValues.put(TrackEntry.COLUMN_TRACK_DURATION, track.getDuration());
                                                trackContentValues.put(TrackEntry.COLUMN_ALBUM_ID, albumId);

                                                Uri trackUri = contentResolver.insert(TrackEntry.CONTENT_URI, trackContentValues);

                                                if (trackUri == null || ContentUris.parseId(trackUri) == -1) {
                                                    Log.e(LOG_TAG, "Error saving artist tracks!");
                                                }
                                            }
                                        }

                                    }
                                    ((ArtistDialogListener) listener).onArtistSaved(artistName);
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogUtils.this.getDialog().cancel();
                            }
                        });
            } else {
                Log.e(LOG_TAG, "Null artist or folder object!");
            }
        } else {
            Log.e(LOG_TAG, "Error creating save artist dialog!");
        }

        return builder;
    }

    private AlertDialog.Builder createUpdateFolderNameDialogBuilder(AlertDialog.Builder builder) {

        Bundle args = getArguments();
        context = getContext();
        if (args != null && context != null) {
            args.setClassLoader(Folder.class.getClassLoader());
            folder = args.getParcelable(Folder.KEY);
            contentResolver = context.getContentResolver();

            Activity activity = getActivity();
            if (folder != null && activity != null) {
                ViewGroup dialogRootView = activity.findViewById(R.id.rename_folder_dialog_root_view);
                View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_change_folder_name, dialogRootView, false);

                final TextInputEditText folderNameInputText = dialogView.findViewById(R.id.new_folder_name_input_field);

                builder.setTitle(getResources().getString(R.string.rename_folder_dialog_title))
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.dialog_save_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String folderName = folderNameInputText.getText().toString().trim();
                                if (!folderName.isEmpty()) {

                                    if (!QueryUtils.folderNameAlreadyExists(context, folderName)) {
                                        ContentValues values = new ContentValues();
                                        values.put(FolderEntry.COLUMN_FOLDER_NAME, folderName);

                                        int numberOfRowsUpdated = contentResolver.update(ContentUris.withAppendedId(FolderEntry.CONTENT_URI, folder.getId()), values, null, null);

                                        if (numberOfRowsUpdated == 1) {
                                            ((FolderDialogListener) listener).onFolderNameUpdated(folderName);
                                        }
                                    } else {
                                        ((FolderDialogListener) listener).onFolderNameAlreadyExists();
                                    }

                                } else {
                                    ((FolderDialogListener) listener).onFolderEmptyName();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogUtils.this.getDialog().dismiss();
                            }
                        });
            } else {
                Log.e(LOG_TAG, "Null activity or folder object!");
            }
        } else {
            Log.e(LOG_TAG, "Error creating update folder name dialog!");
        }

        return builder;
    }

    private AlertDialog.Builder createUpdateFolderIconDialogBuilder(AlertDialog.Builder builder) {

        Bundle args = getArguments();
        context = getContext();

        if (args != null && context != null) {
            args.setClassLoader(Folder.class.getClassLoader());
            folder = args.getParcelable(Folder.KEY);
            contentResolver = context.getContentResolver();

            Activity activity = getActivity();
            if (folder != null && activity != null) {
                ViewGroup dialogRootView = activity.findViewById(R.id.change_folder_icon_dialog_root_view);
                View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_change_folder_icon, dialogRootView, false);

                Spinner folderIconSpinner = dialogView.findViewById(R.id.new_folder_icon_spinner);

                FolderIconSpinnerAdapter folderIconSpinnerAdapter = new FolderIconSpinnerAdapter(context, R.array.folder_icon, getResources().getStringArray(R.array.folder_icon_text));
                folderIconSpinner.setAdapter(folderIconSpinnerAdapter);
                folderIconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        folderIconResourceId = (int) view.getTag();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                builder.setTitle(getResources().getString(R.string.change_folder_icon_dialog_title))
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.dialog_save_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues values = new ContentValues();
                                values.put(FolderEntry.COLUMN_FOLDER_ICON, folderIconResourceId);

                                int numberOfRowsUpdated = contentResolver.update(ContentUris.withAppendedId(FolderEntry.CONTENT_URI, folder.getId()), values, null, null);

                                if (numberOfRowsUpdated == 1) {
                                    ((FolderDialogListener) listener).onFolderIconUpdated();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogUtils.this.getDialog().dismiss();
                            }
                        });
            } else {
                Log.e(LOG_TAG, "Null activity or folder object!");
            }
        } else {
            Log.e(LOG_TAG, "Error creating update folder icon dialog!");
        }

        return builder;
    }

    private AlertDialog.Builder createDeleteArtistDialogBuilder(AlertDialog.Builder builder) {

        Bundle args = getArguments();
        context = getContext();

        if (args != null && context != null) {
            args.setClassLoader(Folder.class.getClassLoader());
            folder = args.getParcelable(Folder.KEY);
            args.setClassLoader(Artist.class.getClassLoader());
            artist = args.getParcelable(Artist.KEY);
            contentResolver = context.getContentResolver();

            if (folder != null && artist != null) {
                builder.setMessage(getString(R.string.dialog_delete_artist_confirmation_message, folder.getName()))
                        .setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                long artistId = artist.getId();

                                List<FolderItem> albumItems = QueryUtils.getOfflineArtistAlbums(context, artistId);

                                for (FolderItem albumItem : albumItems) {

                                    long albumId = albumItem.getId();

                                    int deleteTrackResult = contentResolver.delete(ContentUris.withAppendedId(TrackEntry.CONTENT_URI, albumId), null, null);

                                    if (deleteTrackResult == 1) {
                                        FileUtils.deleteArtistAlbumImage(context, artistId, albumId);
                                    } else {
                                        Log.e(LOG_TAG, "Error deleting the album tracks!");
                                    }
                                }

                                int deleteAlbumResult = contentResolver.delete(ContentUris.withAppendedId(AlbumEntry.CONTENT_URI, artistId), null, null);

                                if (deleteAlbumResult == 1) {
                                    int deleteArtistResult = contentResolver.delete(ContentUris.withAppendedId(ArtistEntry.CONTENT_URI_ID, artistId), null, null);

                                    if (deleteArtistResult == 1) {
                                        FileUtils.deleteArtistImage(context, artistId);
                                        ((ArtistDialogListener) listener).onArtistDeleted(artist.getName());
                                    } else {
                                        Log.e(LOG_TAG, "Error deleting the artist!");
                                    }
                                } else {
                                    Log.e(LOG_TAG, "Error deleting the artist albums!");
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogUtils.this.getDialog().cancel();
                            }
                        });
            } else {
                Log.e(LOG_TAG, "Null artist or folder object!");
            }
        } else {
            Log.e(LOG_TAG, "Error creating delete artist dialog!");
        }

        return builder;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public void addDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    private class DialogException extends Exception {
        DialogException(String msg) {
            super(msg);
        }
    }
}
