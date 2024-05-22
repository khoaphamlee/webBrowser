package com.example.sky.begin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BeginDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "_begin.db";
    private static final int DATABASE_VERSION = 1;

    public BeginDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng để lưu trữ nhật ký
        db.execSQL("CREATE TABLE browser_logs (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, event TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu (nếu cần)
    }

    public List<BeginItem> getAllLogs() {
        List<BeginItem> beginList = new ArrayList<>();

        // Xây dựng truy vấn SELECT để lấy dữ liệu từ bảng nhật ký
        String selectQuery = "SELECT * FROM browser_logs";

        // Mở kết nối đọc của cơ sở dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String oldDate = "";
        // Lặp qua các hàng của kết quả truy vấn và thêm chúng vào danh sách
        if (cursor.moveToLast()) { // Bắt đầu từ cuối danh sách
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                @SuppressLint("Range") String event = cursor.getString(cursor.getColumnIndex("event"));



                // Tạo một đối tượng LogItem từ dữ liệu và thêm vào danh sách

                BeginItem beginItem = new BeginItem(url, event);
                beginList.add(beginItem);
            } while (cursor.moveToPrevious()); // Đọc từ cuối về đầu
        }


        // Đóng cursor và đóng kết nối đến cơ sở dữ liệu
        cursor.close();
        db.close();

        return beginList;
    }


    public void setAllLogs(List<BeginItem> logList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa tất cả các mục hiện có trong bảng nhật ký
            db.delete("browser_logs", null, null);

            // Chèn các mục mới vào bảng nhật ký
            ContentValues values = new ContentValues();
            for (int i = logList.size() - 1; i >= 0; i--) {
                BeginItem beginItem = logList.get(i);

                    values.put("url", beginItem.getUrl());
                    values.put("event", beginItem.getEvent());

                    db.insert("browser_logs", null, values);

            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting logs into database", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}