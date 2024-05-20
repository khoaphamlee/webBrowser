package com.example.sky.tabpreview;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String OLD_KEY = "old";

    public static void saveOld(Context context, int old) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(OLD_KEY, old);
        editor.apply(); // Áp dụng thay đổi
    }

    public static int getOld(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(OLD_KEY, 0); // Trả về giá trị mặc định là 0 nếu không tìm thấy
    }
}

