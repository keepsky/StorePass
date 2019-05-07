package com.nayim.storepass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private Switch mLockPw;
    private Switch mFinger;
    private Switch mClipBd;
    private Switch mOrder;
    private Switch mHidePw;
    private Switch mBgLogout;
    private Switch mAutoLogout;

    private ListView mSettingListView;
    private ArrayList<SettingItem> mSettingItems;
    private Toolbar mToolBar;

    private final static int NUM_SETTING_ITEMS = 6;

    int[] mImage = {
            R.drawable.ic_fingerprint_24dp,
            R.drawable.ic_content_paste_24dp,
            R.drawable.ic_sort_24dp,
            R.drawable.ic_visibility_off_24dp,
            R.drawable.ic_settings_backup_restore_24dp,
            R.drawable.ic_timer_24dp
    };
    String[] mTitle = {"로그인방식", "클립보드", "정렬", "비밀번호", "백그라운드", "자동 로그아웃"};
    String[] mDesc = {"로그인 화면에서 지문(on) 또는 비밀번호(off)를 사용합니다",
            "비밀번호를 클립보드로 복사합니다",
            "제목 또는 날짜 순으로 정렬합니다",
            "비밀번호를 (***) 방식으로 숨김처리합니다",
            "백그라운드에서 자동 로그아웃합니다",
            "일정 시간 미사용시 자동 로그아웃합니다"
    };

    int[] swVal = {PassSetting.KEY_LOCK_TYPE,
            PassSetting.KEY_CLIPBD_ON,
            PassSetting.KEY_SORT_TYPE,
            PassSetting.KEY_HIDE_PW,
            PassSetting.KEY_BG_LOGOUT,
            PassSetting.KEY_AUTO_LOGOUT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolBar = findViewById(R.id.setting_toolbar);
        mToolBar.setTitle("설정");
        setSupportActionBar(mToolBar);

        mSettingItems = getSettingList();

        mSettingListView = findViewById(R.id.setting_list);
        SettingAdapter adapter = new SettingAdapter(mSettingItems);
        mSettingListView.setAdapter(adapter);

    }

    ArrayList<SettingItem> getSettingList(){
        ArrayList<SettingItem> list = new ArrayList<>();
        SettingItem item;
        PassSetting setting = PassSetting.getInstance();

        for(int i=0;i<mImage.length;i++){
            boolean val = setting.getIntSetting(swVal[i])==1?true:false;
            item = new SettingItem(mImage[i], mTitle[i], mDesc[i], val);
            list.add(item);
        }

        return list;
    }

    private class SettingItem {
        int imageId;
        String title;
        String description;
        boolean swId;

        public SettingItem(int imageId, String title, String description, boolean swId) {
            this.imageId = imageId;
            this.title = title;
            this.description = description;
            this.swId = swId;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isSwId() {
            return swId;
        }

        public void setSwId(boolean swId) {
            this.swId = swId;
        }
    }

    private class SettingAdapter extends BaseAdapter {

        private List<SettingItem> mItems;

        public SettingAdapter(List<SettingItem> mItems) {
            this.mItems = mItems;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting, parent, false);

            ImageView settingImage = convertView.findViewById(R.id.setting_icon);
            TextView title = convertView.findViewById(R.id.setting_name_text);
            TextView desc = convertView.findViewById(R.id.setting_desc_text);
            Switch sw = convertView.findViewById(R.id.setting_sw);

            SettingItem item = mItems.get(position);
            settingImage.setImageResource(item.getImageId());
            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            sw.setChecked(item.isSwId());
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(SettingActivity.this, "item = " + position, Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}
