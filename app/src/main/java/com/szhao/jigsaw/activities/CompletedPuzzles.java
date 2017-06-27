package com.szhao.jigsaw.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.adapters.PuzzleCursorAdaptor;
import com.szhao.jigsaw.db.CompletedPuzzlesDatabaseHelper;

public class CompletedPuzzles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_puzzles);

        CompletedPuzzlesDatabaseHelper puzzlesDatabaseHelper = new CompletedPuzzlesDatabaseHelper(this);
        SQLiteDatabase db = puzzlesDatabaseHelper.getReadableDatabase();
        Cursor cursor = CompletedPuzzlesDatabaseHelper.getAllRows(db);
        PuzzleCursorAdaptor puzzleCursorAdaptor = new PuzzleCursorAdaptor(this,cursor,0);
        ListView listView = (ListView)findViewById(R.id.listViewCompletedPuzzles);
        listView.setAdapter(puzzleCursorAdaptor);
    }
}
