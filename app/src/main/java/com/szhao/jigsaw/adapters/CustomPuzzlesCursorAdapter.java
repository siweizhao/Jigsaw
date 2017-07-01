package com.szhao.jigsaw.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.szhao.jigsaw.db.Utility;

/**
 * Created by Owner on 6/30/2017.
 */

public class CustomPuzzlesCursorAdapter extends CursorAdapter {
    private static final String PUZZLE = "PUZZLE";

    public CustomPuzzlesCursorAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView =(ImageView)view;
        byte[] byteArr = cursor.getBlob(cursor.getColumnIndex(PUZZLE));
        imageView.setImageBitmap(Utility.getImage(byteArr));
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View v = new ImageView(context);
        bindView(v, context, cursor);
        return v;
    }
}
