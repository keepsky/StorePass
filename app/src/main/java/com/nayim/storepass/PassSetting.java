package com.nayim.storepass;

import android.content.Context;
import android.content.SharedPreferences;

public class PassSetting {

    static PassSetting sInstance;
    Context mContext;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    public final static int KEY_ACCOUNT = 0;
    public final static int KEY_LOCK_TYPE = 1;
    public final static int KEY_CLIPBD_ON = 2;
    public final static int KEY_SORT_TYPE = 3;
    public final static int KEY_HIDE_PW = 4;
    public final static int KEY_AUTO_LOGOUT = 5;
    public final static int KEY_BG_LOGOUT = 6;
    public final static int KEY_PASSWORD = 7;

    public final static int LAST = 7;

    public final static int ACCOUNT_NO = 1;
    public final static int ACCOUNT_YES = 2;
    public final static int LOCK_TYPE_PW = 0;
    public final static int LOCK_TYPE_FINGER = 1;
    public final static int CLIPBD_ON = 1;
    public final static int CLIPBD_OFF = 0;
    public final static int SORT_TYPE_TITLE = 1;
    public final static int SORT_TYPE_DATE = 0;
    public final static int HIDE_PW_YES = 1;
    public final static int HIDE_PW_NO = 0;
    public final static int AUTO_LOGOUT_YES = 1;
    public final static int AUTO_LOGOUT_NO = 0;
    public final static int BG_LOGOUT_YES = 1;
    public final static int BG_LOGOUT_NO = 0;




    private final static String[] keySetting = {
                    "LOGIN_ACCOUNT",
                    "LOCK_TYPE",
                    "CLIPBD_ON",
                    "SORT_TYPE",
                    "HIDE_PW",
                    "AUTO_LOGOUT",
                    "BG_LOGOUT",
                    "PASSW" };

    private PassSetting(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences("pass_settings", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    public static PassSetting getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new PassSetting(context);
        }

        return sInstance;
    }

    public static PassSetting getInstance() {
        return sInstance;
    }

    private String getKey(int key) {
        return keySetting[key];
    }

    public void setIntSetting(int key, int value) {
        switch(key) {
            case KEY_LOCK_TYPE:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_ACCOUNT:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_CLIPBD_ON:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_SORT_TYPE:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_HIDE_PW:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_AUTO_LOGOUT:
                mEditor.putInt(getKey(key), value);
                break;
            case KEY_BG_LOGOUT:
                mEditor.putInt(getKey(key), value);
                break;

            default:
                return;
        }

        mEditor.commit();
    }

    public void setTextSetting(int key, String value) {
        switch(key) {
            case KEY_PASSWORD:
                mEditor.putString(getKey(key), value);
                break;

            default:
                return;
        }

        mEditor.commit();
    }

    public int getIntSetting(int key) {
        switch(key) {
            case KEY_ACCOUNT:
                return mPref.getInt(getKey(key), ACCOUNT_NO);
            case KEY_LOCK_TYPE:
                return mPref.getInt(getKey(key), LOCK_TYPE_PW);
            case KEY_CLIPBD_ON:
                return mPref.getInt(getKey(key), LOCK_TYPE_PW);
            case KEY_SORT_TYPE:
                mEditor.putInt(getKey(key), SORT_TYPE_TITLE);
                break;
            case KEY_HIDE_PW:
                mEditor.putInt(getKey(key), HIDE_PW_NO);
                break;
            case KEY_AUTO_LOGOUT:
                mEditor.putInt(getKey(key), AUTO_LOGOUT_NO);
                break;
            case KEY_BG_LOGOUT:
                mEditor.putInt(getKey(key), BG_LOGOUT_NO);
                break;

            default:
                break;
        }

        return 0;
    }

    public String getTextSetting(int key) {
        switch(key) {
            case KEY_PASSWORD:
                return mPref.getString(getKey(key), null);

            default:
                break;
        }

        return null;
    }

}
