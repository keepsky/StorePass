package com.nayim.storepass;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {

    private static final String TAG = "ViewActivity";

    //    public static final int REQUEST_CODE_EDIT = 1001;
    private EditText mTitleEditText;
    private EditText mAccountEditText;
    private EditText mPwEditText;
    private EditText mUrlEditText;
    private EditText mContentsEditText;

    private int mPosition;
    private long mPassId;
    private String mTitle;
    private String mAccount;
    private String mPw;
    private String mUrl;
    private String mContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Toolbar viewToolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(viewToolbar);

        mTitleEditText = findViewById(R.id.title_edit);
        mAccountEditText = findViewById(R.id.account_edit);
        mPwEditText = findViewById(R.id.pw_edit);
        mUrlEditText = findViewById(R.id.url_edit);
        mContentsEditText = findViewById(R.id.contents_edit);

        Intent intent = getIntent();
        if(intent != null) {
            mPassId = intent.getLongExtra("id", -1);
            mPosition = intent.getIntExtra("position", -1);
            mTitle = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_TITLE);
            mAccount = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT);
            mPw = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_PW);
            mUrl = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_URL);
            mContents = intent.getStringExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS);

            mTitleEditText.setText(mTitle);
            mAccountEditText.setText(mAccount);
            mPwEditText.setText(mPw);
            mUrlEditText.setText(mUrl);
            mContentsEditText.setText(mContents);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_menu_item:
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);

                intent.putExtra("type", MainActivity.REQUEST_CODE_EDIT);
                intent.putExtra("id", mPassId);
                intent.putExtra("position", mPosition);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_TITLE, mTitle);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT, mAccount);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_PW, mPw);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_URL, mUrl);
                intent.putExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS, mContents);

                startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT);
                return true;

            case R.id.delete_menu_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제 할려고?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = PassDbHelper.getInstance(ViewActivity.this).getWritableDatabase();
                        int count = db.delete(PassContract.PassEntry.TABLE_NAME,
                                PassContract.PassEntry._ID + " = " + mPassId, null);
                        if(count == 0){
                            Toast.makeText(ViewActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                        } else {
//                            mAdaptor.swapCursor(getPassCursor());
                            ArrayList<Password> passList = PassContract.getInstance();
                            passList.remove(mPosition);
                            Toast.makeText(ViewActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "requestCode = " + requestCode);
        Log.e(TAG, "resultCode = " + resultCode);

        if(requestCode == MainActivity.REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            if(intent != null) {
                mPassId = data.getLongExtra("id", -1);
                mTitle = data.getStringExtra(PassContract.PassEntry.COLUMN_NAME_TITLE);
                mAccount = data.getStringExtra(PassContract.PassEntry.COLUMN_NAME_ACCOUNT);
                mPw = data.getStringExtra(PassContract.PassEntry.COLUMN_NAME_PW);
                mUrl = data.getStringExtra(PassContract.PassEntry.COLUMN_NAME_URL);
                mContents = data.getStringExtra(PassContract.PassEntry.COLUMN_NAME_CONTENTS);


                Log.e(TAG, "mTitle = " + mTitle);
                Log.e(TAG, "mAccount = " + mAccount);


                mTitleEditText.setText(mTitle);
                mAccountEditText.setText(mAccount);
                mPwEditText.setText(mPw);
                mUrlEditText.setText(mUrl);
                mContentsEditText.setText(mContents);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }
}
