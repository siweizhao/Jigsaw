package com.szhao.jigsaw.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.szhao.jigsaw.BuildConfig;

public class PuzzleContentProvider extends ContentProvider {

    private DatabaseHelper mDatabaseHelper;
    private static final int CUSTOM_PUZZLES = 1;
    private static final int COMPLETED_PUZZLES = 2;
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri CONTENT_URI_CUSTOM = Uri.parse("content://" + AUTHORITY + "/custompuzzles");
    public static final Uri CONTENT_URI_COMPLETED = Uri.parse("content://" + AUTHORITY + "/completedpuzzles");
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "custompuzzles", CUSTOM_PUZZLES);
        uriMatcher.addURI(AUTHORITY, "completedpuzzles", COMPLETED_PUZZLES);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case CUSTOM_PUZZLES:
                queryBuilder.setTables("CUSTOM");
                break;
            case COMPLETED_PUZZLES:
                queryBuilder.setTables("COMPLETED");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case CUSTOM_PUZZLES:
                return "vnd.android.cursor.dir/vnd.jigsaw.custompuzzles";
            case COMPLETED_PUZZLES:
                return "vnd.android.cursor.dir/vnd.jigsaw.completedpuzzles";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)){
            case CUSTOM_PUZZLES:
                tableName = "CUSTOM";
                break;
            case COMPLETED_PUZZLES:
                tableName = "COMPLETED";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = db.insert(tableName, null, values);
        Uri newUri = ContentUris.withAppendedId(CONTENT_URI_CUSTOM, id);
        getContext().getContentResolver().notifyChange(newUri,null);
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)){
            case CUSTOM_PUZZLES:
                tableName = "CUSTOM";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        db.delete(tableName, selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}