package com.szhao.jigsaw.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.PuzzleSelector;
import com.szhao.jigsaw.adapters.CustomPuzzlesCursorRecyclerViewAdapter;
import com.szhao.jigsaw.adapters.ProvidedPuzzlesRecyclerViewAdapter;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.db.Utility;

public class PuzzleSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    RecyclerView customPuzzlesRecycler;
    CustomPuzzlesCursorRecyclerViewAdapter customPuzzlesCursorRecyclerViewAdapter;
    DifficultyDialog difficultyDialog;
    private static final String TYPE = "type";

    public PuzzleSelectFragment() {
    }

    public static PuzzleSelectFragment newInstance(String type) {
        PuzzleSelectFragment fragment = new PuzzleSelectFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        difficultyDialog = new DifficultyDialog();
        View  view = inflater.inflate(R.layout.fragment_puzzles_grid_view, container, false);
        customPuzzlesRecycler = (RecyclerView)view.findViewById(R.id.puzzlesRecyclerView);
        switch (getArguments().getString(TYPE)){
            case PuzzleSelector.FRAG_PROVIDED_PUZZLES:
                initProvidedPuzzles();
                break;
            case PuzzleSelector.FRAG_CUSTOM_PUZZLES:
                getLoaderManager().initLoader(Utility.TABLE_CUSTOM, null, this);
                break;
            default:
                throw new IllegalArgumentException("unidentified fragment type");
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        customPuzzlesRecycler.setLayoutManager(layoutManager);
        return customPuzzlesRecycler;
    }

    public void initProvidedPuzzles(){
        ProvidedPuzzlesRecyclerViewAdapter customPuzzlesRecyclerViewAdapter = new ProvidedPuzzlesRecyclerViewAdapter(getContext());
        customPuzzlesRecycler.setAdapter(customPuzzlesRecyclerViewAdapter);
        customPuzzlesRecyclerViewAdapter.setListener(
            new ProvidedPuzzlesRecyclerViewAdapter.onPuzzleSelectedListener() {
                @Override
                public void onClick(Bitmap bitmap) {
                    showDialog(bitmap);
                }
            });
    }

    public void showDialog(Bitmap bitmap){
        Utility.storeImage(getContext(), bitmap);
        difficultyDialog.show(getFragmentManager(), "Difficulty");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Utility.TABLE_CUSTOM:
                customPuzzlesCursorRecyclerViewAdapter = new CustomPuzzlesCursorRecyclerViewAdapter(getContext(), null);
                customPuzzlesRecycler.setAdapter(customPuzzlesCursorRecyclerViewAdapter);
                customPuzzlesCursorRecyclerViewAdapter.setListener(
                        new CustomPuzzlesCursorRecyclerViewAdapter.onPuzzleSelectedListener() {
                            @Override
                            public void onClick(Bitmap bitmap, int id) {
                                if (((PuzzleSelector)getContext()).isRemovePuzzles()){
                                    getContext().getContentResolver().delete(PuzzleContentProvider.CONTENT_URI_CUSTOM, "_id = ?", new String[]{String.valueOf(id)});
                                    getContext().getContentResolver().notifyChange(PuzzleContentProvider.CONTENT_URI_CUSTOM, null);
                                } else {
                                    showDialog(bitmap);
                                }
                            }
                        });
                return new CursorLoader(getContext(), PuzzleContentProvider.CONTENT_URI_CUSTOM, null, null, null, "_id DESC");
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        customPuzzlesCursorRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customPuzzlesCursorRecyclerViewAdapter.swapCursor(null);
    }

}
