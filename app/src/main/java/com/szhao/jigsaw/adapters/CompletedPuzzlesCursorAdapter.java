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
import com.szhao.jigsaw.db.Utility;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Owner on 6/1/2017.
 */

public class CompletedPuzzlesCursorAdapter extends CursorAdapter{
    private LayoutInflater cursorInflater;
    private static final String PUZZLE = "PUZZLE";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String DIFFICULTY = "DIFFICULTY";
    private static final String SOLVE_TIME = "SOLVETIME";
    private static final String DATE = "DATE";

    public CompletedPuzzlesCursorAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
        cursorInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor){
        ImageView imageViewPuzzleImage = (ImageView)view.findViewById(R.id.completedPuzzleImage);
        byte[] byteArr = cursor.getBlob(cursor.getColumnIndex(PUZZLE));
        imageViewPuzzleImage.setImageBitmap(Utility.getImage(byteArr));


        TextView textViewDescription = (TextView)view.findViewById(R.id.completedPuzzleDescription);
        textViewDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));

        TextView textViewDifficulty = (TextView)view.findViewById(R.id.completedPuzzleDifficulty);
        String difficulty = "Difficulty: " + cursor.getInt(cursor.getColumnIndex(DIFFICULTY));
        textViewDifficulty.setText(difficulty);

        TextView textViewSolveTime = (TextView)view.findViewById(R.id.completedPuzzleSolveTime);
        long solveTimeSeconds = cursor.getLong(cursor.getColumnIndex(SOLVE_TIME));
        String solveTime = String.format(Locale.CANADA, "%d min, %d sec", solveTimeSeconds/60, solveTimeSeconds % 60);
        textViewSolveTime.setText(solveTime);

        TextView textViewDate = (TextView)view.findViewById(R.id.completedPuzzleDate);
        long dateEpoch = cursor.getLong(cursor.getColumnIndex(DATE));
        String date = DateFormat.format("dd/MM/yyyy", new Date(dateEpoch)).toString();
        textViewDate.setText(date);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return cursorInflater.inflate(R.layout.completed_puzzle_layout, parent, false);
    }
}
