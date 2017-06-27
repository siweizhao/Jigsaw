package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.DbUtility;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Owner on 6/1/2017.
 */

public class PuzzleCursorAdaptor extends CursorAdapter{
    private LayoutInflater cursorInflater;

    public PuzzleCursorAdaptor(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
        cursorInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor){
        ImageView imageViewPuzzleImage = (ImageView)view.findViewById(R.id.completedPuzzleImage);
        byte[] byteArr = cursor.getBlob(cursor.getColumnIndex("PUZZLE"));
        imageViewPuzzleImage.setImageBitmap(DbUtility.getImage(byteArr));

        TextView textViewDescription = (TextView)view.findViewById(R.id.completedPuzzleDescription);
        textViewDescription.setText(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));

        TextView textViewDifficulty = (TextView)view.findViewById(R.id.completedPuzzleDifficulty);
        String difficulty = "Difficulty: " + cursor.getInt(cursor.getColumnIndex("DIFFICULTY"));
        textViewDifficulty.setText(difficulty);

        TextView textViewSolveTime = (TextView)view.findViewById(R.id.completedPuzzleSolveTime);
        long solveTimeSeconds = cursor.getLong(cursor.getColumnIndex("SOLVETIME"));
        String solveTime = String.format(Locale.CANADA, "%d min, %d sec", solveTimeSeconds/60, solveTimeSeconds % 60);
        textViewSolveTime.setText(solveTime);

        TextView textViewDate = (TextView)view.findViewById(R.id.completedPuzzleDate);
        long dateEpoch = cursor.getLong(cursor.getColumnIndex("DATE"));
        String date = DateFormat.format("dd/MM/yyyy", new Date(dateEpoch)).toString();
        textViewDate.setText(date);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.completed_puzzle_layout, parent, false);
    }
}
