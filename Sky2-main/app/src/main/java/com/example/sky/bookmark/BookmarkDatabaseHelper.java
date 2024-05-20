package com.example.sky.bookmark;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sky.LogItem;

import java.util.ArrayList;
import java.util.List;



public class BookmarkDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "browser_bookmarks.db";
    private static final int DATABASE_VERSION = 1;

    public BookmarkDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE browser_bookmarks (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, event TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    public List<BookmarkItem> getAllBookmarks() {
        List<BookmarkItem> bookmarkList = new ArrayList<>();
        String selectQuery = "SELECT * FROM browser_bookmarks";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToLast())
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                @SuppressLint("Range") String event = cursor.getString(cursor.getColumnIndex("event"));
                BookmarkItem bookmarkItem = new BookmarkItem(url, event);
                bookmarkList.add(bookmarkItem);

            } while (cursor.moveToPrevious());
        cursor.close();
        db.close();

        return bookmarkList;
    }






    public void addBookmark(BookmarkItem bookmarkItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", bookmarkItem.getUrl());
        values.put("event", bookmarkItem.getEvent());

        db.insert("browser_bookmarks", null, values);
        db.close();
    }
    public void setAllBookmarks(List<BookmarkItem> bookmarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("browser_bookmarks", null, null);

            ContentValues values = new ContentValues();
            for (int i = bookmarks.size() - 1; i >= 0; i--) {
                BookmarkItem bookmarkItem = bookmarks.get(i);
                if(!bookmarkItem.getUrl().equals("") && !bookmarkItem.getEvent().equals("")) {
                    values.put("url", bookmarkItem.getUrl());
                    values.put("event", bookmarkItem.getEvent());
                    db.insert("browser_bookmarks", null, values);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting bookmarks into database", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
