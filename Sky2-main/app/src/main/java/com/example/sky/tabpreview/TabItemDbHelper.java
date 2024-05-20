package com.example.sky.tabpreview;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabItemDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tab_items_2.db";
    private static final int DATABASE_VERSION = 1;

    public TabItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tab_items (_id INTEGER , title TEXT, url TEXT, history TEXT, bitmap BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu (nếu cần)
    }

    // Phương thức để thêm các dòng dữ liệu
    public void insertTabItem(TabItem tabItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert dữ liệu vào bảng tab_items
            ContentValues values = new ContentValues();
            values.put("_id", tabItem.getId());
            values.put("title", tabItem.getTitle());
            values.put("url", tabItem.getUrl());
            values.put("history", StringUtil.join(tabItem.getHistory(), ", ")); // StringUtil là lớp chứa phương thức join
            // Chuyển đổi bitmap thành mảng byte trước khi lưu vào cơ sở dữ liệu

            byte[] byteArray = DbBitmapUtility.getBytes(tabItem.getBitmap());
            values.put("bitmap", byteArray);
            db.insertOrThrow("tab_items", null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Phương thức để lấy tất cả các dòng dữ liệu
    public List<TabItem> getAllTabItems() {
        List<TabItem> tabItemList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tab_items", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                    @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                    @SuppressLint("Range") String historyString = cursor.getString(cursor.getColumnIndex("history"));
                    List<String> history = Arrays.asList(historyString.split(", "));
                    @SuppressLint("Range") byte[] byteArray = cursor.getBlob(cursor.getColumnIndex("bitmap"));

                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    Bitmap bitmap = DbBitmapUtility.getImage(byteArray);
                    TabItem tabItem = new TabItem(id,title, url, history, bitmap);

                    tabItemList.add(tabItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
        return tabItemList;
    }

    public void setAllTabItems(List<TabItem> tabItemList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa tất cả các mục hiện có trong bảng tab_items
            db.delete("tab_items", null, null);

            // Chèn các mục mới vào bảng tab_items
            ContentValues values = new ContentValues();
            for (TabItem tabItem : tabItemList) {
                values.put("title", tabItem.getTitle());
                values.put("url", tabItem.getUrl());
                values.put("history", StringUtil.join(tabItem.getHistory(), ", ")); // StringUtil là lớp chứa phương thức join
                // Chuyển đổi bitmap thành mảng byte trước khi lưu vào cơ sở dữ liệu
                values.put("_id",tabItem.getId());
                byte[] byteArray = DbBitmapUtility.getBytes(tabItem.getBitmap());
                values.put("bitmap", byteArray);
                db.insertOrThrow("tab_items", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


}

