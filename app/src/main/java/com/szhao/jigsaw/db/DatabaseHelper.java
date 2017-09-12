package com.szhao.jigsaw.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owner on 5/29/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "puzzles";
    private static final int DB_VERSION = 15;
    private static DatabaseHelper mInstance = null;

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
            "PUZZLE STRING, " +
            "DIFFICULTY NUMERIC, " +
            "SOLVETIME NUMERIC," +
            "PRIMARY KEY (PUZZLE, DIFFICULTY));");
        db.execSQL("CREATE TABLE STARTED (" +
                "PUZZLE STRING," +
                "DIFFICULTY NUMERIC, " +
                "SOLVETIME NUMERIC," +
                "POSITIONS STRING, " +
                "PRIMARY KEY (PUZZLE, DIFFICULTY));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion > 8) {
            db.execSQL("DROP TABLE IF EXISTS COMPLETED");
            db.execSQL("DROP TABLE IF EXISTS STARTED");
            onCreate(db);
        }
    }
}

