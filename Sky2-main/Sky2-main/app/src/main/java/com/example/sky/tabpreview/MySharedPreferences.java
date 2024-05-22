package com.example.sky.tabpreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MySharedPreferences {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String OLD_KEY = "old";
    private static final String A_OLD_KEY = "Aold";

    public static void saveOld(Context context, int old) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(OLD_KEY, old);

        editor.apply(); // Áp dụng thay đổi
    }

    public static int getOld(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(OLD_KEY, 0); // Trả về giá trị mặc định là 0 nếu không tìm thấy
    }

    public static void saveAOld(Context context, int Aold) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(A_OLD_KEY, Aold);
        editor.apply(); // Áp dụng thay đổi
    }

    public static int getAOld(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(A_OLD_KEY, 0); // Trả về giá trị mặc định là 0 nếu không tìm thấy
    }
}

