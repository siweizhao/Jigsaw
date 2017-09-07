package com.szhao.jigsaw.activities.dashboard.vh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;

import java.io.File;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentViewHolder extends RecyclerView.ViewHolder{
    private Context context;
    private ImageView puzzleImage;
    private ImageView lockImage;
    private boolean isLocked;

    public ContentViewHolder(Context context, View view){
        super(view);
        this.context = context;
        puzzleImage = (ImageView)view.findViewById(R.id.content_image);
        lockImage = (ImageView) view.findViewById(R.id.content_locked);
    }

    public boolean getLockStatus() {
        return isLocked;
    }

    public void setLock() {
        isLocked = true;
        puzzleImage.setAlpha(0.4f);
        lockImage.setVisibility(View.VISIBLE);
    }

    public void setUnlock() {
        isLocked = false;
        puzzleImage.setAlpha(1f);
        lockImage.setVisibility(View.INVISIBLE);
    }

    public void setPuzzleImage(String filePath){
        Glide.with(context)
                .load(new File(filePath))
                .override(700,550)
                .centerCrop()
                .into(puzzleImage);
    }
}
