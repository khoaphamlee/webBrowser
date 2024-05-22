package com.example.sky.tabpreview;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class TabItem {
    private String title;
    private String url;
    private List<String> history;
    private Bitmap bitmap;

    private int id;

    public TabItem(int id,String title, String url, List<String> history, Bitmap bitmap) {
        this.title = title;
        this.url = url;
        this.history = new ArrayList<>(history);
        this.bitmap = bitmap;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getHistory() {
        return history;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHistory(List<String> history) {
        this.history = new ArrayList<>(history);
    }
}
