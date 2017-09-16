package com.szhao.jigsaw.activities.jigsawgame.vh;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;

/**
 * Created by Owner on 7/25/2017.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder{
    private ImageView mImage;
    private Context mContext;


    public ImageViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        mImage = (ImageView)view.findViewById(R.id.image_only);
    }

    public void setPuzzlePieceImage(Bitmap bitmap){
        mImage.setImageBitmap(bitmap);
    }

    public void setBackgroundImage(int bgId){
        Glide.with(mContext)
                .load(bgId)
                .into(mImage);
    }

}
