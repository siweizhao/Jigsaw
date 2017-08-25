package com.szhao.jigsaw.activities.Dashboard.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.Dashboard.VH.CategoryViewHolder;
import com.szhao.jigsaw.activities.Dashboard.VH.ContentViewHolder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    ArrayList<String> puzzles;

    public ContentRecyclerViewAdapter(Context context){
        this.context = context;
        puzzles = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_content_image, parent, false);
        ContentViewHolder vh = new ContentViewHolder(context, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentViewHolder vh = (ContentViewHolder)holder;
        vh.setPuzzleImage(puzzles.get(position));
    }

    public void setPuzzles(String category){
        puzzles.clear();
        String[] puzzleFilePaths = null;
        try {
            puzzleFilePaths = context.getAssets().list(category);
        } catch (IOException e){
            Log.d("set puzzles", "Error getting puzzles from assets " + e.getStackTrace());
        }
        for (String path : puzzleFilePaths){
            puzzles.add("file:///android_asset/" + category + "/" + path);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return puzzles.size();
    }
}
