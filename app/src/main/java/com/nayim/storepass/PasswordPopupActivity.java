package com.nayim.storepass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordPopupActivity extends Activity {

    EditText mCurPw;
    EditText mNewPw1;
    EditText mNewPw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pw_popup);

        //UI 객체생성
        mCurPw = findViewById(R.id.cur_pw_edit);
        mNewPw1 = findViewById(R.id.new_pw1_edit);
        mNewPw2 = findViewById(R.id.new_pw2_edit);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            Toast.makeText(this, "취소버튼을 누르세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        return;
    }

    public void onClickChange(View view) {

        String realPw = PassSetting.getInstance().getTextSetting(PassSetting.KEY_PASSWORD);
        String curPw = mCurPw.getText().toString();
        if(!realPw.equals(curPw)){
            Toast.makeText(this, "현재 비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String pw1 = mNewPw1.getText().toString();
        String pw2 = mNewPw2.getText().toString();
        if(!"".equals(pw1) && pw1.equals(pw2)) {
            Intent intent = new Intent();
            intent.putExtra("change_pw", pw1);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "새로 입력한 비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickCancel(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
