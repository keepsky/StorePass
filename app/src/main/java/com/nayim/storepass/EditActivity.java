package com.nayim.storepass;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";

    private EditText mTitleEditText;
    private EditText mAccountEditText;
    private EditText mPwEditText;
    private EditText mUrlEditText;
    private EditText mContentsEditText;

//    private long mPassId;
    private int mRequestCode;
    private Password mPassItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar editToolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(editToolbar);

        mTitleEditText = findViewById(R.id.title_edit);
        mAccountEditText = findViewById(R.id.account_edit);
        mPwEditText = findViewById(R.id.pw_edit);
        mUrlEditText = findViewById(R.id.url_edit);
        mContentsEditText = findViewById(R.id.contents_edit);

        Intent intent = getIntent();
        if(intent != null) {

            mRequestCode = intent.getIntExtra(MainActivity.REQUEST_TYPE, -1);
            mPassItem = (Password) intent.getSerializableExtra(MainActivity.PASSWORD_DAO);

            switch(mRequestCode){
                case MainActivity.REQUEST_CODE_EDIT:
                    mTitleEditText.setText(mPassItem.getTitle());
                    mAccountEditText.setText(mPassItem.getAccount());
                    mPwEditText.setText(mPassItem.getPw());
                    mUrlEditText.setText(mPassItem.getUrl());
                    mContentsEditText.setText(mPassItem.getContents());
                    break;

                case MainActivity.REQUEST_CODE_INSERT:
                    break;

                default:
                    Log.e(TAG, "Wrong mRequestCode : " + mRequestCode);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "onOptionsItemSelected(): mRequestCode = " + mRequestCode);

        switch(item.getItemId()){
            case R.id.save_menu_item:
                saveItem();
                Intent intent;
                switch (mRequestCode) {
                    case MainActivity.REQUEST_CODE_EDIT:
                        intent = new Intent(EditActivity.this, ViewActivity.class);
                        break;
                    case MainActivity.REQUEST_CODE_INSERT:
                        intent = new Intent(EditActivity.this, MainActivity.class);
                        break;
                    default :
                        return false;
                }

                intent.putExtra(MainActivity.PASSWORD_DAO, mPassItem);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveItem() {

        Log.e(TAG, "saveItem(): mRequestCode = " + mRequestCode);

        if(mPassItem == null)
            mPassItem = new Password();

        mPassItem.setTitle(mTitleEditText.getText().toString());
        mPassItem.setAccount(mAccountEditText.getText().toString());
        mPassItem.setPw(mPwEditText.getText().toString());
        mPassItem.setUrl(mUrlEditText.getText().toString());
        mPassItem.setContents(mContentsEditText.getText().toString());

        ContentValues val = new ContentValues();
        val.put(PassContract.PassEntry.COLUMN_NAME_TITLE, mPassItem.getTitle());
        if(mRequestCode == MainActivity.REQUEST_CODE_INSERT) {
            Random random = new Random();
            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            val.put(PassContract.PassEntry.COLUMN_NAME_COLOR, color);
            mPassItem.setColor(color);
        }
        val.put(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, mPassItem.getAccount());
        val.put(PassContract.PassEntry.COLUMN_NAME_PW, mPassItem.getPw());
        val.put(PassContract.PassEntry.COLUMN_NAME_URL, mPassItem.getUrl());
        val.put(PassContract.PassEntry.COLUMN_NAME_CONTENTS, mPassItem.getContents());

        Calendar calendar = Calendar.getInstance();
        String today = new StringBuilder()
                .append(calendar.get(calendar.YEAR)).append('-')
                .append(calendar.get(calendar.MONTH)+1).append('-')
                .append(calendar.get(calendar.DATE)).toString();

        val.put(PassContract.PassEntry.COLUMN_NAME_DATE, today);
        mPassItem.setDate(today);

        SQLiteDatabase db = PassDbHelper.getInstance(this).getWritableDatabase();


        switch(mRequestCode) {
            case MainActivity.REQUEST_CODE_INSERT:
                long newRowId = db.insert(PassContract.PassEntry.TABLE_NAME,null, val);
                if(newRowId == -1){
                    Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show();
                }
                break;

            case MainActivity.REQUEST_CODE_EDIT:
                int count = db.update(PassContract.PassEntry.TABLE_NAME, val,
                        PassContract.PassEntry._ID + " = " + mPassItem.getId(), null);
                if(count == 0) {
                    Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;

        }
    }
}
