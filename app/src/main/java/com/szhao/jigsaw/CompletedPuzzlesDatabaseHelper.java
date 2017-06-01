package com.szhao.jigsaw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

/**
 * Created by Owner on 5/29/2017.
 */

public class CompletedPuzzlesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "completed_puzzles";
    private static final int DB_VERSION = 3;

    CompletedPuzzlesDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE COMPLETED (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "DESCRIPTION TEXT, " +
                "DIFFICULTY NUMERIC, " +
                "SOLVETIME NUMERIC, " +
                "DATE NUMERIC, " +
                "PUZZLE BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion > 2) {
            db.execSQL("DROP TABLE IF EXISTS COMPLETED");
            onCreate(db);
        }
    }

    public static void insertPuzzle(SQLiteDatabase db, String description, int difficulty, long solveTime, long date, Bitmap bitmap){
        byte[] image = DbUtility.getBytes(bitmap);
        ContentValues completedPuzzle = new ContentValues();
        completedPuzzle.put("DESCRIPTION", description);
        completedPuzzle.put("DIFFICULTY", difficulty);
        completedPuzzle.put("SOLVETIME", solveTime);
        completedPuzzle.put("DATE", date);
        completedPuzzle.put("PUZZLE", image);
        db.insert("COMPLETED", null, completedPuzzle);
    }

    public static Cursor getAllRows(SQLiteDatabase db){
        return db.rawQuery("SELECT * FROM COMPLETED", null);
    }

}

