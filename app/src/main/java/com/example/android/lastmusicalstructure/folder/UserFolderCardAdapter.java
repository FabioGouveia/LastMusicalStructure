package com.example.android.lastmusicalstructure.folder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.lastmusicalstructure.R;
import com.example.android.lastmusicalstructure.model.Folder;
import com.example.android.lastmusicalstructure.ui.FolderActivity;

import java.util.ArrayList;
import java.util.List;


public class UserFolderCardAdapter extends RecyclerView.Adapter<UserFolderCardAdapter.ViewHolder> {

    private Context context;
    private List<Folder> folders;

    public UserFolderCardAdapter(Context context) {
        this.context = context;
        this.folders = new ArrayList<>();
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    @Override
    public UserFolderCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(UserFolderCardAdapter.ViewHolder holder, int position) {

        final Folder folder = folders.get(position);

        holder.iconTextView.setText(folder.getName());
        holder.iconTextView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(folder.getIcon()), null, null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent folderIntent = new Intent(context, FolderActivity.class);
                folderIntent.putExtra(Folder.KEY, folder);
                folderIntent.putExtra(Folder.DISPLAY_MODE_KEY, Folder.FOLDER_DISPLAY_MODE);
                context.startActivity(folderIntent);

                ((Activity) context).overridePendingTransition(R.anim.enter_from_right_animation, R.anim.exit_to_left_animation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView iconTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            iconTextView = itemView.findViewById(R.id.user_folder_icon);
        }
    }
}
