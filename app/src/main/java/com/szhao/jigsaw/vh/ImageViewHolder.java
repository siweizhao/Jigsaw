package com.szhao.jigsaw.vh;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.Utility;

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

    public Bitmap getBitmap(){
        return ((BitmapDrawable)mImage.getDrawable()).getBitmap();
    }

    public void setPuzzlePieceImage(Bitmap bitmap){
        mImage.setImageBitmap(bitmap);
    }

    public void setBackgroundImage(int bgId){
        Glide.with(mContext)
                .load(bgId)
                .into(mImage);
    }

    public void setBorder(){
        mImage.setBackgroundResource(R.drawable.border);
    }

}
