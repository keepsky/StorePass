package com.nayim.storepass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    private Switch mLockPw;
    private Switch mFinger;
    private Switch mClipBd;
    private Switch mOrder;
    private Switch mHidePw;
    private Switch mBgLogout;
    private Switch mAutoLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        mLockPw = findViewById(R.id.lock_pw_sw);
        mFinger = findViewById(R.id.lock_fp_sw);
        mClipBd = findViewById(R.id.clip_sw);
        mOrder = findViewById(R.id.order_sw);
        mHidePw = findViewById(R.id.hide_pw_sw);
        mBgLogout = findViewById(R.id.bg_logout_sw);
        mAutoLogout = findViewById(R.id.auto_logout_sw);


    }
}
