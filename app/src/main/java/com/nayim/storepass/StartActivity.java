package com.nayim.storepass;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class StartActivity extends AppCompatActivity {

    private final static String TAG = "StartActivity";
    EditText password1;
    EditText password2;
    Button loginBtn;
    int mType;
    String mPassword;

    public final static int SIGNIN = 1;
    public final static int LOGIN = 2;
    public final static String TYPE = "type";
    public final static String PASS = "pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        password1 = findViewById(R.id.pass1Edit);
        password2 = findViewById(R.id.pass2Edit);
        loginBtn = findViewById(R.id.loginButton);

        Intent intent = getIntent();
        mType = intent.getIntExtra(TYPE, -1);
        Log.d(TAG, "onCreate(): mType="+mType);

        switch(mType) {
            case SIGNIN:
                password2.setVisibility(View.VISIBLE);
                break;

            case LOGIN:
                password2.setVisibility(View.INVISIBLE);
                mPassword = intent.getStringExtra(PASS);
                Log.d(TAG, "onCreate(): mPassword="+mPassword);
                break;

            default:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    public void checkLogin(View view) {
        String pw1 = password1.getText().toString();
        String pw2 = password2.getText().toString();

        switch(mType) {
            case SIGNIN:
                if(pw1.equals(pw2)) {
                    Intent intent = new Intent();
                    intent.putExtra(PASS, pw1);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, "입력한 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case LOGIN:
                if(mPassword.equals(pw1)) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, "입력한 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }
}
