package com.szhao.jigsaw.Old.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.Old.vh.SelectPuzzleViewHolder;

/**
 * Created by Owner on 7/11/2017.
 */

public class CustomPuzzlesCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter{

    private onPuzzleSelectedListener listener;
    public interface onPuzzleSelectedListener{
        void onClick(Bitmap bitmap, int id);
    }

    public CustomPuzzlesCursorRecyclerViewAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.cv_puzzle_select_layout, parent, false);
        return new SelectPuzzleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor){
        final SelectPuzzleViewHolder selectPuzzleViewHolder = (SelectPuzzleViewHolder)viewHolder;
        selectPuzzleViewHolder.setData(cursor, mContext);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (listener != null)
                listener.onClick(selectPuzzleViewHolder.getImage(), selectPuzzleViewHolder.getId());
            }
        });
    }

    public void setListener(onPuzzleSelectedListener listener){
        this.listener = listener;
    }
}
