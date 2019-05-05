package com.nayim.storepass;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.nayim.storepass.auth.AuthActivity;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_INSERT = 1000;
    public static final int REQUEST_CODE_EDIT = 1001;
    public static final int REQUEST_CODE_VIEW = 1002;
    public static final int REQUEST_CODE_AUTH = 1003;
    private static final String TAG = "MainActivity";

    private PassCursorAdapter mPassAdapter;
    private PassDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Need to fix for backup and restore
        // Check permission
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



        mDbHelper = PassDbHelper.getInstance(this);
        mDb = mDbHelper.getWritableDatabase();

        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("type", REQUEST_CODE_INSERT);
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        Cursor cursor = getPassCursor();
        mPassAdapter = new PassCursorAdapter(this, cursor);
        mListView = findViewById(R.id.pass_list);
        mListView.setTextFilterEnabled(true);
        mListView.setAdapter(mPassAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                Cursor cursor = (Cursor) mPassAdapter.getItem(position);

                String title = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_TITLE));
                String account = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_ACCOUNT));
                String pw = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_PW));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_URL));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("type", REQUEST_CODE_VIEW);
                intent.putExtra("id", id);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_TITLE, title);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, account);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_PW, pw);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_URL, url);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS, contents);

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

                if(mDbHelper.onBackup(this, mDb))
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.restore_menu_item:
                mDbHelper.onRestore(this, mDb);
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
}
