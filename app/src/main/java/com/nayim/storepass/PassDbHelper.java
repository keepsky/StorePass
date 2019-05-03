package com.nayim.storepass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
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
    private static final String DB_BACKUP_NAME = "mypass.backup";
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s BIGINT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s DATE)"
                    ,PassContract.PassEntry.TABLE_NAME
                    ,PassContract.PassEntry._ID
                    ,PassContract.PassEntry.COLUMN_NAME_TITLE
                    ,PassContract.PassEntry.COLUMN_NAME_COLOR
                    ,PassContract.PassEntry.COLUMN_NAME_ACCOUNT
                    ,PassContract.PassEntry.COLUMN_NAME_PW
                    ,PassContract.PassEntry.COLUMN_NAME_URL
                    ,PassContract.PassEntry.COLUMN_NAME_CONTENTS
                    ,PassContract.PassEntry.COLUMN_NAME_DATE);

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PassContract.PassEntry.TABLE_NAME;

    public static PassDbHelper getInstance(Context context) {
        if(sInstance == null) {
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

    public boolean onBackup(Context context, SQLiteDatabase db) {
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

                Log.e(TAG, "currentDBPath : " + currentDBPath);

                String backupDBPath = DB_BACKUP_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "백업 성공", Toast.LENGTH_SHORT).show();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "백업 실패", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public boolean onRestore(Context context, SQLiteDatabase db) {
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

                String backupDBPath = DB_BACKUP_NAME; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Import Successful!", Toast.LENGTH_SHORT).show();

                onUpgrade(db, DB_VERSION,DB_VERSION);
                onCreate(db);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Import Failed!", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

}
