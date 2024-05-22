package com.example.sky.qr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sky.history.LogItem;
import com.example.sky.bookmark.BookmarkItem;

import java.util.ArrayList;
import java.util.List;



public class QrDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "browser_qr.db";
    private static final int DATABASE_VERSION = 1;

    public QrDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE browser_qr (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    public List<QrItem> getAllQrs() {
        List<QrItem> qrList = new ArrayList<>();
        String selectQuery = "SELECT * FROM browser_qr";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToLast())
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                QrItem qrItem = new QrItem(url);
                qrList.add(qrItem);

            } while (cursor.moveToPrevious());
        cursor.close();
        db.close();

        return qrList;
    }


    public void addQr(QrItem qrItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", qrItem.getUrl());

        db.insert("browser_qr", null, values);
        db.close();
    }
    public void setAllQrs(List<QrItem> qrList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("browser_qr", null, null);

            ContentValues values = new ContentValues();
            for (int i = qrList.size() - 1; i >= 0; i--) {
                QrItem qrItem = qrList.get(i);
                if(!qrItem.getUrl().equals("")) {
                    values.put("url", qrItem.getUrl());
                    db.insert("browser_qr", null, values);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting qr into database", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}

