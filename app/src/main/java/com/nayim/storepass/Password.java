package com.nayim.storepass;

import java.io.Serializable;

public class Password implements Serializable {

    long id;
    String title;
    int color;
    String account;
    String pw;
    String url;
    String contents;
    String date;

    public Password() {
    }

    public Password(long id, String title, int color, String account, String pw, String url, String contents, String date) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.account = account;
        this.pw = pw;
        this.url = url;
        this.contents = contents;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
