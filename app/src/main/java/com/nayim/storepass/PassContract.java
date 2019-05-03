package com.nayim.storepass;

import android.provider.BaseColumns;

public final class PassContract {

    String mTitle;
    String mAccount;
    String mPw;
    String mUrl;
    String mContents;

    private PassContract(){

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
