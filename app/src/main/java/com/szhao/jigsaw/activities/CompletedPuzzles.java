package com.szhao.jigsaw.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.adapters.CompletedPuzzlesCursorAdapter;
import com.szhao.jigsaw.db.DatabaseHelper;

public class CompletedPuzzles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_puzzles);

        DatabaseHelper puzzlesDatabaseHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = puzzlesDatabaseHelper.getReadableDatabase();
        Cursor cursor = DatabaseHelper.getAllRows(db);
        CompletedPuzzlesCursorAdapter completedPuzzlesCursorAdapter = new CompletedPuzzlesCursorAdapter(this,cursor,0);
        ListView listView = (ListView)findViewById(R.id.listViewCompletedPuzzles);
        listView.setAdapter(completedPuzzlesCursorAdapter);
    }
}
