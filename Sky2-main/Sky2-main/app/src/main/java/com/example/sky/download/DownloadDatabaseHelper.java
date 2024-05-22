package com.example.sky.download;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import java.util.ArrayList;
import java.util.List;



public class DownloadDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "browser_download.db";
    private static final int DATABASE_VERSION = 1;

    public DownloadDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE browser_download (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, size LONG, date TEXT, uri TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    public List<DownloadItem> getAllDownloads() {
        List<DownloadItem> downloadList = new ArrayList<>();
        String selectQuery = "SELECT * FROM browser_download";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToLast())
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") Long size = cursor.getLong(cursor.getColumnIndex("size"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex("uri"));
                DownloadItem downloadItem = new DownloadItem(name, size, date, uri);
                downloadList.add(downloadItem);

            } while (cursor.moveToPrevious());
        cursor.close();
        db.close();

        return downloadList;
    }



    public void setAllDownloads(List<DownloadItem> downloadList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("browser_download", null, null);

            ContentValues values = new ContentValues();
            for (int i = downloadList.size() - 1; i >= 0; i--) {
                DownloadItem downloadItem = downloadList.get(i);
                if(!downloadItem.getFileUri().equals("") && !downloadItem.getFileName().equals("")) {
                    values.put("name", downloadItem.getFileName());
                    values.put("size", downloadItem.getFileSize());
                    values.put("date", downloadItem.getFileDate());
                    values.put("uri", downloadItem.getFileUri());
                    db.insert("browser_download", null, values);
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
    public void insertDownloadItem(DownloadItem downloadItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", downloadItem.getFileName());
        values.put("size", downloadItem.getFileSize());
        values.put("date", downloadItem.getFileDate());
        values.put("uri", downloadItem.getFileUri());

        try {
            db.insert("browser_download", null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting download item into database", e);
        } finally {
            db.close();
        }
    }
}

