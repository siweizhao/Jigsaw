package com.szhao.jigsaw.activities.dashboard.vh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.DisplayDimensions;

import java.io.File;

/**
 * Created by Owner on 8/24/2017.
 */

public class ContentViewHolder extends RecyclerView.ViewHolder{
    private Context context;
    private ImageView puzzleImage;
    private ImageView lockImage;
    private boolean isLocked;
    private int width, height;

    public ContentViewHolder(Context context, View view){
        super(view);
        this.context = context;
        puzzleImage = (ImageView)view.findViewById(R.id.content_image);
        lockImage = (ImageView) view.findViewById(R.id.content_locked);
        height = DisplayDimensions.getInstance().getContentRecyclerHeight() - Constants.CONTENT_VH_MARGIN;
        width = Math.round(Constants.GOLDEN_RATIO * height);
    }

    public boolean getLockStatus() {
        return isLocked;
    }

    public void setLock() {
        isLocked = true;
        puzzleImage.setImageAlpha(Constants.LOCKED_ALPHA);
        lockImage.setVisibility(View.VISIBLE);
    }

    public void setUnlock() {
        isLocked = false;
        puzzleImage.setImageAlpha(Constants.UNLOCKED_ALPHA);
        lockImage.setVisibility(View.INVISIBLE);
    }

    public void setPuzzleImage(String filePath){
        Glide.with(context)
                .load(new File(filePath))
                .override(width, height)
                .centerCrop()
                .into(puzzleImage);
    }
}
