package com.nayim.storepass;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class PassCursorAdapter extends CursorAdapter implements Filterable {

    private Context context;
    private PassDbHelper dbHelper;

    public PassCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        this.context = context;
        dbHelper = PassDbHelper.getInstance(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.item_pass, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // For initial circle icon
        ImageView circleImage = view.findViewById(R.id.circle_image);

        // For title
        TextView itemText = view.findViewById(R.id.title_text);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_TITLE));
        itemText.setText(title);

        // For url
        TextView urlText = view.findViewById(R.id.url_text);
        String url = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_URL));
        urlText.setText(url);

        // For account
        TextView accountText = view.findViewById(R.id.account_text);
        String account = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_ACCOUNT));
        accountText.setText(account);

        // For initial char of title
        TextView initialText = view.findViewById(R.id.initial_text);
        char s = title.charAt(0);
        initialText.setText(s + "");
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_COLOR));
        circleImage.setColorFilter(color);

        // For Date
        TextView dateText = view.findViewById(R.id.date_text);
        String date = cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_DATE));
        dateText.setText(date);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
//        return super.convertToString(cursor);

        return cursor.getString(cursor.getColumnIndexOrThrow(PassContract.PassEntry.COLUMN_NAME_TITLE));
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
//        return super.runQueryOnBackgroundThread(constraint);
        if(getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        String filter = "";
        if(constraint != null) {
            filter = constraint.toString();
        }

        return dbHelper.getFilteredCursor(filter);
    }
}