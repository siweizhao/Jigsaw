package com.szhao.jigsaw.Old.vh;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.global.Utility;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Owner on 7/19/2017.
 */

public class CompletedPuzzleViewHolder extends RecyclerView.ViewHolder{

    private ImageView puzzleImage;
    private TextView puzzleCaption;
    private TextView puzzleDifficulty;
    private TextView puzzleSolveTime;
    private TextView puzzleDate;

    public CompletedPuzzleViewHolder(View view){
        super(view);
        puzzleImage = (ImageView)view.findViewById(R.id.completedPuzzleImage);
        puzzleCaption = (TextView)view.findViewById(R.id.completedPuzzleDescription);
        puzzleDifficulty = (TextView)view.findViewById(R.id.completedPuzzleDifficulty);
        puzzleSolveTime = (TextView)view.findViewById(R.id.completedPuzzleSolveTime);
        puzzleDate = (TextView)view.findViewById(R.id.completedPuzzleDate);
    }

    public void setData(Cursor cursor, Context context){
        puzzleCaption.setText(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));

        String difficulty = "Difficulty: " + cursor.getInt(cursor.getColumnIndex("DIFFICULTY"));
        puzzleDifficulty.setText(difficulty);

        long solveTimeSeconds = cursor.getLong(cursor.getColumnIndex("SOLVETIME"));
        String solveTime = String.format(Locale.CANADA, "%d min, %d sec", solveTimeSeconds/60, solveTimeSeconds % 60);
        puzzleSolveTime.setText(solveTime);

        long dateEpoch = cursor.getLong(cursor.getColumnIndex("DATE"));
        String date = DateFormat.format("dd/MM/yyyy", new Date(dateEpoch)).toString();
        puzzleDate.setText(date);

        byte[] byteArr = cursor.getBlob(cursor.getColumnIndex("PUZZLE"));
        Glide.with(context)
                .load(byteArr)
                .asBitmap()
                .centerCrop()
                .override(Utility.IMAGE_DIMENSIONS,Utility.IMAGE_DIMENSIONS)
                .into(puzzleImage);
    }
}
