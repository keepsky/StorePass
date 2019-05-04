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

import java.util.ArrayList;
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

    private long mPassId;
    private int mType;
    private int mPosition;

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

            mType = intent.getIntExtra("type", -1);
            mPassId = intent.getLongExtra("id", -1);
            mPosition = intent.getIntExtra("position", -1);

            switch(mType){
                case MainActivity.REQUEST_CODE_EDIT:
                    String title = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_TITLE);
                    String account = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT);
                    String pw = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_PW);
                    String url = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_URL);
                    String contents = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS);

                    mTitleEditText.setText(title);
                    mAccountEditText.setText(account);
                    mPwEditText.setText(pw);
                    mUrlEditText.setText(url);
                    mContentsEditText.setText(contents);
                    break;

                case MainActivity.REQUEST_CODE_INSERT:
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

        Log.e(TAG, "mType = " + mType);
        Log.e(TAG, "mPassId = " + mPassId);

        switch(item.getItemId()){
            case R.id.save_menu_item:
                Password newItem = saveItem();
                Log.e(TAG, "title = " + newItem.getTitle());

                switch(mType) {
                    case MainActivity.REQUEST_CODE_EDIT:
                        Intent intent = new Intent(EditActivity.this, ViewActivity.class);

                        intent.putExtra(PassContract.PassEntry.COLUMN_NAME_TITLE, newItem.getTitle());
                        intent.putExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, newItem.getAccount());
                        intent.putExtra(PassContract.PassEntry.COLUMN_NAME_PW, newItem.getPw());
                        intent.putExtra(PassContract.PassEntry.COLUMN_NAME_URL, newItem.getUrl());
                        intent.putExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS, newItem.getContents());

                        setResult(RESULT_OK, intent);
                        break;

                    case MainActivity.REQUEST_CODE_INSERT:
                        setResult(RESULT_OK);
                        break;
                }

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Password saveItem() {

        Password item = new Password();
        ArrayList<Password> passList = PassContract.getInstance();

        item.setTitle(mTitleEditText.getText().toString());
        item.setAccount(mAccountEditText.getText().toString());
        item.setPw(mPwEditText.getText().toString());
        item.setUrl(mUrlEditText.getText().toString());
        item.setContents(mContentsEditText.getText().toString());

        Log.e(TAG, "title = " + item.getTitle());
        Log.e(TAG, "account = " + item.getAccount());
        Log.e(TAG, "pw = " + item.getPw());
        Log.e(TAG, "url = " + item.getUrl());


        ContentValues val = new ContentValues();
        val.put(PassContract.PassEntry.COLUMN_NAME_TITLE, item.getTitle());
        if(mPassId == -1) {
            Random random = new Random();
            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            val.put(PassContract.PassEntry.COLUMN_NAME_COLOR, color);
            item.setColor(color);
        } else {
            item.setColor(passList.get(mPosition).getColor());
        }
        val.put(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, item.getAccount());
        val.put(PassContract.PassEntry.COLUMN_NAME_PW, item.getPw());
        val.put(PassContract.PassEntry.COLUMN_NAME_URL, item.getUrl());
        val.put(PassContract.PassEntry.COLUMN_NAME_CONTENTS, item.getContents());

        Calendar calendar = Calendar.getInstance();
        String today = new StringBuilder()
                .append(calendar.get(calendar.YEAR)).append('-')
                .append(calendar.get(calendar.MONTH)+1).append('-')
                .append(calendar.get(calendar.DATE)).toString();

        val.put(PassContract.PassEntry.COLUMN_NAME_DATE, today);
        item.setDate(today);

        SQLiteDatabase db = PassDbHelper.getInstance(this).getWritableDatabase();
        if(mPassId == -1) {
            long newRowId = db.insert(PassContract.PassEntry.TABLE_NAME,null, val);
            if(newRowId == -1){
                Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
            } else {
                passList.add(item);
                Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show();
            }
        } else {
            int count = db.update(PassContract.PassEntry.TABLE_NAME, val,
                    PassContract.PassEntry._ID + " = " + mPassId, null);
            if(count == 0) {
                Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
            } else {
                passList.set(mPosition, item);
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
            }
        }

        return item;
    }
}
