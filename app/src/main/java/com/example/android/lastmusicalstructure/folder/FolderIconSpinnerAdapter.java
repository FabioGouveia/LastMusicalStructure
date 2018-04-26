package com.example.android.lastmusicalstructure.folder;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.lastmusicalstructure.R;


public class FolderIconSpinnerAdapter extends ArrayAdapter<String> {

    private int resourceIconArray;

    public FolderIconSpinnerAdapter(Context context, int resourceIconArray, String[] optionStringArray) {
        super(context, R.layout.row_spinner_new_folder_icon, optionStringArray);
        this.resourceIconArray = resourceIconArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spinner_new_folder_icon, parent, false);
        }

        String optionText = getItem(position);
        TypedArray newFolderIconArray = getContext().getResources().obtainTypedArray(resourceIconArray);

        ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(newFolderIconArray.getResourceId(position, R.drawable.ic_favorite)), null, null, null);
        ((TextView) convertView).setText(optionText);
        convertView.setTag(newFolderIconArray.getResourceId(position, 0));

        newFolderIconArray.recycle();

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
