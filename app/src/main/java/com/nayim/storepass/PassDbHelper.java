package com.nayim.storepass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PassDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "PassDbHelper";
    private static PassDbHelper sInstance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "pass.db";
    public static final String DB_NAME_NEW = "pass_new.db";
    public static final String DB_PATH = "/data/data/PACKAGE/databases/";
    public static final String DB_NAME_BACKUP = "mypass.backup";
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s BIGINT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s DATE)"
                    , PassContract.PassEntry.TABLE_NAME
                    , PassContract.PassEntry._ID
                    , PassContract.PassEntry.COLUMN_NAME_TITLE
                    , PassContract.PassEntry.COLUMN_NAME_COLOR
                    , PassContract.PassEntry.COLUMN_NAME_ACCOUNT
                    , PassContract.PassEntry.COLUMN_NAME_PW
                    , PassContract.PassEntry.COLUMN_NAME_URL
                    , PassContract.PassEntry.COLUMN_NAME_CONTENTS
                    , PassContract.PassEntry.COLUMN_NAME_DATE);

    private static final String SQL_CREATE_NEW_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s BIGINT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s DATE)"
                    , PassContract.PassEntry.TABLE_NAME_NEW
                    , PassContract.PassEntry._ID
                    , PassContract.PassEntry.COLUMN_NAME_TITLE
                    , PassContract.PassEntry.COLUMN_NAME_COLOR
                    , PassContract.PassEntry.COLUMN_NAME_ACCOUNT
                    , PassContract.PassEntry.COLUMN_NAME_PW
                    , PassContract.PassEntry.COLUMN_NAME_URL
                    , PassContract.PassEntry.COLUMN_NAME_CONTENTS
                    , PassContract.PassEntry.COLUMN_NAME_DATE);

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PassContract.PassEntry.TABLE_NAME;

    private static final String SQL_GET_ENTRIES =
            "SELECT * FROM " + PassContract.PassEntry.TABLE_NAME;

    public static PassDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PassDbHelper(context);
        }
        return sInstance;
    }

    public SQLiteDatabase getWritableDb() {
        return sInstance.getWritableDatabase();
    }
    public SQLiteDatabase getReadableDb() {
        return sInstance.getReadableDatabase();
    }

    private PassDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private void createDb(SQLiteDatabase db, String tableName) {
        String query =
                String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s BIGINT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s DATE)"
                        ,tableName
                        ,PassContract.PassEntry._ID
                        ,PassContract.PassEntry.COLUMN_NAME_TITLE
                        ,PassContract.PassEntry.COLUMN_NAME_COLOR
                        ,PassContract.PassEntry.COLUMN_NAME_ACCOUNT
                        ,PassContract.PassEntry.COLUMN_NAME_PW
                        ,PassContract.PassEntry.COLUMN_NAME_URL
                        ,PassContract.PassEntry.COLUMN_NAME_CONTENTS
                        ,PassContract.PassEntry.COLUMN_NAME_DATE);
        db.execSQL(query);
    }

    private void joinDb(SQLiteDatabase db, String tableName) {
        String query = new StringBuilder()
                .append("SELECT * FROM ")
                .append(PassContract.PassEntry.TABLE_NAME + " ")
                .append("INNER JOIN " + tableName)
                .toString();
        db.execSQL(query);
    }

    public synchronized Cursor getFilteredCursor(String prefix) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Log.e(TAG, "getFilteredCursor: Started");
        Log.e(TAG, "getFilteredCursor: prefix="+prefix);

        if(prefix.compareTo("") == 0){
            cursor = db.query(PassContract.PassEntry.TABLE_NAME,
                    null, //new String[]{PassContract.PassEntry.COLUMN_NAME_TITLE},
                    null,
                    null,
                    null,
                    null,
                    PassContract.PassEntry.COLUMN_NAME_ACCOUNT + " DESC");
            Log.e(TAG, "getFilteredCursor: null prefix, cursor.getCount()="+cursor.getCount());
        } else {
            cursor = db.query(PassContract.PassEntry.TABLE_NAME,
                    null, //new String[]{PassContract.PassEntry.COLUMN_NAME_TITLE},
                    PassContract.PassEntry.COLUMN_NAME_TITLE + " like '' || ? || '%'",
                    new String[]{prefix},
                    null,
                    null,
                    PassContract.PassEntry.COLUMN_NAME_ACCOUNT + " DESC");
            Log.e(TAG, "getFilteredCursor: cursor.getCount()="+cursor.getCount());
        }

        if(cursor == null || cursor.getCount() <=0) {
            Log.e(TAG, "getFilteredCursor: fail, cursor.getCount()="+cursor.getCount());
            new Exception("Cursor " + (cursor==null?"null":"fill with wrong data"));
        }

        return cursor;
    }
}
