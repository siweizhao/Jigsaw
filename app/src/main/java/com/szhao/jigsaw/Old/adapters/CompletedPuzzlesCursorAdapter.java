package com.szhao.jigsaw.Old.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.Old.vh.CompletedPuzzleViewHolder;

/**
 * Created by Owner on 6/1/2017.
 */

public class CompletedPuzzlesCursorAdapter extends CursorRecyclerViewAdapter{

    public CompletedPuzzlesCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.completed_puzzle_layout, parent, false);
        return new CompletedPuzzleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ((CompletedPuzzleViewHolder)viewHolder).setData(cursor, mContext);
    }


}
