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
            mInstance = new DatabaseHelper(ctx);
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
}

