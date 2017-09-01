package com.szhao.jigsaw.activities.dashboard.vh;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.global.Utility;

import java.io.File;
import java.util.Date;
import java.util.Locale;

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
                .load(new File(filePath))
                .override(700,550)
                .centerCrop()
                .into(puzzleImage);
    }
}
