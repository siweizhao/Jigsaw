package com.szhao.jigsaw.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.adapters.CompletedPuzzlesCursorAdapter;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.db.Utility;

public class CompletedPuzzles extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView completedPuzzlesRecycler;
    CompletedPuzzlesCursorAdapter completedPuzzlesCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_puzzles);
        completedPuzzlesRecycler = (RecyclerView)findViewById(R.id.completedPuzzlesRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        completedPuzzlesRecycler.setLayoutManager(layoutManager);
        getLoaderManager().initLoader(Utility.TABLE_COMPLETED, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Utility.TABLE_COMPLETED:
                completedPuzzlesCursorAdapter = new CompletedPuzzlesCursorAdapter(this, null);
                completedPuzzlesRecycler.setAdapter(completedPuzzlesCursorAdapter);
                return new CursorLoader(this, PuzzleContentProvider.CONTENT_URI_COMPLETED, null, null, null, "_id DESC");
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        completedPuzzlesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
