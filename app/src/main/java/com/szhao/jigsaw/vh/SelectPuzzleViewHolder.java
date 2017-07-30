package com.szhao.jigsaw.vh;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.Utility;

/**
 * Created by Owner on 7/11/2017.
 */

public class SelectPuzzleViewHolder extends RecyclerView.ViewHolder{

    private ImageView puzzleImage;
    private int id;

    public SelectPuzzleViewHolder(View view){
        super(view);
        puzzleImage = (ImageView)view.findViewById(R.id.puzzleImage);
    }

    public void setData(Cursor cursor, Context context){
        id = cursor.getInt(cursor.getColumnIndex("_id"));
        byte[] byteArr = cursor.getBlob(cursor.getColumnIndex("PUZZLE"));

        Glide.with(context)
                .load(byteArr)
                .asBitmap()
                .centerCrop()
                .override(Utility.IMAGE_DIMENSIONS,Utility.IMAGE_DIMENSIONS)
                .into(puzzleImage);
    }

    public void setPuzzle(int imageId, Context context){
        Glide.with(context)
                .load(imageId)
                .asBitmap()
                .centerCrop()
                .override(Utility.IMAGE_DIMENSIONS,Utility.IMAGE_DIMENSIONS)
                .into(puzzleImage);
    }

    public int getId(){
        return id;
    }

    public Bitmap getImage(){
        return ((BitmapDrawable)puzzleImage.getDrawable()).getBitmap();
    }

}
