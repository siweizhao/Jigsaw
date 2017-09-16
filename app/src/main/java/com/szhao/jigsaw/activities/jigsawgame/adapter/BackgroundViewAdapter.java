package com.szhao.jigsaw.activities.jigsawgame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.jigsawgame.vh.ImageViewHolder;
import com.szhao.jigsaw.global.Constants;

/**
 * Created by Owner on 8/2/2017.
 */

public class BackgroundViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private BackgroundSelectListener listener;
    public interface BackgroundSelectListener{
        void onClick(int bgId);
    }

    public BackgroundViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cv_image_only, parent, false);
        final ImageViewHolder vh = new ImageViewHolder(context,v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(Constants.backgroundIds[vh.getAdapterPosition()]);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageViewHolder vh = (ImageViewHolder)holder;
        vh.setBackgroundImage(Constants.backgroundIds[position]);
    }

    @Override
    public int getItemCount() {
        return Constants.backgroundIds.length;
    }

    public void setListener(BackgroundSelectListener listener){
        this.listener = listener;
    }
}
