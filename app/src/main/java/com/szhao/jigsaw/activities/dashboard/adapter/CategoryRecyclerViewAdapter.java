package com.szhao.jigsaw.activities.dashboard.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.vh.CategoryViewHolder;
import com.szhao.jigsaw.db.PuzzleContentProvider;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Owner on 8/6/2017.
 */

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<String> categories;
    private ItemSelectListener listener;
    String startedPuzzle;
    int numStartedPuzzles;

    public CategoryRecyclerViewAdapter(Context context, String startedPuzzle, int numStartedPuzzles){
        this.context = context;
        categories = new ArrayList<>();
        if (startedPuzzle != null) {
            categories.add("Started");
            this.startedPuzzle = startedPuzzle;
            this.numStartedPuzzles = numStartedPuzzles;
        }
        categories.add("Animals");
        categories.add("Landscapes");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_category_image, parent, false);
        final CategoryViewHolder vh = new CategoryViewHolder(context, v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(categories.get(vh.getAdapterPosition()),2,"");
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder vh = (CategoryViewHolder)holder;
        if (categories.contains("Started") && position == 0){
            vh.setCount(numStartedPuzzles, numStartedPuzzles);
            vh.setDescription("Started");
            vh.setImage(startedPuzzle);
        } else {
            vh.setDescription(categories.get(position));
            try {
                int numPuzzles = context.getAssets().list(categories.get(position)).length;
                vh.setCount(numPuzzles, numPuzzles);
                String filepath = "android_asset/" + categories.get(position) + "/" + context.getAssets().list(categories.get(position))[0];
                vh.setImage(filepath);
            } catch (IOException e) {
                Log.d("CategoryVH", "Error accessing assets folder " + e.getStackTrace());
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

