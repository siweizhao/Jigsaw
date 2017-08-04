package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.Utility;
import com.szhao.jigsaw.puzzle.GlobalGameData;
import com.szhao.jigsaw.vh.ImageViewHolder;

/**
 * Created by Owner on 8/2/2017.
 */

public class BackgroundViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private BackgroundSelectListener listener;
    public interface BackgroundSelectListener{
        void onClick(int bgId);
    }

    public BackgroundViewAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cv_image_only, parent, false);
        final ImageViewHolder vh = new ImageViewHolder(mContext,v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    int position = vh.getAdapterPosition();
                    listener.onClick(Utility.backgroundIds[position]);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageViewHolder vh = (ImageViewHolder)holder;
        vh.setBackgroundImage(Utility.backgroundIds[position]);
    }

    @Override
    public int getItemCount() {
        return Utility.backgroundIds.length;
    }

    public void setListener(BackgroundSelectListener listener){
        this.listener = listener;
    }
}
