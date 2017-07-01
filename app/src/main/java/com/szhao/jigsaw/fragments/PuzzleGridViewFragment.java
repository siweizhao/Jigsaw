package com.szhao.jigsaw.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.adapters.CompletedPuzzlesCursorAdapter;
import com.szhao.jigsaw.adapters.CustomPuzzlesCursorAdapter;
import com.szhao.jigsaw.adapters.ImageAdapter;
import com.szhao.jigsaw.db.DatabaseHelper;

public class PuzzleGridViewFragment extends Fragment {
    onPuzzleSelectedListener puzzleSelectedListener;
    private static final String TYPE = "type";

    public PuzzleGridViewFragment() {
    }

    public static PuzzleGridViewFragment newInstance(String type) {
        PuzzleGridViewFragment fragment = new PuzzleGridViewFragment();
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
        View  view = inflater.inflate(R.layout.fragment_puzzles_grid_view, container, false);
        GridView grid = (GridView)view.findViewById(R.id.puzzlesGridView);
        BaseAdapter adapter;
        if ("custom".equals(getArguments().getString(TYPE))){
            Context context =getActivity().getApplicationContext();
            DatabaseHelper puzzlesDatabaseHelper = DatabaseHelper.getInstance(context);
            SQLiteDatabase db = puzzlesDatabaseHelper.getReadableDatabase();
            Cursor cursor = DatabaseHelper.getCustomPuzzles(db);
            adapter = new CustomPuzzlesCursorAdapter(context,cursor,0);
        } else{
            adapter = new ImageAdapter(getActivity());
        }

        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BitmapDrawable drawable = (BitmapDrawable)((ImageView)view).getDrawable();
                puzzleSelectedListener.onPuzzleSelected(drawable.getBitmap());
            }
        });
        return grid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            puzzleSelectedListener = (onPuzzleSelectedListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement onPuzzleSelectedListener");
        }
    }

    public interface onPuzzleSelectedListener{
        public void onPuzzleSelected(Bitmap image);
    }
}
