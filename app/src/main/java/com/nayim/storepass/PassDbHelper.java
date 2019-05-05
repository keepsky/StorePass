package com.nayim.storepass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class PassDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "PassDbHelper";
    private static PassDbHelper sInstance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "pass.db";
    private static final String DB_NAME_NEW = "pass_new.db";
    private static final String DB_PATH = "/data/data/PACKAGE/databases/";
    private static final String DB_NAME_BACKUP = "mypass.backup";
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

    public boolean backupDb(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            Log.e(TAG, "sd : " + sd.toString());
            Log.e(TAG, "data : " + data.toString());

            if (sd.canWrite()) {
                String currentDBPath = new StringBuilder()
                        .append("//data//")
                        .append(context.getPackageName())
                        .append("//databases//")
                        .append(DB_NAME)
                        .toString();

                String backupDBPath = DB_NAME_BACKUP;

                Log.e(TAG, "currentDBPath : " + currentDBPath);
                Log.e(TAG, "backupDBPath : " + backupDBPath);

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(context, "백업 성공", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "백업 실패", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public boolean restoreDb(Context context, SQLiteDatabase db) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = new StringBuilder()
                        .append("//data//" )
                        .append(context.getPackageName())
                        .append("//databases//")
                        .append(DB_NAME)
                        .toString();

                Log.e(TAG, "currentDBPath : " + currentDBPath);

                String backupDBPath = DB_NAME_BACKUP; // From SD directory.
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if(currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    this.close();
                    sInstance = null;
                }

                Toast.makeText(context, "복원 성공", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "복원 실패", Toast.LENGTH_SHORT).show();

        }
        return false;
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
