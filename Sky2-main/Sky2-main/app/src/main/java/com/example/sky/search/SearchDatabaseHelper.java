package com.example.sky.search;

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

public class SearchDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "_browser_search.db";
    private static final int DATABASE_VERSION = 1;

    public SearchDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng để lưu trữ lịch sử tìm kiếm
        db.execSQL("CREATE TABLE browser_search (id INTEGER PRIMARY KEY AUTOINCREMENT, keyword TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu (nếu cần)
    }

    public List<SearchItem> getAllSearches() {
        List<SearchItem> searchList = new ArrayList<>();

        // Xây dựng truy vấn SELECT để lấy dữ liệu từ bảng lịch sử tìm kiếm
        String selectQuery = "SELECT * FROM browser_search";

        // Mở kết nối đọc của cơ sở dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Lặp qua các hàng của kết quả truy vấn và thêm chúng vào danh sách
        if (cursor.moveToLast()) {
            do {
                @SuppressLint("Range") String keyword = cursor.getString(cursor.getColumnIndex("keyword"));

                // Tạo một đối tượng SearchItem từ dữ liệu và thêm vào danh sách
                SearchItem searchItem = new SearchItem(keyword);
                searchList.add(searchItem);
            } while (cursor.moveToPrevious());
        }

        // Đóng cursor và đóng kết nối đến cơ sở dữ liệu
        cursor.close();
        db.close();

        return searchList;
    }

    public void setAllSearches(List<SearchItem> searchList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa tất cả các mục hiện có trong bảng lịch sử tìm kiếm
            db.delete("browser_search", null, null);

            // Chèn các mục mới vào bảng lịch sử tìm kiếm
            ContentValues values = new ContentValues();
            for (int i = searchList.size() - 1; i >= 0; i--) {
                SearchItem searchItem = searchList.get(i);

                values.put("keyword", searchItem.getKeyword());
                db.insert("browser_search", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting searches into database", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
