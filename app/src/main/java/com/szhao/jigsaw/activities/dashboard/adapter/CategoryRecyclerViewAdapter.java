package com.szhao.jigsaw.activities.dashboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crash.FirebaseCrash;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.vh.CategoryViewHolder;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.PointSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 8/6/2017.
 */

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<String> categories;
    private ItemSelectListener listener;
    private String startedPuzzle;
    private int numStartedPuzzles;
    private int numCategories;

    public CategoryRecyclerViewAdapter(Context context, String startedPuzzle, int numStartedPuzzles){
        this.context = context;
        categories = new ArrayList<>();
        if (startedPuzzle != null) {
            categories.add(context.getString(R.string.started));
            this.startedPuzzle = startedPuzzle;
            this.numStartedPuzzles = numStartedPuzzles;
        }

        String[] puzzleCategories = null;
        try {
            puzzleCategories = context.getAssets().list(Constants.ALL_PUZZLES_DIR);
        } catch (IOException e) {
            Log.d("preload", e.getMessage());
        }
        Collections.addAll(categories, puzzleCategories);
        numCategories = categories.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_category_image, parent, false);
        final CategoryViewHolder vh = new CategoryViewHolder(context, v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(categories.get(vh.getAdapterPosition()), 2);
                }
            }
        });
        return vh;
    }

    public void setDLPuzzle() {
        File dir = context.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE);
        if (!dir.exists())
            return;
        File[] dlCategories = dir.listFiles();
        if (dlCategories != null)
            for (File category : dlCategories) {
                if (!categories.contains(category.getName()))
                    categories.add(category.getName());
            }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder vh = (CategoryViewHolder)holder;
        //Load started puzzles
        if (categories.contains(context.getString(R.string.started)) && position == 0){
            vh.setCount(numStartedPuzzles, numStartedPuzzles);
            vh.setDescription(context.getString(R.string.started));
            vh.setImage(startedPuzzle);
        } else {
            String title = categories.get(position);
            vh.setDescription(title);
            if (position < numCategories) {
                //Load initial puzzles
                try {
                    int numPuzzles = context.getAssets().list(categories.get(position)).length;
                    vh.setCount(numPuzzles, numPuzzles);
                    String filepath = "android_asset/" + categories.get(position) + "/" + context.getAssets().list(categories.get(position))[0];
                    vh.setImage(filepath);
                } catch (IOException e) {
                    FirebaseCrash.logcat(Log.ERROR, "Load Puzzles", "Error loading puzzles from assets");
                    FirebaseCrash.report(e);
                }
            } else {
                //Load downloaded puzzles
                File dir = new File(context.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE), title);
                File[] dirFiles = dir.listFiles();
                if (dirFiles.length > 0) {
                    int numPuzzles = dirFiles.length;
                    int numAvailablePuzzles = PointSystem.getInstance().getNumAvailablePuzzlesByCategory(title);
                    vh.setCount(numAvailablePuzzles, numPuzzles);
                    String filePath = dirFiles[0].getAbsolutePath();
                    vh.setImage(filePath);
                    PointSystem.getInstance().setCategoryViewHolder(vh);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setListener(ItemSelectListener listener){
        this.listener = listener;
    }

}

