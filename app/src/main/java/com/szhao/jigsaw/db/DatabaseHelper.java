package com.szhao.jigsaw.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

/**
 * Created by Owner on 5/29/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;
    private static final String DB_NAME = "puzzles";
    private static final int DB_VERSION = 4;

    private DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
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

        db.execSQL("CREATE TABLE CUSTOM ("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "DESCRIPTION TEXT, "+
            "PUZZLE BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion > 2) {
            db.execSQL("DROP TABLE IF EXISTS COMPLETED");
            onCreate(db);
        }
    }

    public static void insertPuzzleCompleted(SQLiteDatabase db, String description, int difficulty, long solveTime, long date, Bitmap bitmap){
        byte[] image = Utility.getBytes(bitmap);
        ContentValues completedPuzzle = new ContentValues();
        completedPuzzle.put("DESCRIPTION", description);
        completedPuzzle.put("DIFFICULTY", difficulty);
        completedPuzzle.put("SOLVETIME", solveTime);
        completedPuzzle.put("DATE", date);
        completedPuzzle.put("PUZZLE", image);
        db.insert("COMPLETED", null, completedPuzzle);
    }

    public static void insertCustomPuzzle(SQLiteDatabase db, String description, Bitmap bitmap){
        byte[] image = Utility.getBytes(bitmap);
        ContentValues customPuzzle = new ContentValues();
        customPuzzle.put("DESCRIPTION", description);
        customPuzzle.put("PUZZLE", image);
        db.insert("CUSTOM", null, customPuzzle);
    }

    public static Cursor getAllRows(SQLiteDatabase db){
        return db.rawQuery("SELECT * FROM COMPLETED ORDER BY _id DESC", null);
    }

    public static Cursor getCustomPuzzles(SQLiteDatabase db){
        return db.rawQuery("SELECT * FROM CUSTOM ORDER BY _id DESC", null);
    }
}

