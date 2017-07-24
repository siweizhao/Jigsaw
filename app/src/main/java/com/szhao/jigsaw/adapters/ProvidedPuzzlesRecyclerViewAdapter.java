package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.vh.SelectPuzzleViewHolder;

/**
 * Created by Owner on 7/12/2017.
 */

public class ProvidedPuzzlesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int[] imageIds = new int[]{
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
            R.drawable.puzzle_1,
            R.drawable.puzzle_2,
            R.drawable.puzzle_3,
            R.drawable.puzzle_4,
    };

    private Context mContext;
    private onPuzzleSelectedListener listener;
    public interface onPuzzleSelectedListener{
        void onClick(Bitmap bitmap);
    }

    public ProvidedPuzzlesRecyclerViewAdapter(Context context){
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.puzzle_select_layout, parent, false);
        return new SelectPuzzleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        final SelectPuzzleViewHolder selectPuzzleViewHolder = (SelectPuzzleViewHolder)viewholder;
        selectPuzzleViewHolder.setPuzzle(imageIds[position], mContext);
        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(selectPuzzleViewHolder.getImage());
            }
        });
    }

    public void setListener(ProvidedPuzzlesRecyclerViewAdapter.onPuzzleSelectedListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return imageIds.length;
    }
}
