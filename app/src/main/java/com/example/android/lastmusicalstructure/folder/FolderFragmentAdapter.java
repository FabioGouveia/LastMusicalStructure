package com.example.android.lastmusicalstructure.folder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.ui.FolderFragment;

import java.util.List;

public class FolderFragmentAdapter extends FragmentStatePagerAdapter {

    private Folder folder;
    private List<FolderItem> items;

    public FolderFragmentAdapter(FragmentManager fm, Folder folder, List<FolderItem> items) {
        super(fm);
        this.items = items;
        this.folder = folder;
    }

    @Override
    public Fragment getItem(int position) {
        return FolderFragment.newInstance(folder, items.get(position));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).getName();
    }
}
