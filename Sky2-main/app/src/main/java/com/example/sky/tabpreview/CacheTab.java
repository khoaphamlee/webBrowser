package com.example.sky.tabpreview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

import androidx.fragment.app.Fragment;

public class CacheTab {
    static public LruCache<Integer, Bundle> lruCache = new LruCache<>(20);
}
