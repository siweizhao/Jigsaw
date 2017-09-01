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
    private static final int COMPLETED_PUZZLES = 1;
    private static final int STARTED_PUZZLES = 2;
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri CONTENT_URI_COMPLETED = Uri.parse("content://" + AUTHORITY + "/completedpuzzles");
    public static final Uri CONTENT_URI_STARTED = Uri.parse("content://" + AUTHORITY + "/startedpuzzles");
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "completedpuzzles", COMPLETED_PUZZLES);
        uriMatcher.addURI(AUTHORITY, "startedpuzzles", STARTED_PUZZLES);

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
            case COMPLETED_PUZZLES:
                queryBuilder.setTables("COMPLETED");
                break;
            case STARTED_PUZZLES:
                queryBuilder.setTables("STARTED");
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
            case COMPLETED_PUZZLES:
                return "vnd.android.cursor.dir/vnd.jigsaw.completedpuzzles";
            case STARTED_PUZZLES:
                return "vnd.android.cursor.dir/vnd.jigsaw.startedpuzzles";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName;
        Uri newUri;
        long id;
        switch (uriMatcher.match(uri)){
            case COMPLETED_PUZZLES:
                tableName = "COMPLETED";
                id = db.insert(tableName, null, values);
                newUri = ContentUris.withAppendedId(CONTENT_URI_COMPLETED, id);
                break;
            case STARTED_PUZZLES:
                tableName = "STARTED";
                id = db.insert(tableName, null, values);
                newUri = ContentUris.withAppendedId(CONTENT_URI_STARTED, id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(newUri,null);
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)){
            case COMPLETED_PUZZLES:
                tableName = "COMPLETED";
                break;
            case STARTED_PUZZLES:
                tableName = "STARTED";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        db.delete(tableName, selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)){
            case COMPLETED_PUZZLES:
                tableName = "COMPLETED";
                break;
            case STARTED_PUZZLES:
                tableName = "STARTED";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        db.update(tableName, values, selection, selectionArgs);
        return 0;
    }
}