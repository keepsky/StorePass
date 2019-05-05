package com.nayim.storepass;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    public static final String PASSWORD_DAO = "passDao";
    public static final String REQUEST_TYPE = "request_type";

    public static final int REQUEST_CODE_INSERT = 1000;
    public static final int REQUEST_CODE_EDIT = 1001;
    public static final int REQUEST_CODE_VIEW = 1002;
    public static final int REQUEST_CODE_AUTH = 1003;

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.WRITE_EXTERNAL_STORAGE};  // 외부 저장소

    private PassCursorAdapter mPassAdapter;
    private PassDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ListView mListView;
    private View mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainView = findViewById(R.id.main_layout);

        // TODO: Need to fix for backup and restore
        // Check permission
        checkPermission();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(!Settings.System.canWrite(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                intent.setData(Uri.parse("package:" + this.getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }


        // TODO: Implement authentication feature
//        startActivityForResult(new Intent(MainActivity.this, AuthActivity.class), REQUEST_CODE_AUTH);


        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(REQUEST_TYPE, REQUEST_CODE_INSERT);
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        openDatabase();
        bindViewWithDb();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                Cursor cursor = (Cursor) mPassAdapter.getItem(position);

                Password item = new Password(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_TITLE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_COLOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_ACCOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_PW)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_CONTENTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_DATE)));

                intent.putExtra(REQUEST_TYPE, REQUEST_CODE_VIEW);
                intent.putExtra(PASSWORD_DAO, item);

                startActivityForResult(intent, REQUEST_CODE_VIEW);
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // TODO: Need to fix for backup and restore
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(Settings.System.canWrite(this)) {
//               Log.d(TAG, "canWrite() : true");
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit, s=[" + s + "]");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: " + s);
                if("".equals(s)) {
                    mListView.clearTextFilter();
//                    mPassAdapter.swapCursor(getPassCursor());
                } else if (s.length() >= 2){
                    mListView.setFilterText(s);
                }
                return true;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.setting_menu_item:
                return true;

            case R.id.backup_menu_item:
                backupDatabase();
                return true;

            case R.id.restore_menu_item:
                restoreDialog();
                return true;

            case R.id.help_menu_item:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getPassCursor() {
        PassDbHelper dbHelper = PassDbHelper.getInstance(this);
        return dbHelper.getReadableDatabase()
                .query(PassContract.PassEntry.TABLE_NAME,
                        null, null, null, null, null, PassContract.PassEntry.COLUMN_NAME_ACCOUNT + " DESC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode : " + requestCode);

        if((requestCode == REQUEST_CODE_INSERT || requestCode == REQUEST_CODE_VIEW )&& resultCode == RESULT_OK) {

            mPassAdapter.swapCursor(getPassCursor());

            // TODO: Need something to refresh list view?
//            mPassAdapter.notifyDataSetChanged();
        }
    }

    private void restoreDialog() {
        final CharSequence[] PhoneModels = {"교체", "병합", "취소"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
//        alt_bld.setIcon(R.drawable.);
        alt_bld.setTitle("방법을 선책하세요");
        alt_bld.setSingleChoiceItems(PhoneModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        replaceDatabase();
                        break;
                    case 1:
                        restoreDatabase();
                        break;
                    default:
                        break;
                }
                dialog.cancel();
            }
        });

        AlertDialog alert = alt_bld.create();
        alert.show();
    }
    private void openDatabase() {
        mDbHelper = PassDbHelper.getInstance(this);
        mDb = mDbHelper.getWritableDatabase();
    }
    private void bindViewWithDb() {
        mListView = findViewById(R.id.pass_list);
        mListView.setTextFilterEnabled(true);

        Cursor cursor = getPassCursor();
        Log.e(TAG, "onOptionsItemSelected(): cursor.getCount()=" + cursor.getCount());

        mPassAdapter = new PassCursorAdapter(this, cursor);
        mListView.setAdapter(mPassAdapter);
    }

    public boolean backupDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            Log.e(TAG, "sd : " + sd.toString());
            Log.e(TAG, "data : " + data.toString());

            if (sd.canWrite()) {
                String currentDBPath = new StringBuilder()
                        .append("//data//")
                        .append(this.getPackageName())
                        .append("//databases//")
                        .append(PassDbHelper.DB_NAME)
                        .toString();

                String backupDBPath = PassDbHelper.DB_NAME_BACKUP;

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
                    Toast.makeText(this, "백업 성공", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "백업 실패", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void restoreDatabase() {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = new StringBuilder()
                        .append("//data//" )
                        .append(this.getPackageName())
                        .append("//databases//")
                        .append(PassDbHelper.DB_NAME_NEW)
                        .toString();

                String orgDBPath = new StringBuilder()
                        .append("//data//" )
                        .append(this.getPackageName())
                        .append("//databases//")
                        .append(PassDbHelper.DB_NAME)
                        .toString();

                Log.e(TAG, "restoreDatabase(): currentDBPath : " + currentDBPath);
                Log.e(TAG, "restoreDatabase(): orgDBPath : " + orgDBPath);


                String backupDBPath = PassDbHelper.DB_NAME_BACKUP; // From SD directory.

                File orgDB = new File(data, orgDBPath);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.e(TAG, "restoreDatabase(): 1");
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                if(orgDB.exists()) {
                    boolean b;
                    Log.e(TAG, "restoreDatabase(): 2");
                    ArrayList<Password> passList = getAllPassItems();
                    mDbHelper.close();
                    b = orgDB.delete();
                    Log.e(TAG, "restoreDatabase(): orgDB.delete()="+b);
                    b = currentDB.renameTo(orgDB);
                    Log.e(TAG, "restoreDatabase(): currentDB.renameTo(orgDB)="+b);

                    openDatabase();
                    putAllPassItems(mDb, passList);
                    bindViewWithDb();
                }
                Toast.makeText(this, "복원 성공", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "복원 실패", Toast.LENGTH_SHORT).show();

        }
    }


    public void replaceDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = new StringBuilder()
                        .append("//data//" )
                        .append(this.getPackageName())
                        .append("//databases//")
                        .append(PassDbHelper.DB_NAME)
                        .toString();

                Log.e(TAG, "currentDBPath : " + currentDBPath);

                String backupDBPath = PassDbHelper.DB_NAME_BACKUP; // From SD directory.
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if(currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    mDbHelper.close();
                    openDatabase();
                    bindViewWithDb();

                }

                Toast.makeText(this, "복원 성공", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "복원 실패", Toast.LENGTH_SHORT).show();

        }
    }

    private ArrayList<Password> getAllPassItems() {
        ArrayList<Password> arrayList = new ArrayList<>();

        Cursor cursor = mDbHelper.getReadableDatabase()
                .rawQuery("SELECT * FROM "+PassContract.PassEntry.TABLE_NAME, null);

        if(cursor.moveToFirst()) {
            do {
                Password item = new Password();
                item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_TITLE)));
                item.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_COLOR)));
                item.setAccount(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_ACCOUNT)));
                item.setPw(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_PW)));
                item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_URL)));
                item.setContents(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_CONTENTS)));
                item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_DATE)));
                arrayList.add(item);
            } while(cursor.moveToNext());
        }

        return arrayList;
    }

    private void putAllPassItems(SQLiteDatabase db, ArrayList<Password> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {
            Password item = arrayList.get(i);
            ContentValues val = new ContentValues();
            val.put(PassContract.PassEntry.COLUMN_NAME_TITLE, item.getTitle());
            val.put(PassContract.PassEntry.COLUMN_NAME_COLOR, item.getColor());
            val.put(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, item.getAccount());
            val.put(PassContract.PassEntry.COLUMN_NAME_PW, item.getPw());
            val.put(PassContract.PassEntry.COLUMN_NAME_URL, item.getUrl());
            val.put(PassContract.PassEntry.COLUMN_NAME_CONTENTS, item.getContents());
            val.put(PassContract.PassEntry.COLUMN_NAME_DATE, item.getDate());
            long newRowId = db.insert(PassContract.PassEntry.TABLE_NAME, null, val);
        }
    }

    private void checkPermission() {
        int writeExtStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(writeExtStoragePermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mMainView, "이 앱을 실행하려면 외부저장소 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
        if ( requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mMainView, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {

                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mMainView, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }
        }
    }
}
