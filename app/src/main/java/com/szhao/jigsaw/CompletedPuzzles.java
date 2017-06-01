package com.szhao.jigsaw;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CompletedPuzzles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_puzzles);

        CompletedPuzzlesDatabaseHelper puzzlesDatabaseHelper = new CompletedPuzzlesDatabaseHelper(this);
        SQLiteDatabase db = puzzlesDatabaseHelper.getReadableDatabase();
        Cursor cursor = CompletedPuzzlesDatabaseHelper.getAllRows(db);

        String[] fromFieldNames = new String[]
                {"PUZZLE", "DIFFICULTY", "DESCRIPTION", "SOLVETIME", "DATE"};
        int[] toViewIDs = new int[]
                {R.id.completedPuzzleImage, R.id.completedPuzzleDifficulty, R.id.completedPuzzleDescription,
                R.id.completedPuzzleSolveTime, R.id.completedPuzzleDate};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.completed_puzzle_layout,
                cursor,
                fromFieldNames,
                toViewIDs
        );

        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder(){
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (!(view instanceof ImageView)) return false;
                ImageView image = (ImageView) view;
                byte[] byteArr = cursor.getBlob(columnIndex);
                image.setImageBitmap(BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length));
                return true;
            }
        };
        cursorAdapter.setViewBinder(viewBinder);
        ListView listView = (ListView)findViewById(R.id.listViewCompletedPuzzles);
        listView.setAdapter(cursorAdapter);
    }
}
