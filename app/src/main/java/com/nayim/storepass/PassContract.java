package com.nayim.storepass;

import android.provider.BaseColumns;

import java.util.ArrayList;

public final class PassContract {

//    String mTitle;
//    String mAccount;
//    String mPw;
//    String mUrl;
//    String mContents;

    static ArrayList<Password> mPassList;

    private PassContract(){

    }

    public static ArrayList<Password> getInstance() {
        if(mPassList == null) {
            mPassList = new ArrayList<>();
        }

        return mPassList;
    }

    public static class PassEntry implements BaseColumns {
        public static final String TABLE_NAME = "pass";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_PW = "pw";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
