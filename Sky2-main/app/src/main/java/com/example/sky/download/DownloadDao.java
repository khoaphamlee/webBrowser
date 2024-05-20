package com.example.sky.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DownloadDao {
    private DownloadDatabaseHelper dbHelper;

    public DownloadDao(Context context) {
        dbHelper = new DownloadDatabaseHelper(context);
    }

    public void insertDownloadItem(DownloadItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DownloadDatabaseHelper.COLUMN_FILE_NAME, item.getFileName());
        values.put(DownloadDatabaseHelper.COLUMN_FILE_SIZE, item.getFileSize());
        values.put(DownloadDatabaseHelper.COLUMN_FILE_DATE, item.getFileDate());
        values.put(DownloadDatabaseHelper.COLUMN_FILE_URI, item.getFileUri());

        db.insert(DownloadDatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }

    public List<DownloadItem> getAllDownloadItems() {
        List<DownloadItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DownloadDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex(DownloadDatabaseHelper.COLUMN_FILE_NAME));
                long fileSize = cursor.getLong(cursor.getColumnIndex(DownloadDatabaseHelper.COLUMN_FILE_SIZE));
                String fileDate = cursor.getString(cursor.getColumnIndex(DownloadDatabaseHelper.COLUMN_FILE_DATE));
                String fileUri = cursor.getString(cursor.getColumnIndex(DownloadDatabaseHelper.COLUMN_FILE_URI));

                items.add(new DownloadItem(fileName, fileSize, fileDate, fileUri));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }
}
