package com.szhao.jigsaw.activities.dashboard.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.vh.ContentViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> puzzles;
    private ItemSelectListener listener;
    private Cursor startedPuzzlesCursor;
    ArrayList<Integer> startedDifficulties;
    ArrayList<String> startedPositions;

    public ContentRecyclerViewAdapter(Context context, Cursor cursor){
        this.context = context;
        this.startedPuzzlesCursor = cursor;
        puzzles = new ArrayList<>();
        startedDifficulties = new ArrayList<>();
        startedPositions = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_content_image, parent, false);
        final ContentViewHolder vh = new ContentViewHolder(context, v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    int currAdapterPosition = vh.getAdapterPosition();
                    int difficulty =  startedDifficulties.size() == 0 ? 2 : startedDifficulties.get(currAdapterPosition);
                    String positions = startedPositions.size() == 0 ? "" : startedPositions.get(currAdapterPosition);
                    listener.onClick(puzzles.get(currAdapterPosition), difficulty, positions);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentViewHolder vh = (ContentViewHolder)holder;
        vh.setPuzzleImage(puzzles.get(position));
    }

    public void setPuzzles(String category){
        puzzles.clear();
        startedDifficulties.clear();
        startedPositions.clear();
        if (category.equals("Started")){
            startedPuzzlesCursor.move(-1);
            while(startedPuzzlesCursor.moveToNext()){
                puzzles.add(startedPuzzlesCursor.getString(startedPuzzlesCursor.getColumnIndex("PUZZLE")));
                startedDifficulties.add(startedPuzzlesCursor.getInt(startedPuzzlesCursor.getColumnIndex("DIFFICULTY")));
                startedPositions.add(startedPuzzlesCursor.getString(startedPuzzlesCursor.getColumnIndex("POSITIONS")));
            }
            Collections.reverse(puzzles);
        } else {
            String[] puzzleFilePaths = null;
            try {
                puzzleFilePaths = context.getAssets().list(category);
            } catch (IOException e) {
                Log.d("set puzzles", "Error getting puzzles from assets " + e.getStackTrace());
            }
            for (String path : puzzleFilePaths) {
                puzzles.add("android_asset/" + category + "/" + path);
            }
        }
        notifyDataSetChanged();
    }

    public void setCustomPuzzles(){
        puzzles.clear();
        File dir = context.getDir("custom_puzzles", Context.MODE_PRIVATE);
        File[] puzzleFilePaths = dir.listFiles();
        for (File filePath : puzzleFilePaths){
            puzzles.add(filePath.getAbsolutePath());
        }
        Collections.reverse(puzzles);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return puzzles.size();
    }

    public void setListener(ItemSelectListener listener){
        this.listener = listener;
    }
}
