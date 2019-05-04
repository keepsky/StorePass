package com.nayim.storepass;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomPassAdapter extends BaseAdapter {

    public Context context;
    ArrayList<Password> mPassList;

    public CustomPassAdapter(Context context, ArrayList<Password> passList) {
        this.context = context;
        this.mPassList = passList;
    }

    @Override
    public int getCount() {
        return mPassList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPassList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_pass, null);

            // For title
            TextView itemText = convertView.findViewById(R.id.title_text);
            String title = mPassList.get(position).getTitle();
            itemText.setText(title);

            // For url
            TextView urlText = convertView.findViewById(R.id.url_text);
            urlText.setText(mPassList.get(position).getUrl());

            // For account
            TextView accountText = convertView.findViewById(R.id.account_text);
            String account = mPassList.get(position).getAccount();
            accountText.setText(account);

            // For displaying initial char of title in the circle icon
            ImageView circleImage = convertView.findViewById(R.id.circle_image);
            TextView initialText = convertView.findViewById(R.id.initial_text);
            char s = title.charAt(0);
            initialText.setText(s + "");
            circleImage.setColorFilter(mPassList.get(position).getColor());

            // For Date
            TextView dateText = convertView.findViewById(R.id.date_text);
            dateText.setText(mPassList.get(position).getDate());

        }
        return convertView;
    }

    public void insertItem(Password item) {
        if(mPassList != null) {
            mPassList.add(item);
        }
    }

    public void updateItem(Password item, int position) {
        if(mPassList != null) {
            mPassList.set(position, item);
        }
    }

}
