package com.example.sky.search;

import android.graphics.Bitmap;

import java.util.Date;

public class SearchItem {
    private String keyword;


    public SearchItem(String keyword) {
        this.keyword= keyword;

    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
