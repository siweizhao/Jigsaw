package com.szhao.jigsaw.activities.Dashboard.VH;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentViewHolder extends RecyclerView.ViewHolder{
    private Context context;
    private ImageView puzzleImage;

    public ContentViewHolder(Context context, View view){
        super(view);
        this.context = context;
        puzzleImage = (ImageView)view.findViewById(R.id.content_image);
    }

    public void setPuzzleImage(String filePath){
        Glide.with(context)
                .load(Uri.parse(filePath))
                .override(700,550)
                .centerCrop()
                .into(puzzleImage);
    }

}
