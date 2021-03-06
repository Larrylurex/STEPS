package ru.tomatapps.steps.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by LarryLurex (dmitry.borodin90@gmail.com) on 07.09.2015.
 */
public class StepsContentProvider extends ContentProvider {
    private DbHelper helper;
    private UriMatcher uriMatcher;


    @Override
    public boolean onCreate() {
        helper = new DbHelper(getContext());
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.SETTINGS_PATH, StepsContract.SETTINGS_CODE);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.SETTINGS_PATH_LIMIT, StepsContract.SETTINGS_LIMIT_CODE);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.STATISTICS_PATH, StepsContract.STATISTICS_CODE);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.STATISTICS_LIST_PATH, StepsContract.STATISTICS_LIST_CODE);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.STATISTICS_CHART_PATH, StepsContract.STATISTICS_CHART_CODE);
        uriMatcher.addURI(StepsContract.AUTHORITY, StepsContract.STATISTICS_TRANSPORT_PATH, StepsContract.STATISTICS_TRANSPORT_CODE);
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case StepsContract.SETTINGS_CODE:
            case StepsContract.SETTINGS_LIMIT_CODE:
                return StepsContract.SETTINGS_CONTENT_TYPE;
            case StepsContract.STATISTICS_CODE:
            case StepsContract.STATISTICS_CHART_CODE:
            case StepsContract.STATISTICS_LIST_CODE:
            case StepsContract.STATISTICS_TRANSPORT_CODE:
                return StepsContract.STATISTICS_CONTENT_TYPE;
        }
        return null;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String table;
        String groupBy = null;
        String limit = null;
        switch (uriMatcher.match(uri)){
            case StepsContract.SETTINGS_CODE:
                table = StepsContract.T_SETTINGS;
                break;
            case StepsContract.SETTINGS_LIMIT_CODE:
                table = StepsContract.T_SETTINGS;
                limit = "1";
                break;
            case StepsContract.STATISTICS_CODE:
                table = StepsContract.T_STATISTICS;
                break;
            case StepsContract.STATISTICS_CHART_CODE:
                table = StepsContract.T_STATISTICS;
                groupBy = StepsContract.COL_TRANSPORT + " , " + StepsContract.COL_DATE;
                break;
            case StepsContract.STATISTICS_LIST_CODE:
                table = StepsContract.T_STATISTICS;
                groupBy = StepsContract.COL_TRANSPORT;
                break;
            case StepsContract.STATISTICS_TRANSPORT_CODE:
                table = StepsContract.T_STATISTICS;
                projection[0] = "DISTINCT " + projection[0];
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder, limit);
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        switch (uriMatcher.match(uri)){
            case StepsContract.SETTINGS_CODE:
                table = StepsContract.T_SETTINGS;
                break;
            case StepsContract.STATISTICS_CODE:
                table = StepsContract.T_STATISTICS;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        long inserted = db.insert(table, null, values);
        return ContentUris.withAppendedId(uri, inserted);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        switch (uriMatcher.match(uri)){
            case StepsContract.SETTINGS_CODE:
                table = StepsContract.T_SETTINGS;
                break;
            case StepsContract.STATISTICS_CODE:
                table = StepsContract.T_STATISTICS;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        return db.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        switch (uriMatcher.match(uri)){
            case StepsContract.SETTINGS_CODE:
                table = StepsContract.T_SETTINGS;
                break;
            case StepsContract.STATISTICS_CODE:
                table = StepsContract.T_STATISTICS;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        return db.update(table, values, selection, selectionArgs);
    }


    private class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context ctx){
            super(ctx, "stepsAppDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + StepsContract.T_SETTINGS + " ( " +
                    StepsContract.COL_ID + " integer primary key autoincrement, " +
                    StepsContract.COL_TRANSPORT + " text," +
                    StepsContract.COL_PRICE + " real, " +
                    StepsContract.COL_DEFAULT + " integer );");
            db.execSQL("CREATE TABLE " + StepsContract.T_STATISTICS + " ( " +
                    StepsContract.COL_ID + " integer primary key autoincrement, " +
                    StepsContract.COL_TRANSPORT + " text," +
                    StepsContract.COL_PRICE + " real, " +
                    StepsContract.COL_DATE + " integer NOT NULL DEFAULT (strftime('%s', 'now')));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
