package com.tr4android.appcompatextension;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tr4android.support.extension.widget.CircleImageView;

/**
 * Created by ThomasR on 02.09.2015.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private int[] mIcons = new int[]{R.drawable.ic_folder_black_24dp, R.drawable.ic_folder_black_24dp, R.drawable.ic_folder_black_24dp, R.drawable.ic_folder_black_24dp, R.drawable.ic_insert_chart_black_24dp, R.drawable.ic_grid_on_black_24dp, R.drawable.ic_insert_drive_file_black_24dp};
    private int[] mColors = new int[]{R.color.material_indigo, R.color.material_indigo, R.color.material_indigo, R.color.material_indigo, R.color.material_yellow, R.color.material_green, R.color.material_blue};
    private String[] mFileNames = new String[]{"Big Buck Bunny", "Caminandes", "Sintel", "Trailers", "Movies Presentation", "Movies Expense Summary", "Movie Posters"};
    private String[] mFileInfos = new String[]{"Jan 6, 2015", "Jan 9, 2015", "Jan 17, 2015", "Jan 28, 2015", "Jan 20, 2015", "Jan 20, 2015", "Jan 20, 2015"};

    private Context mContext; // for resolving colors

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mIconImageView;
        public TextView mNameTextView;
        public TextView mInfoTextView;

        public ViewHolder(View v) {
            super(v);
            mIconImageView = (CircleImageView) v.findViewById(R.id.icon);
            mNameTextView = (TextView) v.findViewById(R.id.name);
            mInfoTextView = (TextView) v.findViewById(R.id.info);
        }
    }

    public FileAdapter(Context context) {
        mContext = context;
    }

    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_file, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIconImageView.setPlaceholder(mIcons[position], mContext.getResources().getColor(mColors[position]));
        holder.mNameTextView.setText(mFileNames[position]);
        holder.mInfoTextView.setText(mFileInfos[position]);
    }

    @Override
    public int getItemCount() {
        return mFileNames.length;
    }
}
