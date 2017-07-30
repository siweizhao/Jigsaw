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

public class PuzzlePieceViewHolder extends RecyclerView.ViewHolder{
    ImageView puzzlePieceImage;


    public PuzzlePieceViewHolder(View view) {
        super(view);
        puzzlePieceImage = (ImageView)view.findViewById(R.id.puzzlePiece);
    }

    public Bitmap getBitmap(){
        return ((BitmapDrawable)puzzlePieceImage.getDrawable()).getBitmap();
    }

    public void setPuzzlePieceImage(Context context, Bitmap image){
        puzzlePieceImage.setImageBitmap(image);
        puzzlePieceImage.setBackgroundColor(Color.GREEN);
    }

}
